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

package com.google.chcapi.perfdiag.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Describes DICOM study instance.
 *
 * @author Mikhail Ukhlin
 */
public class Instance {

  /** DICOM study UID attribute. */
  @JsonProperty("0020000D")
  private Attribute<String> studyUID;

  /** DICOM series UID attribute. */
  @JsonProperty("0020000E")
  private Attribute<String> seriesUID;

  /** DICOM study instance UID attribute. */
  @JsonProperty("00080018")
  private Attribute<String> instanceUID;

  /**
   * Returns UID of DICOM study or {@code null} if not available.
   *
   * @return UID of DICOM study.
   */
  public String getStudyUID() {
    return studyUID == null ? null : studyUID.getFirstValue();
  }

  /**
   * Returns UID of DICOM series or {@code null} if not available.
   *
   * @return UID of DICOM series.
   */
  public String getSeriesUID() {
    return seriesUID == null ? null : seriesUID.getFirstValue();
  }

  /**
   * Returns UID of DICOM study instance or {@code null} if not available.
   *
   * @return UID of DICOM study instance.
   */
  public String getInstanceUID() {
    return instanceUID == null ? null : instanceUID.getFirstValue();
  }
}
