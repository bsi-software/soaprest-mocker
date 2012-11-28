/**
 *
 *     Copyright (C) 2012 Jacek Obarymski
 *
 *     This file is part of SOAP/REST Mock Service.
 *
 *     SOAP/REST Mock Service is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License, version 3
 *     as published by the Free Software Foundation.
 *
 *     SOAP/REST Mock Service is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with SOAP/REST Mock Service; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.sf.jaceko.mock.it.helper.request;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import net.sf.jaceko.mock.model.request.MockResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
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

	public MockResponse sendPostRequest(String url, String requestBody, String mediaType) throws UnsupportedEncodingException, IOException,
			ClientProtocolException {
		HttpEntityEnclosingRequestBase httpRequest = new HttpPost(url);
		addRequestBody(httpRequest, requestBody, mediaType);

		return executeRequest(httpRequest);
	}

	public MockResponse sendPutRequest(String url, String requestBody, String mediaType) throws UnsupportedEncodingException, IOException,
			ClientProtocolException {
		HttpEntityEnclosingRequestBase httpRequest = new HttpPut(url);
		addRequestBody(httpRequest, requestBody, mediaType);
		return executeRequest(httpRequest);
	}

	private void addRequestBody(HttpEntityEnclosingRequestBase httpRequest, String requestBody, String mediaType)
			throws UnsupportedEncodingException {
		StringBuilder contenType = new StringBuilder();
				contenType.append(mediaType);
				contenType.append(";charset=UTF-8");
		httpRequest.setHeader("Content-Type", contenType.toString());
		HttpEntity requestEntity = new StringEntity(requestBody);
		httpRequest.setEntity(requestEntity);
	}

	public MockResponse sendGetRequest(String url) throws IOException, ClientProtocolException {
		HttpGet httpGet = new HttpGet(url);
		return executeRequest(httpGet);
	}

	public MockResponse sendDeleteRequest(String url) throws ClientProtocolException, IOException {
		HttpDelete httpDelete = new HttpDelete(url);
		return executeRequest(httpDelete);
	}

	private MockResponse executeRequest(HttpRequestBase httpRequest) throws IOException, ClientProtocolException {
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
