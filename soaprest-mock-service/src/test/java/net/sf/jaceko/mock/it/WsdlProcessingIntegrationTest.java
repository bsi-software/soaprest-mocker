package net.sf.jaceko.mock.it;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import net.sf.jaceko.mock.dom.DocumentImpl;
import net.sf.jaceko.mock.it.helper.request.HttpRequestSender;
import net.sf.jaceko.mock.model.request.MockResponse;

public class WsdlProcessingIntegrationTest {

	private static final String SERVICES = "http://localhost:8080/mock/services";
	HttpRequestSender requestSender = new HttpRequestSender();
	
	@Test
	public void shouldFetchSayHelloOperationFromWsdlFile() throws ClientProtocolException, IOException, ParserConfigurationException,
			SAXException {
		MockResponse response = requestSender.sendGetRequest(SERVICES);
		assertThat(response.getCode(), is(HttpStatus.SC_OK));
		Document serviceResponseDoc = new DocumentImpl(response.getBody());
		
		assertThat(serviceResponseDoc,
				hasXPath("//services/service[@name='hello-soap-withwsdl']/operations/operation-ref/@name", equalTo("sayHello")));

	
	}


}
