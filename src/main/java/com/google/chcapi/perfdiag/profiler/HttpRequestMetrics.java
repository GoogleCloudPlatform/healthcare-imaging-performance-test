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

  /** Time in milliseconds when request has been sent. */
  private final long startTime;

  /** Time in milliseconds when response has been received. */
  private final long responseTime;

  /** Time in milliseconds when response content has been retrieved. */
  private final long endTime;

  /** Number of bytes read. */
  private final long bytesRead;

  /** Response cache status. */
  private final CacheStatus cacheStatus;

  /**
   * Constructs a new HTTP request metrics with the specified start time, response time, end time
   * and number of bytes read.
   *
   * @param startTime Time in milliseconds when request has been sent.
   * @param responseTime Time in milliseconds when response has been received.
   * @param endTime Time in milliseconds when response content has been retrieved.
   * @param bytesRead Number of bytes read.
   * @param cacheStatus Response cache status.
   */
  public HttpRequestMetrics(long startTime, long responseTime, long endTime, long bytesRead,
      CacheStatus cacheStatus) {
    this.startTime = startTime;
    this.responseTime = responseTime;
    this.endTime = endTime;
    this.bytesRead = bytesRead;
    this.cacheStatus = cacheStatus;
  }

  /**
   * Returns time in milliseconds when request has been sent.
   *
   * @return Time in milliseconds when request has been sent.
   */
  public long getStartTime() {
    return startTime;
  }

  /**
   * Returns time in milliseconds when response has been received.
   *
   * @return Time in milliseconds when response has been received.
   */
  public long getResponseTime() {
    return responseTime;
  }

  /**
   * Returns time in milliseconds when response content has been retrieved.
   *
   * @return Time in milliseconds when response content has been retrieved.
   */
  public long getEndTime() {
    return endTime;
  }

  /**
   * Returns latency of first byte received in milliseconds.
   *
   * @return Latency of first byte received in milliseconds.
   */
  public long getResponseLatency() {
    return responseTime - startTime;
  }

  /**
   * Returns latency of all bytes received in milliseconds.
   *
   * @return Latency of all bytes received in milliseconds.
   */
  public long getReadLatency() {
    return endTime - responseTime;
  }

  /**
   * Returns total request latency in milliseconds.
   *
   * @return Total request latency in milliseconds.
   */
  public long getTotalLatency() {
    return endTime - startTime;
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
   * Returns response cache status.
   * 
   * @return Response cache status.
   */
  public CacheStatus getCacheStatus() {
    return cacheStatus;
  }
  
  /**
   * Returns bytes read per second.
   *
   * @return Bytes read per second.
   */
  public double getTransferRate() {
    return (double) getBytesRead() / (double) getTotalLatency() * 1000.0;
  }
  
}
