package net.sf.jaceko.mock.service;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.junit.matchers.JUnitMatchers.hasItems;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Collection;

import net.sf.jaceko.mock.configuration.MockserviceConfiguration;
import net.sf.jaceko.mock.configuration.WebService;
import net.sf.jaceko.mock.configuration.WebserviceOperation;
import net.sf.jaceko.mock.exception.ServiceNotConfiguredException;
import net.sf.jaceko.mock.model.MockResponse;
import net.sf.jaceko.mock.service.DelayService;
import net.sf.jaceko.mock.service.WebserviceMockSvcLayer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class WebserviceMockSvcLayerTest {

	private static String NOT_USED_RESOURCE_ID = null;
	private static String NOT_USED_REQUEST_PARAM = null;
	private static String NOT_USED_REQUEST_BODY = null;

	@Mock
	private MockserviceConfiguration configuration;

	@Mock
	private DelayService delayService;

	private WebserviceMockSvcLayer serviceLayer = new WebserviceMockSvcLayer();

	@Before
	public void init() {
		initMocks(this);

		serviceLayer.setMockserviceConfiguration(configuration);
		serviceLayer.setDelayService(delayService);
	}

	@Test
	public void shouldReturnDefaultResponse_OK() {

		String serviceName = "mptu";
		String operationId = "prepayRequest";
		String defaultResponse = "<defaultResp>abc</defaultResp>";
		int defaultResponseCode = 200;
		WebserviceOperation operation = new WebserviceOperation(null, null, defaultResponse, defaultResponseCode);

		when(configuration.getWebServiceOperation(serviceName, operationId)).thenReturn(operation);

		MockResponse response = serviceLayer.performRequest(serviceName, operationId, NOT_USED_REQUEST_BODY,
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

		when(configuration.getWebServiceOperation(serviceName, operationId)).thenReturn(operation);

		MockResponse response = serviceLayer.performRequest(serviceName, operationId, NOT_USED_REQUEST_BODY,
				NOT_USED_REQUEST_PARAM, NOT_USED_RESOURCE_ID);
		assertThat(response.getCode(), is(defaultResponseCode));
	}

	@Test(expected = ServiceNotConfiguredException.class)
	public void performRequestShouldThrowExceptionIfOperationNotFound() {

		when(configuration.getWebServiceOperation(anyString(), anyString())).thenReturn(null);

		serviceLayer.performRequest("", "", "", NOT_USED_REQUEST_PARAM, NOT_USED_RESOURCE_ID);

	}
	
	@Test
	public void shouldAddSeriesOfResponses() {
		String serviceName = "svc123";
		String operationId = "someRequest";
		MockResponse response1 = new MockResponse("<someResp/>");
		MockResponse response2 = new MockResponse("<someResp2/>");
		WebserviceOperation operation = new WebserviceOperation();

		when(configuration.getWebServiceOperation(serviceName, operationId)).thenReturn(operation);
		serviceLayer.addCustomResponse(serviceName, operationId, response1);
		serviceLayer.addCustomResponse(serviceName, operationId, response2);
		
		assertThat(operation.getResponse(1), is(response1));
		assertThat(operation.getResponse(2), is(response2));

	}

	@Test
	public void shouldSetCustomResponse() {
		String serviceName = "mptu";
		String operationId = "prepayRequest";
		String expectedCustomResponse = "<dummyResp/>";
		WebserviceOperation operation = new WebserviceOperation();
		int requestInOrder = 2;

		when(configuration.getWebServiceOperation(serviceName, operationId)).thenReturn(operation);
		serviceLayer.setCustomResponse(serviceName, operationId, requestInOrder, new MockResponse(expectedCustomResponse));
		MockResponse customResponse = operation.getResponse(requestInOrder);
		assertThat(customResponse.getBody(), is(expectedCustomResponse));

	}

	@Test
	public void shouldResetInvocationNumberWhileSettingUpRequest() {
		String serviceName = "mptu";
		String operationId = "prepayRequest";
		WebserviceOperation operation = new WebserviceOperation();
		int requestInOrder = 2;

		when(configuration.getWebServiceOperation(serviceName, operationId)).thenReturn(operation);
		serviceLayer.setCustomResponse(serviceName, operationId, requestInOrder, new MockResponse("<dummyResp/>"));
		assertThat(operation.getNextInvocationNumber(), is(1));
		assertThat(operation.getNextInvocationNumber(), is(2));

		serviceLayer.setCustomResponse(serviceName, operationId, requestInOrder, new MockResponse("<dummyResp2/>"));
		assertThat(operation.getNextInvocationNumber(), is(1));

	}

	@Test(expected = ServiceNotConfiguredException.class)
	public void setCustomResponseShouldThrowExceptionIfOperationNotFound() {

		when(configuration.getWebServiceOperation(anyString(), anyString())).thenReturn(null);

		serviceLayer.setCustomResponse("", "", 0, new MockResponse(null));

	}

	@Test
	public void shouldReturnCustomResponseIfSet() {

		String serviceName = "mptu";
		String operationId = "prepayRequest";
		String defaultResponse = "<defaultResp>abc</defaultResp>";
		MockResponse expectedCustomResponse = new MockResponse("<dummyResp/>");

		WebserviceOperation operation = new WebserviceOperation(null, null, defaultResponse, 0);
		operation.setCustomResponse(expectedCustomResponse, 1);

		when(configuration.getWebServiceOperation(serviceName, operationId)).thenReturn(operation);

		MockResponse response = serviceLayer.performRequest(serviceName, operationId, NOT_USED_REQUEST_BODY,
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

		when(configuration.getWebServiceOperation(serviceName, operationId)).thenReturn(operation);

		serviceLayer
				.performRequest(serviceName, operationId, NOT_USED_REQUEST_BODY, NOT_USED_REQUEST_PARAM, NOT_USED_RESOURCE_ID);

		MockResponse response = serviceLayer.performRequest(serviceName, operationId, NOT_USED_REQUEST_BODY,
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

		when(configuration.getWebServiceOperation(serviceName, operationId)).thenReturn(operation);

		serviceLayer
				.performRequest(serviceName, operationId, NOT_USED_REQUEST_BODY, NOT_USED_REQUEST_PARAM, NOT_USED_RESOURCE_ID);

		MockResponse response = serviceLayer.performRequest(serviceName, operationId, NOT_USED_REQUEST_BODY,
				NOT_USED_REQUEST_PARAM, NOT_USED_RESOURCE_ID);
		assertThat(response, is(expectedCustomResponse2));

	}

	@Test
	public void shouldReturnDefaultResponseAfterMockInit() {

		String serviceName = "mptu";
		String operationId = "prepayRequest";

		String defaultResponse = "<defaultResp>abc</defaultResp>";
		WebserviceOperation operation = new WebserviceOperation(null, null, defaultResponse, 0);
		operation.setCustomResponse(new MockResponse("<dummyResp/>"), 1);
		operation.setCustomResponse(new MockResponse("<dummyResp2/>"), 2);

		when(configuration.getWebServiceOperation(serviceName, operationId)).thenReturn(operation);

		serviceLayer.initMock(serviceName, operationId);

		MockResponse response = serviceLayer.performRequest(serviceName, operationId, NOT_USED_REQUEST_BODY,
				NOT_USED_REQUEST_PARAM, NOT_USED_RESOURCE_ID);
		assertThat(response.getBody(), is(defaultResponse));

	}

	@Test
	public void shouldGetRecordedRequest() {

		String request1 = "<req1/>";
		String serviceName = "mptu";
		String operationId = "prepayRequest";
		when(configuration.getWebServiceOperation(serviceName, operationId)).thenReturn(new WebserviceOperation());
		serviceLayer.performRequest(serviceName, operationId, request1, NOT_USED_REQUEST_PARAM, NOT_USED_RESOURCE_ID);
		Collection<String> recordedRequests = serviceLayer.getRecordedRequestBodies(serviceName, operationId);

		assertThat(recordedRequests.size(), is(1));
		assertThat(recordedRequests, hasItem(request1));
	}

	@Test
	public void shouldGetRecordedRequests() {

		String serviceName = "mptu";
		String operationId = "prepayRequest";
		String request1 = "<req1/>";
		String request2 = "<req2/>";

		when(configuration.getWebServiceOperation(serviceName, operationId)).thenReturn(new WebserviceOperation());
		serviceLayer.performRequest(serviceName, operationId, request1, NOT_USED_REQUEST_PARAM, NOT_USED_RESOURCE_ID);
		serviceLayer.performRequest(serviceName, operationId, request2, NOT_USED_REQUEST_PARAM, NOT_USED_RESOURCE_ID);

		Collection<String> recordedRequests = serviceLayer.getRecordedRequestBodies(serviceName, operationId);

		assertThat(recordedRequests.size(), is(2));
		assertThat(recordedRequests, hasItems(request1, request2));

	}

	@Test
	public void shouldGetRecordedRequestParam() {

		String serviceName = "billdesk";
		String operationId = "POST";
		when(configuration.getWebServiceOperation(serviceName, operationId)).thenReturn(new WebserviceOperation());
		String queryString = "msg=ABC123&ccno=dummy";
		serviceLayer.performRequest(serviceName, operationId, NOT_USED_REQUEST_BODY, queryString, null);

		Collection<String> recordedRequestParams = serviceLayer.getRecordedUrlParams(serviceName, operationId);

		assertThat(recordedRequestParams.size(), is(1));
		assertThat(recordedRequestParams, hasItem(queryString));

	}

	@Test
	public void shouldGetRecordedRequestParams() {

		String serviceName = "billdesk";
		String operationId = "POST";
		when(configuration.getWebServiceOperation(serviceName, operationId)).thenReturn(new WebserviceOperation());
		String queryString1 = "msg=ABC123&ccno=dummy";
		String queryString2 = "msg=DEF123&ccno=dummy2&xid=something";
		serviceLayer.performRequest(serviceName, operationId, NOT_USED_REQUEST_BODY, queryString1, NOT_USED_RESOURCE_ID);
		serviceLayer.performRequest(serviceName, operationId, NOT_USED_REQUEST_BODY, queryString2, NOT_USED_RESOURCE_ID);

		Collection<String> recordedRequestParams = serviceLayer.getRecordedUrlParams(serviceName, operationId);

		assertThat(recordedRequestParams.size(), is(2));
		assertThat(recordedRequestParams, hasItem(queryString1));
		assertThat(recordedRequestParams, hasItem(queryString2));

	}

	@Test
	public void shouldGetRecordedResourceIds() {
		String serviceName = "dummyRestService";
		String operationId = "GET";
		when(configuration.getWebServiceOperation(serviceName, operationId)).thenReturn(new WebserviceOperation());
		String resourceId1 = "id1";
		String resourceId2 = "id2";
		serviceLayer.performRequest(serviceName, operationId, NOT_USED_REQUEST_BODY, NOT_USED_REQUEST_PARAM, resourceId1);
		serviceLayer.performRequest(serviceName, operationId, NOT_USED_REQUEST_BODY, NOT_USED_REQUEST_PARAM, resourceId2);

		Collection<String> recordedResourceIds = serviceLayer.getRecordedResourceIds(serviceName, operationId);

		assertThat(recordedResourceIds.size(), is(2));
		assertThat(recordedResourceIds, hasItem(resourceId1));
		assertThat(recordedResourceIds, hasItem(resourceId2));

	}

	@Test
	public void shouldGetRecordedRequestsForDifferentOperations() {
		String request1 = "<req1/>";
		String serviceName = "mptu";
		String operationId = "prepayRequest";

		String request2 = "<req2/>";
		String serviceName2 = "ticketing";
		String operationId2 = "reserveRequest";

		String request3 = "<req3/>";
		String operationId3 = "confirmRequest";

		when(configuration.getWebServiceOperation(serviceName, operationId)).thenReturn(new WebserviceOperation());
		when(configuration.getWebServiceOperation(serviceName2, operationId2)).thenReturn(new WebserviceOperation());
		when(configuration.getWebServiceOperation(serviceName2, operationId3)).thenReturn(new WebserviceOperation());

		serviceLayer.performRequest(serviceName, operationId, request1, NOT_USED_REQUEST_PARAM, NOT_USED_RESOURCE_ID);
		serviceLayer.performRequest(serviceName2, operationId2, request2, NOT_USED_REQUEST_PARAM, NOT_USED_RESOURCE_ID);
		serviceLayer.performRequest(serviceName2, operationId3, request3, NOT_USED_REQUEST_PARAM, NOT_USED_RESOURCE_ID);

		Collection<String> recordedPrepayRequests = serviceLayer.getRecordedRequestBodies(serviceName, operationId);
		assertThat(recordedPrepayRequests.size(), is(1));
		assertThat(recordedPrepayRequests, hasItem(request1));

		Collection<String> recordedReserveRequests = serviceLayer.getRecordedRequestBodies(serviceName2, operationId2);
		assertThat(recordedReserveRequests.size(), is(1));
		assertThat(recordedReserveRequests, hasItem(request2));

		Collection<String> recordedConfirmRequests = serviceLayer.getRecordedRequestBodies(serviceName2, operationId3);
		assertThat(recordedConfirmRequests.size(), is(1));
		assertThat(recordedConfirmRequests, hasItem(request3));

	}

	@Test
	public void shouldClearRecordedRequestsOnMockInit() {

		String request1 = "<req1/>";
		String serviceName = "mptu";
		String operationId = "prepayRequest";

		when(configuration.getWebServiceOperation(serviceName, operationId)).thenReturn(new WebserviceOperation());
		serviceLayer.performRequest(serviceName, operationId, request1, NOT_USED_REQUEST_PARAM, NOT_USED_RESOURCE_ID);

		Collection<String> recordedPrepayRequests = serviceLayer.getRecordedRequestBodies(serviceName, operationId);
		assertThat(recordedPrepayRequests.size(), is(1));

		serviceLayer.initMock(serviceName, operationId);

		recordedPrepayRequests = serviceLayer.getRecordedRequestBodies(serviceName, operationId);

		assertThat(recordedPrepayRequests.size(), is(0));
	}

	@Test
	public void shouldGetWsdlText() {

		String serviceName = "ticketing";
		String expectedWsdlText = "<dummyWsdl></dummyWsdl>";
		WebService soapService = new WebService(serviceName, expectedWsdlText);

		when(configuration.getWebService(serviceName)).thenReturn(soapService);

		String returnedWsdlText = serviceLayer.getWsdl(serviceName);
		assertThat(returnedWsdlText, is(expectedWsdlText));

	}

	@Test(expected = ServiceNotConfiguredException.class)
	public void shouldReturneExceptionIfServiceDoesNotExistInConfiguraction() {
		when(configuration.getWebService(anyString())).thenReturn(null);
		serviceLayer.getWsdl("a");
	}

	@Test
	public void shouldDelayByZeroSecByDefault() {
		when(configuration.getWebServiceOperation(anyString(), anyString())).thenReturn(new WebserviceOperation());
		serviceLayer.performRequest("", "", "", NOT_USED_REQUEST_PARAM, NOT_USED_RESOURCE_ID);

		verify(delayService).delaySec(0);
	}

	@Test
	public void shouldDelayRequest() {
		WebserviceOperation operation = new WebserviceOperation();
		int delaySec = 5;
		MockResponse customResponse = new MockResponse(delaySec);
		operation.setCustomResponse(customResponse, 1);

		when(configuration.getWebServiceOperation(anyString(), anyString())).thenReturn(operation);
		serviceLayer.performRequest("", "", "", NOT_USED_REQUEST_PARAM, NOT_USED_RESOURCE_ID);

		verify(delayService).delaySec(delaySec);
	}

	@Test(expected = ServiceNotConfiguredException.class)
	public void shouldThrowExceptionPerforingRequestIfWebserviceOperationNotFound() {
		when(configuration.getWebServiceOperation(anyString(), anyString())).thenReturn(null);
		serviceLayer.performRequest("", "", "", NOT_USED_REQUEST_PARAM, NOT_USED_RESOURCE_ID);
	}

	@Test(expected = ServiceNotConfiguredException.class)
	public void shouldThrowExceptionGettingRecordedRequestBodiesIfWebserviceOperationNotFound() {
		when(configuration.getWebServiceOperation(anyString(), anyString())).thenReturn(null);
		serviceLayer.getRecordedRequestBodies("", "");
	}

	@Test(expected = ServiceNotConfiguredException.class)
	public void shouldThrowExceptionGetingRecordedRequestBodiesIfWebserviceOperationNotFound() {
		when(configuration.getWebServiceOperation(anyString(), anyString())).thenReturn(null);
		serviceLayer.getRecordedUrlParams("", "");
	}

}
