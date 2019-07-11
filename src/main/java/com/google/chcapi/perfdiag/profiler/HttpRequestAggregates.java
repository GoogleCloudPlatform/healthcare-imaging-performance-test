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

import java.util.Arrays;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

/**
 * Aggregated metrics of multiple HTTP requests.
 * 
 * @author Mikhail Ukhlin
 * @see HttpRequestProfiler
 * @see HttpRequestMetrics
 */
public class HttpRequestAggregates {
  
  /**
   * Median percentile.
   */
  public static final double MEDIAN = 50.0;
  
  /**
   * 1st percentile.
   */
  public static final double P1 = 1.0;
  
  /**
   * 2nd percentile.
   */
  public static final double P2 = 2.0;
  
  /**
   * 5th percentile.
   */
  public static final double P5 = 5.0;
  
  /**
   * 10th percentile.
   */
  public static final double P10 = 10.0;
  
  /**
   * 90th percentile.
   */
  public static final double P90 = 90.0;
  
  /**
   * 95th percentile.
   */
  public static final double P95 = 95.0;
  
  /**
   * 98th percentile.
   */
  public static final double P98 = 98.0;
  
  /**
   * 99th percentile.
   */
  public static final double P99 = 99.0;
  
  /**
   * Number of measured requests.
   */
  private int requestCount;
  
  /**
   * Total latency in milliseconds.
   */
  private long totalLatency;
  
  /**
   * Minimum latency among all requests in milliseconds.
   */
  private long minLatency;
  
  /**
   * Maximum latency among all requests in milliseconds.
   */
  private long maxLatency;
  
  /**
   * Total bytes read.
   */
  private long totalBytesRead;
  
  /**
   * Buffer to store request latencies for further percentiles evaluation.
   */
  private double[] requestLatencies;
  
  /**
   * The mean cached instance.
   */
  private Double mean;
  
  /**
   * The {@code Percentile} cached instance.
   */
  private Percentile percentile;
  
  /**
   * Constructs a new {@code HttpRequestAggregates} with default expected request count and zero
   * total latency.
   */
  public HttpRequestAggregates() {
    this(128, 0L);
  }
  
  /**
   * Constructs a new {@code HttpRequestAggregates} with the specified expected request count and
   * total latency.
   * 
   * @param expectedRequestCount The expected request count.
   * @param totalLatency Total latency in milliseconds.
   */
  public HttpRequestAggregates(int expectedRequestCount, long totalLatency) {
    this.requestLatencies = new double[expectedRequestCount];
    this.totalLatency = totalLatency;
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
   * Returns total latency in milliseconds.
   * 
   * @return Total latency in milliseconds.
   */
  public long getTotalLatency() {
    return totalLatency;
  }
  
  /**
   * Returns minimum latency among all requests in milliseconds.
   * 
   * @return Minimum latency among all requests in milliseconds.
   */
  public long getMinLatency() {
    return minLatency;
  }
  
  /**
   * Returns maximum latency among all requests in milliseconds.
   * 
   * @return Maximum latency among all requests in milliseconds.
   */
  public long getMaxLatency() {
    return maxLatency;
  }
  
  /**
   * Returns mean of all requests.
   * 
   * @return Mean of all requests.
   */
  public double getMean() {
    if (requestCount > 0) {
      if (mean == null) {
        mean = new Mean().evaluate(requestLatencies, 0, requestCount);
      }
      return mean;
    }
    return 0.0;
  }
  
  /**
   * Returns standard deviation of all requests.
   * 
   * @return Standard deviation of all requests.
   */
  public double getStddev() {
    return new StandardDeviation().evaluate(requestLatencies, getMean(), 0, requestCount);
  }
  
  /**
   * Returns the specified percentile.
   * 
   * @param p The percentile to evaluate.
   * @return Evaluated percentile.
   */
  public double getPercentile(double p) {
    if (percentile == null) {
      percentile = new Percentile();
      Arrays.sort(requestLatencies, 0, requestCount);
      percentile.setData(requestLatencies, 0, requestCount);
    }
    return percentile.evaluate(p);
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
    return totalLatency > 0L ? (double) totalBytesRead / (double) totalLatency * 1000.0 : 0.0;
  }
  
  /**
   * Adds the specified HTTP request metrics to this aggregates.
   * 
   * @param metrics HTTP request metrics to add.
   */
  public void addMetrics(HttpRequestMetrics metrics) {
    totalBytesRead += metrics.getBytesRead();
    
    final long latency = metrics.getTotalLatency();
    if (minLatency == 0L || minLatency > latency) {
      minLatency = latency;
    }
    if (maxLatency == 0L || maxLatency < latency) {
      maxLatency = latency;
    }
    
    if (requestLatencies.length == requestCount) {
      requestLatencies = Arrays.copyOf(requestLatencies, requestCount * 3 / 2 + 1);
    }
    requestLatencies[requestCount++] = latency;
    mean = null;
    percentile = null;
  }
  
  /**
   * Adds the specified aggregated metrics of multiple HTTP requests to this aggregates.
   * 
   * @param aggregates Aggregated metrics of multiple HTTP requests to add.
   */
  public void addAggregates(HttpRequestAggregates aggregates) {
    totalBytesRead += aggregates.totalBytesRead;
    totalLatency += aggregates.totalLatency;
    
    if (minLatency == 0L || minLatency > aggregates.minLatency) {
      minLatency = aggregates.minLatency;
    }
    if (maxLatency == 0L || maxLatency < aggregates.maxLatency) {
      maxLatency = aggregates.maxLatency;
    }
    
    final int newRequestCount = requestCount + aggregates.requestCount;
    if (requestLatencies.length < newRequestCount) {
      requestLatencies = Arrays.copyOf(requestLatencies, newRequestCount * 3 / 2 + 1);
    }
    System.arraycopy(aggregates.requestLatencies, 0, requestLatencies, requestCount,
        aggregates.requestCount);
    requestCount = newRequestCount;
    mean = null;
    percentile = null;
  }
  
}
