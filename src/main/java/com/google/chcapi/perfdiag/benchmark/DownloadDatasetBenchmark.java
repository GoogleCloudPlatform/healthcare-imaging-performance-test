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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.chcapi.perfdiag.benchmark.config.DicomStoreConfig;
import com.google.chcapi.perfdiag.model.Study;
import com.google.chcapi.perfdiag.profiler.HttpRequestProfiler;
import com.google.chcapi.perfdiag.profiler.HttpRequestProfilerFactory;
import com.google.chcapi.perfdiag.profiler.HttpRequestStatistics;

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
  private final HttpRequestStatistics retrieveStudyStats = new HttpRequestStatistics();
  
  /**
   * Thread pool.
   */
  private final ExecutorService pool = Executors.newFixedThreadPool(20);
  
  @Override
  protected void runIteration(int iteration, PrintStream output) throws Exception {
    // Fetch list of available studies
    final List<Study> studies = fetchStudies();
    
    // Create separate task for each study
    final List<Callable<HttpRequestProfiler>> tasks = new ArrayList<>();
    for (Study study : studies) {
      final String studyId = study.getStudyInstanceUID().getValue1();
      tasks.add(new Callable<HttpRequestProfiler>() {
        @Override public HttpRequestProfiler call() throws Exception {
          final HttpRequestProfiler request =
              HttpRequestProfilerFactory.createRetrieveDicomStudyRequest(dicomStoreConfig, studyId);
          request.execute();
          return request;
        }
      });
    }
    
    // Wait for completion and update statistics
    for (Future<HttpRequestProfiler> future : pool.invokeAll(tasks)) {
      try {
        final HttpRequestProfiler request = future.get();
        output.println(request.toCSVString(iteration));
        retrieveStudyStats.addProfile(request);
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
    final List<Study> studies = MAPPER.readValue(request.execute(),
        new TypeReference<List<Study>>() {});
    printStudiesFound(studies.size());
    printStatistics(request);
    return studies;
  }
  
  /* Object mapper to convert JSON response */
  private static final ObjectMapper MAPPER = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  
}
