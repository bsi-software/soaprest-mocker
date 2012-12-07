package net.sf.jaceko.mock.it;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.text.MessageFormat;

import javax.xml.parsers.ParserConfigurationException;

import net.sf.jaceko.mock.dom.DocumentImpl;
import net.sf.jaceko.mock.it.helper.request.HttpRequestSender;
import net.sf.jaceko.mock.model.request.MockResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Interation tests of the services resource returning information about configured webservice mocks. 
 * Check the ws-mock.properties file for mock configuration
 * 
 * To run tests in eclipse start server typing executing mvn jetty:run
 *
 */
public class ServicesResourceIntegrationTest {
	private static final String SERVICES = "http://localhost:8080/mock/services";

	private static final String OPERATIONS = "http://localhost:8080/mock/services/{0}/{1}/operations/{2}";
	HttpRequestSender requestSender = new HttpRequestSender();

	@Test
	public void shouldReturnServicesInformation() throws ClientProtocolException, IOException, ParserConfigurationException,
			SAXException {
		MockResponse response = requestSender.sendGetRequest(SERVICES);
		assertThat(response.getCode(), is(HttpStatus.SC_OK));
		Document serviceResponseDoc = new DocumentImpl(response.getBody());

		assertThat(serviceResponseDoc, hasXPath("//services/service[@name='hello-soap']"));
		assertThat(serviceResponseDoc, hasXPath("//services/service[@name='hello-soap-withwsdl']"));
		assertThat(serviceResponseDoc, hasXPath("//services/service[@name='dummy-rest']"));
		assertThat(serviceResponseDoc, hasXPath("//services/service[@name='dummy-rest-notauthorized']"));

		assertThat(serviceResponseDoc, hasXPath("count(//services/service[@type='SOAP'])", equalTo("3")));
		assertThat(serviceResponseDoc, hasXPath("count(//services/service[@type='REST'])", equalTo("2")));

		assertThat(serviceResponseDoc,
				hasXPath("//services/service[@name='hello-soap']/operations/operation-ref/@name", equalTo("sayHello")));
		assertThat(
				serviceResponseDoc,
				hasXPath("//services/service[@name='hello-soap']/operations/operation-ref/@uri",
						equalTo("http://localhost:8080/mock/services/SOAP/hello-soap/operations/sayHello")));

		assertThat(
				serviceResponseDoc,
				hasXPath("//services/service[@name='dummy-rest']/operations/operation-ref[@name='GET' and @uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/GET']"));

		assertThat(
				serviceResponseDoc,
				hasXPath("//services/service[@name='dummy-rest']/operations/operation-ref[@name='PUT' and @uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/PUT']"));

		assertThat(
				serviceResponseDoc,
				hasXPath("//services/service[@name='dummy-rest']/operations/operation-ref[@name='POST' and @uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/POST']"));

		assertThat(
				serviceResponseDoc,
				hasXPath("//services/service[@name='dummy-rest']/operations/operation-ref[@name='DELETE' and @uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/DELETE']"));

	}

	@Test
	public void shouldReturnSOAPOperationDetails() throws ClientProtocolException, IOException, ParserConfigurationException,
			SAXException {
		String serviceType = "SOAP";
		String serviceName = "hello-soap";
		String operationName = "sayHello";
		MockResponse response = requestSender.sendGetRequest(MessageFormat.format(OPERATIONS, serviceType, serviceName,
				operationName));
		assertThat(response.getCode(), is(HttpStatus.SC_OK));
		Document serviceResponseDoc = new DocumentImpl(response.getBody());

		assertThat(serviceResponseDoc, hasXPath("//operation[@name='sayHello']"));

		assertThat(
				serviceResponseDoc,
				hasXPath("//operation/setup-resources/resource-ref[@description='operation initialization' and @http-method='POST' and @uri='http://localhost:8080/mock/services/SOAP/hello-soap/operations/sayHello/init']"));
		assertThat(
				serviceResponseDoc,
				hasXPath("//operation/setup-resources/resource-ref[@description='add custom response' and @http-method='POST' and @uri='http://localhost:8080/mock/services/SOAP/hello-soap/operations/sayHello/responses']"));

		assertThat(
				serviceResponseDoc,
				hasXPath("//operation/setup-resources/resource-ref[@description='set first custom response' and @http-method='PUT' and @uri='http://localhost:8080/mock/services/SOAP/hello-soap/operations/sayHello/responses/1']"));

		assertThat(
				serviceResponseDoc,
				hasXPath("//operation/setup-resources/resource-ref[@description='set second custom response' and @http-method='PUT' and @uri='http://localhost:8080/mock/services/SOAP/hello-soap/operations/sayHello/responses/2']"));
		assertThat(serviceResponseDoc, hasXPath("count(//operation/setup-resources/resource-ref)", equalTo("4")));

		assertThat(
				serviceResponseDoc,
				hasXPath("//operation/verification-resources/resource-ref[@description='recorded requests' and @http-method='GET' and @uri='http://localhost:8080/mock/services/SOAP/hello-soap/operations/sayHello/recorded-requests']"));

		assertThat(serviceResponseDoc, hasXPath("count(//operation/verification-resources/resource-ref)", equalTo("1")));
	}

	@Test
	public void shouldReturnREST_GET_OperationDetails() throws ClientProtocolException, IOException,
			ParserConfigurationException, SAXException {
		String serviceType = "REST";
		String serviceName = "dummy-rest";
		String operationName = "GET";
		MockResponse response = requestSender.sendGetRequest(MessageFormat.format(OPERATIONS, serviceType, serviceName,
				operationName));
		assertThat(response.getCode(), is(HttpStatus.SC_OK));
		Document serviceResponseDoc = new DocumentImpl(response.getBody());

		assertThat(serviceResponseDoc, hasXPath("//operation[@name='GET']"));

		assertThat(
				serviceResponseDoc,
				hasXPath("//operation/setup-resources/resource-ref[@description='operation initialization' and @http-method='POST' and @uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/GET/init']"));
		assertThat(
				serviceResponseDoc,
				hasXPath("//operation/setup-resources/resource-ref[@description='add custom response' and @http-method='POST' and @uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/GET/responses']"));

		assertThat(
				serviceResponseDoc,
				hasXPath("//operation/setup-resources/resource-ref[@description='set first custom response' and @http-method='PUT' and @uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/GET/responses/1']"));

		assertThat(
				serviceResponseDoc,
				hasXPath("//operation/setup-resources/resource-ref[@description='set second custom response' and @http-method='PUT' and @uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/GET/responses/2']"));

		assertThat(serviceResponseDoc, hasXPath("count(//operation/setup-resources/resource-ref)", equalTo("4")));
		assertThat(
				serviceResponseDoc,
				hasXPath("//operation/verification-resources/resource-ref[@description='recorded requests' and @http-method='GET' and @uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/GET/recorded-requests']"));

		assertThat(
				serviceResponseDoc,
				hasXPath("//operation/verification-resources/resource-ref[@description='recorded resource ids' and @http-method='GET' and @uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/GET/recorded-resource-ids']"));

		assertThat(
				serviceResponseDoc,
				hasXPath("//operation/verification-resources/resource-ref[@description='recorded request parameters' and @http-method='GET' and @uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/GET/recorded-request-params']"));

		assertThat(serviceResponseDoc, hasXPath("count(//operation/verification-resources/resource-ref)", equalTo("3")));

	}

	@Test
	public void shouldReturnREST_PUT_OperationDetails() throws ClientProtocolException, IOException,
			ParserConfigurationException, SAXException {
		String serviceType = "REST";
		String serviceName = "dummy-rest";
		String operationName = "PUT";
		MockResponse response = requestSender.sendGetRequest(MessageFormat.format(OPERATIONS, serviceType, serviceName,
				operationName));
		assertThat(response.getCode(), is(HttpStatus.SC_OK));
		Document serviceResponseDoc = new DocumentImpl(response.getBody());

		assertThat(serviceResponseDoc, hasXPath("//operation[@name='PUT']"));

		assertThat(
				serviceResponseDoc,
				hasXPath("//operation/setup-resources/resource-ref[@description='operation initialization' and @http-method='POST' and @uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/PUT/init']"));
		assertThat(
				serviceResponseDoc,
				hasXPath("//operation/setup-resources/resource-ref[@description='add custom response' and @http-method='POST' and @uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/PUT/responses']"));

		assertThat(
				serviceResponseDoc,
				hasXPath("//operation/setup-resources/resource-ref[@description='set first custom response' and @http-method='PUT' and @uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/PUT/responses/1']"));

		assertThat(
				serviceResponseDoc,
				hasXPath("//operation/setup-resources/resource-ref[@description='set second custom response' and @http-method='PUT' and @uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/PUT/responses/2']"));

		assertThat(serviceResponseDoc, hasXPath("count(//operation/setup-resources/resource-ref)", equalTo("4")));

		assertThat(
				serviceResponseDoc,
				hasXPath("//operation/verification-resources/resource-ref[@description='recorded requests' and @http-method='GET' and @uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/PUT/recorded-requests']"));

		assertThat(
				serviceResponseDoc,
				hasXPath("//operation/verification-resources/resource-ref[@description='recorded resource ids' and @http-method='GET' and @uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/PUT/recorded-resource-ids']"));

		assertThat(
				serviceResponseDoc,
				hasXPath("//operation/verification-resources/resource-ref[@description='recorded request parameters' and @http-method='GET' and @uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/PUT/recorded-request-params']"));

		assertThat(serviceResponseDoc, hasXPath("count(//operation/verification-resources/resource-ref)", equalTo("3")));

	}
	
	@Test
	public void shouldReturnREST_DELETE_OperationDetails() throws ClientProtocolException, IOException,
			ParserConfigurationException, SAXException {
		String serviceType = "REST";
		String serviceName = "dummy-rest";
		String operationName = "DELETE";
		MockResponse response = requestSender.sendGetRequest(MessageFormat.format(OPERATIONS, serviceType, serviceName,
				operationName));
		assertThat(response.getCode(), is(HttpStatus.SC_OK));
		Document serviceResponseDoc = new DocumentImpl(response.getBody());

		assertThat(serviceResponseDoc, hasXPath("//operation[@name='DELETE']"));

		assertThat(
				serviceResponseDoc,
				hasXPath("//operation/setup-resources/resource-ref[@description='operation initialization' and @http-method='POST' and @uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/DELETE/init']"));
		assertThat(
				serviceResponseDoc,
				hasXPath("//operation/setup-resources/resource-ref[@description='add custom response' and @http-method='POST' and @uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/DELETE/responses']"));

		assertThat(
				serviceResponseDoc,
				hasXPath("//operation/setup-resources/resource-ref[@description='set first custom response' and @http-method='PUT' and @uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/DELETE/responses/1']"));

		assertThat(
				serviceResponseDoc,
				hasXPath("//operation/setup-resources/resource-ref[@description='set second custom response' and @http-method='PUT' and @uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/DELETE/responses/2']"));

		assertThat(serviceResponseDoc, hasXPath("count(//operation/setup-resources/resource-ref)", equalTo("4")));

		assertThat(
				serviceResponseDoc,
				hasXPath("//operation/verification-resources/resource-ref[@description='recorded requests' and @http-method='GET' and @uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/DELETE/recorded-requests']"));

		assertThat(
				serviceResponseDoc,
				hasXPath("//operation/verification-resources/resource-ref[@description='recorded resource ids' and @http-method='GET' and @uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/DELETE/recorded-resource-ids']"));

		assertThat(
				serviceResponseDoc,
				hasXPath("//operation/verification-resources/resource-ref[@description='recorded request parameters' and @http-method='GET' and @uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/DELETE/recorded-request-params']"));

		assertThat(serviceResponseDoc, hasXPath("count(//operation/verification-resources/resource-ref)", equalTo("3")));

	}
	
	@Test
	public void shouldReturnREST_POST_OperationDetails() throws ClientProtocolException, IOException,
			ParserConfigurationException, SAXException {
		String serviceType = "REST";
		String serviceName = "dummy-rest";
		String operationName = "POST";
		MockResponse response = requestSender.sendGetRequest(MessageFormat.format(OPERATIONS, serviceType, serviceName,
				operationName));
		assertThat(response.getCode(), is(HttpStatus.SC_OK));
		Document serviceResponseDoc = new DocumentImpl(response.getBody());

		assertThat(serviceResponseDoc, hasXPath("//operation[@name='POST']"));

		assertThat(
				serviceResponseDoc,
				hasXPath("//operation/setup-resources/resource-ref[@description='operation initialization' and @http-method='POST' and @uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/POST/init']"));
		assertThat(
				serviceResponseDoc,
				hasXPath("//operation/setup-resources/resource-ref[@description='add custom response' and @http-method='POST' and @uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/POST/responses']"));

		assertThat(
				serviceResponseDoc,
				hasXPath("//operation/setup-resources/resource-ref[@description='set first custom response' and @http-method='PUT' and @uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/POST/responses/1']"));

		assertThat(
				serviceResponseDoc,
				hasXPath("//operation/setup-resources/resource-ref[@description='set second custom response' and @http-method='PUT' and @uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/POST/responses/2']"));

		assertThat(serviceResponseDoc, hasXPath("count(//operation/setup-resources/resource-ref)", equalTo("4")));

		assertThat(
				serviceResponseDoc,
				hasXPath("//operation/verification-resources/resource-ref[@description='recorded requests' and @http-method='GET' and @uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/POST/recorded-requests']"));

		assertThat(
				serviceResponseDoc,
				hasXPath("//operation/verification-resources/resource-ref[@description='recorded request parameters' and @http-method='GET' and @uri='http://localhost:8080/mock/services/REST/dummy-rest/operations/POST/recorded-request-params']"));

		assertThat(serviceResponseDoc, hasXPath("count(//operation/verification-resources/resource-ref)", equalTo("2")));

	}
	


	
	
	@Test
	public void shouldReturn404IfWrongOperationType() throws ClientProtocolException, IOException {
		String serviceName = "hello-soap";
		String operationName = "sayHello";
		MockResponse response = requestSender.sendGetRequest(MessageFormat.format(OPERATIONS, "SOAP123", serviceName,
				operationName));
		assertThat(response.getCode(), is(HttpStatus.SC_NOT_FOUND));
	}

	@Test
	public void shouldReturn404IfServiceNotFound() throws ClientProtocolException, IOException {
		String serviceName = "not-existing";
		String operationName = "sayHello";
		MockResponse response = requestSender
				.sendGetRequest(MessageFormat.format(OPERATIONS, "SOAP", serviceName, operationName));
		assertThat(response.getCode(), is(HttpStatus.SC_NOT_FOUND));

	}

	@Test
	public void shouldReturn404IfOperationNotFound() throws ClientProtocolException, IOException {
		String serviceName = "hello-soap";
		String operationName = "sayHello";
		MockResponse response = requestSender.sendGetRequest(MessageFormat.format(OPERATIONS, "SOAP123", serviceName,
				operationName));
		assertThat(response.getCode(), is(HttpStatus.SC_NOT_FOUND));
	}

}
