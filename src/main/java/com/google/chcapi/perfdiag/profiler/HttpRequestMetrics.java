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
 * Metrics of HTTP request.
 * 
 * @author Mikhail Ukhlin
 */
public class HttpRequestMetrics {
  
  /**
   * Latency of first byte received in milliseconds.
   */
  private long responseLatency;
  
  /**
   * Latency of all bytes received in milliseconds.
   */
  private long readLatency;
  
  /**
   * Number of bytes read.
   */
  private long bytesRead;
  
  /**
   * Constructs a new HTTP request metrics with the specified response latency, read latence and
   * number of bytes read.
   * 
   * @param responseLatency Latency of first byte received in milliseconds.
   * @param readLatency Latency of all bytes received in milliseconds.
   * @param bytesRead Number of bytes read.
   */
  public HttpRequestMetrics(long responseLatency, long readLatency, long bytesRead) {
    this.responseLatency = responseLatency;
    this.readLatency = readLatency;
    this.bytesRead = bytesRead;
  }
  
  /**
   * Returns latency of first byte received in milliseconds.
   * 
   * @return Latency of first byte received in milliseconds.
   */
  public long getResponseLatency() {
    return responseLatency;
  }
  
  /**
   * Returns latency of all bytes received in milliseconds.
   * 
   * @return Latency of all bytes received in milliseconds.
   */
  public long getReadLatency() {
    return readLatency;
  }
  
  /**
   * Returns total latency in milliseconds (response latency + read latency).
   * 
   * @return Total latency in milliseconds.
   */
  public long getTotalLatency() {
    return responseLatency + readLatency;
  }
  
  /**
   * Returns number of bytes read.
   * 
   * @return Number of bytes read.
   */
  public long getBytesRead() {
    return bytesRead;
  }
  
  /**
   * Returns bytes read per second.
   * 
   * @return Bytes read per second.
   */
  public double getTransferRate() {
    return readLatency > 0L ? (double) bytesRead / (double) readLatency * 1000.0 : 0.0;
  }
  
  /**
   * Return CSV string for this request metrics in the following format:
   * {@code ITERATION, RESPONSE_LATENCY, READ_LATENCY, TOTAL_LATENCY, BYTES_READ, TRANSFER_RATE}.
   * 
   * @param iteration The benchmark iteration number.
   * @return CSV string for this request metrics.
   */
  public String toCSVString(int iteration) {
    return iteration + ", " + getResponseLatency() + ", " + getReadLatency() + ", "
        + getTotalLatency() + ", " + getBytesRead() + ", " + getTransferRate();
  }
  
}
