
# Imaging performance testing suite

This repository contains a tool suite to benchmark DICOM data transmission
routines backed by Google Cloud Healthcare API.

## Public datasets

You may use publicly available DICOM data already hosted by HCLS in GCP such as
[NIH-Chest-Xray](https://cloud.google.com/healthcare/docs/resources/public-datasets/nih-chest#cloud-healthcare-api)
and [TCIA](https://cloud.google.com/healthcare/docs/resources/public-datasets/tcia#cloud-healthcare-api)
image sets.

## Environment settings

In order to access Google Cloud Healthcare API endpoints you need to create
[service account](https://cloud.google.com/docs/authentication/production#obtaining_and_providing_service_account_credentials_manually)
and setup [GOOGLE_APPLICATION_CREDENTIALS](https://cloud.google.com/docs/authentication/production#providing_service_account_credentials)
environment variable.

## Command line options

The command line syntax is `benchmark <BENCHMARK> <OPTIONS...>`; where `BENCHMARK`
is the name of the benchmark and `OPTIONS` is a list of the following options:
- `-i`, `--iterations` - Optional number of iterations how many times the routine is executed
  (default is 1).
- `-t`, `--threads` - Optional maximum number of threads to run in parallel in download requests
  (default is 10).
- `-o`, `--output` - Optional file to write the result to. If not provided, the result will be
  written to standard output.
- `-p`, `--project` - Required ID of the GCP project.
- `-l`, `--location` - Required ID of location (region).
- `-d`, `--dataset` - Required ID of dataset in the project.
- `-s`, `--dicom-store` - Required ID of DICOM store.

## Download dataset benchmark

This benchmark shows the user how fast it is to download a large dataset (a whole DICOM store).
It involves sending requests to get study information (QIDO) and sending paralleled requests to
retrieve all studies in the dicom store (WADO).

The name of this benchmark is `download-dataset`.

Example of command line:

    benchmark download-dataset -i 3 -t 5 -o results.csv -p chc-nih-chest-xray -l us-central1 -d nih-chest-xray -s nih-chest-xray

In the example above the `download-dataset` benchmark will be executed 3 times, will use 5 threads
to read studies in parallel from [NIH-Chest-Xray](https://cloud.google.com/healthcare/docs/resources/public-datasets/nih-chest#cloud-healthcare-api)
public dataset and store gathered metrics to `results.csv` file.

The format of the output file is CSV. Each line represents metrics of separate WADO request and
has the following format:

    ITERATION, RESPONSE_LATENCY, READ_LATENCY, TOTAL_LATENCY, BYTES_READ, TRANSFER_RATE

Where:
- `ITERATION` is number of iteration.
- `RESPONSE_LATENCY` is latency of first byte received in milliseconds.
- `READ_LATENCY` is latency of reading study in milliseconds.
- `TOTAL_LATENCY` is total latency of reading whole study in milliseconds.
- `BYTES_READ` is number of bytes read for the study.
- `TRANSFER_RATE` is bytes read per second for the study.

## Retrieve study benchmark

This benchmark shows how fast it can be to retrieve a whole study with Google Cloud Healthcare
Imaging API. It involves sending request to get instance information (QIDO) and sending
paralleled GET requests to retrieve each instance (WADO).

The name of this benchmark is `retrieve-study`. The command line has additional required option
`-y`, `--dicom-study` which is ID of study in DICOM store.

Example of command line:

    benchmark retrieve-study -i 3 -t 5 -o results.csv -p chc-nih-chest-xray -l us-central1 -d nih-chest-xray -s nih-chest-xray -y 1.2.276.0.7230010.3.1.2.2148188175.13.1558046897.715757

In the example above the `retrieve-study` benchmark will be executed 3 times, will use 5 threads
to read instances of study with ID = `1.2.276.0.7230010.3.1.2.2148188175.13.1558046897.715757`
in parallel from [NIH-Chest-Xray](https://cloud.google.com/healthcare/docs/resources/public-datasets/nih-chest#cloud-healthcare-api)
public dataset and store gathered metrics to `results.csv` file.

The format of the output file is CSV. Each line represents metrics of separate WADO request and
has the following format:

    ITERATION, RESPONSE_LATENCY, READ_LATENCY, TOTAL_LATENCY, BYTES_READ, TRANSFER_RATE

Where:
- `ITERATION` is number of iteration.
- `RESPONSE_LATENCY` is latency of first byte received in milliseconds.
- `READ_LATENCY` is latency of reading instance in milliseconds.
- `TOTAL_LATENCY` is total latency of reading whole instance in milliseconds.
- `BYTES_READ` is number of bytes read for the instance.
- `TRANSFER_RATE` is bytes read per second for the instance.
