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
import net.sf.jaceko.mock.service.DelayService;
import net.sf.jaceko.mock.service.WebserviceMockSvcLayer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;


public class WebserviceMockSvcLayerTest {

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
	public void shouldReturnDefaultResponse() {

		String serviceName = "mptu";
		String operationId = "prepayRequest";
		String defaultResponse = "<defaultResp>abc</defaultResp>";

		WebserviceOperation operation = new WebserviceOperation(null, null, defaultResponse);

		when(configuration.getWebServiceOperation(serviceName, operationId)).thenReturn(operation);

		String response = serviceLayer.performRequest(serviceName, operationId, null, null);
		assertThat(response, is(defaultResponse));

	}

	@Test(expected = ServiceNotConfiguredException.class)
	public void performRequestShouldThrowExceptionIfOperationNotFound() {

		when(configuration.getWebServiceOperation(anyString(), anyString())).thenReturn(null);

		serviceLayer.performRequest("", "", "", null);

	}

	@Test
	public void shouldSetCustomResponse() {
		String serviceName = "mptu";
		String operationId = "prepayRequest";
		String expectedCustomResponse = "<dummyResp/>";
		WebserviceOperation operation = new WebserviceOperation();
		int requestInOrder = 2;

		when(configuration.getWebServiceOperation(serviceName, operationId)).thenReturn(operation);
		serviceLayer.setCustomResponse(serviceName, operationId, requestInOrder, expectedCustomResponse);
		String customResponse = operation.getResponseText(requestInOrder);
		assertThat(customResponse, is(expectedCustomResponse));

	}
	
	
	
	
	
	@Test
	public void shouldResetInvocationNumberWhileSettingUpRequest() {
		String serviceName = "mptu";
		String operationId = "prepayRequest";
		WebserviceOperation operation = new WebserviceOperation();
		int requestInOrder = 2;

		when(configuration.getWebServiceOperation(serviceName, operationId)).thenReturn(operation);
		serviceLayer.setCustomResponse(serviceName, operationId, requestInOrder, "<dummyResp/>");
		assertThat(operation.getNextInvocationNumber(), is(1));
		assertThat(operation.getNextInvocationNumber(), is(2));
		
		serviceLayer.setCustomResponse(serviceName, operationId, requestInOrder, "<dummyResp2/>");
		assertThat(operation.getNextInvocationNumber(), is(1));

	}
	
	
	

	@Test(expected = ServiceNotConfiguredException.class)
	public void setCustomResponseShouldThrowExceptionIfOperationNotFound() {

		when(configuration.getWebServiceOperation(anyString(), anyString())).thenReturn(null);

		serviceLayer.setCustomResponse("", "", 0, "");

	}

	@Test
	public void shouldReturnCustomResponseIfSet() {

		String serviceName = "mptu";
		String operationId = "prepayRequest";
		String defaultResponse = "<defaultResp>abc</defaultResp>";
		String expectedCustomResponse = "<dummyResp/>";

		WebserviceOperation operation = new WebserviceOperation(null, null, defaultResponse);
		operation.setCustomResponseText(expectedCustomResponse, 1);

		when(configuration.getWebServiceOperation(serviceName, operationId)).thenReturn(operation);

		String response = serviceLayer.performRequest(serviceName, operationId, null, null);
		assertThat(response, is(expectedCustomResponse));

	}

	@Test
	public void shouldReturnDefaultResponseInSecondCall() {

		String serviceName = "mptu";
		String operationId = "prepayRequest";
		String defaultResponse = "<defaultResp>abc</defaultResp>";
		String expectedCustomResponse = "<dummyResp/>";

		WebserviceOperation operation = new WebserviceOperation(null, null, defaultResponse);
		operation.setCustomResponseText(expectedCustomResponse, 1);
		operation.setCustomResponseText(expectedCustomResponse, 1);

		when(configuration.getWebServiceOperation(serviceName, operationId)).thenReturn(operation);

		String response = serviceLayer.performRequest(serviceName, operationId, null, null);

		response = serviceLayer.performRequest(serviceName, operationId, null, null);
		assertThat(response, is(defaultResponse));

	}

	@Test
	public void shouldReturnCustomResponseInSecondCall() {

		String serviceName = "mptu";
		String operationId = "prepayRequest";
		String defaultResponse = "<defaultResp>abc</defaultResp>";
		String expectedCustomResponse1 = "<dummyResp/>";
		String expectedCustomResponse2 = "<dummyResp2/>";

		WebserviceOperation operation = new WebserviceOperation(null, null, defaultResponse);
		operation.setCustomResponseText(expectedCustomResponse1, 1);
		operation.setCustomResponseText(expectedCustomResponse2, 2);

		when(configuration.getWebServiceOperation(serviceName, operationId)).thenReturn(operation);

		String response = serviceLayer.performRequest(serviceName, operationId, null, null);

		response = serviceLayer.performRequest(serviceName, operationId, null, null);
		assertThat(response, is(expectedCustomResponse2));

	}

	@Test
	public void shouldReturnDefaultResponseAfterMockInit() {

		String serviceName = "mptu";
		String operationId = "prepayRequest";

		String defaultResponse = "<defaultResp>abc</defaultResp>";
		WebserviceOperation operation = new WebserviceOperation(null, null, defaultResponse);
		operation.setCustomResponseText("<dummyResp/>", 1);
		operation.setCustomResponseText("<dummyResp2/>", 2);

		when(configuration.getWebServiceOperation(serviceName, operationId)).thenReturn(operation);

		serviceLayer.initMock(serviceName, operationId);

		String response = serviceLayer.performRequest(serviceName, operationId, null, null);
		assertThat(response, is(defaultResponse));

	}

	@Test
	public void shouldGetRecordedRequest() {

		String request1 = "<req1/>";
		String serviceName = "mptu";
		String operationId = "prepayRequest";
		when(configuration.getWebServiceOperation(serviceName, operationId)).thenReturn(
				new WebserviceOperation());
		serviceLayer.performRequest(serviceName, operationId, request1, null);
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

		when(configuration.getWebServiceOperation(serviceName, operationId)).thenReturn(
				new WebserviceOperation());
		serviceLayer.performRequest(serviceName, operationId, request1, null);
		serviceLayer.performRequest(serviceName, operationId, request2, null);

		Collection<String> recordedRequests = serviceLayer.getRecordedRequestBodies(serviceName, operationId);

		assertThat(recordedRequests.size(), is(2));
		assertThat(recordedRequests, hasItems(request1, request2));

	}

	@Test
	public void shouldGetRecordedRequestParam() {

		String serviceName = "billdesk";
		String operationId = "POST";
		when(configuration.getWebServiceOperation(serviceName, operationId)).thenReturn(
				new WebserviceOperation());
		String queryString = "msg=ABC123&ccno=dummy";
		serviceLayer.performRequest(serviceName, operationId, null, queryString);

		Collection<String> recordedRequestParams = serviceLayer
				.getRecordedUrlParams(serviceName, operationId);

		assertThat(recordedRequestParams.size(), is(1));
		assertThat(recordedRequestParams, hasItem(queryString));

	}

	@Test
	public void shouldGetRecordedRequestParams() {

		String serviceName = "billdesk";
		String operationId = "POST";
		when(configuration.getWebServiceOperation(serviceName, operationId)).thenReturn(
				new WebserviceOperation());
		String queryString1 = "msg=ABC123&ccno=dummy";
		String queryString2 = "msg=DEF123&ccno=dummy2&xid=something";
		serviceLayer.performRequest(serviceName, operationId, null, queryString1);
		serviceLayer.performRequest(serviceName, operationId, null, queryString2);

		Collection<String> recordedRequestParams = serviceLayer
				.getRecordedUrlParams(serviceName, operationId);

		assertThat(recordedRequestParams.size(), is(2));
		assertThat(recordedRequestParams, hasItem(queryString1));
		assertThat(recordedRequestParams, hasItem(queryString2));

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

		when(configuration.getWebServiceOperation(serviceName, operationId)).thenReturn(
				new WebserviceOperation());
		when(configuration.getWebServiceOperation(serviceName2, operationId2)).thenReturn(
				new WebserviceOperation());
		when(configuration.getWebServiceOperation(serviceName2, operationId3)).thenReturn(
				new WebserviceOperation());

		serviceLayer.performRequest(serviceName, operationId, request1, null);
		serviceLayer.performRequest(serviceName2, operationId2, request2, null);
		serviceLayer.performRequest(serviceName2, operationId3, request3, null);

		Collection<String> recordedPrepayRequests = serviceLayer.getRecordedRequestBodies(serviceName,
				operationId);
		assertThat(recordedPrepayRequests.size(), is(1));
		assertThat(recordedPrepayRequests, hasItem(request1));

		Collection<String> recordedReserveRequests = serviceLayer.getRecordedRequestBodies(serviceName2,
				operationId2);
		assertThat(recordedReserveRequests.size(), is(1));
		assertThat(recordedReserveRequests, hasItem(request2));

		Collection<String> recordedConfirmRequests = serviceLayer.getRecordedRequestBodies(serviceName2,
				operationId3);
		assertThat(recordedConfirmRequests.size(), is(1));
		assertThat(recordedConfirmRequests, hasItem(request3));

	}

	@Test
	public void shouldClearRecordedRequestsOnMockInit() {

		String request1 = "<req1/>";
		String serviceName = "mptu";
		String operationId = "prepayRequest";

		when(configuration.getWebServiceOperation(serviceName, operationId)).thenReturn(
				new WebserviceOperation());
		serviceLayer.performRequest(serviceName, operationId, request1, null);

		Collection<String> recordedPrepayRequests = serviceLayer.getRecordedRequestBodies(serviceName,
				operationId);
		assertThat(recordedPrepayRequests.size(), is(1));

		serviceLayer.initMock(serviceName, operationId);

		recordedPrepayRequests = serviceLayer.getRecordedRequestBodies(serviceName, operationId);

		assertThat(recordedPrepayRequests.size(), is(0));
	}

	@Test
	public void shouldSetCustomRequestDelay() {
		String serviceName = "mptu";
		String operationId = "prepayRequest";
		String defaultResponse = "<defaultResp>abc</defaultResp>";
		int delaySec = 5;

		WebserviceOperation operation = new WebserviceOperation(null, null, defaultResponse);

		when(configuration.getWebServiceOperation(serviceName, operationId)).thenReturn(operation);

		serviceLayer.setRequestDelay(serviceName, operationId, delaySec);
		assertThat(operation.getCustomDelaySec(), is(delaySec));

	}
	
	
	@Test
	public void shouldResetInvocationNumberWhileSettingUpDelay() {
		String serviceName = "mptu";
		String operationId = "prepayRequest";
		WebserviceOperation operation = new WebserviceOperation(null, null, "");

		when(configuration.getWebServiceOperation(serviceName, operationId)).thenReturn(operation);

		serviceLayer.setRequestDelay(serviceName, operationId, 5);
		assertThat(operation.getNextInvocationNumber(), is(1));
		assertThat(operation.getNextInvocationNumber(), is(2));

		serviceLayer.setRequestDelay(serviceName, operationId, 5);
		assertThat(operation.getNextInvocationNumber(), is(1));

	}

	@Test(expected = ServiceNotConfiguredException.class)
	public void setRequestDelayShouldThrowExceptionIfOperationNotFound() {

		when(configuration.getWebServiceOperation(anyString(), anyString())).thenReturn(null);

		serviceLayer.setRequestDelay("", "", 5);
	}

	@Test
	public void shouldGetWsdlText() {

		String serviceName = "ticketing";
		String expectedWsdlText = "<dummyWsdl></dummyWsdl>";
		WebService soapService = new WebService(serviceName, expectedWsdlText);

		when(configuration.getSoapService(serviceName)).thenReturn(soapService);

		String returnedWsdlText = serviceLayer.getWsdl(serviceName);
		assertThat(returnedWsdlText, is(expectedWsdlText));

	}

	@Test(expected = ServiceNotConfiguredException.class)
	public void shouldReturneExceptionIfServiceDoesNotExistInConfiguraction() {
		when(configuration.getSoapService(anyString())).thenReturn(null);
		serviceLayer.getWsdl("a");
	}

	@Test
	public void shouldDelayByZeroSecByDefault() {
		when(configuration.getWebServiceOperation(anyString(), anyString())).thenReturn(
				new WebserviceOperation());
		serviceLayer.performRequest("", "", "", null);

		verify(delayService).delaySec(0);
	}

	@Test
	public void shouldDelayRequest() {
		WebserviceOperation operation = new WebserviceOperation();
		int delaySec = 5;
		operation.setCustomDelaySec(delaySec);

		when(configuration.getWebServiceOperation(anyString(), anyString())).thenReturn(operation);
		serviceLayer.performRequest("", "", "", null);

		verify(delayService).delaySec(delaySec);
	}

	@Test
	public void shouldDelayByZeroSecAfterMockReinit() {
		WebserviceOperation operation = new WebserviceOperation();
		int delaySec = 5;
		operation.setCustomDelaySec(delaySec);
		String serviceName = "mptu";
		String operationId = "prepayRequest";

		when(configuration.getWebServiceOperation(anyString(), anyString())).thenReturn(operation);
		serviceLayer.performRequest(serviceName, operationId, "", null);
		verify(delayService).delaySec(delaySec);
		serviceLayer.initMock(serviceName, operationId);
		serviceLayer.performRequest(serviceName, operationId, "", null);
		verify(delayService).delaySec(0);

	}

	@Test(expected = ServiceNotConfiguredException.class)
	public void shouldThrowExceptionPerforingRequestIfWebserviceOperationNotFound() {
		when(configuration.getWebServiceOperation(anyString(), anyString())).thenReturn(null);
		serviceLayer.performRequest("", "", "", null);
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
