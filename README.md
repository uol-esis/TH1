# TH1

## Building the project

This project is written in Java 21 and uses Maven as its build tool.
To build the project, you need to have Java 21 installed.
Clone this repository and run the following command in the root directory of the project:

```bash
./mvnw clean install package
```

This will build the project.
To run the project, run the following command:

```bash
java -jar target/TH1-1.0-SNAPSHOT.jar
```

## OpenAPI

This project uses OpenAPI to document the API and generate the server code.
The OpenAPI Document is located [here](https://github.com/uol-esis/TH1-OpenAPI).
You can read up on OpenAPI and the specification [here](https://spec.openapis.org/oas/v3.0.3) and use the
[Reference Guide](https://swagger.io/docs/specification/v3_0/about/) by Swagger.

To generate the server code we are using the OpenAPI Generator via the Maven plugin.
You can find the documentation for the Java generator [here](https://openapi-generator.tech/docs/generators/java/).

If you want to run the project for the first time or have made changes to the OpenAPI specification, you need to
generate the server code. You can do this by running the following command:

```bash
./mvnw clean compile
```

After generating the server code, you can run the project as described above.
While the project is running, you can access the Swagger UI at http://localhost:8080/swagger-ui/index.html

> **Note:** If you are using an IDE, you might have to set `target/generated-sources/openapi/src/main/java` as a
> generated sources root to avoid compilation errors.

## Docker

In order to run and test the application locally, you need to set up a database for the app to connect to. This might be
done using docker.

In the `docker` directory, you will find a `docker-compose.yaml` file which defines all necessary variables to set start
a postgres database. Make sure, Docker and Docker compose are installed.

Then, run the following command in the root directory of the project:

> **NOTE:** The docker commands will be much simpler if you `cd` into the `docker` directory. This will allow you to not
> pass the `-f docker/docker-compose.yaml` flag to the docker commands.

```bash
docker compose -f docker/docker-compose.yaml up -d db && docker compose -f docker/docker-compose.yaml logs -f
```

This will pull (if not already done) and start a postgres database in a docker container. The logs will be shown in the
console. You might at any time press CTRL+C to stop the logs from showing. The database will keep running in the
background.

### Testing whole docker stack

To test the whole application using docker simply run the following command:

```bash
docker compose -f docker/docker-compose.yaml up -d --build && docker compose -f docker/docker-compose.yaml logs -f backend
```

This will build the th1 application from the current state of the repository and start it in a docker container. The
logs will be shown in the console. You might at any time press CTRL+C to stop the logs from showing. The application
will keep running in the background.

To stop the application, run the following command:

```bash
docker compose -f docker/docker-compose.yaml down
```

> **Note:** Please consult the [wiki page](https://github.com/uol-esis/TH1/wiki/Docker) for further information about
> the docker setup.

## Authentication

The provided `compose`-file will load a default realm for testing purposes. The default credentials are:

| Username    | Password    | Privileges                   |
|-------------|-------------|------------------------------|
| `admin`     | `admin`     | `manage realm` (no th1 user) |
| `u-user`    | `u-user`    | `user`                       |
| `v-visitor` | `v-visitor` | `visitor`                    |

