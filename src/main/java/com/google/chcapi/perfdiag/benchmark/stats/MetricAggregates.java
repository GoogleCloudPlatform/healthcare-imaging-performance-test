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
 * Accumulates metric values from iterations and allows to calculate statistics.
 * 
 * @author Mikhail Ukhlin
 */
public class MetricAggregates {

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
   * Buffer to store metric values from iterations for further statistics calculations.
   */
  private final double[] values;
  
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
   * Constructs a new {@code MetricAggregates} with the specified number of iterations.
   * 
   * @param iterations Number of iterations.
   */
  public MetricAggregates(int iterations) {
    this.values = new double[iterations];
  }
  
  /**
   * Adds metric value from completed iteration.
   * 
   * @param value The value to add.
   */
  public void addValue(double value) {
    values[count++] = value;
  }
  
  /**
   * Returns minimum value.
   * 
   * @return Minimum value.
   */
  public double getMin() {
    return new Min().evaluate(values, 0, count);
  }
  
  /**
   * Returns maximum value.
   * 
   * @return Maximum value.
   */
  public double getMax() {
    return new Max().evaluate(values, 0, count);
  }
  
  /**
   * Returns mean value.
   * 
   * @return Mean value.
   */
  public double getMean() {
    if (mean == null) {
      mean = new Mean().evaluate(values, 0, count);
    }
    return mean;
  }
  
  /**
   * Returns standard deviation.
   * 
   * @return Standard deviation.
   */
  public double getStddev() {
    return new StandardDeviation().evaluate(values, getMean(), 0, count);
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
      Arrays.sort(values, 0, count);
      percentile.setData(values, 0, count);
    }
    return percentile.evaluate(p);
  }
  
}
