package net.sf.jaceko.mock.it;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.ws.rs.core.MediaType;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.jaceko.mock.it.helper.request.HttpRequestSender;
import net.sf.jaceko.mock.model.request.MockResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Integration tests of REST mock returning json default response
 * 
 * @author Jacek Obarymski
 * 
 */
public class RestMockGETJsonIntegrationTest {

	// mocked endpoints configured in ws-mock.properties
	private static final String REST_MOCK_ENDPOINT = "http://localhost:8080/mock/services/REST/dummy-rest-json/endpoint";

	private static final String REST_MOCK_GET_INIT = "http://localhost:8080/mock/services/REST/dummy-rest-json/operations/GET/init";

	HttpRequestSender requestSender = new HttpRequestSender();

	@Before
	public void initMock() throws UnsupportedEncodingException, ClientProtocolException, IOException {
		// initalizing mock, clearing history of previous requests
		requestSender.sendPostRequest(REST_MOCK_GET_INIT, "", MediaType.TEXT_XML);
	}
	
	// default json response defined in ws-mock.properties
	@Test
	public void shouldReturnDefaultJsonResponse() throws ClientProtocolException, IOException, ParserConfigurationException,
			SAXException {
		MockResponse response = requestSender.sendGetRequest(REST_MOCK_ENDPOINT);
		assertThat(response.getCode(), is(HttpStatus.SC_OK));
		assertThat(response.getContentType(), is(APPLICATION_JSON_TYPE));
		assertThat(response.getBody(), sameJSONAs("{'myArray': [{ 'name': 'John Doe', 'age': 29 },{ 'name': 'Anna Smith', 'age': 24 }]}"));
	}
	
}
