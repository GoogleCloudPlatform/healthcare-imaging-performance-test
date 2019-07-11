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

import java.util.ResourceBundle;

import com.google.chcapi.perfdiag.profiler.HttpRequestMetrics;
import com.google.chcapi.perfdiag.profiler.HttpRequestAggregates;

/**
 * Helper class used to format and print benchmark messages from resource bundle.
 * 
 * @author Mikhail Ukhlin
 */
public abstract class BenchmarkMessages {
  
  /* Resource bundle that contains benchmark messages */
  private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("benchmark-messages");
  
  /**
   * Returns a formatted string using the specified message key and arguments.
   * 
   * @param key Message key in resource bundle.
   * @param args Arguments referenced by the format specifiers in the message.
   * @return A formatted string using the specified message key and arguments.
   */
  public static String format(String key, Object... args) {
    return String.format(BUNDLE.getString(key), args);
  }
  
  /**
   * Prints a formatted message using the specified message key and arguments to stdout.
   * 
   * @param key Message key in resource bundle.
   * @param args Arguments referenced by the format specifiers in the message.
   * @see #format(String, Object...)
   */
  public static void print(String key, Object... args) {
    System.out.println(format(key, args));
  }
  
  /**
   * Prints a formatted error message using the specified message key and arguments to stderr.
   * 
   * @param key Message key in resource bundle.
   * @param args Arguments referenced by the format specifiers in the message.
   * @see #format(String, Object...)
   */
  public static void printError(String key, Object... args) {
    System.err.println(format(key, args));
  }
  
  /**
   * Prints {@code '.'} character to stdout indicating that some process is working.
   */
  public static void printProgress() {
    System.out.print(".");
  }
  
  /**
   * Prints benchmark started message to stdout.
   * 
   * @param iterations How many times the routine is executed.
   */
  public static void printBenchmarkStarted(int iterations) {
    print("message.benchmarkStarted", iterations);
  }
  
  /**
   * Prints benchmark iteration started message to stdout.
   * 
   * @param iteration The iteration number.
   */
  public static void printIterationStarted(int iteration) {
    print("message.iterationStarted", iteration);
  }
  
  /**
   * Prints benchmark completed message to stdout.
   */
  public static void printBenchmarkCompleted() {
    print("message.benchmarkCompleted");
  }
  
  /**
   * Prints studies found message to stdout.
   * 
   * @param studies Number of found studies.
   * @param threads Maximum number of threads to run in parallel.
   */
  public static void printStudiesFound(int studies, int threads) {
    print("message.stadiesFound", studies, Math.min(studies, threads));
  }
  
  /**
   * Prints study instances found message to stdout.
   * 
   * @param instances Number of found study instances.
   * @param threads Maximum number of threads to run in parallel.
   */
  public static void printInstancesFound(int instances, int threads) {
    print("message.instancesFound", instances, Math.min(instances, threads));
  }
  
  /**
   * Prints request failed error message to stderr.
   * 
   * @param cause Exception cause.
   */
  public static void printRequestFailed(Exception cause) {
    printError("message.requestFailed", cause.getMessage());
  }
  
  /**
   * Prints percentiles statistics to stdout.
   * 
   * @param count Number of iterations.
   * @param aggregates Total aggregates for all iterations.
   */
  public static void printPercentiles(int count, HttpRequestAggregates aggregates) {
    print("message.percentiles",
        count,
        aggregates.getMinLatency(),
        aggregates.getMaxLatency(),
        aggregates.getMean(),
        aggregates.getStddev(),
        aggregates.getPercentile(HttpRequestAggregates.MEDIAN),
        aggregates.getPercentile(HttpRequestAggregates.P1),
        aggregates.getPercentile(HttpRequestAggregates.P2),
        aggregates.getPercentile(HttpRequestAggregates.P5),
        aggregates.getPercentile(HttpRequestAggregates.P10),
        aggregates.getPercentile(HttpRequestAggregates.P90),
        aggregates.getPercentile(HttpRequestAggregates.P95),
        aggregates.getPercentile(HttpRequestAggregates.P98),
        aggregates.getPercentile(HttpRequestAggregates.P99));
  }
  
  /**
   * Prints metrics of retrieve study benchmark to stdout.
   * 
   * @param queryInstancesMetrics Metrics of querying instances.
   * @param firstResponseMetrics Metrics of first byte received.
   * @param firstInstanceMetrics Metrics of first instance read.
   * @param iterationMetrics Metrics of whole iteration.
   */
  public static void printRetrieveStudyMetrics(
      HttpRequestMetrics queryInstancesMetrics,
      HttpRequestMetrics firstResponseMetrics,
      HttpRequestMetrics firstInstanceMetrics,
      HttpRequestAggregates iterationMetrics) {
    print("message.retrieveStudyMetrics",
        queryInstancesMetrics.getTotalLatency(),
        firstResponseMetrics.getResponseLatency(),
        firstInstanceMetrics.getTotalLatency(),
        iterationMetrics.getTotalLatency(),
        iterationMetrics.getTotalBytesRead(),
        iterationMetrics.getTotalTransferRate());
  }
  
  /**
   * Prints metrics of download dataset benchmark to stdout.
   * 
   * @param queryStudiesMetrics Metrics of querying studies.
   * @param firstResponseMetrics Metrics of first byte received.
   * @param firstStudyMetrics Metrics of first study read.
   * @param iterationMetrics Metrics of whole iteration.
   */
  public static void printDownloadDatasetMetrics(
      HttpRequestMetrics queryStudiesMetrics,
      HttpRequestMetrics firstResponseMetrics,
      HttpRequestMetrics firstStudyMetrics,
      HttpRequestAggregates iterationMetrics) {
    print("message.downloadDatasetMetrics",
        queryStudiesMetrics.getTotalLatency(),
        firstResponseMetrics.getResponseLatency(),
        firstStudyMetrics.getTotalLatency(),
        iterationMetrics.getTotalLatency(),
        iterationMetrics.getTotalBytesRead(),
        iterationMetrics.getTotalTransferRate());
  }
  
}
