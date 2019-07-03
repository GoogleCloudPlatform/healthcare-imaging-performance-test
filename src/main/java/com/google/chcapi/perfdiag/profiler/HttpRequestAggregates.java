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

package com.google.chcapi.perfdiag.profiler;

/**
 * Aggregated metrics of multiple HTTP requests.
 * 
 * @author Mikhail Ukhlin
 * @see HttpRequestProfiler
 * @see HttpRequestMetrics
 */
public class HttpRequestAggregates {
  
  /**
   * Number of measured requests.
   */
  private int requestCount;
  
  /**
   * Total latency of first byte received in milliseconds.
   */
  private long totalResponseLatency;
  
  /**
   * Buffer to store response latencies for further percentiles evaluation.
   */
  private final DoubleBuffer responseLatencyBuffer;
  
  /**
   * Total latency of all bytes received in milliseconds.
   */
  private long totalReadLatency;
  
  /**
   * Buffer to store read latencies for further percentiles evaluation.
   */
  private final DoubleBuffer readLatencyBuffer;
  
  /**
   * Total bytes read.
   */
  private long totalBytesRead;
  
  /**
   * Constructs a new {@code HttpRequestAggregates} with the specified expected request count.
   * 
   * @param expectedRequestCount The expected request count.
   */
  public HttpRequestAggregates(int expectedRequestCount) {
    this.responseLatencyBuffer = new DoubleBuffer(expectedRequestCount);
    this.readLatencyBuffer = new DoubleBuffer(expectedRequestCount);
  }
  
  /**
   * Returns number of measured requests.
   * 
   * @return Number of measured requests.
   */
  public int getRequestCount() {
    return requestCount;
  }
  
  /**
   * Returns total latency of first byte received in milliseconds.
   * 
   * @return Total latency of first byte received in milliseconds.
   */
  public long getTotalResponseLatency() {
    return totalResponseLatency;
  }
  
  /**
   * Returns average latency of first byte received in milliseconds.
   * 
   * @return Average latency of first byte received in milliseconds.
   */
  public long getAverageResponseLatency() {
    return requestCount > 0 ? totalResponseLatency / requestCount : 0L;
  }
  
  /**
   * Returns median latency of first byte received in milliseconds.
   * 
   * @return Median latency of first byte received in milliseconds.
   */
  public double getMedianResponseLatency() {
    return responseLatencyBuffer.getMedian();
  }
  
  /**
   * Returns percentile latency of first byte received in milliseconds.
   * 
   * @return Percentile latency of first byte received in milliseconds.
   */
  public double getPercentileResponseLatency() {
    return responseLatencyBuffer.getPercentile();
  }
  
  /**
   * Returns total latency of all bytes received in milliseconds.
   * 
   * @return Total latency of all bytes received in milliseconds.
   */
  public long getTotalReadLatency() {
    return totalReadLatency;
  }
  
  /**
   * Returns average latency of all bytes received in milliseconds.
   * 
   * @return Average latency of all bytes received in milliseconds.
   */
  public long getAverageReadLatency() {
    return requestCount > 0 ? totalReadLatency / requestCount : 0L;
  }
  
  /**
   * Returns median latency of all bytes received in milliseconds.
   * 
   * @return Median latency of all bytes received in milliseconds.
   */
  public double getMedianReadLatency() {
    return readLatencyBuffer.getMedian();
  }
  
  /**
   * Returns percentile latency of all bytes received in milliseconds.
   * 
   * @return Percentile latency of all bytes received in milliseconds.
   */
  public double getPercentileReadLatency() {
    return readLatencyBuffer.getPercentile();
  }
  
  /**
   * Returns total latency in milliseconds.
   * 
   * @return Total latency in milliseconds.
   */
  public long getTotalLatency() {
    return totalResponseLatency + totalReadLatency;
  }
  
  /**
   * Returns average latency in milliseconds.
   * 
   * @return Average latency in milliseconds.
   */
  public long getAverageLatency() {
    return requestCount > 0 ? getTotalLatency() / requestCount : 0L;
  }
  
  /**
   * Returns total bytes read.
   * 
   * @return Total bytes read.
   */
  public long getTotalBytesRead() {
    return totalBytesRead;
  }
  
  /**
   * Returns total bytes read per second.
   * 
   * @return Total bytes read per second.
   */
  public double getTotalTransferRate() {
    return totalReadLatency > 0L
        ? (double) totalBytesRead / (double) getTotalLatency() * 1000.0
        : 0.0;
  }
  
  /**
   * Adds the specified HTTP request metrics.
   * 
   * @param metrics HTTP request metrics to add.
   */
  public void addMetrics(HttpRequestMetrics metrics) {
    totalResponseLatency += metrics.getResponseLatency();
    responseLatencyBuffer.add(metrics.getResponseLatency());
    totalReadLatency += metrics.getReadLatency();
    readLatencyBuffer.add(metrics.getReadLatency());
    totalBytesRead += metrics.getBytesRead();
    requestCount++;
  }
  
}
