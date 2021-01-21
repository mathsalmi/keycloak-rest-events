package com.msalmi;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;
import java.util.logging.Logger;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RestEventListenerProvider implements EventListenerProvider {

	private static final Logger log = Logger.getLogger(RestEventListenerProvider.class.getName());

	private final String urlEvents;
	private final String urlAdminEvents;
	private final Set<EventType> ignoredEvents;
	private final Set<OperationType> ignoredOperations;
	private final HttpClient httpClient;

	public RestEventListenerProvider(String urlEvents, String urlAdminEvents, Set<EventType> ignoredEvents,
			Set<OperationType> ignoredOperations, HttpClient httpClient) {
		this.urlEvents = urlEvents;
		this.urlAdminEvents = urlAdminEvents;
		this.ignoredEvents = ignoredEvents;
		this.ignoredOperations = ignoredOperations;
		this.httpClient = httpClient;
	}

	@Override
	public void onEvent(Event event) {
		handleEvent(event, ignoredEvents, event.getType(), urlEvents);
	}

	@Override
	public void onEvent(AdminEvent adminEvent, boolean includeRepresentation) {
		handleEvent(adminEvent, ignoredOperations, adminEvent.getOperationType(), urlAdminEvents);
	}

	@Override
	public void close() {

	}

	private <T, R> void handleEvent(T event, Set<R> ignoredTypes, R currentType, String url) {
		if (ignoredTypes.contains(currentType)) {
			return;
		}

		if (url == null || url.isBlank()) {
			log.warning("event captured but not sent since url parameter is blank");
			return;
		}

		try {
			sendEvent(new URI(url), toJson(event));
		} catch (JsonProcessingException e) {
			log.severe("failed to encode event as JSON. Error: " + e.getMessage());
		} catch (URISyntaxException e) {
			log.severe("failed to send event. Invalid URL. Error: " + e.getMessage());
		} catch (Exception e) {
			log.severe("failed to send event. Error: " + e.getMessage());
		}

	}

	private String toJson(Object o) throws JsonProcessingException {
		var om = new ObjectMapper();
		return om.writeValueAsString(o);
	}

	private void sendEvent(URI url, String jsonEvent) throws IOException, InterruptedException {
		// @formatter:off
		var request = HttpRequest.newBuilder(url)
				.POST(HttpRequest.BodyPublishers.ofString(jsonEvent))
				.header("Content-Type", "application/json")
				.build();
		// @formatter:on

		httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
	}
}