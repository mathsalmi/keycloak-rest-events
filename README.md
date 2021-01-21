# keycloak-rest-events
Send Keycloak events to Restful APIs.

## Requirements

- Java 11
- Maven 3.6

## Building

- Run `mvn package`
- It should generate a JAR archive under `./target/keycloak-rest-events.jar`

## Deploying to Keycloak

1. Move the built JAR file to Keycloak's directory `standalone/deployments/` (on Keycloak under Docker: `/opt/jboss/keycloak/standalone/deployments`)
2. Watch the `standalone/deployments/` for the file `keycloak-rest-events.jar.deployed`
3. Provide environment variables to Keycloak to set the endpoint URL and define the ignored events.
	- `REST_EVENT_LISTENER_URL_EVENTS`: endpoint url that will receive events
	- `REST_EVENT_LISTENER_URL_ADMIN_EVENTS`: endpoint url that will receive admin events
	- `REST_EVENT_LISTENER_IGNORED_EVENTS`: comma-separated list of ignored event types
	- `REST_EVENT_LISTENER_IGNORED_OPERATIONS`: comma-separated list of ignored operation types

:warning: If you find instead the file `keycloak-rest-events.jar.failed`, you can run the command `cat keycloak-sha1.jar.failed` to find out what went wrong with your deployment.
