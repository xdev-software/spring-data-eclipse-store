[![Latest version](https://img.shields.io/maven-central/v/software.xdev/spring-data-eclipse-store?logo=apache%20maven)](https://mvnrepository.com/artifact/software.xdev/spring-data-eclipse-store)
[![Build](https://img.shields.io/github/actions/workflow/status/xdev-software/spring-data-eclipse-store/checkBuild.yml?branch=develop)](https://github.com/xdev-software/spring-data-eclipse-store/actions/workflows/checkBuild.yml?query=branch%3Adevelop)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=xdev-software_spring-data-eclipse-store&metric=alert_status)](https://sonarcloud.io/dashboard?id=xdev-software_spring-data-eclipse-store)

<div align="center">
    <img src="assets/Logo.png" height="200" alt="XDEV Spring-Data Eclipse-Store Logo">
</div>

# spring-data-eclipse-store

A library to simplify using [EclipseStore](https://eclipsestore.io/) in the [Spring environment](https://spring.io/projects/spring-data/).

What makes this library special is, that it creates a working copy of the data.
This way EclipseStore behaves almost exactly like relational database from a coding perspective.

## Features

The library provides following features:

* Enforces the
  **[Spring data repository concept](https://docs.spring.io/spring-data/jpa/reference/repositories/core-concepts.html)**
  for EclipseStore by
  using [working copies](https://xdev-software.github.io/spring-data-eclipse-store/working-copies.html)
* **[Drop in compatible](https://xdev-software.github.io/spring-data-eclipse-store/installation.html#drop-in-compatible)** for your existing Spring application
* Utilizes **ultra-fast EclipseStore serializing and storing**
* Enables your application to **select
  any [EclipseStore target](https://docs.eclipsestore.io/manual/storage/storage-targets/index.html)** (e.g.
  [PostgreSQL](https://docs.eclipsestore.io/manual/storage/storage-targets/sql-databases/postgresql.html),
  [AWS S3](https://docs.eclipsestore.io/manual/storage/storage-targets/blob-stores/aws-s3.html) or
  [IBM COS](https://github.com/xdev-software/eclipse-store-afs-ibm-cos))
* Can save up to **99%[^1] of monthly costs** in the IBM Cloud and up to 82%[^2] in the AWS Cloud

[^1]:If the COS Connector is used in the IBM Cloud instead of a PostgreSQL and approx. 10,000 entries with a total size
of 1
GB of data are stored. ([IBM Cloud Pricing](https://cloud.ibm.com/estimator/estimates), as of 08.01.2024)

[^2]: If the S3 connector is used instead of DynamoDB under the same conditions at
AWS. ([AWS Pricing Calculator](https://calculator.aws/#/estimate?id=ab85cddf77f0d1aa0457111ed82785dfb836b1d8), as of
08.01.2024)

## Installation & Usage

[**Installation
guide** for the latest release](https://github.com/xdev-software/spring-data-eclipse-store/releases/latest#Installation)

[**Detailed
instructions** are in the documentation](https://xdev-software.github.io/spring-data-eclipse-store/installation.html)

### Supported versions

| Spring-Data-Eclipse-Store | Java   | Spring Data | EclipseStore |
|---------------------------|--------|-------------|--------------|
| ``<= 1.0.2``              | ``17`` | ``3.2.2``   | ``1.1.0``    |
| ``1.0.3``                 | ``17`` | ``3.2.3``   | ``1.2.0``    |

## Demo

To see how easy it is to implement EclipseStore in your Spring project, take a look at
the [demos](./spring-data-eclipse-store-demo):

* [Simple demo](https://github.com/xdev-software/spring-data-eclipse-store/tree/develop/spring-data-eclipse-store-demo/src/main/java/software/xdev/spring/data/eclipse/store/demo/simple)
* [Complex demo](https://github.com/xdev-software/spring-data-eclipse-store/tree/develop/spring-data-eclipse-store-demo/src/main/java/software/xdev/spring/data/eclipse/store/demo/complex)
* [Demo with coexisting JPA](https://github.com/xdev-software/spring-data-eclipse-store/tree/develop/spring-data-eclipse-store-jpa/src/main/java/software/xdev/spring/data/eclipse/store/jpa)
* [Dual storage demo](https://github.com/xdev-software/spring-data-eclipse-store/tree/develop/spring-data-eclipse-store-demo/src/main/java/software/xdev/spring/data/eclipse/store/demo/dual/storage)

> [!NOTE]  
> Since the library is using reflection to copy data, the following JVM-Arguments may have to be set:
> ```
> --add-opens=java.base/java.util=ALL-UNNAMED
> --add-exports java.base/jdk.internal.misc=ALL-UNNAMED
> --add-opens=java.base/java.lang=ALL-UNNAMED
> --add-opens=java.base/java.time=ALL-UNNAMED 
> ```

## Support

If you need support as soon as possible, and you can't wait for any pull request, feel free to
use [our support](https://xdev.software/en/services/support).

## Contributing
See the [contributing guide](./CONTRIBUTING.md) for detailed instructions on how to get started with our project.

## Dependencies and Licenses
View the [license of the current project](LICENSE) or the [summary including all dependencies](https://xdev-software.github.io/spring-data-eclipse-store/dependencies)
