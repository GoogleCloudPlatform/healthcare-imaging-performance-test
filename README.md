
# Imaging performance testing suite

This repository contains a tool suite to benchmark DICOM data transmission
routines backed by Google Cloud Healthcare API.

## Testing data

The tool requires Google Cloud healthcare DICOM stores populated with testing
data. Follow this [doc](https://cloud.google.com/healthcare/docs/how-tos/dicom)
for how to create DICOM stores and import DICOM data.

You may use publicly available DICOM data already hosted by HCLS in GCP such as
[NIH-Chest-Xray](https://cloud.google.com/healthcare/docs/resources/public-datasets/nih-chest)
and [TCIA](https://cloud.google.com/healthcare/docs/resources/public-datasets/tcia)
image sets.

## Prerequisites

### Applications

Before running the tool, ensure that you've installed the following applications:

- [Java SE Runtime Environment 8](https://www.oracle.com/technetwork/java/javase/downloads/index.html) or later
- [Git](https://git-scm.com/)
- [Apache Maven](https://maven.apache.org/)
- [Google Cloud SDK](https://cloud.google.com/sdk/)

### Authentication set up

The tool uses Google Cloud ADC (Application Default Credentials) to authenticate
to Google Cloud Healthcare APIs.

To use Goolge user credentials, run:

    gcloud auth application-default login

To use a service account, follow this [doc](https://cloud.google.com/docs/authentication/production#providing_service_account_credentials).

## Running performance testing tool

To run the tool:

1. Open a terminal in some folder and run `git clone https://github.com/GoogleCloudPlatform/healthcare-imaging-performance-test.git`
   to clone source code of the tool from GitHub or download
   [ZIP archive](https://github.com/GoogleCloudPlatform/healthcare-imaging-performance-test/archive/master.zip)
   and extract it contents.
2. Go to `healthcare-imaging-performance-test` folder and run `mvn clean install` to create JAR bundle.
3. Go to `target` folder and run `java -jar healthcare-imaging-performance-test-X.Y.Z-jar-with-dependencies.jar benchmark <BENCHMARK> <OPTIONS...>`,
   where `X.Y.Z` is the version of the tool, `BENCHMARK` is the name of the benchmark and `OPTIONS` is a list of the following options:
   
```
  -i, --iterations
  Optional number of iterations how many times the routine is executed (default is 1).
  -t, --max-threads
  Optional maximum number of threads to run in parallel in download requests (default is 10).
  -o, --output
  Optional file to write the result to. If not provided, the result will be written to standard output.
* -p, --project
  Required ID of the GCP project.
* -l, --location
  Required ID of location (region).
* -d, --dataset
  Required ID of dataset in the project.
* -s, --dicom-store
  Required ID of DICOM store.
```

> Note: * are required options. 

### Download dataset benchmark

This benchmark shows the user how fast it is to download a large dataset (a whole DICOM store).
It involves sending requests to get study information (QIDO) and sending paralleled requests to
retrieve all studies in the dicom store (WADO).

The name of this benchmark is `download-dataset`.

Example of command line:

    java -jar healthcare-imaging-performance-test-X.Y.Z-jar-with-dependencies.jar benchmark download-dataset -i 3 -t 5 -o results.csv -p chc-nih-chest-xray -l us-central1 -d nih-chest-xray -s nih-chest-xray

In the example above the `download-dataset` benchmark will be executed 3 times, will use maximum 5 threads
to read studies in parallel from [NIH-Chest-Xray](https://cloud.google.com/healthcare/docs/resources/public-datasets/nih-chest#cloud-healthcare-api)
public dataset and store gathered metrics to `results.csv` file.

The format of the output file is CSV. Each line represents metrics of separate WADO request and
has the following format:

    ITERATION, QUERYING_STUDIES_LATENCY, FIRST_BYTE_RECEIVED_LATENCY, READING_FIRST_STUDY_LATENCY, READING_WHOLE_DATASET_LATENCY, TOTAL_BYTES_READ MB_READ_PER_SECOND

Where:
- `ITERATION` is number of iteration.
- `QUERYING_STUDIES_LATENCY` is latency of querying studies in milliseconds.
- `FIRST_BYTE_RECEIVED_LATENCY` is latency of first byte received in milliseconds.
- `READING_FIRST_STUDY_LATENCY` is latency of reading first study in milliseconds.
- `READING_WHOLE_DATASET_LATENCY` is total latency of reading whole dataset in milliseconds.
- `TOTAL_BYTES_READ` is total number of bytes read for the whole dataset.
- `MB_READ_PER_SECOND` is megabytes read per second for the whole dataset.

### Retrieve study benchmark

This benchmark shows how fast it can be to retrieve a whole study with Google Cloud Healthcare
Imaging API. It involves sending request to get instance information (QIDO) and sending
paralleled GET requests to retrieve each instance (WADO).

The name of this benchmark is `retrieve-study`. The command line has additional required option
`-y`, `--dicom-study` which is ID of study in DICOM store.

Example of command line:

    java -jar healthcare-imaging-performance-test-X.Y.Z-jar-with-dependencies.jar benchmark retrieve-study -i 3 -t 5 -o results.csv -p chc-nih-chest-xray -l us-central1 -d nih-chest-xray -s nih-chest-xray -y 1.2.276.0.7230010.3.1.2.2148188175.13.1558046897.715757

In the example above the `retrieve-study` benchmark will be executed 3 times, will use maximum 5 threads
to read instances of study with ID = `1.2.276.0.7230010.3.1.2.2148188175.13.1558046897.715757`
in parallel from [NIH-Chest-Xray](https://cloud.google.com/healthcare/docs/resources/public-datasets/nih-chest#cloud-healthcare-api)
public dataset and store gathered metrics to `results.csv` file.

The format of the output file is CSV. Each line represents metrics of separate WADO request and
has the following format:

    ITERATION, QUERYING_INSTANCES_LATENCY, FIRST_BYTE_RECEIVED_LATENCY, READING_FIRST_INSTANCE_LATENCY, READING_WHOLE_STUDY_LATENCY, TOTAL_BYTES_READ, MB_READ_PER_SECOND

Where:
- `ITERATION` is number of iteration.
- `QUERYING_INSTANCES_LATENCY` is latency of querying instances in milliseconds.
- `FIRST_BYTE_RECEIVED_LATENCY` is latency of first byte received in milliseconds.
- `READING_FIRST_INSTANCE_LATENCY` is latency of reading first instance in milliseconds.
- `READING_WHOLE_STUDY_LATENCY` is total latency of reading whole study in milliseconds.
- `TOTAL_BYTES_READ` is total number of bytes read for the whole study.
- `MB_READ_PER_SECOND` is megabytes read per second for the whole study.
