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

import com.google.chcapi.perfdiag.benchmark.stats.LatencyAggregates;

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
   * Prints metrics of retrieve study iteration to stdout.
   * 
   * @param queryInstancesLatency Latency of querying instances.
   * @param firstResponseLatency Latency of first byte received.
   * @param firstInstanceLatency Latency of reading first instance.
   * @param totalLatency Latency of reading whole study.
   * @param totalBytesRead Total bytes read.
   */
  public static void printRetrieveStudyMetrics(long queryInstancesLatency,
      long firstResponseLatency, long firstInstanceLatency, long totalLatency, long totalBytesRead) {
    print("message.retrieveStudyMetrics",
        queryInstancesLatency,
        firstResponseLatency,
        firstInstanceLatency,
        totalLatency,
        totalBytesRead,
        (double) totalBytesRead / (double) totalLatency * 1000.0);
  }
  
  /**
   * Prints aggregates of retrieve study benchmark to stdout.
   * 
   * @param queryInstancesAggregates Aggregates for latency of querying instances.
   * @param firstResponseAggregates Aggregates for latency of first byte received.
   * @param firstInstanceAggregates Aggregates for latency of reading first instance.
   * @param totalAggregates Aggregates for latency of downloading the whole study.
   */
  public static void printRetrieveStudyAggregates(
      LatencyAggregates queryInstancesAggregates,
      LatencyAggregates firstResponseAggregates,
      LatencyAggregates firstInstanceAggregates,
      LatencyAggregates totalAggregates) {
    print("message.retrieveStudyAggregatesHeader");
    printAggregates(queryInstancesAggregates, firstResponseAggregates, firstInstanceAggregates, totalAggregates);
  }
  
  /**
   * Prints metrics of download dataset iteration to stdout.
   * 
   * @param queryStudiesLatency Latency of querying studies.
   * @param firstResponseLatency Latency of first byte received.
   * @param firstStudyLatency Latency of reading first study.
   * @param totalLatency Latency of downloading the whole dataset.
   * @param totalBytesRead Total bytes read.
   */
  public static void printDownloadDatasetMetrics(long queryStudiesLatency,
      long firstResponseLatency, long firstStudyLatency, long totalLatency, long totalBytesRead) {
    print("message.downloadDatasetMetrics",
        queryStudiesLatency,
        firstResponseLatency,
        firstStudyLatency,
        totalLatency,
        totalBytesRead,
        (double) totalBytesRead / (double) totalLatency * 1000.0);
  }
  
  /**
   * Prints aggregates of download dataset benchmark to stdout.
   * 
   * @param queryStudiesAggregates Aggregates for latency of querying studies.
   * @param firstResponseAggregates Aggregates for latency of first byte received.
   * @param firstStudyAggregates Aggregates for latency of reading first study.
   * @param totalAggregates Aggregates for latency of downloading the whole dataset.
   */
  public static void printDownloadDatasetAggregates(
      LatencyAggregates queryStudiesAggregates,
      LatencyAggregates firstResponseAggregates,
      LatencyAggregates firstStudyAggregates,
      LatencyAggregates totalAggregates) {
    print("message.downloadDatasetAggregatesHeader");
    printAggregates(queryStudiesAggregates, firstResponseAggregates, firstStudyAggregates, totalAggregates);
  }
  
  /**
   * Prints aggregates of a benchmark to stdout.
   * 
   * @param queryIDsAggregates Aggregates for latency of querying IDs.
   * @param firstResponseAggregates Aggregates for latency of first byte received.
   * @param firstDownloadAggregates Aggregates for latency of reading first item.
   * @param totalAggregates Aggregates for latency of downloading the whole dataset.
   */
  public static void printAggregates(
      LatencyAggregates queryIDsAggregates,
      LatencyAggregates firstResponseAggregates,
      LatencyAggregates firstDownloadAggregates,
      LatencyAggregates totalAggregates) {
    print("message.aggregates",
        queryIDsAggregates.getMin(),
        firstResponseAggregates.getMin(),
        firstDownloadAggregates.getMin(),
        totalAggregates.getMin(),
        queryIDsAggregates.getMax(),
        firstResponseAggregates.getMax(),
        firstDownloadAggregates.getMax(),
        totalAggregates.getMax(),
        queryIDsAggregates.getMean(),
        firstResponseAggregates.getMean(),
        firstDownloadAggregates.getMean(),
        totalAggregates.getMean(),
        queryIDsAggregates.getStddev(),
        firstResponseAggregates.getStddev(),
        firstDownloadAggregates.getStddev(),
        totalAggregates.getStddev(),
        queryIDsAggregates.getPercentile(LatencyAggregates.MEDIAN),
        firstResponseAggregates.getPercentile(LatencyAggregates.MEDIAN),
        firstDownloadAggregates.getPercentile(LatencyAggregates.MEDIAN),
        totalAggregates.getPercentile(LatencyAggregates.MEDIAN),
        queryIDsAggregates.getPercentile(LatencyAggregates.P1),
        firstResponseAggregates.getPercentile(LatencyAggregates.P1),
        firstDownloadAggregates.getPercentile(LatencyAggregates.P1),
        totalAggregates.getPercentile(LatencyAggregates.P1),
        queryIDsAggregates.getPercentile(LatencyAggregates.P2),
        firstResponseAggregates.getPercentile(LatencyAggregates.P2),
        firstDownloadAggregates.getPercentile(LatencyAggregates.P2),
        totalAggregates.getPercentile(LatencyAggregates.P2),
        queryIDsAggregates.getPercentile(LatencyAggregates.P5),
        firstResponseAggregates.getPercentile(LatencyAggregates.P5),
        firstDownloadAggregates.getPercentile(LatencyAggregates.P5),
        totalAggregates.getPercentile(LatencyAggregates.P5),
        queryIDsAggregates.getPercentile(LatencyAggregates.P10),
        firstResponseAggregates.getPercentile(LatencyAggregates.P10),
        firstDownloadAggregates.getPercentile(LatencyAggregates.P10),
        totalAggregates.getPercentile(LatencyAggregates.P10),
        queryIDsAggregates.getPercentile(LatencyAggregates.P90),
        firstResponseAggregates.getPercentile(LatencyAggregates.P90),
        firstDownloadAggregates.getPercentile(LatencyAggregates.P90),
        totalAggregates.getPercentile(LatencyAggregates.P90),
        queryIDsAggregates.getPercentile(LatencyAggregates.P95),
        firstResponseAggregates.getPercentile(LatencyAggregates.P95),
        firstDownloadAggregates.getPercentile(LatencyAggregates.P95),
        totalAggregates.getPercentile(LatencyAggregates.P95),
        queryIDsAggregates.getPercentile(LatencyAggregates.P98),
        firstResponseAggregates.getPercentile(LatencyAggregates.P98),
        firstDownloadAggregates.getPercentile(LatencyAggregates.P98),
        totalAggregates.getPercentile(LatencyAggregates.P98),
        queryIDsAggregates.getPercentile(LatencyAggregates.P99),
        firstResponseAggregates.getPercentile(LatencyAggregates.P99),
        firstDownloadAggregates.getPercentile(LatencyAggregates.P99),
        totalAggregates.getPercentile(LatencyAggregates.P99));
  }
  
}
