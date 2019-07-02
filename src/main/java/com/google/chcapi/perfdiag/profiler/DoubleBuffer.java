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

import org.apache.commons.math3.stat.descriptive.rank.Percentile;

/**
 * Buffer to store double values. It is required to calculate percentiles.
 * 
 * @author Mikhail Ukhlin
 */
public class DoubleBuffer {
  
  /**
   * The buffer of double values.
   */
  private double[] values;
  
  /**
   * Current number of values in the buffer.
   */
  private int length;
  
  /**
   * The {@code Percentile} cached instance.
   */
  private Percentile percentile;
  
  /**
   * Constructs a new double buffer with default capacity of 128.
   */
  public DoubleBuffer() {
    this(128);
  }
  
  /**
   * Constructs a new double buffer with the specified capacity.
   * 
   * @param capacity Initial capacity.
   */
  public DoubleBuffer(int capacity) {
    this.values = new double[capacity];
  }
  
  /**
   * Adds a new value to this double buffer.
   * 
   * @param value The value to add.
   */
  public void add(double value) {
    if (values.length == length) {
      values = Arrays.copyOf(values, length * 3 / 2 + 1);
    }
    values[length++] = value;
    percentile = null;
  }
  
  /**
   * Returns median for this buffer.
   * 
   * @return Median for this buffer.
   */
  public double getMedian() {
    return evaluate(50.0);
  }
  
  /**
   * Returns percentile for this buffer.
   * 
   * @return Percentile for this buffer.
   */
  public double getPercentile() {
    return evaluate(100.0);
  }
  
  /**
   * Evaluates the specified percentile for this buffer.
   * 
   * @param p The percentile to evaluate.
   * @return Evaluated percentile.
   */
  private double evaluate(double p) {
    if (percentile == null) {
      percentile = new Percentile();
      Arrays.sort(values, 0, length);
      percentile.setData(values, 0, length);
    }
    return percentile.evaluate(p);
  }
  
}
