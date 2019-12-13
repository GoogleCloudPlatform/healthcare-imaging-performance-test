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

import com.google.chcapi.perfdiag.benchmark.stats.MetricAggregates;

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
    print("message.stadiesFound", studies, threads);
  }
  
  /**
   * Prints study instances found message to stdout.
   * 
   * @param instances Number of found study instances.
   * @param frames Number of frames.
   * @param threads Maximum number of threads to run in parallel.
   */
  public static void printInstancesFound(int instances, int frames, int threads) {
    print("message.instancesFound", instances, frames, threads);
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
   * @param transferRate Bytes read per second.
   * @param frameRate Frames read per second.
   * @param cacheHits Number of cache hits.
   * @param cacheMisses Number of cache misses.
   */
  public static void printRetrieveStudyMetrics(long queryInstancesLatency,
      long firstResponseLatency, long firstInstanceLatency, long totalLatency, long totalBytesRead,
      double transferRate, double frameRate, int cacheHits, int cacheMisses) {
    print("message.retrieveStudyMetrics",
        queryInstancesLatency,
        firstResponseLatency,
        firstInstanceLatency,
        totalLatency,
        totalBytesRead,
        transferRate,
        frameRate,
        cacheHits,
        cacheMisses);
  }
  
  /**
   * Prints aggregates of retrieve study benchmark to stdout.
   * 
   * @param queryInstancesAggregates Aggregates for latency of querying instances.
   * @param firstResponseAggregates Aggregates for latency of first byte received.
   * @param firstFrameAggregates Aggregates for latency of reading first frame.
   * @param totalAggregates Aggregates for latency of downloading the whole study.
   * @param transferRateAggregates Aggregates for transfer rate of downloading the whole study.
   * @param frameRateAggregates Aggregates for frame rate of downloading the whole study.
   */
  public static void printRetrieveStudyAggregates(
      MetricAggregates queryInstancesAggregates,
      MetricAggregates firstResponseAggregates,
      MetricAggregates firstFrameAggregates,
      MetricAggregates totalAggregates,
      MetricAggregates transferRateAggregates,
      MetricAggregates frameRateAggregates) {
    print("message.retrieveStudyAggregates",
        queryInstancesAggregates.getMin(),
        firstResponseAggregates.getMin(),
        firstFrameAggregates.getMin(),
        totalAggregates.getMin(),
        transferRateAggregates.getMin(),
        frameRateAggregates.getMin(),
        queryInstancesAggregates.getMax(),
        firstResponseAggregates.getMax(),
        firstFrameAggregates.getMax(),
        totalAggregates.getMax(),
        transferRateAggregates.getMax(),
        frameRateAggregates.getMax(),
        queryInstancesAggregates.getMean(),
        firstResponseAggregates.getMean(),
        firstFrameAggregates.getMean(),
        totalAggregates.getMean(),
        transferRateAggregates.getMean(),
        frameRateAggregates.getMean(),
        queryInstancesAggregates.getStddev(),
        firstResponseAggregates.getStddev(),
        firstFrameAggregates.getStddev(),
        totalAggregates.getStddev(),
        transferRateAggregates.getStddev(),
        frameRateAggregates.getStddev(),
        queryInstancesAggregates.getPercentile(MetricAggregates.MEDIAN),
        firstResponseAggregates.getPercentile(MetricAggregates.MEDIAN),
        firstFrameAggregates.getPercentile(MetricAggregates.MEDIAN),
        totalAggregates.getPercentile(MetricAggregates.MEDIAN),
        transferRateAggregates.getPercentile(MetricAggregates.MEDIAN),
        frameRateAggregates.getPercentile(MetricAggregates.MEDIAN),
        queryInstancesAggregates.getPercentile(MetricAggregates.P1),
        firstResponseAggregates.getPercentile(MetricAggregates.P1),
        firstFrameAggregates.getPercentile(MetricAggregates.P1),
        totalAggregates.getPercentile(MetricAggregates.P1),
        transferRateAggregates.getPercentile(MetricAggregates.P1),
        frameRateAggregates.getPercentile(MetricAggregates.P1),
        queryInstancesAggregates.getPercentile(MetricAggregates.P2),
        firstResponseAggregates.getPercentile(MetricAggregates.P2),
        firstFrameAggregates.getPercentile(MetricAggregates.P2),
        totalAggregates.getPercentile(MetricAggregates.P2),
        transferRateAggregates.getPercentile(MetricAggregates.P2),
        frameRateAggregates.getPercentile(MetricAggregates.P2),
        queryInstancesAggregates.getPercentile(MetricAggregates.P5),
        firstResponseAggregates.getPercentile(MetricAggregates.P5),
        firstFrameAggregates.getPercentile(MetricAggregates.P5),
        totalAggregates.getPercentile(MetricAggregates.P5),
        transferRateAggregates.getPercentile(MetricAggregates.P5),
        frameRateAggregates.getPercentile(MetricAggregates.P5),
        queryInstancesAggregates.getPercentile(MetricAggregates.P10),
        firstResponseAggregates.getPercentile(MetricAggregates.P10),
        firstFrameAggregates.getPercentile(MetricAggregates.P10),
        totalAggregates.getPercentile(MetricAggregates.P10),
        transferRateAggregates.getPercentile(MetricAggregates.P10),
        frameRateAggregates.getPercentile(MetricAggregates.P10),
        queryInstancesAggregates.getPercentile(MetricAggregates.P90),
        firstResponseAggregates.getPercentile(MetricAggregates.P90),
        firstFrameAggregates.getPercentile(MetricAggregates.P90),
        totalAggregates.getPercentile(MetricAggregates.P90),
        transferRateAggregates.getPercentile(MetricAggregates.P90),
        frameRateAggregates.getPercentile(MetricAggregates.P90),
        queryInstancesAggregates.getPercentile(MetricAggregates.P95),
        firstResponseAggregates.getPercentile(MetricAggregates.P95),
        firstFrameAggregates.getPercentile(MetricAggregates.P95),
        totalAggregates.getPercentile(MetricAggregates.P95),
        transferRateAggregates.getPercentile(MetricAggregates.P95),
        frameRateAggregates.getPercentile(MetricAggregates.P95),
        queryInstancesAggregates.getPercentile(MetricAggregates.P98),
        firstResponseAggregates.getPercentile(MetricAggregates.P98),
        firstFrameAggregates.getPercentile(MetricAggregates.P98),
        totalAggregates.getPercentile(MetricAggregates.P98),
        transferRateAggregates.getPercentile(MetricAggregates.P98),
        frameRateAggregates.getPercentile(MetricAggregates.P98),
        queryInstancesAggregates.getPercentile(MetricAggregates.P99),
        firstResponseAggregates.getPercentile(MetricAggregates.P99),
        firstFrameAggregates.getPercentile(MetricAggregates.P99),
        totalAggregates.getPercentile(MetricAggregates.P99),
        transferRateAggregates.getPercentile(MetricAggregates.P99),
        frameRateAggregates.getPercentile(MetricAggregates.P99));
  }
  
  /**
   * Prints metrics of download dataset iteration to stdout.
   * 
   * @param queryStudiesLatency Latency of querying studies.
   * @param firstResponseLatency Latency of first byte received.
   * @param firstStudyLatency Latency of reading first study.
   * @param totalLatency Latency of downloading the whole dataset.
   * @param totalBytesRead Total bytes read.
   * @param transferRate Bytes read per second.
   * @param cacheHits Number of cache hits.
   * @param cacheMisses Number of cache misses.
   */
  public static void printDownloadDatasetMetrics(long queryStudiesLatency,
      long firstResponseLatency, long firstStudyLatency, long totalLatency, long totalBytesRead,
      double transferRate, int cacheHits, int cacheMisses) {
    print("message.downloadDatasetMetrics",
        queryStudiesLatency,
        firstResponseLatency,
        firstStudyLatency,
        totalLatency,
        totalBytesRead,
        transferRate,
        cacheHits,
        cacheMisses);
  }
  
  /**
   * Prints aggregates of download dataset benchmark to stdout.
   * 
   * @param queryStudiesAggregates Aggregates for latency of querying studies.
   * @param firstResponseAggregates Aggregates for latency of first byte received.
   * @param firstStudyAggregates Aggregates for latency of reading first study.
   * @param totalAggregates Aggregates for latency of downloading the whole dataset.
   * @param transferRateAggregates Aggregates for transfer rate of downloading the whole dataset.
   */
  public static void printDownloadDatasetAggregates(
      MetricAggregates queryStudiesAggregates,
      MetricAggregates firstResponseAggregates,
      MetricAggregates firstStudyAggregates,
      MetricAggregates totalAggregates,
      MetricAggregates transferRateAggregates) {
    print("message.downloadDatasetAggregates",
        queryStudiesAggregates.getMin(),
        firstResponseAggregates.getMin(),
        firstStudyAggregates.getMin(),
        totalAggregates.getMin(),
        transferRateAggregates.getMin(),
        queryStudiesAggregates.getMax(),
        firstResponseAggregates.getMax(),
        firstStudyAggregates.getMax(),
        totalAggregates.getMax(),
        transferRateAggregates.getMax(),
        queryStudiesAggregates.getMean(),
        firstResponseAggregates.getMean(),
        firstStudyAggregates.getMean(),
        totalAggregates.getMean(),
        transferRateAggregates.getMean(),
        queryStudiesAggregates.getStddev(),
        firstResponseAggregates.getStddev(),
        firstStudyAggregates.getStddev(),
        totalAggregates.getStddev(),
        transferRateAggregates.getStddev(),
        queryStudiesAggregates.getPercentile(MetricAggregates.MEDIAN),
        firstResponseAggregates.getPercentile(MetricAggregates.MEDIAN),
        firstStudyAggregates.getPercentile(MetricAggregates.MEDIAN),
        totalAggregates.getPercentile(MetricAggregates.MEDIAN),
        transferRateAggregates.getPercentile(MetricAggregates.MEDIAN),
        queryStudiesAggregates.getPercentile(MetricAggregates.P1),
        firstResponseAggregates.getPercentile(MetricAggregates.P1),
        firstStudyAggregates.getPercentile(MetricAggregates.P1),
        totalAggregates.getPercentile(MetricAggregates.P1),
        transferRateAggregates.getPercentile(MetricAggregates.P1),
        queryStudiesAggregates.getPercentile(MetricAggregates.P2),
        firstResponseAggregates.getPercentile(MetricAggregates.P2),
        firstStudyAggregates.getPercentile(MetricAggregates.P2),
        totalAggregates.getPercentile(MetricAggregates.P2),
        transferRateAggregates.getPercentile(MetricAggregates.P2),
        queryStudiesAggregates.getPercentile(MetricAggregates.P5),
        firstResponseAggregates.getPercentile(MetricAggregates.P5),
        firstStudyAggregates.getPercentile(MetricAggregates.P5),
        totalAggregates.getPercentile(MetricAggregates.P5),
        transferRateAggregates.getPercentile(MetricAggregates.P5),
        queryStudiesAggregates.getPercentile(MetricAggregates.P10),
        firstResponseAggregates.getPercentile(MetricAggregates.P10),
        firstStudyAggregates.getPercentile(MetricAggregates.P10),
        totalAggregates.getPercentile(MetricAggregates.P10),
        transferRateAggregates.getPercentile(MetricAggregates.P10),
        queryStudiesAggregates.getPercentile(MetricAggregates.P90),
        firstResponseAggregates.getPercentile(MetricAggregates.P90),
        firstStudyAggregates.getPercentile(MetricAggregates.P90),
        totalAggregates.getPercentile(MetricAggregates.P90),
        transferRateAggregates.getPercentile(MetricAggregates.P90),
        queryStudiesAggregates.getPercentile(MetricAggregates.P95),
        firstResponseAggregates.getPercentile(MetricAggregates.P95),
        firstStudyAggregates.getPercentile(MetricAggregates.P95),
        totalAggregates.getPercentile(MetricAggregates.P95),
        transferRateAggregates.getPercentile(MetricAggregates.P95),
        queryStudiesAggregates.getPercentile(MetricAggregates.P98),
        firstResponseAggregates.getPercentile(MetricAggregates.P98),
        firstStudyAggregates.getPercentile(MetricAggregates.P98),
        totalAggregates.getPercentile(MetricAggregates.P98),
        transferRateAggregates.getPercentile(MetricAggregates.P98),
        queryStudiesAggregates.getPercentile(MetricAggregates.P99),
        firstResponseAggregates.getPercentile(MetricAggregates.P99),
        firstStudyAggregates.getPercentile(MetricAggregates.P99),
        totalAggregates.getPercentile(MetricAggregates.P99),
        transferRateAggregates.getPercentile(MetricAggregates.P99));
  }

}
