package com.stashinvest.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;
import com.stashinvest.rest.User;
import com.stashinvest.rest.Users;

// A generic class for handling http/https get/post requests.
public class HttpRequests<T> {
	public static enum HttpRequestMethod {
		GET, POST;
	}

	private static final String USERS_SERVICE_ENDPOINT = "/invest-stash-rest/rest/v1/users";
	private static final String ACCOUNT_KEY_ENDPOINT = "/v1/account";
	private static final String HTTP_PROTOCOL = "http";
	private static final String HTTPS_PROTOCOL = "https";
	private static final int HTTP_PORT = 8080;
	private static final int HTTPS_PORT = 443;
	private static final String LOCAL_HOST = "localhost";
	private static final String ACCOUNT_KEY_SERVICE_HOST = "account-key-service.herokuapp.com";
	private static final String USER_AGENT = "Mozilla/5.0";
	private static final String JSON_MEDIA_TYPE = "application/json";

	public T httpsPost(String jsonBody, Class<T> clazz) throws IOException {

		URL url = new URL(HTTPS_PROTOCOL, ACCOUNT_KEY_SERVICE_HOST, HTTPS_PORT,
				ACCOUNT_KEY_ENDPOINT);
		HttpsURLConnection connection = (HttpsURLConnection) url
				.openConnection();
		connection.setRequestMethod(HttpRequestMethod.POST.toString());
		setConnectionParameters(connection, HttpRequestMethod.POST);

		connection.setFixedLengthStreamingMode(jsonBody.getBytes().length);
		try (OutputStreamWriter out = new OutputStreamWriter(
				connection.getOutputStream())) {
			out.write(jsonBody);
		}
		StringBuilder sb = new StringBuilder();
		try (BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()))) {
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				sb.append(inputLine);
			}
		}
		return new Gson().fromJson(sb.toString(), clazz);
	}

	public T httpPost(String jsonBody, Class<T> clazz) throws IOException {

		URL url = new URL(HTTP_PROTOCOL, LOCAL_HOST, HTTP_PORT,
				USERS_SERVICE_ENDPOINT);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod(HttpRequestMethod.POST.toString());
		setConnectionParameters(connection, HttpRequestMethod.POST);
		connection.setFixedLengthStreamingMode(jsonBody.getBytes().length);
		try (OutputStreamWriter out = new OutputStreamWriter(
				connection.getOutputStream())) {
			out.write(jsonBody);
		}
		StringBuilder sb = new StringBuilder();
		try (BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()))) {
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				sb.append(inputLine);
			}
		}
		return new Gson().fromJson(sb.toString(), clazz);
	}

	public T httpGet(Class<T> clazz) throws IOException {
		URL url = new URL(HTTP_PROTOCOL, LOCAL_HOST, HTTP_PORT,
				USERS_SERVICE_ENDPOINT);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod(HttpRequestMethod.GET.toString());
		setConnectionParameters(connection, HttpRequestMethod.GET);
		Gson gson = new Gson();
		StringBuilder sb = new StringBuilder();
		try (BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()))) {
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				sb.append(inputLine);
			}
		}
		return gson.fromJson(sb.toString(), clazz);
	}

	public void setConnectionParameters(URLConnection connection,
			HttpRequestMethod requestMethod) throws ProtocolException {
		connection.setRequestProperty("User-Agent", USER_AGENT);
		connection.setRequestProperty("Content-Type", JSON_MEDIA_TYPE);
		connection.setRequestProperty("Accept", JSON_MEDIA_TYPE);
		if (requestMethod.equals(HttpRequestMethod.POST)) {
			connection.setDoOutput(true);
		}
	}

	public static void main(String[] args) throws IOException {
		User user = new User();
		user.setEmail("alia@gmail.com");
		user.setPhoneNumber("9019106724");
		user.setFullName("Alia Ebaid");
		user.setPassword("hi5");
		user.setMetadata("Age 3, Prek");
		HttpRequests<Users> requests = new HttpRequests<>();
	}
}
