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

import java.io.InputStream;
import java.io.IOException;

import java.net.HttpURLConnection;

/**
 * Wrapper around {@code HttpURLConnection} that allows to make HTTP request, read response and
 * store metrics (response latency, read latency, bytes read and transfer rate).
 * 
 * @author Mikhail Ukhlin
 * @see HttpRequestProfilerFactory
 */
public class HttpRequestProfiler {
  
  /**
   * HTTP connection prepared for request to Google Cloud Healthcare API.
   */
  private final HttpURLConnection connection;
  
  /**
   * Latency of first byte received in milliseconds.
   */
  private long responseLatency;
  
  /**
   * Latency of all bytes received in milliseconds.
   */
  private long readLatency;
  
  /**
   * Bytes read.
   */
  private long bytesRead;
  
  /**
   * Constructs a new {@code HttpRequestProfiler} with the specified HTTP connection.
   * 
   * @param connection The {@code HttpURLConnection} instance prepared for request.
   */
  public HttpRequestProfiler(HttpURLConnection connection) {
    this.connection = connection;
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
   * Returns bytes read.
   * 
   * @return Bytes read.
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
   * Profiles HTTP request and stores results.
   * 
   * @return Content of HTTP response as array of bytes.
   * @throws IOException if an IO error occurred or request failed.
   */
  public byte[] execute() throws IOException {
    // Send request and measure latency
    final long startTime = System.currentTimeMillis();
    connection.getInputStream();
    final long responseTime = System.currentTimeMillis();
    
    // Check status code
    int status = connection.getResponseCode();
    if (status < HttpURLConnection.HTTP_OK || status >= HttpURLConnection.HTTP_MULT_CHOICE) {
      final String message = connection.getResponseMessage();
      throw new IOException(message != null ? status + " " + message : Integer.toString(status));
    }
    
    // Read content if any
    byte[] content = status == HttpURLConnection.HTTP_NO_CONTENT ? EMPTY_CONTENT : readContent();
    responseLatency = responseTime - startTime;
    return content;
  }
  
  /**
   * Reads content of HTTP response and assigns {@link #readLatency} and {@link #bytesRead} fields.
   * This method takes into account {@code Content-Length} HTTP header for optimal performance.
   * 
   * @return Content of HTTP response as array of bytes.
   * @throws IOException if an IO error occurred.
   */
  private byte[] readContent() throws IOException {
    final int length = connection.getContentLength();
    if (length == 0) {
      return EMPTY_CONTENT;
    } else {
      int offset = 0;
      final int blockSize = Math.max(length, DEFAULT_BLOCK_SIZE);
      byte[] content = new byte[blockSize];
      final long timestamp = System.currentTimeMillis();
      try (InputStream input = connection.getInputStream()) {
        for (int count; (count = input.read(content, offset, blockSize)) >= 0; offset += count) {
          if (offset + count + blockSize > content.length) {
            content = Arrays.copyOf(content, content.length * 3 / 2 + 1);
          }
        }
      }
      readLatency = System.currentTimeMillis() - timestamp;
      return (bytesRead = offset) < content.length ? Arrays.copyOf(content, offset) : content;
    }
  }
  
  /**
   * Return CSV string concatenated using request metrics in the following format:
   * {@code ITERATION, RESPONSE LATENCY, READ LATENCY, BYTES READ, TRANSFER RATE}.
   * 
   * @param iteration The benchmark iteration number.
   * @return CSV string concatenated using request metrics.
   */
  public String toCSVString(int iteration) {
    return iteration + ", " + getResponseLatency() + ", " + getReadLatency() + ", "
        + getBytesRead() + ", " + getTransferRate();
  }
  
  /**
   * Returns string representation of this profiling request.
   * 
   * @return String representation of this profiling request.
   */
  @Override
  public String toString() {
    return connection.getURL().toString();
  }



  /* Constant for empty response content */
  private static final byte[] EMPTY_CONTENT = new byte[0];
  
  /* Default size of blocks used to read response content */
  private static final int DEFAULT_BLOCK_SIZE = 8192;
  
}
