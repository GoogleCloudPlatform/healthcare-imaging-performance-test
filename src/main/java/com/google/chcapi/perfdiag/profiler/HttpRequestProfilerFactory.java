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

import java.util.List;
import java.util.Arrays;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.nio.charset.StandardCharsets;

import java.net.URL;
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

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
  
  /* Root URL of Google Cloud Healthcare API */
  private static final String API_ROOT_URL = "https://healthcare.googleapis.com/v1beta1";
  
  /* OAuth 2.0 scopes */
  private static final List<String> OAUTH_SCOPES = Arrays.asList(
      "https://www.googleapis.com/auth/cloud-healthcare",
      "https://www.googleapis.com/auth/cloudplatformprojects.readonly");
  
  /**
   * OAuth 2.0 credential.
   */
  private static GoogleCredential credential;
  
  /**
   * Performs OAuth2 authorization and returns credential. This method uses Application Default
   * Credentials mechanism.
   * 
   * @return OAuth 2.0 credential.
   * @throws IOException if an IO error occurred.
   */
  public static GoogleCredential getCredential() throws IOException {
    if (credential == null) {
      credential = GoogleCredential.getApplicationDefault().createScoped(OAUTH_SCOPES);
      credential.refreshToken();
    }
    return credential;
  }
  
  /**
   * Constructs the {@code projects.locations.datasets.dicomStores.searchForStudies}
   * profiling request for the specified DICOM store configuration.
   * 
   * @param config DICOM store configuration.
   * @return The {@link HttpRequestProfiler} instance.
   * @throws IOException if an IO error occurred.
   */
  public static HttpRequestProfiler createListDicomStudiesRequest(DicomStoreConfig config)
      throws IOException {
    final URL url = toURL(buildDicomWebURL(config));
    return new HttpRequestProfiler(createHttpGetConnection(url));
  }
  
  /**
   * Constructs the {@code projects.locations.datasets.dicomStores.studies.retrieveStudy}
   * profiling request for the specified DICOM store configuration and study ID.
   * 
   * @param config DICOM store configuration.
   * @param studyId ID of the study to retrieve.
   * @return The {@link HttpRequestProfiler} instance.
   * @throws IOException if an IO error occurred.
   */
  public static HttpRequestProfiler createRetrieveDicomStudyRequest(DicomStoreConfig config,
      String studyId) throws IOException {
    final URL url = toURL(buildDicomWebURL(config).append("/").append(encodeURLToken(studyId)));
    return new HttpRequestProfiler(createHttpGetConnection(url));
  }
  
  /**
   * Constructs the {@code projects.locations.datasets.dicomStores.studies.searchForInstances}
   * profiling request for the specified DICOM study configuration.
   * 
   * @param config DICOM study configuration.
   * @return The {@link HttpRequestProfiler} instance.
   * @throws IOException if an IO error occurred.
   */
  public static HttpRequestProfiler createListDicomStudyInstancesRequest(DicomStudyConfig config)
      throws IOException {
    final URL url = toURL(buildDicomWebURL(config)
        .append("/").append(encodeURLToken(config.getDicomStudyId()))
        .append("/instances"));
    return new HttpRequestProfiler(createHttpGetConnection(url));
  }
  
  /**
   * Constructs the {@code projects.locations.datasets.dicomStores.studies.series.instances.retrieveInstance}
   * profiling request for the specified DICOM study configuration, series and instance IDs.
   * 
   * @param config DICOM study configuration.
   * @param seriesId ID of the series.
   * @param instanceId ID of the instance to retrieve.
   * @return The {@link HttpRequestProfiler} instance.
   * @throws IOException if an IO error occurred.
   */
  public static HttpRequestProfiler createRetrieveDicomStudyInstanceRequest(DicomStudyConfig config,
      String seriesId, String instanceId) throws IOException {
    final URL url = toURL(buildDicomWebURL(config)
        .append("/").append(encodeURLToken(config.getDicomStudyId()))
        .append("/series/").append(encodeURLToken(seriesId))
        .append("/instances/").append(encodeURLToken(instanceId)));
    return new HttpRequestProfiler(createHttpGetConnection(url));
  }
  
  // Utility
  
  /**
   * Constructs a new {@code HttpURLConnection} prepared for HTTP GET request for the specified URL.
   * 
   * @param url The HTTP request URL.
   * @return A new {@code HttpURLConnection} prepared for HTTP GET request.
   * @throws IOException if an IO error occurred.
   */
  private static HttpURLConnection createHttpGetConnection(URL url)
      throws IOException {
    final String accessToken = getCredential().getAccessToken();
    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.addRequestProperty("Authorization", "Bearer " + accessToken);
    connection.setRequestMethod("GET");
    connection.setDoOutput(false);
    return connection;
  }
  
  /**
   * Constructs DICOM Web request URL using parameters from the specified DICOM store configuration.
   * 
   * @param config DICOM store configuration.
   * @return DICOM Web request URL as {@code StringBuilder} instance for further URL construction.
   */
  private static StringBuilder buildDicomWebURL(DicomStoreConfig config) {
    return new StringBuilder(API_ROOT_URL)
        .append("/projects/").append(encodeURLToken(config.getProjectId()))
        .append("/locations/").append(encodeURLToken(config.getLocationId()))
        .append("/datasets/").append(encodeURLToken(config.getDatasetId()))
        .append("/dicomStores/").append(encodeURLToken(config.getDicomStoreId()))
        .append("/dicomWeb/studies");
  }
  
  /**
   * Encodes the specified token to be used in URL address.
   * 
   * @param token The token to encode.
   * @return Encoded token to be used in URL address.
   */
  private static String encodeURLToken(String token) {
    try {
      return URLEncoder.encode(token, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      // Should never happen
      throw new IllegalStateException(e);
    }
  }
  
  /**
   * Constructs an {@code URL} instance for the specified string.
   * 
   * @param url The {@code StringBuilder} instance that contains URL in a string form.
   * @return An {@code URL} instance for the specified string.
   */
  private static URL toURL(StringBuilder url) {
    try {
      return new URL(url.toString());
    } catch (MalformedURLException e) {
      // Should never happen
      throw new IllegalStateException(e);
    }
  }
  
}
