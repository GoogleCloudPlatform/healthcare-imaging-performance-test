// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.chcapi.perfdiag.benchmark;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

import picocli.CommandLine.Mixin;
import picocli.CommandLine.Command;

import org.apache.commons.io.output.NullOutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;

import com.google.chcapi.perfdiag.model.Instance;
import com.google.chcapi.perfdiag.benchmark.config.DicomStudyConfig;
import com.google.chcapi.perfdiag.benchmark.stats.MetricAggregates;
import com.google.chcapi.perfdiag.profiler.HttpRequestProfiler;
import com.google.chcapi.perfdiag.profiler.HttpRequestProfilerFactory;
import com.google.chcapi.perfdiag.profiler.HttpRequestMetrics;

/**
 * This benchmark shows how fast it can be to retrieve a whole study with Google Cloud Healthcare
 * Imaging API. It involves sending request to get instance information (QIDO) and sending
 * paralleled GET requests to retrieve each instance (WADO).
 * 
 * @author Mikhail Ukhlin
 */
@Command
public class RetrieveStudyBenchmark extends Benchmark {
  
  /**
   * DICOM study configuration from command line.
   */
  @Mixin
  protected DicomStudyConfig dicomStudyConfig;
  
  /**
   * Aggregates for latency of querying instances.
   */
  private MetricAggregates queryInstancesAggregates;
  
  /**
   * Aggregates for latency of first byte received.
   */
  private MetricAggregates firstResponseAggregates;
  
  /**
   * Aggregates for latency of reading first instance.
   */
  private MetricAggregates firstInstanceAggregates;
  
  /**
   * Aggregates for latency of reading whole study.
   */
  private MetricAggregates totalAggregates;
  
  /**
   * Aggregates for transfer rate of downloading the whole study.
   */
  private MetricAggregates transferRateAggregates;
  
  /**
   * Validates configuration and initializes aggregates.
   */
  @Override
  protected void validateConfig() {
    super.validateConfig();
    final int iterations = commonConfig.getIterations();
    queryInstancesAggregates = new MetricAggregates(iterations);
    firstResponseAggregates = new MetricAggregates(iterations);
    firstInstanceAggregates = new MetricAggregates(iterations);
    totalAggregates = new MetricAggregates(iterations);
    transferRateAggregates = new MetricAggregates(iterations);
  }
  
  /**
   * Retrieves DICOM study instances in parallel and stores metrics for each request to the
   * specified output stream if any.
   * 
   * @param iteration Iteration number.
   * @param output Output stream to write metrics or {@code null} if output file is not specified.
   * @throws Exception if an error occurred.
   */
  @Override
  protected void runIteration(int iteration, PrintStream output) throws Exception {
    final AtomicReference<HttpRequestMetrics> firstResponseMetrics = new AtomicReference<>();
    final AtomicReference<HttpRequestMetrics> firstInstanceMetrics = new AtomicReference<>();
    
    // Fetch list of available study instances
    final HttpRequestProfiler queryInstancesRequest =
        HttpRequestProfilerFactory.createListDicomStudyInstancesRequest(dicomStudyConfig);
    final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    final long iterationStartTime = System.currentTimeMillis();
    final HttpRequestMetrics queryInstancesMetrics = queryInstancesRequest.execute(buffer);
    final List<Instance> instances = MAPPER.readValue(buffer.toByteArray(),
        new TypeReference<List<Instance>>() {});
    printInstancesFound(instances.size(), commonConfig.getMaxThreads());
    
    if (instances.size() > 0) {
      // Create separate task for each study instance
      final ExecutorService pool = Executors.newFixedThreadPool(commonConfig.getMaxThreads());
      final List<Callable<HttpRequestMetrics>> tasks = new ArrayList<>();
      for (Instance instance : instances) {
        final String seriesId = instance.getSeriesUID();
        final String instanceId = instance.getInstanceUID();
        if (!(seriesId == null || instanceId == null)) {
          tasks.add(new Callable<HttpRequestMetrics>() {
            @Override public HttpRequestMetrics call() throws Exception {
              // Execute request
              final HttpRequestProfiler request =
                  HttpRequestProfilerFactory.createRetrieveDicomStudyInstanceRequest(dicomStudyConfig,
                      seriesId, instanceId);
              final HttpRequestMetrics metrics = request.execute(NullOutputStream.NULL_OUTPUT_STREAM);
              
              // Update first response and first instance metrics
              firstResponseMetrics.updateAndGet(m -> {
                return m == null || metrics.getResponseTime() < m.getResponseTime() ? metrics : m;
              });
              firstInstanceMetrics.updateAndGet(m -> {
                return m == null || metrics.getEndTime() < m.getEndTime() ? metrics : m;
              });
              
              // Print progress
              printProgress();
              return metrics;
            }
          });
        }
      }
      
      // Wait for completion
      final List<Future<HttpRequestMetrics>> futures = pool.invokeAll(tasks);
      final long totalLatency = System.currentTimeMillis() - iterationStartTime;
      
      if (output == System.out) {
        // New line after progress
        output.println();
      }
      
      // Print requests metrics and count bytes read
      int cacheHits = 0;
      int cacheMisses = 0;
      long totalBytesRead = queryInstancesMetrics.getBytesRead();
      for (Future<HttpRequestMetrics> future : futures) {
        try {
          final HttpRequestMetrics metrics = future.get();
          totalBytesRead += metrics.getBytesRead();
          cacheHits = metrics.getCacheStatus().incrementHits(cacheHits);
          cacheMisses = metrics.getCacheStatus().incrementMisses(cacheMisses);
        } catch (Exception e) {
          printRequestFailed(e);
        }
      }
      
      // Update aggregates
      final double transferRate = (double) totalBytesRead / (double) totalLatency / 1048.576;
      queryInstancesAggregates.addValue(queryInstancesMetrics.getTotalLatency());
      firstResponseAggregates.addValue(firstResponseMetrics.get().getResponseLatency());
      firstInstanceAggregates.addValue(firstInstanceMetrics.get().getTotalLatency());
      totalAggregates.addValue(totalLatency);
      transferRateAggregates.addValue(transferRate);
      
      // Print metrics
      printRetrieveStudyMetrics(queryInstancesMetrics.getTotalLatency(),
          firstResponseMetrics.get().getResponseLatency(),
          firstInstanceMetrics.get().getTotalLatency(), totalLatency, totalBytesRead,
          transferRate, cacheHits, cacheMisses);
      
      // Print iteration metrics to CSV file if output option is specified
      if (output != null) {
        if (iteration == 0) {
          output.println("ITERATION, QUERYING_INSTANCES_LATENCY, FIRST_BYTE_RECEIVED_LATENCY, "
              + "READING_FIRST_INSTANCE_LATENCY, READING_WHOLE_STUDY_LATENCY, "
              + "TOTAL_BYTES_READ, MB_READ_PER_SECOND");
        }
        output.print(iteration);
        output.print(", ");
        output.print(queryInstancesMetrics.getTotalLatency());
        output.print(", ");
        output.print(firstResponseMetrics.get().getResponseLatency());
        output.print(", ");
        output.print(firstInstanceMetrics.get().getTotalLatency());
        output.print(", ");
        output.print(totalLatency);
        output.print(", ");
        output.print(totalBytesRead);
        output.print(", ");
        output.print(transferRate);
        output.println();
      }
    }
  }
  
  /**
   * Prints calculated aggregates for all iterations to stdout.
   */
  @Override
  protected void printAggregates() {
    printRetrieveStudyAggregates(queryInstancesAggregates, firstResponseAggregates,
        firstInstanceAggregates, totalAggregates, transferRateAggregates);
  }
  
  /* Object mapper to convert JSON response */
  private static final ObjectMapper MAPPER = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  
}
