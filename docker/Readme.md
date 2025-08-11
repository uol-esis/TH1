# Development Docker Compose

This project includes a predefined `docker-compose.yaml` file which should help you get all services started as fast as
possible.

To start all services required simply run:

```shell
docker compose up -d db keycloak metabase
```

After this, you need to open the website of metabase at http://localhost:3000 in order to configure metabase to use the
database and to create an API key which then can be set for TH1 via the `application-dev.yaml` or, when running the
docker image of TH1, via the `.env`. Checkout the `.env.sample` to have the naming right.

All other configurations - especially the setup of keycloak - should happen automatically.

Please note: When using Docker Desktop to run docker images, you need to enable the usage of the option
`network_mode: host` via the settings in docker desktop.

## (OPTIONAL) Setting up an S3 Objectstorage

To make use of the object Storage you need to configure the object storage and obtain the credentials. All those steps
have been gathered in individual scripts:

| script              | goal                                                                       |
|---------------------|----------------------------------------------------------------------------|
| createGarageToml.sh | generates a new `garage.toml` file for the object storage to be functional |
| configure.sh        | configures the object storage; will be used by a helper container          |

The steps to get the whole stack started are as follows (from within the `docker` directory):

1. Start and configure the object storage with
   `docker compose up garage garage_config -d && docker compose logs garage_config -f`
    1. Wait until you see the following output in the console:
    ```console
    THE FOLLOWING KEY HAS BEEN GENERATED
    ================
    KEY ID: <SOME_KEY_ID>
    SECRET_KEY: <SOME_SECRET_KEY>
    ```
    2. When everything worked correctly, you should now be back to the command line prompt.
2. Copy the credentials into `.env` for docker deployment or `application-dev.properties` for local
   development.
3. If using the docker deployment, start the rest of the stack with `docker compose up --build`.

> [!TIP]
> To start all relevant services for development, you may use the following command:
> ```bash
> docker compose up garage garage_config db -d && docker compose logs -f
> ```

As Garage is just starting with developing a UI for bucket administration, the scripts are required to be able to
configure the stack. However, once the UI is ready, we will have an eye on it to check if it can replace the current
setup.

### Why we chose Garage

For a long time MinIO seemed to be the most viable option when it comes to open source object storage solutions.
However, with recent developments (https://github.com/minio/object-browser/pull/3509) it seems, that the project is
moving into a more business oriented direction. This itself is not a problem, but since we want to provide an easily
manageable solution for our users, we required a UI administration solution that is not behind a paywall. During the
search for alternatives, we stumbled upon Garage (and other object Storages like Apache Ozone). Garage seemed well
documented as well as straight forward to set up and use. That's not to say, that we will definitely keep using Garage -
we implemented the communication based on the S3 protocol, so changing the underlying storage should not be too
hard - but for now, we will stick to it.