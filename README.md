# knative-serverless-controller

> A Quarkus microservice that manages MAESTRO's serverless Knative deployments.

[![License](https://img.shields.io/badge/license-Apache2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

This project provides an autonomous controller microservice exposing the essential
tools and operations for handling MAESTRO's Knative serverless deployments. It
communicates with MAESTRO to execute incoming requests.

## Table of Contents

- [Features](#features)
- [API Documentation](#api-documentation)
- [Prerequisites](#prerequisites)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
    - [Dev mode](#dev-mode)
    - [Packaging and running](#packaging-and-running)
    - [Native executable](#native-executable)
    - [Containerized version](#containerized-version)
    - [Building and publishing images](#building-and-publishing-images)
- [License](#license)
- [Acknowledgement](#acknowledgement)

## Features

The microservice exposes a set of REST API endpoints responsible for handling:

1. **Serverless deployments** (deployment and undeployment requests)
2. **MAESTRO authentication** (access-token generation)
3. **Namespaces** in the K8s cluster (fetch, create, delete)
4. **Pods** in the K8s cluster (fetch, create, delete)
5. **Health check** for the controller
6. **Knative dummy service** (create, delete)

## API Documentation

The REST services are documented using the **OpenAPI v3.0.3** specification and
served through **Swagger UI**. Once the microservice is running, the documentation
is available at:

```
http://PROJECT_IP:PROJECT_PORT/q/swagger-ui
```

where `PROJECT_IP` and `PROJECT_PORT` correspond to the parameters used to run the
microservice, for example: <http://localhost:9500/q/swagger-ui>.

<img src="img/swagger_knative_serverless_controller.png" width="auto" alt="Swagger UI for the knative-serverless-controller">

## Prerequisites

To build and run the application you will need:

| Tool                | Version           | Notes                                                                |
| ------------------- | ----------------- |----------------------------------------------------------------------|
| **Java**            | 17                | Required for dev/JVM mode.                                           |
| **Maven**           | 3.9.6 or higher   | Optional, the bundled Maven Wrapper (`./mvnw`) handles this for you. |
| **GraalVM**         | for JDK 17        | Optional, only needed to build a native executable locally.          |
| **Docker**          | —                 | Optional, only needed for the containerized version.                 |

Reference JDK used during development:

```text
$ java --version

openjdk 17.0.9 2023-10-17
OpenJDK Runtime Environment GraalVM CE 17.0.9+9.1 (build 17.0.9+9-jvmci-23.0-b22)
OpenJDK 64-Bit Server VM GraalVM CE 17.0.9+9.1 (build 17.0.9+9-jvmci-23.0-b22, mixed mode, sharing)
```

#### MAESTRO backend

The controller requires communication with MAESTRO. You can
run MAESTRO locally or point to a remote instance. Either way, set the MAESTRO
backend URL in the configuration (see [Configuration](#configuration)).

#### Kubernetes & Knative (optional)

This controller exposes endpoints for creating/deleting dummy Knative Serving deployments, so
it needs access to a Kubernetes cluster with Knative Serving installed to test.

## Configuration

Project parameters are set in **`src/main/resources/application.yaml`**:

| Property                                        | Description                                                                                                   |
| ----------------------------------------------- | ------------------------------------------------------------------------------------------------------------- |
| `quarkus.http.port`                             | Port on which the microservice listens.                                                                       |
| `quarkus.rest-client.maestro-rest-api.url`      | Base URL of the MAESTRO backend.                                                                              |

For the containerized production profile, these values are supplied through a
`.env` file, see [`.env.example`](.env.example) for the expected keys.

## Running the Application

This project is built with the **[Quarkus](https://quarkus.io/)** framework **v3.37.2**.

### Dev mode

Start the application in live-reload dev mode:

```bash
./mvnw compile quarkus:dev
```

### Packaging and running

Package the application:

```bash
./mvnw package
```

This produces `quarkus-run.jar` in `target/quarkus-app/`. Note that this is **not**
an _über-jar_, dependencies are copied into `target/quarkus-app/lib/`. Run it with:

```bash
java -jar target/quarkus-app/quarkus-run.jar
```

To build an _über-jar_ instead:

```bash
./mvnw package -Dquarkus.package.type=uber-jar
```

The _über-jar_ is then runnable with:

```bash
java -jar target/*-runner.jar
```

### Native executable

Build a native executable (requires GraalVM):

```bash
./mvnw package -Pnative
```

If you don't have GraalVM installed, build it inside a container:

```bash
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

Then run it:

```bash
./target/knative-serverless-controller-{version}-runner
```

For more on native builds, see the [Quarkus Maven tooling guide](https://quarkus.io/guides/maven-tooling).

### Containerized version

Two Compose files are provided:

- **`docker-compose.yml`**: uses the project's dev-profile properties.
- **`docker-compose.prod.yml`**: used together with a `.env` file (see `.env.example`)
  to configure the project's parameters explicitly.

Start the containerized version:

```bash
# Dev version
docker compose up -d

# Production version
docker compose -f docker-compose.prod.yml up -d
```

### Building and publishing images

Container images are built and pushed to the project registry
(`ghcr.io/aero-project-eu/maestro-serverless-controller`). Two image flavours are produced from the Dockerfiles under
`src/main/docker/`:

| Flavour | Dockerfile           | Tag suffix | Notes                                  |
| ------- | -------------------- | ---------- | -------------------------------------- |
| JVM     | `Dockerfile.jvm`     | _(none)_   | Runs the packaged JVM application.     |
| Native  | `Dockerfile.native`  | `-native`  | Requires a pre-built native binary.    |

**Prerequisites**

- Docker with the Buildx plugin enabled.
- Authentication to the registry: `docker login ghcr.io -u {github_user}`.
  Set the image coordinates once so the tag lives in a single place:

```bash
export IMAGE=ghcr.io/aero-project-eu/maestro-serverless-controller
export VERSION=1.0.5
```

**JVM image**: build for both platforms in a single multi-arch push:

```bash
docker buildx build \
  --platform linux/amd64,linux/arm64 \
  --provenance=false \
  -f ./src/main/docker/Dockerfile.jvm \
  -t ${IMAGE}:${VERSION} \
  --push .
```

> **Note:** pushing single-platform builds to the *same* tag one after another
> overwrites the previous one — the last push wins. Use the combined
> `--platform linux/amd64,linux/arm64` command above to get a real multi-arch tag.

**Native image**: build the native binary first, then the image. Because the binary
is platform-specific, build one platform at a time and match the image platform to the
binary you produced:

```bash
# 1. Produce the native binary (build runs inside a container)
./mvnw clean package -Dnative -Dquarkus.native.container-build=true
 
# 2. Build and push the native image
docker buildx build \
  --platform linux/amd64 \
  --provenance=false \
  -f ./src/main/docker/Dockerfile.native \
  -t ${IMAGE}:${VERSION}-native \
  --push .
```

**Verify a published image** and its platforms:

```bash
docker buildx imagetools inspect ${IMAGE}:${VERSION}
```
## License

This project is licensed under the **Apache License 2.0** — see the
[LICENSE](LICENSE) file for details.

## Acknowledgement

This work has been funded by the European Union under Horizon Europe grant 101092850
(project AERO).