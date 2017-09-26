package com.stashinvest.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ProtocolException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stashinvest.rest.AccountKeyServiceRequest;
import com.stashinvest.rest.AccountKeyServiceResponse;

//
public class AccountKeyRequest {

	public static enum HTTPRequestMethod {
		GET, POST;
	}

	public static final String ACCOUNT_KEY_ENDPOINT = "/v1/account";
	public static final String HTTPS_PROTOCOL = "https";
	public static final int HTTPS_PORT = 443;
	public static final String ACCOUNT_KEY_SERVICE_HOST = "account-key-service.herokuapp.com";
	public static final String USER_AGENT = "Mozilla/5.0";
	public static final String JSON_MEDIA_TYPE = "application/json";

	/**
	 * @param request
	 *            AccountKeyServiceRequest that contains user email and
	 *            generated key
	 * @return AccountKeyServiceResponse containing account_key
	 * @throws IOException
	 */
	public static AccountKeyServiceResponse post(
			AccountKeyServiceRequest request) throws IOException {

		URL url = new URL(HTTPS_PROTOCOL, ACCOUNT_KEY_SERVICE_HOST, HTTPS_PORT,
				ACCOUNT_KEY_ENDPOINT);
		HttpsURLConnection connection = (HttpsURLConnection) url
				.openConnection();
		setConnectionParameters(connection);
		Gson gson = new Gson();
		String requestJson = gson.toJson(request);
		connection.setFixedLengthStreamingMode(requestJson.getBytes().length);
		try (OutputStreamWriter out = new OutputStreamWriter(
				connection.getOutputStream())) {
			out.write(requestJson);
		}
		gson = new GsonBuilder().setFieldNamingPolicy(
				FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
		StringBuilder sb = new StringBuilder();
		try (BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()))) {
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				sb.append(inputLine);
			}
		}
		return gson.fromJson(sb.toString(), AccountKeyServiceResponse.class);
	}

	private static void setConnectionParameters(HttpsURLConnection connection)
			throws ProtocolException {
		connection.setRequestMethod(HTTPRequestMethod.POST.toString());
		connection.setRequestProperty("User-Agent", "Mozilla/5.0");
		connection.setRequestProperty("Content-Type", JSON_MEDIA_TYPE);
		connection.setRequestProperty("Accept", JSON_MEDIA_TYPE);
		connection.setDoOutput(true);
	}
}
