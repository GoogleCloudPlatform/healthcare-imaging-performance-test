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
  private static final long serialVersionUID = 3569311908883665301L;
  
  /**
   * Constructs a new {@code BenchmarkException} with the specified detail message.
   * 
   * @param message Detail message.
   */
  public BenchmarkException(String message) {
    super(message);
  }
  
  // Factory methods
  
  /**
   * Creates {@code BenchmarkException} instance with invalid iterations error detail message.
   * 
   * @param iterations Invalid number of iterations provided from command line.
   * @return {@code BenchmarkException} instance with invalid iterations error detail message.
   */
  public static BenchmarkException iterationsInvalid(int iterations) {
    return new BenchmarkException(BenchmarkMessages.format("error.iterationsInvalid", iterations));
  }
  
  /**
   * Creates {@code BenchmarkException} instance with invalid threads error detail message.
   * 
   * @param threads Invalid number of threads provided from command line.
   * @return {@code BenchmarkException} instance with invalid threads error detail message.
   */
  public static BenchmarkException threadsInvalid(int threads) {
    return new BenchmarkException(BenchmarkMessages.format("error.threadsInvalid", threads));
  }
  
  /**
   * Creates {@code BenchmarkException} instance with IO error detail message.
   * 
   * @param cause Exception cause.
   * @return {@code BenchmarkException} instance with IO error detail message.
   */
  public static BenchmarkException ioError(Exception cause) {
    return new BenchmarkException(BenchmarkMessages.format("error.ioError", cause.getMessage()));
  }
  
  /**
   * Creates {@code BenchmarkException} instance with refresh token error detail message.
   * 
   * @return {@code BenchmarkException} instance with refresh token error detail message.
   */
  public static BenchmarkException refreshTokenFailed() {
    return new BenchmarkException(BenchmarkMessages.format("error.refreshTokenFailed"));
  }
  
  /**
   * Creates {@code BenchmarkException} instance with authorization error detail message.
   * 
   * @param cause Exception cause.
   * @return {@code BenchmarkException} instance with authorization error detail message.
   */
  public static BenchmarkException authorizationFailed(Exception cause) {
    return new BenchmarkException(BenchmarkMessages.format("error.authorizationFailed",
        cause.getMessage()));
  }
  
  /**
   * Creates {@code BenchmarkException} instance with iteration error detail message.
   * 
   * @param iteration Number of failed iteration.
   * @param cause Exception cause.
   * @return {@code BenchmarkException} instance with iteration error detail message.
   */
  public static BenchmarkException iterationFailed(int iteration, Exception cause) {
    return new BenchmarkException(BenchmarkMessages.format("error.iterationFailed",
        iteration, cause.getMessage()));
  }
  
}
