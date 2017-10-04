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

import org.apache.log4j.Logger;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

// A generic class for handling http/https get/post requests.
public class HttpRequests<T> {
    private HttpURLConnection httpConnection;
    private HttpsURLConnection httpsConnection;

    public static enum HttpRequestMethod {
	GET, POST;
    }

    private static final Logger log = Logger.getLogger(HttpRequests.class);
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
	httpsConnection = (HttpsURLConnection) url.openConnection();
	httpsConnection.setRequestMethod(HttpRequestMethod.POST.toString());
	setConnectionParameters(httpsConnection, HttpRequestMethod.POST);

	httpsConnection.setFixedLengthStreamingMode(jsonBody.getBytes().length);
	try (OutputStreamWriter out = new OutputStreamWriter(
		httpsConnection.getOutputStream())) {
	    out.write(jsonBody);
	}
	StringBuilder sb = new StringBuilder();
	try (BufferedReader in = new BufferedReader(new InputStreamReader(
		httpsConnection.getInputStream()))) {
	    String inputLine;
	    while ((inputLine = in.readLine()) != null) {
		sb.append(inputLine);
	    }
	}
	// setFieldNamingPolicy is used to here to convert from
	// lower_case_with_underscore names retrieved from end point to camel
	// case to match POJO class
	Gson gson = new GsonBuilder().setFieldNamingPolicy(
		FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
	return gson.fromJson(sb.toString(), clazz);
    }

    public T httpPost(String jsonBody, Class<T> clazz) {
	try {
	    URL url = new URL(HTTP_PROTOCOL, LOCAL_HOST, HTTP_PORT,
		    USERS_SERVICE_ENDPOINT);
	    httpConnection = (HttpURLConnection) url.openConnection();
	    httpConnection.setRequestMethod(HttpRequestMethod.POST.toString());
	    setConnectionParameters(httpConnection, HttpRequestMethod.POST);
	    httpConnection
		    .setFixedLengthStreamingMode(jsonBody.getBytes().length);
	    try (OutputStreamWriter out = new OutputStreamWriter(
		    httpConnection.getOutputStream())) {
		out.write(jsonBody);
	    }
	    StringBuilder sb = new StringBuilder();
	    try (BufferedReader in = new BufferedReader(new InputStreamReader(
		    httpConnection.getInputStream()))) {
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
		    sb.append(inputLine);
		}
	    }
	    return new Gson().fromJson(sb.toString(), clazz);
	} catch (IOException e) {
	    log.error(e);
	}
	return null;
    }

    public T httpGet(String query, Class<T> clazz) {
	try {
	    URL url = null;
	    if (query == null) {
		url = new URL(HTTP_PROTOCOL, LOCAL_HOST, HTTP_PORT,
			USERS_SERVICE_ENDPOINT);
	    } else {
		url = new URL(HTTP_PROTOCOL, LOCAL_HOST, HTTP_PORT,
			USERS_SERVICE_ENDPOINT + query);
	    }
	    httpConnection = (HttpURLConnection) url.openConnection();
	    httpConnection.setRequestMethod(HttpRequestMethod.GET.toString());
	    setConnectionParameters(httpConnection, HttpRequestMethod.GET);
	    Gson gson = new Gson();
	    StringBuilder sb = new StringBuilder();
	    try (BufferedReader in = new BufferedReader(new InputStreamReader(
		    httpConnection.getInputStream()))) {
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
		    sb.append(inputLine);
		}
	    }
	    return gson.fromJson(sb.toString(), clazz);
	} catch (IOException e) {
	    log.error(e);
	}
	return null;
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

    public HttpURLConnection getHttpConnection() {
	return httpConnection;
    }

    public HttpsURLConnection getHttpsConnection() {
	return httpsConnection;
    }
}
