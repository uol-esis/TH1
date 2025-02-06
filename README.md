# TH1

## Building the project

This project is written in Java 21 and uses Maven as its build tool.
To build the project, you need to have Java 21 installed.
Clone this repository and run the following command in the root directory of the project:

```bash
./mvnw clean install
```

This will build the project.
To run the project, run the following command:

```bash
java -jar target/TH1-1.0-SNAPSHOT.jar
```

## OpenAPI

This project uses OpenAPI to document the API and generate the server code.
The OpenAPI specification is located in the `openapi/openapi.yaml` file.
You can read up on OpenAPI and the specification [here](https://spec.openapis.org/oas/v3.0.3) and use the
[Reference Guide](https://swagger.io/docs/specification/v3_0/about/) by Swagger.

To generate the server code we are using the OpenAPI Generator via the Maven plugin.
You can find the documentation for the Java generator [here](https://openapi-generator.tech/docs/generators/java/).

If you want to run the project for the first time or have made changes to the OpenAPI specification, you need to
generate the server code. You can do this by running the following command:

```bash
./mvnw clean install
```

After generating the server code, you can run the project as described above.
While the project is running, you can access the Swagger UI at http://localhost:8080/swagger-ui/index.html

> **Note:** If you are using an IDE, you might have to set `target/generated-sources/openapi/src/main/java` as a
> generated sources root to avoid compilation errors.


## Docker

In order to run and test the application locally, you need to set up a database for the app to connect to. This might be 
done using docker.

In the `docker` directory, you will find a `docker-compose.yml` file which defines all necessary variables to set start 
a postgres database. Make sure, Docker and Docker compose are installed. Also, make sure to duplicate the `*.env.sample` 
files and remove the `.sample` ending. The env files are filled with the necessary environment variables for the application
to start. However, it is **strongly** recommended to change the variables values to avoid any security issues even in 
the dev environment.

Then, run the following command in the root directory of the project:

```bash
docker compose -f docker/docker-compose.yml up -d db && docker compose logs -f
```

This will pull (if not already done) and start a postgres database in a docker container. The logs will be shown in the
console. You might at any time press CTRL+C to stop the logs from showing. The database will keep running in the background.

### Testing whole docker stack

To test the whole application inside docker, prepare the `*.env` files as described above. Then run the following command:

```bash
docker compose -f docker/docker-compose.yml up -d --build && docker compose logs -f backend
```

This will build the th1 application from the current state of the repository and start it in a docker container. The logs
will be shown in the console. You might at any time press CTRL+C to stop the logs from showing. The application will keep
running in the background.

To stop the application, run the following command:

```bash
docker compose down
```

> **Note:** Please consult the wiki page for further information about the docker setup.
