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

package com.google.chcapi.benchmark.routine;

import java.io.File;
import java.io.PrintStream;

import picocli.CommandLine.Option;

public abstract class Benchmark implements Runnable {
  
  @Option(
      names = {"-i", "--iterations"},
      descriptionKey = "benchmark.iterations.description",
      required = false
  )
  private int iterations = 1;
  
  @Option(
      names = {"-o", "--output"},
      descriptionKey = "benchmark.output.description",
      required = false
  )
  private File outputFile = null;
  
  @Option(
      names = {"-s", "--dicom-store-name"},
      descriptionKey = "benchmark.dicom-store-name.description",
      required = true
  )
  private String dicomStoreName;
  
  @Override
  public void run() {
    runBenchmark();
    printResults();
  }
  
  private void runBenchmark() {
    for (int i = 0; i < iterations; i++) {
      try {
        doRunBenchmark(i + 1);
      } catch (Exception e) {
        throw new BenchmarkException("Iteration " + (i + 1) + " failed", e);
      }
    }
  }
  
  private void printResults() {
    try {
      if (outputFile == null) {
        // Print benchmark results to stdout
        doPrintResults(System.out);
      } else {
        // Print benchmark results to the specified output file
        try (PrintStream output = new PrintStream(outputFile)) {
          doPrintResults(output);
        }
      }
    } catch (Exception e) {
      throw new BenchmarkException("Error while printing benchmark results", e);
    }
  }
  
  protected abstract void doRunBenchmark(int iteration) throws Exception;
  
  protected abstract void doPrintResults(PrintStream output) throws Exception;
  
}
