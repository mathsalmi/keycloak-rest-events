package com.msalmi;

import java.net.http.HttpClient;
import java.util.Collections;
import java.util.Set;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.OperationType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class RestEventListenerProviderFactory implements EventListenerProviderFactory {

	@Override
	public EventListenerProvider create(KeycloakSession keycloakSession) {
		var urlEvents = System.getenv("REST_EVENT_LISTENER_URL_EVENTS");
		var urlAdminEvents = System.getenv("REST_EVENT_LISTENER_URL_ADMIN_EVENTS");
		var ignoredEvents = transformStringToEventType(System.getenv("REST_EVENT_LISTENER_IGNORED_EVENTS"));
		var ignoredOperations = transformStringToOperationType(System.getenv("REST_EVENT_LISTENER_IGNORED_OPERATIONS"));

		var httpClient = getHttpClient();
		return new RestEventListenerProvider(urlEvents, urlAdminEvents, ignoredEvents, ignoredOperations, httpClient);
	}

	@Override
	public void init(Config.Scope scope) {

	}

	@Override
	public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

	}

	@Override
	public void close() {

	}

	@Override
	public String getId() {
		return "keycloak-rest-events";
	}

	private Set<EventType> transformStringToEventType(String itemsChunk) {
		final Set<EventType> finalItems = Collections.emptySet();

		if (itemsChunk == null || itemsChunk.isBlank()) {
			return finalItems;
		}

		final var items = itemsChunk.split(",");
		if (items != null && items.length > 0) {
			for (final var item : items) {
				finalItems.add(EventType.valueOf(item));
			}
		}

		return finalItems;
	}

	private Set<OperationType> transformStringToOperationType(String itemsChunk) {
		final Set<OperationType> finalItems = Collections.emptySet();

		if (itemsChunk == null || itemsChunk.isBlank()) {
			return finalItems;
		}

		final var items = itemsChunk.split(",");
		if (items != null && items.length > 0) {
			for (final var item : items) {
				finalItems.add(OperationType.valueOf(item));
			}
		}

		return finalItems;
	}
	
	private HttpClient getHttpClient() {
		return HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
	}
}