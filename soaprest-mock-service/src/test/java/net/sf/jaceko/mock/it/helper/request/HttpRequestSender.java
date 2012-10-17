package net.sf.jaceko.mock.it.helper.request;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


public class HttpRequestSender {

	private HttpClient httpclient = new DefaultHttpClient();

	public String sendPostRequest(String url, String request) throws UnsupportedEncodingException, IOException,
			ClientProtocolException {
		HttpPost httpRequest = new HttpPost(url);
		httpRequest.setHeader("Content-Type", "text/xml;charset=UTF-8");
		HttpEntity requestEntity = new StringEntity(request);

		httpRequest.setEntity(requestEntity);

		String body = executeRequest(httpRequest);
		return body;
	}
	
	public String sendGetRequest(String url) throws IOException, ClientProtocolException {
		HttpGet httpGet = new HttpGet(url);
		String body = executeRequest(httpGet);
		return body;
	}

	private String executeRequest(HttpRequestBase httpRequest) throws IOException, ClientProtocolException {
		HttpResponse response = httpclient.execute(httpRequest);
		HttpEntity entity = response.getEntity();
		String body = null;
		if (entity != null) {
			body = EntityUtils.toString(entity);
			entity.getContent().close();
		}
		return body;
	}

	public void setHttpclient(HttpClient httpclient) {
		this.httpclient = httpclient;
	}

}
