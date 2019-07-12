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

package com.google.chcapi.perfdiag.benchmark.stats;

import java.util.Arrays;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Max;
import org.apache.commons.math3.stat.descriptive.rank.Min;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

/**
 * Accumulates latencies from iterations and allows to calculate statistics.
 * 
 * @author Mikhail Ukhlin
 */
public class LatencyAggregates {

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
   * Buffer to store latencies from iterations for further statistics calculations.
   */
  private final double[] latencies;
  
  /**
   * Current number of completed iterations.
   */
  private int count;
  
  /**
   * The mean cached instance.
   */
  private Double mean;
  
  /**
   * The {@code Percentile} cached instance.
   */
  private Percentile percentile;
  
  /**
   * Constructs a new {@code LatencyAggregates} with the specified number of iterations.
   * 
   * @param iterations Number of iterations.
   */
  public LatencyAggregates(int iterations) {
    this.latencies = new double[iterations];
  }
  
  /**
   * Adds latency from completed iteration.
   * 
   * @param latency The latency to add.
   */
  public void addLatency(long latency) {
    latencies[count++] = latency;
  }
  
  /**
   * Returns minimum latency.
   * 
   * @return Minimum latency.
   */
  public double getMin() {
    return new Min().evaluate(latencies, 0, count);
  }
  
  /**
   * Returns maximum latency.
   * 
   * @return Maximum latency.
   */
  public double getMax() {
    return new Max().evaluate(latencies, 0, count);
  }
  
  /**
   * Returns mean latency.
   * 
   * @return Mean latency.
   */
  public double getMean() {
    if (mean == null) {
      mean = new Mean().evaluate(latencies, 0, count);
    }
    return mean;
  }
  
  /**
   * Returns standard deviation.
   * 
   * @return Standard deviation.
   */
  public double getStddev() {
    return new StandardDeviation().evaluate(latencies, getMean(), 0, count);
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
      Arrays.sort(latencies, 0, count);
      percentile.setData(latencies, 0, count);
    }
    return percentile.evaluate(p);
  }
  
}
