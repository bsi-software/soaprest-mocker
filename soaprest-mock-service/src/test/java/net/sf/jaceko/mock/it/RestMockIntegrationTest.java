package net.sf.jaceko.mock.it;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import net.sf.jaceko.mock.it.helper.dom.DocumentImpl;
import net.sf.jaceko.mock.it.helper.request.HttpRequestSender;

import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class RestMockIntegrationTest {

	// mocked endpoints configured in ws-mock.properties
	public static String REST_MOCK_ENDPOINT = "http://localhost:8080/mock/endpoint/rest/dummy-rest";

	HttpRequestSender requestSender = new HttpRequestSender();

	@Test
	public void shouldGetDefaultRESTGetResponse()
			throws ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		String response = requestSender.sendGetRequest(REST_MOCK_ENDPOINT);
		Document serviceResponseDoc = new DocumentImpl(response);
		assertThat(
				serviceResponseDoc,
				hasXPath("//get_response_data",
						equalTo("dummy REST GET response text")));

	}

}
