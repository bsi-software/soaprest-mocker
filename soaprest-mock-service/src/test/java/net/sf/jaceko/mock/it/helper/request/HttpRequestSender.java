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

import net.sf.jaceko.mock.model.request.MockResponse;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestSender {

	private HttpClient httpclient = new DefaultHttpClient();


    public MockResponse sendPostRequest(String url, String requestBody, String mediaType) throws UnsupportedEncodingException,
            IOException, ClientProtocolException {
        return sendPostRequest(url, requestBody, mediaType, new HashMap<String, String>());
    }

	public MockResponse sendPostRequest(String url, String requestBody, String mediaType, Map<String, String> headers) throws UnsupportedEncodingException,
			IOException, ClientProtocolException {
		HttpEntityEnclosingRequestBase httpRequest = new HttpPost(url);
		addRequestBody(httpRequest, requestBody, mediaType);

		return executeRequest(httpRequest, headers);
	}

    public MockResponse sendPutRequest(String url, String requestBody, String mediaType) throws UnsupportedEncodingException,
            IOException, ClientProtocolException {

        return sendPutRequest(url, requestBody, mediaType, new HashMap<String, String>());
    }

	public MockResponse sendPutRequest(String url, String requestBody, String mediaType, Map<String, String> headers) throws UnsupportedEncodingException,
			IOException, ClientProtocolException {
		HttpEntityEnclosingRequestBase httpRequest = new HttpPut(url);
		addRequestBody(httpRequest, requestBody, mediaType);
		return executeRequest(httpRequest, headers);
	}

	private void addRequestBody(HttpEntityEnclosingRequestBase httpRequest, String requestBody, String mediaType)
			throws UnsupportedEncodingException {
		StringBuilder contentType = new StringBuilder();
		contentType.append(mediaType);
		contentType.append(";charset=UTF-8");
		httpRequest.setHeader("Content-Type", contentType.toString());
		if (requestBody != null) {
			HttpEntity requestEntity = new StringEntity(requestBody);
			httpRequest.setEntity(requestEntity);
		}
	}

	public MockResponse sendGetRequest(String url, Map<String, String> headers) throws IOException, ClientProtocolException {
		HttpGet httpGet = new HttpGet(url);
		return executeRequest(httpGet, headers);
	}

    public MockResponse sendGetRequest(String url) throws IOException, ClientProtocolException {
        HttpGet httpGet = new HttpGet(url);
        return executeRequest(httpGet, new HashMap<String, String>());
    }

    private MockResponse executeRequest(HttpRequestBase httpRequest, Map<String, String> headers) throws IOException {
        for (String headername : headers.keySet()) {
            httpRequest.addHeader(headername, headers.get(headername));
        }
        return executeRequest(httpRequest);
    }

    public MockResponse sendDeleteRequest(String url) throws ClientProtocolException, IOException {
		HttpDelete httpDelete = new HttpDelete(url);
		return executeRequest(httpDelete, new HashMap<String, String>());
	}

    public MockResponse sendDeleteRequest(String url, Map<String, String> headers) throws ClientProtocolException, IOException {
		HttpDelete httpDelete = new HttpDelete(url);
		return executeRequest(httpDelete, headers);
	}

	private MockResponse executeRequest(HttpRequestBase httpRequest) throws IOException, ClientProtocolException {
		HttpResponse response = httpclient.execute(httpRequest);
		HttpEntity entity = response.getEntity();
		ContentType contentType = ContentType.getOrDefault(entity);
		String body = null;
		if (entity != null) {
			body = EntityUtils.toString(entity);
			entity.getContent().close();
		}
        Map<String, String> headers = new HashMap<String, String>();
        Header[] allHeaders = response.getAllHeaders();
        for (Header header : allHeaders) {
            headers.put(header.getName(), header.getValue());
        }
        int responseCode = response.getStatusLine().getStatusCode();
		return MockResponse.body(body).code(responseCode).contentType(MediaType.valueOf(contentType.getMimeType())).headers(headers).build();
	}

	public void setHttpclient(HttpClient httpclient) {
		this.httpclient = httpclient;
	}

}
