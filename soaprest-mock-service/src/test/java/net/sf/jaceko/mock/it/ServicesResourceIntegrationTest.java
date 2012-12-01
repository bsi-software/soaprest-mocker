package net.sf.jaceko.mock.it;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import net.sf.jaceko.mock.dom.DocumentImpl;
import net.sf.jaceko.mock.it.helper.request.HttpRequestSender;
import net.sf.jaceko.mock.model.request.MockResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


public class ServicesResourceIntegrationTest {
	private static final String SERVICES = "http://localhost:8080/mock/services";

	HttpRequestSender requestSender = new HttpRequestSender();

	@Test
	public void shouldGetServicesInformation() throws ClientProtocolException, IOException, ParserConfigurationException,
			SAXException {
		MockResponse response = requestSender.sendGetRequest(SERVICES);
		assertThat(response.getCode(), is(HttpStatus.SC_OK));
		Document serviceResponseDoc = new DocumentImpl(response.getBody());

		assertThat(serviceResponseDoc, hasXPath("//services/service[@name='hello-soap']"));
		assertThat(serviceResponseDoc, hasXPath("//services/service[@name='hello-soap-withwsdl']"));
		assertThat(serviceResponseDoc, hasXPath("//services/service[@name='dummy-rest']"));
		assertThat(serviceResponseDoc, hasXPath("//services/service[@name='dummy-rest-notauthorized']"));
		
		assertThat(serviceResponseDoc, hasXPath("count(//services/service[@type='SOAP'])", equalTo("2")));
		assertThat(serviceResponseDoc, hasXPath("count(//services/service[@type='REST'])", equalTo("2")));
		
		assertThat(serviceResponseDoc, hasXPath("//services/service[@name='hello-soap']/operations/operation-ref/@name", equalTo("sayHello")));
		assertThat(serviceResponseDoc, hasXPath("//services/service[@name='hello-soap']/operations/operation-ref/@uri", equalTo("http://localhost:8080/mock/services/SOAP/hello-soap/operations/sayHello")));


		
		
		assertThat(serviceResponseDoc, hasXPath("//services/service[@name='dummy-rest']/operations/operation-ref[@name='GET']"));
		assertThat(serviceResponseDoc, hasXPath("//services/service[@name='dummy-rest']/operations/operation-ref[@uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/GET']"));
		
		assertThat(serviceResponseDoc, hasXPath("//services/service[@name='dummy-rest']/operations/operation-ref[@name='PUT']"));
		assertThat(serviceResponseDoc, hasXPath("//services/service[@name='dummy-rest']/operations/operation-ref[@uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/PUT']"));
		
		assertThat(serviceResponseDoc, hasXPath("//services/service[@name='dummy-rest']/operations/operation-ref[@name='POST']"));
		assertThat(serviceResponseDoc, hasXPath("//services/service[@name='dummy-rest']/operations/operation-ref[@uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/POST']"));
		
		assertThat(serviceResponseDoc, hasXPath("//services/service[@name='dummy-rest']/operations/operation-ref[@name='DELETE']"));
		assertThat(serviceResponseDoc, hasXPath("//services/service[@name='dummy-rest']/operations/operation-ref[@uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/DELETE']"));

	}

}
