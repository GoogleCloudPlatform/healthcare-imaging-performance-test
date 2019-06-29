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
 * This benchmark shows the user how fast it is to download a large dataset (a whole dicom store).
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
   * Aggregated statistics for retrieve study requests.
   */
  private final HttpRequestAggregates retrieveStudyStats = new HttpRequestAggregates();
  
  /**
   * Thread pool.
   */
  private final ExecutorService pool = Executors.newFixedThreadPool(20);
  
  @Override
  protected void runIteration(int iteration, PrintStream output) throws Exception {
    // Fetch list of available studies
    final List<Study> studies = fetchStudies();
    
    // Create separate task for each study
    final List<Callable<HttpRequestMetrics>> tasks = new ArrayList<>();
    for (Study study : studies) {
      final String studyId = study.getStudyUID();
      if (studyId != null) {
        tasks.add(new Callable<HttpRequestMetrics>() {
          @Override public HttpRequestMetrics call() throws Exception {
            final HttpRequestProfiler request =
                HttpRequestProfilerFactory.createRetrieveDicomStudyRequest(dicomStoreConfig, studyId);
            final HttpRequestMetrics metrics = request.execute(NullOutputStream.NULL_OUTPUT_STREAM);
            printRequestExecuted(request, metrics.getBytesRead());
            return metrics;
          }
        });
      }
    }
    
    // Wait for completion and update statistics
    for (Future<HttpRequestMetrics> future : pool.invokeAll(tasks)) {
      try {
        final HttpRequestMetrics metrics = future.get();
        output.println(metrics.toCSVString(iteration));
        retrieveStudyStats.addProfile(metrics);
      } catch (Exception e) {
        printRequestFailed(e);
      }
    }
  }
  
  @Override
  protected void printMetrics() {
    printStatistics(retrieveStudyStats);
  }
  
  private List<Study> fetchStudies() throws Exception {
    final HttpRequestProfiler request =
        HttpRequestProfilerFactory.createListDicomStudiesRequest(dicomStoreConfig);
    final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    final HttpRequestMetrics metrics = request.execute(buffer);
    final List<Study> studies = MAPPER.readValue(buffer.toByteArray(),
        new TypeReference<List<Study>>() {});
    printStudiesFound(studies.size(), metrics.getTotalLatency());
    return studies;
  }
  
  /* Object mapper to convert JSON response */
  private static final ObjectMapper MAPPER = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  
}
