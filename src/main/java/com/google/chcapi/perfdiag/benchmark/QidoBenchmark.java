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

import java.util.concurrent.atomic.AtomicReference;

import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

import com.google.chcapi.perfdiag.benchmark.config.DicomWebRequestConfig;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;

import com.google.chcapi.perfdiag.benchmark.stats.MetricAggregates;
import com.google.chcapi.perfdiag.profiler.HttpRequestProfiler;
import com.google.chcapi.perfdiag.profiler.HttpRequestProfilerFactory;
import com.google.chcapi.perfdiag.profiler.HttpRequestMetrics;

/**
 * This benchmark shows how fast it can be to retrieve a whole study with Google Cloud Healthcare
 * Imaging API. It involves sending request to get instance information (QIDO) and sending
 * paralleled GET requests to retrieve each instance (WADO).
 *
 * @author Mikhail Ukhlin
 */
@Command
public class QidoBenchmark extends Benchmark {

    /**
     * DICOM study configuration from command line.
     */
    @Mixin
    protected DicomWebRequestConfig requestConfig;

    /**
     * Aggregates for latency of first byte received.
     */
    private MetricAggregates firstResponseAggregates;

    /**
     * Aggregates for latency of reading all resources.
     */
    private MetricAggregates totalAggregates;

    /**
     * Validates configuration and initializes aggregates.
     */
    @Override
    protected void validateConfig() {
        super.validateConfig();
        final int iterations = commonConfig.getIterations();
        firstResponseAggregates = new MetricAggregates(iterations);
        totalAggregates = new MetricAggregates(iterations);
    }

    /**
     * Retrieves DICOM study instances in parallel and stores metrics for each request to the
     * specified output stream if any.
     *
     * @param iteration Iteration number.
     * @param output Output stream to write metrics or {@code null} if output file is not specified.
     * @throws Exception if an error occurred.
     */
    @Override
    protected void runIteration(int iteration, PrintStream output) throws Exception {
        final AtomicReference<HttpRequestMetrics> firstResponseMetrics = new AtomicReference<>();

        // Fetch list of available study instances
        final HttpRequestProfiler qidoRequest =
                HttpRequestProfilerFactory.createQidoRequest(requestConfig);
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final HttpRequestMetrics metrics = qidoRequest.execute(buffer);

        if (output == System.out) {
            // New line after progress
            output.println();
        }

        // Update aggregates
        firstResponseAggregates.addValue(metrics.getResponseLatency());
        totalAggregates.addValue(metrics.getTotalLatency());

        // Print metrics
        printQidoMetrics(metrics.getResponseLatency(), metrics.getTotalLatency());

        // Print iteration metrics to CSV file if output option is specified
        if (output != null) {
            if (iteration == 0) {
                output.println("ITERATION, FIRST_BYTE_RECEIVED_LATENCY, "
                        + "TOTAL_LATENCY");
            }
            output.print(iteration);
            output.print(", ");
            output.print(metrics.getResponseLatency());
            output.print(", ");
            output.print(metrics.getTotalLatency());
            output.println();
        }
    }

    /**
     * Prints calculated aggregates for all iterations to stdout.
     */
    @Override
    protected void printAggregates() {
        printQidoAggregates(firstResponseAggregates, totalAggregates);
    }

    /* Object mapper to convert JSON response */
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

}
