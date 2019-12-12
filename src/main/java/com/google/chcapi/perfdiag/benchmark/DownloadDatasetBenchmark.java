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

import com.google.chcapi.perfdiag.model.Attributes;
import com.google.chcapi.perfdiag.benchmark.config.DicomStoreConfig;
import com.google.chcapi.perfdiag.benchmark.stats.MetricAggregates;
import com.google.chcapi.perfdiag.profiler.HttpRequestProfiler;
import com.google.chcapi.perfdiag.profiler.HttpRequestProfilerFactory;
import com.google.chcapi.perfdiag.profiler.HttpRequestMetrics;

/**
 * This benchmark shows the user how fast it is to download a large dataset (a whole DICOM store).
 * It involves sending requests to get study information (QIDO) and sending paralleled requests to
 * retrieve all studies in the dicom store (WADO).
 * 
 * @author Mikhail Ukhlin
 */
@Command
public class DownloadDatasetBenchmark extends Benchmark {
  
  /**
   * DICOM store configuration from command line.
   */
  @Mixin
  protected DicomStoreConfig dicomStoreConfig;
  
  /**
   * Aggregates for latency of querying studies.
   */
  private MetricAggregates queryStudiesAggregates;
  
  /**
   * Aggregates for latency of first byte received.
   */
  private MetricAggregates firstResponseAggregates;
  
  /**
   * Aggregates for latency of reading first study.
   */
  private MetricAggregates firstStudyAggregates;
  
  /**
   * Aggregates for latency of downloading the whole dataset.
   */
  private MetricAggregates totalAggregates;
  
  /**
   * Aggregates for transfer rate of downloading the whole dataset.
   */
  private MetricAggregates transferRateAggregates;
  
  /**
   * Validates configuration and initializes aggregates.
   */
  @Override
  protected void validateConfig() {
    super.validateConfig();
    final int iterations = commonConfig.getIterations();
    queryStudiesAggregates = new MetricAggregates(iterations);
    firstResponseAggregates = new MetricAggregates(iterations);
    firstStudyAggregates = new MetricAggregates(iterations);
    totalAggregates = new MetricAggregates(iterations);
    transferRateAggregates = new MetricAggregates(iterations);
  }
  
  /**
   * Retrieves DICOM studies in parallel and stores metrics for each request to the specified
   * output stream if any.
   * 
   * @param iteration Iteration number.
   * @param output Output stream to write metrics or {@code null} if output file is not specified.
   * @throws Exception if an error occurred.
   */
  @Override
  protected void runIteration(int iteration, PrintStream output) throws Exception {
    final AtomicReference<HttpRequestMetrics> firstResponseMetrics = new AtomicReference<>();
    final AtomicReference<HttpRequestMetrics> firstStudyMetrics = new AtomicReference<>();
    
    // Fetch list of available studies
    final HttpRequestProfiler queryStudiesRequest =
        HttpRequestProfilerFactory.createListDicomStudiesRequest(dicomStoreConfig);
    final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    final long iterationStartTime = System.currentTimeMillis();
    final HttpRequestMetrics queryStudiesMetrics = queryStudiesRequest.execute(buffer);
    final List<Attributes> studies = MAPPER.readValue(buffer.toByteArray(),
        new TypeReference<List<Attributes>>() {});
    printStudiesFound(studies.size(), commonConfig.getMaxThreads());
    
    if (studies.size() > 0) {
      // Create separate task for each study
      final ExecutorService pool = Executors.newFixedThreadPool(commonConfig.getMaxThreads());
      final List<Callable<HttpRequestMetrics>> tasks = new ArrayList<>();
      for (Attributes study : studies) {
        final String studyId = study.getStudyUID();
        if (studyId != null) {
          tasks.add(new Callable<HttpRequestMetrics>() {
            @Override public HttpRequestMetrics call() throws Exception {
              // Execute request
              final HttpRequestProfiler request =
                  HttpRequestProfilerFactory.createRetrieveDicomStudyRequest(dicomStoreConfig, studyId);
              final HttpRequestMetrics metrics = request.execute(NullOutputStream.NULL_OUTPUT_STREAM);
              
              // Update first response and first study metrics
              firstResponseMetrics.updateAndGet(m -> {
                return m == null || metrics.getResponseTime() < m.getResponseTime() ? metrics : m;
              });
              firstStudyMetrics.updateAndGet(m -> {
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
      
      // Print requests metrics and count bytes read
      int cacheHits = 0;
      int cacheMisses = 0;
      long totalBytesRead = queryStudiesMetrics.getBytesRead();
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
      queryStudiesAggregates.addValue(queryStudiesMetrics.getTotalLatency());
      firstResponseAggregates.addValue(firstResponseMetrics.get().getResponseLatency());
      firstStudyAggregates.addValue(firstStudyMetrics.get().getTotalLatency());
      totalAggregates.addValue(totalLatency);
      transferRateAggregates.addValue(transferRate);
      
      // Print iteration metrics to stdout
      printDownloadDatasetMetrics(queryStudiesMetrics.getTotalLatency(),
          firstResponseMetrics.get().getResponseLatency(), firstStudyMetrics.get().getTotalLatency(),
          totalLatency, totalBytesRead, transferRate, cacheHits, cacheMisses);
      
      // Print iteration metrics to CSV file if output option is specified
      if (output != null) {
        if (iteration == 0) {
          output.println("ITERATION, QUERYING_STUDIES_LATENCY, FIRST_BYTE_RECEIVED_LATENCY, "
              + "READING_FIRST_STUDY_LATENCY, READING_WHOLE_DATASET_LATENCY, "
              + "TOTAL_BYTES_READ, MB_READ_PER_SECOND");
        }
        output.print(iteration);
        output.print(", ");
        output.print(queryStudiesMetrics.getTotalLatency());
        output.print(", ");
        output.print(firstResponseMetrics.get().getResponseLatency());
        output.print(", ");
        output.print(firstStudyMetrics.get().getTotalLatency());
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
   * Prints calculated aggreagtes for all iterations to stdout.
   */
  @Override
  protected void printAggregates() {
    printDownloadDatasetAggregates(queryStudiesAggregates, firstResponseAggregates,
        firstStudyAggregates, totalAggregates, transferRateAggregates);
  }
  
  /* Object mapper to convert JSON response */
  private static final ObjectMapper MAPPER = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  
}
