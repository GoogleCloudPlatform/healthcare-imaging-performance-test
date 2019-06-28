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
import com.google.chcapi.perfdiag.profiler.HttpRequestProfiler;
import com.google.chcapi.perfdiag.profiler.HttpRequestStatistics;

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
   * Prints a formatted string using the specified message key and arguments to stdout.
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
   * Prints message about benchmark iteration start to stdout.
   * 
   * @param iteration The iteration number.
   */
  public static void printIterationStarted(int iteration) {
    print("message.iterationStarted", iteration);
  }
  
  /**
   * Prints message about benchmark completion to stdout.
   */
  public static void printBenchmarkCompleted() {
    print("message.benchmarkCompleted");
  }
  
  /**
   * Prints message about number of found studies to stdout.
   * 
   * @param count Number of found studies.
   */
  public static void printStudiesFound(int count) {
    print("message.stadiesFound", count);
  }
  
  /**
   * Prints message about number of found study instances to stdout.
   * 
   * @param count Number of found study instances.
   */
  public static void printInstancesFound(int count) {
    print("message.instancesFound", count);
  }
  
  /**
   * Prints request executed message to stdout.
   * 
   * @param length Bytes read.
   */
  public static void printRequestExecuted(HttpRequestProfiler request, int length) {
    print("message.requestExecuted", length, request);
  }
  
  /**
   * Prints statistics of the specified profilig request to stdout.
   * 
   * @param request Profiling request.
   */
  public static void printStatistics(HttpRequestProfiler request) {
    print("message.requestStatistics", 
        request.getResponseLatency(), request.getReadLatency(),
        request.getBytesRead(), request.getTransferRate());
  }
  
  /**
   * Prints the specified aggregated request statistics to stdout.
   * 
   * @param statistics The aggregated request statistics.
   */
  public static void printStatistics(HttpRequestStatistics statistics) {
    print("message.aggregatedStatistics", statistics.getRequestCount(),
        statistics.getTotalResponseLatency(), statistics.getAverageResponseLatency(),
        statistics.getTotalReadLatency(), statistics.getAverageReadLatency(),
        statistics.getTotalBytesRead(), statistics.getTotalTransferRate());
  }
  
  /**
   * Prints error message about failed profiling request to stderr.
   * 
   * @param cause Exception cause.
   */
  public static void printRequestFailed(Exception cause) {
    printError("error.requestFailed", cause.getMessage());
  }
  
}
