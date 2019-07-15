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
 * Base abstract class for benchmarks. Subclasses should implement two methods:
 * <ul>
 *   <li>{@link #runIteration(int, PrintStream)} which is invoked for each benchmark iteration.</li>
 *   <li>{@link #printAggregates()} which is invoked after all benchmark iterations.</li>
 * </ul>
 * The {@link #validateConfig()} method may be overridden if benchmark requires additional
 * validation of command line options.
 * 
 * @author Mikhail Ukhlin
 * @see RetrieveStudyBenchmark
 * @see DownloadDatasetBenchmark
 */
public abstract class Benchmark extends BenchmarkMessages implements Runnable {
  
  /**
   * Common benchmark configuration from command line.
   */
  @Mixin
  protected CommonConfig commonConfig;
  
  /**
   * Latency of iteration in milliseconds.
   */
  protected long iterationLatency;
  
  /**
   * Benchmark entry point.
   */
  @Override
  public void run() {
    validateConfig();
    authorize();
    executeBenchmark();
    printAggregates();
  }
  
  /**
   * Validates configuration provided from command line.
   * 
   * @throws BenchmarkException if validation failed.
   */
  protected void validateConfig() {
    if (commonConfig.getIterations() < 1) {
      throw BenchmarkException.iterationsInvalid(commonConfig.getIterations());
    } else if (commonConfig.getMaxThreads() < 1) {
      throw BenchmarkException.threadsInvalid(commonConfig.getMaxThreads());
    }
  }
  
  /**
   * Acquires access token before benchmark execution.
   * 
   * @throws BenchmarkException if an error occurred.
   * @see HttpRequestProfilerFactory#refreshToken()
   */
  private void authorize() {
    try {
      HttpRequestProfilerFactory.refreshToken();
    } catch (Exception e) {
      throw BenchmarkException.authorizationFailed(e);
    }
  }
  
  /**
   * Executes benchmark. This method just invokes {@link #runIterations(PrintStream)} and provides
   * output stream depending on {@code --output} command line option.
   * 
   * @throws BenchmarkException if an error occurred.
   * @see #runIterations(PrintStream)
   * @see CommonConfig#getOutputFile()
   */
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
  
  /**
   * Runs benchmark iterations one by one invoking the {@link #runIteration(int, PrintStream)}
   * method. The number of iterations is specified by the {@code --iterations} command line option.
   * 
   * @param output Output stream to write metrics.
   * @throws BenchmarkException if an error occurred.
   * @see #runIteration(int, PrintStream)
   * @see CommonConfig#getIterations()
   */
  private void runIterations(PrintStream output) {
    final int iterations = commonConfig.getIterations();
    printBenchmarkStarted(iterations);
    for (int i = 0; i < iterations; i++) {
      try {
        printIterationStarted(i);
        runIteration(i, output);
      } catch (Exception e) {
        throw BenchmarkException.iterationFailed(i, e);
      }
    }
    printBenchmarkCompleted();
  }
  
  /**
   * Runs benchmark iteration and writes metrics to the specified output stream.
   * 
   * @param iteration Iteration number.
   * @param output Output stream to write metrics.
   * @throws Exception if an error occurred.
   */
  protected abstract void runIteration(int iteration, PrintStream output) throws Exception;
  
  /**
   * Prints aggregates to stdout include median, mean and percentiles calculated from the raw data
   * when all iterations are done.
   */
  protected abstract void printAggregates();
  
}
