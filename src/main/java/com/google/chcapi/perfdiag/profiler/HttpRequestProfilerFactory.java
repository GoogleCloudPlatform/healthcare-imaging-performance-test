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

package com.google.chcapi.perfdiag.profiler;

import java.util.Arrays;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.nio.charset.StandardCharsets;

import java.net.URLEncoder;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import com.google.chcapi.perfdiag.benchmark.config.DicomStoreConfig;
import com.google.chcapi.perfdiag.benchmark.config.DicomStudyConfig;

/**
 * Factory class that allows to create HTTP profiling requests to Google Cloud Healthcare API.
 * 
 * @author Mikhail Ukhlin
 * @see HttpRequestProfiler
 */
public final class HttpRequestProfilerFactory {
  
  /* Do not allow instances */
  private HttpRequestProfilerFactory() {
    throw new IllegalAccessError();
  }
  
  /**
   * OAuth 2.0 credential obtained using Google Application Default Credentials mechanism.
   */
  public static final GoogleCredential CREDENTIAL;
  static {
    try {
      CREDENTIAL = GoogleCredential.getApplicationDefault().createScoped(Arrays.asList(
          "https://www.googleapis.com/auth/cloud-healthcare",
          "https://www.googleapis.com/auth/cloudplatformprojects.readonly"));
    } catch (IOException e) {
      throw new IllegalStateException(e.getMessage());
    }
  }
  
  /* Root URL of Google Cloud Healthcare API */
  private static final String API_ROOT_URL = "https://healthcare.googleapis.com/v1beta1";
  
  /**
   * Constructs the {@code projects.locations.datasets.dicomStores.searchForStudies}
   * profiling request for the specified DICOM store configuration.
   * 
   * @param config DICOM store configuration.
   * @return The {@link HttpRequestProfiler} instance.
   */
  public static HttpRequestProfiler createListDicomStudiesRequest(DicomStoreConfig config) {
    return new HttpRequestProfiler(createHttpGetRequest(
        buildDicomWebURI(config)
        .toString(), false));
  }
  
  /**
   * Constructs the {@code projects.locations.datasets.dicomStores.studies.retrieveStudy}
   * profiling request for the specified DICOM store configuration and study ID.
   * 
   * @param config DICOM store configuration.
   * @param studyId ID of the study to retrieve.
   * @return The {@link HttpRequestProfiler} instance.
   */
  public static HttpRequestProfiler createRetrieveDicomStudyRequest(DicomStoreConfig config,
      String studyId) {
    return new HttpRequestProfiler(createHttpGetRequest(
        buildDicomWebURI(config)
        .append("/").append(encodeURIToken(studyId))
        .toString(), true));
  }
  
  /**
   * Constructs the {@code projects.locations.datasets.dicomStores.studies.searchForInstances}
   * profiling request for the specified DICOM study configuration.
   * 
   * @param config DICOM study configuration.
   * @return The {@link HttpRequestProfiler} instance.
   */
  public static HttpRequestProfiler createListDicomStudyInstancesRequest(DicomStudyConfig config) {
    return new HttpRequestProfiler(createHttpGetRequest(
        buildDicomWebURI(config)
        .append("/").append(encodeURIToken(config.getDicomStudyId()))
        .append("/instances")
        .toString(), false));
  }
  
  /**
   * Constructs the {@code projects.locations.datasets.dicomStores.studies.series.instances.retrieveInstance}
   * profiling request for the specified DICOM study configuration, series and instance IDs.
   * 
   * @param config DICOM study configuration.
   * @param seriesId ID of the series.
   * @param instanceId ID of the instance to retrieve.
   * @return The {@link HttpRequestProfiler} instance.
   */
  public static HttpRequestProfiler createRetrieveDicomStudyInstanceRequest(DicomStudyConfig config,
      String seriesId, String instanceId) {
    return new HttpRequestProfiler(createHttpGetRequest(
        buildDicomWebURI(config)
        .append("/").append(encodeURIToken(config.getDicomStudyId()))
        .append("/series/").append(encodeURIToken(seriesId))
        .append("/instances/").append(encodeURIToken(instanceId))
        .toString(), true));
  }
  
  /**
   * Constructs a new HTTP GET request for the specified URI.
   * 
   * @param download {@code true} if it is download request.
   * @param uri The HTTP request URI.
   * @return A new prepared HTTP GET request instance.
   */
  private static HttpUriRequest createHttpGetRequest(String uri, boolean download) {
    final HttpGet request = new HttpGet(uri);
    if (download) {
      request.setHeader("Accept", "multipart/related; type=application/dicom; transfer-syntax=*");
    }
    request.setHeader("Authorization", "Bearer " + CREDENTIAL.getAccessToken());
    return request;
  }
  
  /**
   * Constructs DICOM Web request URI using parameters from the specified DICOM store configuration.
   * 
   * @param config DICOM store configuration.
   * @return DICOM Web request URI as {@code StringBuilder} instance for further URI construction.
   */
  private static StringBuilder buildDicomWebURI(DicomStoreConfig config) {
    return new StringBuilder(API_ROOT_URL)
        .append("/projects/").append(encodeURIToken(config.getProjectId()))
        .append("/locations/").append(encodeURIToken(config.getLocationId()))
        .append("/datasets/").append(encodeURIToken(config.getDatasetId()))
        .append("/dicomStores/").append(encodeURIToken(config.getDicomStoreId()))
        .append("/dicomWeb/studies");
  }
  
  /**
   * Encodes the specified token to be used in URI address.
   * 
   * @param token The token to encode.
   * @return Encoded token to be used in URI address.
   */
  private static String encodeURIToken(String token) {
    try {
      return URLEncoder.encode(token, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      // Should never happen
      throw new IllegalStateException(e);
    }
  }
  
}
