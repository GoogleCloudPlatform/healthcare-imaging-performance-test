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

import org.apache.http.Header;
import org.apache.http.HttpResponse;

/**
 * Enumerates cache statuses from {@code X-Cache-Status} or {@code X-Cache} response headers.
 * 
 * @author Mikhail Ukhlin
 */
public enum CacheStatus {
  
  /**
   * Cache status is unknown.
   */
  NA,
  
  /**
   * The response contains valid, fresh content direct from the cache.
   */
  HIT {
    @Override public int incrementHits(int hits) {
      return hits + 1;
    }
  },
  
  /**
   * The response was not found in the cache and so was fetched from an origin server.
   */
  MISS {
    @Override public int incrementMisses(int misses) {
      return misses + 1;
    }
  };
  
  /**
   * Increments the specified number of cache hits if applicable.
   * 
   * @param hits Current number of cache hits.
   * @return Updated number of cache hits.
   */
  public int incrementHits(int hits) {
    return hits;
  }
  
  /**
   * Increments the specified number of cache misses if applicable.
   * 
   * @param misses Current number of cache misses.
   * @return Updated number of cache misses.
   */
  public int incrementMisses(int misses) {
    return misses;
  }
  
  /**
   * Returns cache status obtained from {@code X-Cache-Status} or {@code X-Cache} headers of the
   * specified HTTP response.
   * 
   * @param response HTTP response.
   * @return Cache status obtained from {@code X-Cache-Status} or {@code X-Cache} headers of the
   *         specified HTTP response.
   */
  public static CacheStatus fromResponse(HttpResponse response) {
    Header header;
    // Try X-Cache-Status
    header = response.getFirstHeader("X-Cache-Status");
    if (header != null) {
      String value = header.getValue();
      if (value != null) {
        value = value.toUpperCase();
        if (value.equals(HIT.name())) {
          return HIT;
        } else if (value.equals(MISS.name())) {
          return MISS;
        }
      }
    }
    // Try X-Cache
    header = response.getFirstHeader("X-Cache");
    if (header != null) {
      String value = header.getValue();
      if (value != null) {
        value = value.toUpperCase();
        if (value.contains(HIT.name())) {
          return HIT;
        } else if (value.contains(MISS.name())) {
          return MISS;
        }
      }
    }
    // Unknown cache status
    return NA;
  }
  
}
