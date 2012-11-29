package net.sf.jaceko.mock.service;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Collection;

import net.sf.jaceko.mock.model.request.MockResponse;
import net.sf.jaceko.mock.model.webservice.WebService;
import net.sf.jaceko.mock.model.webservice.WebserviceOperation;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class RequestExecutorTest {

	private static final String NOT_USED_RESOURCE_ID = null;
	private static final String NOT_USED_REQUEST_PARAM = null;
	private static final String NOT_USED_REQUEST_BODY = null;

	@Mock
	private MockConfigurationHolder configurationHolder;
	
	@Mock
	private Delayer delayer;

	private RequestExecutor requestExeutor = new RequestExecutor();
	private RecordedRequestsHolder recordedRequestsHolder;

	@Before
	public void init() {
		initMocks(this);
		requestExeutor.setMockserviceConfiguration(configurationHolder);
		recordedRequestsHolder = new RecordedRequestsHolder();
		recordedRequestsHolder.setMockserviceConfiguration(configurationHolder);
		requestExeutor.setRecordedRequestsHolder(recordedRequestsHolder);
		requestExeutor.setDelayer(delayer);
	}

	@Test
	public void shouldReturnDefaultResponse_OK() {

		String serviceName = "mptu";
		String operationId = "prepayRequest";
		String defaultResponse = "<defaultResp>abc</defaultResp>";
		int defaultResponseCode = 200;
		WebserviceOperation operation = new WebserviceOperation(null, null, defaultResponse, defaultResponseCode);

		when(configurationHolder.getWebServiceOperation(serviceName, operationId)).thenReturn(operation);

		MockResponse response = requestExeutor.performRequest(serviceName, operationId, NOT_USED_REQUEST_BODY,
				NOT_USED_REQUEST_PARAM, NOT_USED_RESOURCE_ID);
		assertThat(response.getBody(), is(defaultResponse));
		assertThat(response.getCode(), is(defaultResponseCode));
	}

	@Test
	public void shouldReturnDefaultResponse_NOT_AUTHORIZED() {

		String serviceName = "mptu";
		String operationId = "prepayRequest";
		int defaultResponseCode = 403;
		WebserviceOperation operation = new WebserviceOperation(null, null, null, defaultResponseCode);

		when(configurationHolder.getWebServiceOperation(serviceName, operationId)).thenReturn(operation);

		MockResponse response = requestExeutor.performRequest(serviceName, operationId, NOT_USED_REQUEST_BODY,
				NOT_USED_REQUEST_PARAM, NOT_USED_RESOURCE_ID);
		assertThat(response.getCode(), is(defaultResponseCode));
	}

	@Test
	public void shouldReturnCustomResponseIfSet() {

		String serviceName = "mptu";
		String operationId = "prepayRequest";
		String defaultResponse = "<defaultResp>abc</defaultResp>";
		MockResponse expectedCustomResponse = new MockResponse("<dummyResp/>");

		WebserviceOperation operation = new WebserviceOperation(null, null, defaultResponse, 0);
		operation.setCustomResponse(expectedCustomResponse, 1);

		when(configurationHolder.getWebServiceOperation(serviceName, operationId)).thenReturn(operation);

		MockResponse response = requestExeutor.performRequest(serviceName, operationId, NOT_USED_REQUEST_BODY,
				NOT_USED_REQUEST_PARAM, NOT_USED_RESOURCE_ID);
		assertThat(response, is(expectedCustomResponse));

	}

	@Test
	public void shouldReturnDefaultResponseInSecondCall() {

		String serviceName = "mptu";
		String operationId = "prepayRequest";
		String defaultResponse = "<defaultResp>abc</defaultResp>";
		MockResponse expectedCustomResponse = new MockResponse("<dummyResp/>");

		WebserviceOperation operation = new WebserviceOperation(null, null, defaultResponse, 0);
		operation.setCustomResponse(expectedCustomResponse, 1);
		operation.setCustomResponse(expectedCustomResponse, 1);

		when(configurationHolder.getWebServiceOperation(serviceName, operationId)).thenReturn(operation);

		requestExeutor.performRequest(serviceName, operationId, NOT_USED_REQUEST_BODY, NOT_USED_REQUEST_PARAM,
				NOT_USED_RESOURCE_ID);

		MockResponse response = requestExeutor.performRequest(serviceName, operationId, NOT_USED_REQUEST_BODY,
				NOT_USED_REQUEST_PARAM, NOT_USED_RESOURCE_ID);
		assertThat(response.getBody(), is(defaultResponse));

	}

	@Test
	public void shouldReturnCustomResponseInSecondCall() {

		String serviceName = "mptu";
		String operationId = "prepayRequest";
		String defaultResponse = "<defaultResp>abc</defaultResp>";
		MockResponse expectedCustomResponse1 = new MockResponse("<dummyResp/>");
		MockResponse expectedCustomResponse2 = new MockResponse("<dummyResp2/>");

		WebserviceOperation operation = new WebserviceOperation(null, null, defaultResponse, 0);
		operation.setCustomResponse(expectedCustomResponse1, 1);
		operation.setCustomResponse(expectedCustomResponse2, 2);

		when(configurationHolder.getWebServiceOperation(serviceName, operationId)).thenReturn(operation);

		requestExeutor.performRequest(serviceName, operationId, NOT_USED_REQUEST_BODY, NOT_USED_REQUEST_PARAM,
				NOT_USED_RESOURCE_ID);

		MockResponse response = requestExeutor.performRequest(serviceName, operationId, NOT_USED_REQUEST_BODY,
				NOT_USED_REQUEST_PARAM, NOT_USED_RESOURCE_ID);
		assertThat(response, is(expectedCustomResponse2));

	}

	@Test
	public void shouldReturnDefaultResponseAfterOperationInit() {

		String serviceName = "mptu";
		String operationId = "prepayRequest";

		String defaultResponse = "<defaultResp>abc</defaultResp>";
		WebserviceOperation operation = new WebserviceOperation(null, null, defaultResponse, 0);
		operation.setCustomResponse(new MockResponse("<dummyResp/>"), 1);
		operation.setCustomResponse(new MockResponse("<dummyResp2/>"), 2);

		when(configurationHolder.getWebServiceOperation(serviceName, operationId)).thenReturn(operation);

		operation.init();

		MockResponse response = requestExeutor.performRequest(serviceName, operationId, NOT_USED_REQUEST_BODY,
				NOT_USED_REQUEST_PARAM, NOT_USED_RESOURCE_ID);
		assertThat(response.getBody(), is(defaultResponse));

	}


	@Test
	public void shouldGetWsdlText() {

		String serviceName = "ticketing";
		String expectedWsdlText = "<dummyWsdl></dummyWsdl>";
		WebService soapService = new WebService(serviceName, expectedWsdlText);

		when(configurationHolder.getWebService(serviceName)).thenReturn(soapService);

		String returnedWsdlText = requestExeutor.getWsdl(serviceName);
		assertThat(returnedWsdlText, is(expectedWsdlText));

	}

	@Test
	public void shouldDelayByZeroSecByDefault() {
		when(configurationHolder.getWebServiceOperation(anyString(), anyString())).thenReturn(new WebserviceOperation());
		requestExeutor.performRequest("", "", "", NOT_USED_REQUEST_PARAM, NOT_USED_RESOURCE_ID);

		verify(delayer).delaySec(0);
	}

	@Test
	public void shouldDelayRequest() {
		WebserviceOperation operation = new WebserviceOperation();
		int delaySec = 5;
		MockResponse customResponse = new MockResponse(delaySec);
		operation.setCustomResponse(customResponse, 1);

		when(configurationHolder.getWebServiceOperation(anyString(), anyString())).thenReturn(operation);
		requestExeutor.performRequest("", "", "", NOT_USED_REQUEST_PARAM, NOT_USED_RESOURCE_ID);

		verify(delayer).delaySec(delaySec);
	}
	
	@Test
	public void shouldRecordRequest() {

		String request1 = "<req1/>";
		String serviceName = "mptu";
		String operationId = "prepayRequest";
		when(configurationHolder.getWebServiceOperation(serviceName, operationId)).thenReturn(new WebserviceOperation());
		requestExeutor.performRequest(serviceName, operationId, request1, NOT_USED_REQUEST_PARAM, NOT_USED_RESOURCE_ID);
		Collection<String> recordedRequests = recordedRequestsHolder.getRecordedRequestBodies(serviceName, operationId);

		assertThat(recordedRequests.size(), is(1));
		assertThat(recordedRequests, hasItem(request1));
	}

}