package net.sf.jaceko.mock.it.helper.request;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import net.sf.jaceko.mock.model.MockResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpRequestSender {

	private HttpClient httpclient = new DefaultHttpClient();

	public MockResponse sendPostRequest(String url, String requestBody)
			throws UnsupportedEncodingException, IOException,
			ClientProtocolException {
		HttpEntityEnclosingRequestBase httpRequest = new HttpPost(url);
		addRequestBody(httpRequest, requestBody);

		return executeRequest(httpRequest);
	}

	public String sendPutRequest(String url, String requestBody)
			throws UnsupportedEncodingException, IOException,
			ClientProtocolException {
		HttpEntityEnclosingRequestBase httpRequest = new HttpPut(url);
		addRequestBody(httpRequest, requestBody);

		return executeRequest(httpRequest).getBody();
	}

	private void addRequestBody(HttpEntityEnclosingRequestBase httpRequest,
			String requestBody) throws UnsupportedEncodingException {
		httpRequest.setHeader("Content-Type", "text/xml;charset=UTF-8");
		HttpEntity requestEntity = new StringEntity(requestBody);

		httpRequest.setEntity(requestEntity);
	}

	public MockResponse sendGetRequest(String url) throws IOException,
			ClientProtocolException {
		HttpGet httpGet = new HttpGet(url);
		return executeRequest(httpGet);
	}

	private MockResponse executeRequest(HttpRequestBase httpRequest)
			throws IOException, ClientProtocolException {
		HttpResponse response = httpclient.execute(httpRequest);
		HttpEntity entity = response.getEntity();
		String body = null;
		if (entity != null) {
			body = EntityUtils.toString(entity);
			entity.getContent().close();
		}
		return new MockResponse(body, response.getStatusLine().getStatusCode());
	}

	public void setHttpclient(HttpClient httpclient) {
		this.httpclient = httpclient;
	}

}
