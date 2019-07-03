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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.chcapi.perfdiag.model.Study;
import com.google.chcapi.perfdiag.benchmark.config.DicomStoreConfig;
import com.google.chcapi.perfdiag.profiler.HttpRequestProfiler;
import com.google.chcapi.perfdiag.profiler.HttpRequestProfilerFactory;
import com.google.chcapi.perfdiag.profiler.HttpRequestMetrics;
import com.google.chcapi.perfdiag.profiler.HttpRequestAggregates;

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
   * Metrics for query studies request.
   */
  private HttpRequestMetrics queryStudiesMetrics;
  
  /**
   * Metrics for first study retrieved.
   */
  private HttpRequestMetrics firstStudyMetrics;
  
  /**
   * Aggregated statistics for retrieve study requests.
   */
  private HttpRequestAggregates downloadDatasetSummary;
  
  /**
   * Retrieves DICOM studies in parallel and stores metrics for each request to the specified
   * output stream.
   * 
   * @param iteration Iteration number.
   * @param output Output stream to write metrics.
   * @throws Exception if an error occurred.
   */
  @Override
  protected void runIteration(int iteration, PrintStream output) throws Exception {
    // Fetch list of available studies
    final List<Study> studies = fetchStudies();
    printStudiesFound(studies.size(), commonConfig.getMaxThreads());
    
    // Create separate task for each study
    final ExecutorService pool = Executors.newFixedThreadPool(commonConfig.getMaxThreads());
    final List<Callable<HttpRequestMetrics>> tasks = new ArrayList<>();
    for (Study study : studies) {
      final String studyId = study.getStudyUID();
      if (studyId != null) {
        tasks.add(new Callable<HttpRequestMetrics>() {
          @Override public HttpRequestMetrics call() throws Exception {
            final HttpRequestProfiler request =
                HttpRequestProfilerFactory.createRetrieveDicomStudyRequest(dicomStoreConfig, studyId);
            final HttpRequestMetrics metrics = request.execute(NullOutputStream.NULL_OUTPUT_STREAM);
            // Save first instance metrics if it is first request
            synchronized (DownloadDatasetBenchmark.this) {
              if (firstStudyMetrics == null) {
                firstStudyMetrics = metrics;
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
    downloadDatasetSummary = new HttpRequestAggregates(tasks.size());
    final List<Future<HttpRequestMetrics>> futures = pool.invokeAll(tasks);
    if (output == System.out) {
      // New line after progress
      output.println();
    }
    for (Future<HttpRequestMetrics> future : futures) {
      try {
        final HttpRequestMetrics metrics = future.get();
        output.println(metrics.toCSVString(iteration));
        downloadDatasetSummary.addMetrics(metrics);
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
    printDownloadDatasetSummary(queryStudiesMetrics, firstStudyMetrics, downloadDatasetSummary);
  }
  
  /**
   * Queries DICOM store for available studies and returns list of studies.
   * 
   * @return List of available studies.
   * @throws Exception if an error occurred.
   */
  private List<Study> fetchStudies() throws Exception {
    final HttpRequestProfiler request =
        HttpRequestProfilerFactory.createListDicomStudiesRequest(dicomStoreConfig);
    final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    queryStudiesMetrics = request.execute(buffer);
    return MAPPER.readValue(buffer.toByteArray(), new TypeReference<List<Study>>() {});
  }
  
  /* Object mapper to convert JSON response */
  private static final ObjectMapper MAPPER = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  
}
