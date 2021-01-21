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

	private String urlEvents = "";
	private String urlAdminEvents = "";
	private Set<EventType> ignoredEvents = Collections.emptySet();
	private Set<OperationType> ignoredOperations = Collections.emptySet();

	@Override
	public EventListenerProvider create(KeycloakSession keycloakSession) {
		var httpClient = HttpClient.newHttpClient();
		return new RestEventListenerProvider(urlEvents, urlAdminEvents, ignoredEvents, ignoredOperations, httpClient);
	}

	@Override
	public void init(Config.Scope scope) {
		this.urlEvents = scope.get("urlEvents");
		this.urlAdminEvents = scope.get("urlAdminEvents");
		this.ignoredEvents = transformStringToEventType(scope.getArray("ignoredEvents"));
		this.ignoredOperations = transformStringToOperationType(scope.getArray("ignoredOperations"));
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

	private Set<EventType> transformStringToEventType(String[] items) {
		final Set<EventType> finalItems = Collections.emptySet();

		if (items != null && items.length > 0) {
			for (final var item : items) {
				finalItems.add(EventType.valueOf(item));
			}
		}

		return finalItems;
	}

	private Set<OperationType> transformStringToOperationType(String[] items) {
		final Set<OperationType> finalItems = Collections.emptySet();

		if (items != null && items.length > 0) {
			for (final var item : items) {
				finalItems.add(OperationType.valueOf(item));
			}
		}

		return finalItems;
	}
}