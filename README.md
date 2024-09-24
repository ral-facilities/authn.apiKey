# authn_apikey

The application validates a username along with an SHA-256 hash against a database.

This project uses Quarkus, a Java Framework. <https://quarkus.io/>

## Running the application in dev mode

You can run this application locally in dev mode that enables live coding using:

```shell script
./mvnw compile quarkus:dev
```
> **_NOTE:_**  Quarkus also ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## List of endpoints

| Location        | Example                                                      | Results                        |
| --------------- | ------------------------------------------------------------ | ------------------------------ |
| `/authenticate` | `curl -X POST "http://127.0.0.1:8080/authn.apikey/authenticate" -H "Authorization: Bearer 1159bfc57922c1708b63e31c04589f4b33155c5b24327bcb5b7b25859c84e399" -H "Content-Type: application/json" -d '{"user": "app2"}'` | `app2 logged in successfully ` |

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw clean package -DskipTests
```

It produces the `.jar` file in the `target/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

## Testing

The application uses integration tests to fully verify the api endpoints. The package needs to be built above first.

A database is needed with some test data in it.

```shell script
# This will start a local db with some dummy data in it to tests against
docker compose up
```
then the tests can be run:

```shell script
./mvnw failsafe:integration-test
```

The application can be run using:

```shell script
java -jar target/quarkus-app/quarkus-run.jar
```

If you want to build an [_über-jar_](https://blog.payara.fish/what-is-a-java-uber-jar), execute the following command:

```shell script
./mvnw package -DskipTests -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a [native](https://quarkus.io/guides/building-native-image#producing-a-native-executable) executable using docker by:

```shell script
./mvnw package -DskipTests -Dnative -Dquarkus.native.container-build=true
```

You can then execute the native executable with: `./target/*-runner`

## Docker :whale:

Once these packages have been built, they can be copied into a container and run.

Various dockerfiles exist in the `src/main/docker` folder for this.

For example, to build a native docker image locally:

```shell script
# From the root folder
docker build -f src/main/docker/Dockerfile.native -t quarkus/qarkus_auth
```

Then the image can be run:

```shell script
docker run -i --rm -p 8080:8080 quarkus/qarkus_auth
```

Once the CI has built the image, it can be run by:

```shell script
docker run -p 8080:8080 harbor.stfc.ac.uk/icat/authn_apikey:<git branch name>
```



