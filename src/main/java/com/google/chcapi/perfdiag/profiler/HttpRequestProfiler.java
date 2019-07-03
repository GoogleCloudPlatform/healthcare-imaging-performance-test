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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.CloseableHttpClient;


/**
 * HTTP request wrapper that allows to execute request, read response and calculate metrics
 * (response latency, read latency and number of bytes read).
 * 
 * @author Mikhail Ukhlin
 * @see HttpRequestProfilerFactory
 */
public class HttpRequestProfiler {
  
  /* HTTP client instance */
  private static final CloseableHttpClient HTTP_CLIENT = HttpClients.createDefault();
  
  /**
   * Prepared HTTP request.
   */
  private final HttpUriRequest request;
  
  /**
   * Constructs a new {@code HttpRequestProfiler} with the specified HTTP request.
   * 
   * @param request Prepared HTTP request instance.
   */
  public HttpRequestProfiler(HttpUriRequest request) {
    this.request = request;
  }
  
  /**
   * Executes HTTP request and returns request metrics. GCP authorization is done if the user is
   * not already signed in. The access token is refreshed if it has expired (HTTP 401 is returned
   * from the server) and the request is retried with the new access token.
   * 
   * @param stream Stream to write response content.
   * @return Metrics of the HTTP request.
   * @throws IOException if an IO error occurred or request failed.
   */
  public HttpRequestMetrics execute(OutputStream stream) throws IOException {
    try {
      return doExecute(stream);
    } catch (HttpResponseException e) {
      // Token expired?
      if (e.getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
        // Refresh token and try again
        HttpRequestProfilerFactory.refreshToken();
        return doExecute(stream);
      }
      // Rethrow exception
      throw e;
    }
  }
  
  /**
   * Executes HTTP request and returns request metrics.
   * 
   * @param stream Stream to write response content.
   * @return Metrics of the HTTP request.
   * @throws IOException if an IO error occurred or request failed.
   */
  private HttpRequestMetrics doExecute(OutputStream stream) throws IOException {
    // Execute request and measure metrics
    long timestamp = System.currentTimeMillis();
    try (CloseableHttpResponse response = HTTP_CLIENT.execute(request)) {
      final long responseLatency = System.currentTimeMillis() - timestamp;
      
      // Check status code
      final int status = response.getStatusLine().getStatusCode();
      if (status < HttpStatus.SC_OK || status >= HttpStatus.SC_MULTIPLE_CHOICES) {
        // Request failed
        throw new HttpResponseException(status, response.getStatusLine().getReasonPhrase());
      }
      
      // Does content exist?
      if (status == HttpStatus.SC_NO_CONTENT) {
        // No content
        return new HttpRequestMetrics(responseLatency, 0L, 0L);
      }
      
      // Read content
      timestamp = System.currentTimeMillis();
      try (InputStream input = response.getEntity().getContent()) {
        final long bytesRead = IOUtils.copyLarge(input, stream);
        final long readLatency = System.currentTimeMillis() - timestamp;
        return new HttpRequestMetrics(responseLatency, readLatency, bytesRead);
      }
    }
  }
  
  /**
   * Returns string representation of this profiling request.
   * 
   * @return String representation of this profiling request.
   */
  @Override
  public String toString() {
    return request.getURI().toString();
  }
  
}
