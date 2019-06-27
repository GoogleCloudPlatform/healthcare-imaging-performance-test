// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.chcapi.perfdiag.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DicomAttribute<T> {

  private String vr;
  
  @JsonProperty
  private T[] Value;

  public String getVr() {
    return vr;
  }

  public void setVr(String vr) {
    this.vr = vr;
  }

  public void setValue(T[] Value) {
    this.Value = Value;
  }

  @JsonIgnore
  public T getValue1() {
    return Value[0];
  }

  @JsonIgnore
  public T getValue2() {
    return Value[1];
  }

  @JsonIgnore
  public T[] getValue() {
    return Value;
  }

}
