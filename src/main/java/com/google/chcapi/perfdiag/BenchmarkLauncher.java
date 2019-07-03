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

package com.google.chcapi.perfdiag;

import java.util.ResourceBundle;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.IExecutionExceptionHandler;
import picocli.CommandLine.ParseResult;

import com.google.chcapi.perfdiag.benchmark.DownloadDatasetBenchmark;
import com.google.chcapi.perfdiag.benchmark.RetrieveStudyBenchmark;

/**
 * The CLI for benchmark DICOM data transmission routines backed by Google Cloud Healthcare API.
 * 
 * @author Mikhail Ukhlin
 * @see DownloadDatasetBenchmark
 * @see RetrieveStudyBenchmark
 */
@Command
public class BenchmarkLauncher implements Runnable {
  
  /* Signle instance */
  private static final BenchmarkLauncher LAUNCHER = new BenchmarkLauncher();
  
  /* Singleton */
  private BenchmarkLauncher() {
    super();
  }
  
  /**
   * Just prints usage information to stdout (executed only when command line has no benchmark
   * specified).
   */
  @Override
  public void run() {
    CLI.usage(System.out);
  }
  
  /**
   * Command line entry point for DICOM performance diagnostic tool.
   * 
   * @param args Command line arguments.
   */
  public static void main(String[] args) {
    System.exit(CLI.execute(args));
  }
  
  /**
   * Creates exception handler for errors occurred in the benchmark routines. Returned exception
   * handler will print error message to stderr without stacktrace and return 1 as exit code.
   * 
   * @return Exception handler for errors occurred in the benchmark routines.
   */
  private static IExecutionExceptionHandler createBenchmarkExceptionHandler() {
    return new IExecutionExceptionHandler() {
      @Override public int handleExecutionException(Exception e, CommandLine cl, ParseResult pr) {
        System.err.println(e.getMessage());
        return CommandLine.ExitCode.SOFTWARE;
      }
    };
  }
  
  /* CLI instance */
  private static final CommandLine CLI = new CommandLine(LAUNCHER).setCommandName("perfdiag")
      .setResourceBundle(ResourceBundle.getBundle("cli-messages"))
      .setExecutionExceptionHandler(createBenchmarkExceptionHandler())
      .addSubcommand("benchmark", new CommandLine(LAUNCHER)
        .addSubcommand("help", new HelpCommand())
        .addSubcommand("download-dataset", new DownloadDatasetBenchmark())
        .addSubcommand("retrieve-study", new RetrieveStudyBenchmark()));
  
}
