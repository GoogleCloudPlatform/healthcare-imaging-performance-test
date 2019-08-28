
# Imaging performance testing suite

This repository contains a suite of tools used to benchmark DICOM data
transmission routines backed by the Cloud Healthcare API.

## Prerequisites

### Test data

To use the tool, you first need to have created a DICOM store in the Cloud
Healthcare API. You also need to populate this DICOM store with some
test data. See [Creating and managing DICOM
stores](https://cloud.google.com/healthcare/docs/how-tos/dicom)
for information on how to create a DICOM store.

Google hosts publicly available DICOM data that you can use for testing.
These include the
[NIH Chest X-ray](https://cloud.google.com/healthcare/docs/resources/public-datasets/nih-chest)
and [TCIA](https://cloud.google.com/healthcare/docs/resources/public-datasets/tcia)
datasets.

### Applications

Before running the tool, make sure that you've installed the following applications:

- [Java SE Runtime Environment 8](https://www.oracle.com/technetwork/java/javase/downloads/index.html) or later
- [Git](https://git-scm.com/)
- [Apache Maven](https://maven.apache.org/)
- [Google Cloud SDK](https://cloud.google.com/sdk/)

### Authentication setup

The tool uses Google Cloud ADC (Application Default Credentials) to authenticate
to the Cloud Healthcare API.

To authenticate from your machine, run the the following command and then follow
the instructions:

    gcloud auth application-default login

To use a service account instead, see [Providing service account credentials](https://cloud.google.com/docs/authentication/production#providing_service_account_credentials).

## Running the performance testing tool

To run the tool:

1. Open a terminal and clone the repository by running `git clone
   https://github.com/GoogleCloudPlatform/healthcare-imaging-performance-test.git`.
   If you'd prefer to download a ZIP version of the repository, download the
   repository as a ZIP, then install
   [ZIP archive](https://github.com/GoogleCloudPlatform/healthcare-imaging-performance-test/archive/master.zip)
   and extract the ZIP file's contents.
2. Change to the `healthcare-imaging-performance-test` folder and run `mvn clean install` to create a JAR bundle.
3. Change to the `target` folder and run `java -jar healthcare-imaging-performance-test-X.Y.Z-jar-with-dependencies.jar benchmark <BENCHMARK> <OPTIONS...>`,
   where `X.Y.Z` is the version of the tool, `BENCHMARK` is the name of the benchmark and `OPTIONS` is a list of the following options:
  
```
  -i, --iterations
  Optional number of iterations for how many times the routine is executed (default is 1).
  -t, --max-threads
  Optional maximum number of threads to run in parallel in download requests (default is 10).
  -o, --output
  Optional file to write the result to. If not provided, the result is written to standard output.
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

### Download the dataset benchmark

This benchmark shows how quickly you can download a large dataset (as an entire DICOM store).
It involves sending requests to get study information (QIDO) while also sending parallel requests to
retrieve all of the studies in the DICOM store (WADO).

The name of this benchmark is `download-dataset`.

An example command line execution:

    java -jar healthcare-imaging-performance-test-X.Y.Z-jar-with-dependencies.jar benchmark download-dataset -i 3 -t 5 -o results.csv -p chc-nih-chest-xray -l us-central1 -d nih-chest-xray -s nih-chest-xray

In the example above the `download-dataset` benchmark will:

* Execute 3 times
* Use a maximum of 5 threads to read studies in parallel from the [NIH Chest X-ray](https://cloud.google.com/healthcare/docs/resources/public-datasets/nih-chest#cloud-healthcare-api) public dataset
* Write the gathered metrics to a `results.csv` file

The format of the output file is CSV. Each line represents the metrics of separate WADO requests and
has the following format:

    ITERATION, QUERYING_STUDIES_LATENCY, FIRST_BYTE_RECEIVED_LATENCY, READING_FIRST_STUDY_LATENCY, READING_WHOLE_DATASET_LATENCY, TOTAL_BYTES_READ MB_READ_PER_SECOND

Where:
- `ITERATION` is the number of iterations.
- `QUERYING_STUDIES_LATENCY` is the latency of querying studies in milliseconds.
- `FIRST_BYTE_RECEIVED_LATENCY` is the latency of the first byte received in milliseconds.
- `READING_FIRST_STUDY_LATENCY` is the latency of reading the first study in milliseconds.
- `READING_WHOLE_DATASET_LATENCY` is the total latency of reading the whole dataset in milliseconds.
- `TOTAL_BYTES_READ` is the total number of bytes read for the whole dataset.
- `MB_READ_PER_SECOND` is the megabytes read per-second for the whole dataset.

### Retrieve study benchmark

This benchmark shows how quickly you can retrieve a whole study. It involves sending requests to get instance information (QIDO) while also sending
parallel GET requests to retrieve each instance (WADO).

The name of this benchmark is `retrieve-study`. The command line has two additional required options:
`-y` and `--dicom-study`. `--dicom_study` is the ID of the study in the DICOM store.

An example command line execution:

    java -jar healthcare-imaging-performance-test-X.Y.Z-jar-with-dependencies.jar benchmark retrieve-study -i 3 -t 5 -o results.csv -p chc-nih-chest-xray -l us-central1 -d nih-chest-xray -s nih-chest-xray -y 1.2.276.0.7230010.3.1.2.2148188175.13.1558046897.715757

In the example above the `retrieve-study` benchmark will:

* Execute 3 times
* Use a maximum of 5 threads to read instances of a study with ID = `1.2.276.0.7230010.3.1.2.2148188175.13.1558046897.715757` in parallel from the [NIH Chest X-ray](https://cloud.google.com/healthcare/docs/resources/public-datasets/nih-chest#cloud-healthcare-api)
public dataset
* Write the gathered metrics to a `results.csv` file

The format of the output file is CSV. Each line represents the metrics of separate WADO requests and
has the following format:

    ITERATION, QUERYING_INSTANCES_LATENCY, FIRST_BYTE_RECEIVED_LATENCY, READING_FIRST_INSTANCE_LATENCY, READING_WHOLE_STUDY_LATENCY, TOTAL_BYTES_READ, MB_READ_PER_SECOND

Where:
- `ITERATION` is the number of iterations.
- `QUERYING_INSTANCES_LATENCY` is the latency of querying instances in milliseconds.
- `FIRST_BYTE_RECEIVED_LATENCY` is the latency of the first byte received in milliseconds.
- `READING_FIRST_INSTANCE_LATENCY` is the latency of reading the first instance in milliseconds.
- `READING_WHOLE_STUDY_LATENCY` is the total latency of reading the whole study in milliseconds.
- `TOTAL_BYTES_READ` is the total number of bytes read for the whole study.
- `MB_READ_PER_SECOND` is the megabytes read per-second for the whole study.
