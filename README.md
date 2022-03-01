# REACH Dossier

## Environment variables

The following environment variables are required to run locally (beyond the variables required to run the other reach applications):

```
HTTP_REACH_DOSSIER_PORT=8097
DB_REACH_DOSSIER_URL=spring.datasource.url=jdbc:sqlserver://localhost:1433;database=dossier;
TEMP_DOSSIER_DAYS_TO_EXPIRE=1
```

## Running and developing locally

reach-dossier requires a MSSQL DB to run.  Additionally, some tests require a MSSQL DB as there is no in-memory equivalent which 
supports T-SQL and XML columns. 

#### Dependent components

**Reach Database**

The `reach-database` can be created as part of the chemicals `build.sh` or 
by executing the docker-compose file of chemicals' `docker/reach-database`

```
cd docker/reach-database
docker-compose up
```

**Reach Dossier DBs**

The Reach Dossier service talks to its own DB `reach-dossier`, and `reach-dossier-unit-test` DB is required for unit tests. 
Ensure the `reach-database` container is up and running, and then run the following command at the root dir of the project.

```
docker exec -i reach-database /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P L0calDBPass < database/setupDb.sql
``` 

#### Liquibase

To run the latest Liquibase changesets against the DB, from the `database` directory run

```
mvn process-resources
```

Once the dbs mentioned above are up and running, you can then run the commands below to start the dossier service locally

```
cd docker
# check pom.xml for setting the argument BUILD_VERSION
docker-compose build --build-arg BUILD_VERSION=3.3.0-SNAPSHOT
# check the docker-composse file and ensure the env variables mentioned are set correctly
docker-compose up --no-build
```

## Querying the API
Reach dossier offers a [graphql](https://graphql.org/) api on endpoint `/graphql`. Due to the way authentication is setup 
in the application [Graphiql](https://github.com/graphql/graphiql) (the graphql integrated frontend) was not setup in the 
application so I recommend developers using something along the lines of 
[GraphQl playground](https://github.com/prisma-labs/graphql-playground) (a graphql IDE). The latest version of 
[Postman](https://learning.getpostman.com/docs/postman/sending-api-requests/graphql/) has also now added support for GraphQL although
the support is still in Beta (and is missing some features such as automatic schema detection) but can be used as well.
Regular HTTP requests can be made following the information on [the official docs](https://graphql.org/learn/serving-over-http/)

## API Authentication
The API Authentication in this service is achieved via `JwtAuthenticationEntryPoint` an implementation of 
Spring's security API `AuthenticationEntryPoint`
