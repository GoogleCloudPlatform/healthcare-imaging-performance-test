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

package com.google.chcapi.benchmark;

import picocli.CommandLine;

public class BenchmarkLauncher {
  
  /**
   * Command line entry point for DICOM performance diagnostic tool.
   * 
   * @param args Command line arguments.
   */
  public static void main(String[] args) {
    System.exit(new CommandLine(new Benchmark())
        .addSubcommand(new CommandLine.HelpCommand())
        .addSubcommand(new DownloadDatasetCommand())
        .addSubcommand(new RetrieveStudyCommand())
        .execute(args));
  }
  
  /**
   * Common options for benchmarks.
   * 
   * @author Mikhail Ukhlin
   */
  static abstract class BenchmarkOptions {
    
    @CommandLine.Option(
        names = {"-i", "--iterations"},
        descriptionKey = "benchmark.iterations.description",
        required = false
    )
    protected int iterations = 1;
    
    @CommandLine.Option(
        names = {"-o", "--output"},
        descriptionKey = "benchmark.output.description",
        required = false
    )
    protected int output = 1;
    
    @CommandLine.Option(
        names = {"-s", "--dicom-store-name"},
        descriptionKey = "benchmark.dicom-store-name.description",
        required = true
    )
    protected String dicomStoreName;
    
  }
  
  // Commands
  
  @CommandLine.Command(name = "benchmark", resourceBundle = "cli-messages")
  static class Benchmark implements Runnable {
    
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec benchmarkSpec;
    
    @Override public void run() {
      benchmarkSpec.commandLine().usage(benchmarkSpec.commandLine().getOut());
    }
    
  }
  
  @CommandLine.Command(name = "download-dataset")
  static class DownloadDatasetCommand extends BenchmarkOptions implements Runnable {
    
    @Override public void run() {
      System.out.println("Downloading dataset: " + dicomStoreName);
    }
    
  }
  
  @CommandLine.Command(name = "retrieve-study")
  static class RetrieveStudyCommand extends BenchmarkOptions implements Runnable {
    
    @CommandLine.Option(
        names = "--study-id",
        descriptionKey = "benchmark.retrieve-study.study-id.description",
        required = true
    )
    private String studyId;
    
    @Override public void run() {
      System.out.println("Retrieving study: " + studyId);
    }
    
  }
  
}
