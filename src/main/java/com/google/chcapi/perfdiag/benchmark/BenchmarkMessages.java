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

import com.google.chcapi.perfdiag.profiler.HttpRequestAggregates;
import com.google.chcapi.perfdiag.profiler.HttpRequestMetrics;

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
   * Prints retrieve study benchmark summary information to stdout.
   * 
   * @param queryInstancesMetrics Metrics of querying instances.
   * @param firstInstanceMetrics Metrics of first instance retrieval.
   * @param summaryMetrics Summary metrics.
   */
  public static void printRetrieveStudySummary(HttpRequestMetrics queryInstancesMetrics,
      HttpRequestMetrics firstInstanceMetrics,
      HttpRequestAggregates summaryMetrics) {
    print("message.retrieveStudySummary",
        summaryMetrics.getRequestCount(),
        summaryMetrics.getTotalLatency() + queryInstancesMetrics.getTotalLatency(),
        queryInstancesMetrics.getTotalLatency(),
        firstInstanceMetrics.getResponseLatency(),
        firstInstanceMetrics.getReadLatency(),
        summaryMetrics.getTotalLatency(),
        summaryMetrics.getTotalBytesRead(),
        summaryMetrics.getTotalTransferRate(),
        summaryMetrics.getAverageLatency(),
        summaryMetrics.getMedianReadLatency(),
        summaryMetrics.getPercentileReadLatency());
  }
  
  /**
   * Prints download dataset benchmark summary information to stdout.
   * 
   * @param queryStudiesMetrics Metrics of querying studies.
   * @param firstStudyMetrics Metrics of first study retrieval.
   * @param summaryMetrics Summary metrics.
   */
  public static void printDownloadDatasetSummary(HttpRequestMetrics queryStudiesMetrics,
      HttpRequestMetrics firstStudyMetrics,
      HttpRequestAggregates summaryMetrics) {
    print("message.downloadDatasetSummary",
        summaryMetrics.getRequestCount(),
        summaryMetrics.getTotalLatency() + queryStudiesMetrics.getTotalLatency(),
        queryStudiesMetrics.getTotalLatency(),
        firstStudyMetrics.getResponseLatency(),
        summaryMetrics.getTotalLatency(),
        summaryMetrics.getTotalBytesRead(),
        summaryMetrics.getTotalTransferRate(),
        summaryMetrics.getAverageLatency(),
        summaryMetrics.getMedianReadLatency(),
        summaryMetrics.getPercentileReadLatency());
  }
  
}
