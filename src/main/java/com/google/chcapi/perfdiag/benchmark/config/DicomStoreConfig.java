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
 * Configuration parameters of DICOM store.
 * 
 * @author Mikhail Ukhlin
 */
public class DicomStoreConfig {
  
  /**
   * Project ID.
   */
  @Option(
      names = {"-p", "--project"},
      descriptionKey = "option.project.description",
      required = true
  )
  private String projectId;
  
  /**
   * Location ID (region).
   */
  @Option(
      names = {"-l", "--location"},
      descriptionKey = "option.location.description",
      required = true
  )
  private String locationId;
  
  /**
   * Dataset ID.
   */
  @Option(
      names = {"-d", "--dataset"},
      descriptionKey = "option.dataset.description",
      required = true
  )
  private String datasetId;
  
  /**
   * DICOM store ID.
   */
  @Option(
      names = {"-s", "--dicom-store"},
      descriptionKey = "option.dicom-store.description",
      required = true
  )
  private String dicomStoreId;
  
  /**
   * Returns project ID.
   * 
   * @return Project ID.
   */
  public String getProjectId() {
    return projectId;
  }
  
  /**
   * Returns location ID (region).
   * 
   * @return Location ID.
   */
  public String getLocationId() {
    return locationId;
  }
  
  /**
   * Returns dataset ID.
   * 
   * @return Dataset ID.
   */
  public String getDatasetId() {
    return datasetId;
  }
  
  /**
   * Returns DICOM store ID.
   * 
   * @return DICOM store ID.
   */
  public String getDicomStoreId() {
    return dicomStoreId;
  }
  
}
