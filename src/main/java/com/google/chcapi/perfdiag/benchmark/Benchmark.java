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

import java.io.File;
import java.io.PrintStream;
import java.io.FileNotFoundException;

import picocli.CommandLine.Mixin;

import com.google.chcapi.perfdiag.benchmark.config.CommonConfig;
import com.google.chcapi.perfdiag.profiler.HttpRequestProfilerFactory;

/**
 * 
 * 
 * @author Mikhail Ukhlin
 */
public abstract class Benchmark extends BenchmarkMessages implements Runnable {
  
  @Mixin
  protected CommonConfig commonConfig;
  
  @Override
  public void run() {
    authorize();
    executeBenchmark();
    printMetrics();
  }
  
  /**
   * Acquires access token before benchmark execution.
   */
  private void authorize() {
    try {
      HttpRequestProfilerFactory.setClientSecrets(commonConfig.getClientSecretsFile());
      HttpRequestProfilerFactory.getCredential(true);
    } catch (Exception e) {
      throw BenchmarkException.authorizationFailed(e);
    }
  }
  
  private void executeBenchmark() {
    final File outputFile = commonConfig.getOutputFile();
    if (outputFile == null) {
      // Run benchmark and write metrics to stdout
      runIterations(System.out);
    } else {
      // Run benchmark and write metrics to the specified output file
      try (PrintStream output = new PrintStream(outputFile)) {
        runIterations(output);
      } catch (FileNotFoundException e) {
        throw BenchmarkException.ioError(e);
      }
    }
  }
  
  private void runIterations(PrintStream output) {
    for (int i = 0; i < commonConfig.getIterations(); i++) {
      try {
        printIterationStarted(i);
        runIteration(i, output);
      } catch (Exception e) {
        throw BenchmarkException.iterationFailed(i, e);
      }
    }
    printBenchmarkCompleted();
  }
  
  protected abstract void runIteration(int iteration, PrintStream output) throws Exception;
  
  protected abstract void printMetrics();
  
}
