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

package com.google.chcapi.perfdiag.benchmark.config;

import java.io.File;

import picocli.CommandLine.Option;

/**
 * Common benchmark configuration.
 * 
 * @author Mikhail Ukhlin
 */
public class CommonConfig {
  
  /**
   * How many times the routine is executed.
   */
  @Option(
      names = {"-i", "--iterations"},
      descriptionKey = "option.iterations.description",
      required = false
  )
  private int iterations = 1;
  
  /**
   * File to write the result to.
   */
  @Option(
      names = {"-o", "--output"},
      descriptionKey = "option.output.description",
      required = false
  )
  private File outputFile = null;
  
  /**
   * Returns number of iterations the routine is executed.
   * 
   * @return Number of iterations the routine is executed.
   */
  public int getIterations() {
    return iterations;
  }
  
  /**
   * Returns file to write the result to. If not provided, the result will be written to stdout.
   * 
   * @return File to write the result to.
   */
  public File getOutputFile() {
    return outputFile;
  }
  
}
