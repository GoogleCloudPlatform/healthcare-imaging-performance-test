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

import java.io.PrintStream;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command
public class RetrieveStudyBenchmark extends Benchmark {
  
  @Option(
      names = "--study-id",
      descriptionKey = "benchmark.retrieve-study.study-id.description",
      required = true
  )
  private String studyId;
  
  @Override
  protected void doRunBenchmark(int iteration) throws Exception {
    
  }
  
  @Override
  protected void doPrintResults(PrintStream output) {
    output.println("Retrieve study results: TEST");
  }
  
}
