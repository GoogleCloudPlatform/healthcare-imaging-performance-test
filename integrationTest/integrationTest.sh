#!/bin/bash

# Copyright 2019 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

project="${1}"
location="${2}"
dataset="${3}"
store="${4}"
study="${5}"

checkFile () {
  fileSize=$(stat --printf="%s" ./$1)
  if [ $? -eq 1 ] || [ $fileSize -lt 1 ]; then
    exit 1;
  fi
}

jarVersion="$(grep -m 1 "<version>" ./pom.xml \
  | grep -Eo "[[:digit:]]+.[[:digit:]]+.[[:digit:]]+")"

jarName="healthcare-imaging-performance-test-${jarVersion}-jar-with-dependencies.jar"
java -jar ./target/$jarName benchmark download-dataset -i 3 -t 12 \
  -o resultsDownload.csv -p $project -l $location -d $dataset -s $store

checkFile resultsDownload.csv

java -jar ./target/$jarName benchmark retrieve-study -i 3 -t 12 \
  -o resultsRetrieve.csv -p $project -l $location -d $dataset -s $store -y $study

checkFile resultsRetrieve.csv

