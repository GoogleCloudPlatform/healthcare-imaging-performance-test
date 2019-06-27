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

package com.google.chcapi.perfdiag.benchmark;

/**
 * Thrown to indicate that an error has occurred at benchmark execution step.
 * 
 * @author Mikhail Ukhlin
 * @see Benchmark
 */
public class BenchmarkException extends RuntimeException {
  private static final long serialVersionUID = -5376779383961136057L;
  
  /**
   * Constructs a new {@code BenchmarkException} with the specified detail message.
   * 
   * @param message Detail message.
   */
  public BenchmarkException(String message) {
    super(message);
  }
  
  // Factory methods
  
  public static BenchmarkException ioError(Exception cause) {
    return new BenchmarkException(BenchmarkMessages.format("error.ioError", cause.getMessage()));
  }
  
  public static BenchmarkException authorizationFailed(Exception cause) {
    return new BenchmarkException(BenchmarkMessages.format("error.authorizationFailed",
        cause.getMessage()));
  }
  
  public static BenchmarkException iterationFailed(int iteration, Exception cause) {
    return new BenchmarkException(BenchmarkMessages.format("error.iterationFailed",
        iteration, cause.getMessage()));
  }
  
}
