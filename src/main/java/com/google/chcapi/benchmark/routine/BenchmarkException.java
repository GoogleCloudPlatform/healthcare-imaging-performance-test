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

package com.google.chcapi.benchmark.routine;

/**
 * Thrown to indicate that an error has occurred at benchmark execution step.
 * 
 * @author Mikhail Ukhlin
 * @see Benchmark
 */
public class BenchmarkException extends RuntimeException {
  private static final long serialVersionUID = 2135107338989204856L;
  
  /**
   * Constructs a new {@code BenchmarkException} with the specified detail message.
   * 
   * @param message Detail message.
   */
  public BenchmarkException(String message) {
    super(message);
  }
  
  /**
   * Constructs a new {@code BenchmarkException} with the specified detail message and cause.
   * Detail message will be set as concatenation of the specified detail message and message of
   * the exception cause.
   * 
   * @param message Detail message.
   * @param cause Exception cause.
   */
  public BenchmarkException(String message, Throwable cause) {
    super(message + (cause.getMessage() != null ? ": " + cause.getMessage() : ""), cause);
  }
  
  /**
   * Constructs a new {@code BenchmarkException} with the specified cause. Detail message will be
   * set to message of the exception cause.
   * 
   * @param message Detail message.
   * @param cause Exception cause.
   */
  public BenchmarkException(Throwable cause) {
    super(cause.getMessage(), cause);
  }
  
}
