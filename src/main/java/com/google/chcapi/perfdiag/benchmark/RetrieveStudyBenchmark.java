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
import com.google.chcapi.perfdiag.profiler.HttpRequestProfiler;
import com.google.chcapi.perfdiag.profiler.HttpRequestProfilerFactory;
import com.google.chcapi.perfdiag.profiler.HttpRequestMetrics;
import com.google.chcapi.perfdiag.profiler.HttpRequestAggregates;

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
   * Metrics for query instances request.
   */
  private HttpRequestMetrics queryInstancesMetrics;
  
  /**
   * Metrics for first instance retrieved.
   */
  private HttpRequestMetrics firstInstanceMetrics;
  
  /**
   * Aggregated metrics for retrieve study instance requests.
   */
  private HttpRequestAggregates retrieveStudySummary;
  
  /**
   * Retrieves DICOM study instances in parallel and stores metrics for each request to the
   * specified output stream.
   * 
   * @param iteration Iteration number.
   * @param output Output stream to write metrics.
   * @throws Exception if an error occurred.
   */
  @Override
  protected void runIteration(int iteration, PrintStream output) throws Exception {
    // Fetch list of available study instances
    final List<Instance> instances = fetchStudyInstances();
    printInstancesFound(instances.size(), commonConfig.getMaxThreads());
    
    // Create separate task for each study instance
    final ExecutorService pool = Executors.newFixedThreadPool(commonConfig.getMaxThreads());
    final List<Callable<HttpRequestMetrics>> tasks = new ArrayList<>();
    for (Instance instance : instances) {
      final String seriesId = instance.getSeriesUID();
      final String instanceId = instance.getInstanceUID();
      if (!(seriesId == null || instanceId == null)) {
        tasks.add(new Callable<HttpRequestMetrics>() {
          @Override public HttpRequestMetrics call() throws Exception {
            final HttpRequestProfiler request =
                HttpRequestProfilerFactory.createRetrieveDicomStudyInstanceRequest(dicomStudyConfig,
                    seriesId, instanceId);
            final HttpRequestMetrics metrics = request.execute(NullOutputStream.NULL_OUTPUT_STREAM);
            // Save first instance metrics if it is first request
            synchronized (RetrieveStudyBenchmark.this) {
              if (firstInstanceMetrics == null) {
                firstInstanceMetrics = metrics;
              }
            }
            // Print progress
            printProgress();
            return metrics;
          }
        });
      }
    }
    
    // Wait for completion and update metrics
    retrieveStudySummary = new HttpRequestAggregates(tasks.size());
    final List<Future<HttpRequestMetrics>> futures = pool.invokeAll(tasks);
    if (output == System.out) {
      // New line after progress
      output.println();
    }
    for (Future<HttpRequestMetrics> future : futures) {
      try {
        final HttpRequestMetrics metrics = future.get();
        output.println(metrics.toCSVString(iteration));
        retrieveStudySummary.addMetrics(metrics);
      } catch (Exception e) {
        printRequestFailed(e);
      }
    }
  }
  
  /**
   * Prints gathered metrics to stdout.
   */
  @Override
  protected void printMetrics() {
    printRetrieveStudySummary(queryInstancesMetrics, firstInstanceMetrics, retrieveStudySummary);
  }
  
  /**
   * Queries DICOM store for available study instances and returns list of study instances.
   * 
   * @return List of available study instances.
   * @throws Exception if an error occurred.
   */
  private List<Instance> fetchStudyInstances() throws Exception {
    final HttpRequestProfiler request =
        HttpRequestProfilerFactory.createListDicomStudyInstancesRequest(dicomStudyConfig);
    final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    queryInstancesMetrics = request.execute(buffer);
    return MAPPER.readValue(buffer.toByteArray(), new TypeReference<List<Instance>>() {});
  }
  
  /* Object mapper to convert JSON response */
  private static final ObjectMapper MAPPER = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  
}
