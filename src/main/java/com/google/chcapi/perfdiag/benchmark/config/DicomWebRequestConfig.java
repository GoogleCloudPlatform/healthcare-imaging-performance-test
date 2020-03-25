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

import picocli.CommandLine.Option;

/**
 * Configuration parameters of DICOM study.
 *
 * @author Mikhail Ukhlin
 */
public class DicomWebRequestConfig extends DicomStoreConfig {

    /**
     * DICOM web request path.
     */
    @Option(
            names = {"-r", "--request-path"},
            descriptionKey = "option.request-path.description",
            required = true
    )
    private String requestPath;

    /**
     * Returns DICOM web request path.
     *
     * @return DICOM web request path.
     */
    public String getRequestPath() {
        return requestPath;
    }

}
