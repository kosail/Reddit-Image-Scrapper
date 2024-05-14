package com.korealm.reddit.imagescrapper;

import javax.json.*;
import java.util.Optional;
import java.io.StringReader;
import java.io.IOException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Browser {
	private String subRedditURL = "https://www.reddit.com/r/";
	private HttpClient client;
	private Optional<JsonObject> jsonFetched = Optional.ofNullable(null);

	public Browser (final String subRedditURL) {
		if (subRedditURL == null || subRedditURL.isEmpty()) {
			throw new IllegalArgumentException("Subreddit name/url cannot be empty!");
		}

		this.subRedditURL += subRedditURL + ".json";
		client = HttpClient.newHttpClient(); // We will have only one HTTP client by the moment.
	}

	public void fetchImages () throws IOException, InterruptedException, StatusCodeException {
		HttpRequest request = HttpRequest.newBuilder()
							.uri(URI.create(subRedditURL)) // Handing out the complete link to the subreddit's json which hold's all posts information in a json format.
							.GET() // Tell HttpRequest that we want a HTTP GET method.
							.build(); // Ready for being executed.

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		if (response.statusCode() != 200) {
			throw new StatusCodeException("The returned code by Reddit was " + response.statusCode() + " thus we failed fetching the posts image list.");
		}

		JsonReader jsonReader = Json.createReader(new StringReader((String) response.body()));
		jsonFetched = Optional.of(jsonReader.readObject());
		jsonReader.close();
	}

	public Optional<JsonObject> getJson() {
		return jsonFetched;
	}
}
