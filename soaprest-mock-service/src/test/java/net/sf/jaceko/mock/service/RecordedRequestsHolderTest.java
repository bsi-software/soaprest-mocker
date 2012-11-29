package net.sf.jaceko.mock.service;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.junit.matchers.JUnitMatchers.hasItems;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Collection;

import net.sf.jaceko.mock.model.webservice.WebserviceOperation;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class RecordedRequestsHolderTest {

	private static final String NOT_USED_RESOURCE_ID = null;
	private static final String NOT_USED_REQUEST_PARAM = null;
	private static final String NOT_USED_REQUEST_BODY = null;

	
	private RecordedRequestsHolder recordedRequestsHolder = new RecordedRequestsHolder();  
	
	@Mock
	private MockConfigurationHolder configurationHolder;

	
	@Before
	public void init() {
		initMocks(this);
		recordedRequestsHolder.setMockserviceConfiguration(configurationHolder);
	}

	
	@Test
	public void shouldGetRecordedRequest() {

		String request1 = "<req1/>";
		String serviceName = "mptu";
		String operationId = "prepayRequest";
		when(configurationHolder.getWebServiceOperation(serviceName, operationId)).thenReturn(new WebserviceOperation());
		recordedRequestsHolder.recordRequest(serviceName, operationId, request1, NOT_USED_REQUEST_PARAM, NOT_USED_RESOURCE_ID);
		Collection<String> recordedRequests = recordedRequestsHolder.getRecordedRequestBodies(serviceName, operationId);

		assertThat(recordedRequests.size(), is(1));
		assertThat(recordedRequests, hasItem(request1));
	}

	@Test
	public void shouldGetRecordedRequests() {

		String serviceName = "mptu";
		String operationId = "prepayRequest";
		String request1 = "<req1/>";
		String request2 = "<req2/>";

		when(configurationHolder.getWebServiceOperation(serviceName, operationId)).thenReturn(new WebserviceOperation());

		recordedRequestsHolder.recordRequest(serviceName, operationId, request1, NOT_USED_REQUEST_PARAM, NOT_USED_RESOURCE_ID);
		recordedRequestsHolder.recordRequest(serviceName, operationId, request2, NOT_USED_REQUEST_PARAM, NOT_USED_RESOURCE_ID);

		Collection<String> recordedRequests = recordedRequestsHolder.getRecordedRequestBodies(serviceName, operationId);

		assertThat(recordedRequests.size(), is(2));
		assertThat(recordedRequests, hasItems(request1, request2));

	}
	
	@Test
	public void shouldGetRecordedRequestParam() {

		String serviceName = "billdesk";
		String operationId = "POST";
		when(configurationHolder.getWebServiceOperation(serviceName, operationId)).thenReturn(new WebserviceOperation());
		String queryString = "msg=ABC123&ccno=dummy";
		recordedRequestsHolder.recordRequest(serviceName, operationId, NOT_USED_REQUEST_BODY, queryString, null);

		Collection<String> recordedRequestParams = recordedRequestsHolder.getRecordedUrlParams(serviceName, operationId);

		assertThat(recordedRequestParams.size(), is(1));
		assertThat(recordedRequestParams, hasItem(queryString));

	}

	@Test
	public void shouldGetRecordedRequestParams() {

		String serviceName = "billdesk";
		String operationId = "POST";
		when(configurationHolder.getWebServiceOperation(serviceName, operationId)).thenReturn(new WebserviceOperation());
		String queryString1 = "msg=ABC123&ccno=dummy";
		String queryString2 = "msg=DEF123&ccno=dummy2&xid=something";
		recordedRequestsHolder.recordRequest(serviceName, operationId, NOT_USED_REQUEST_BODY, queryString1, NOT_USED_RESOURCE_ID);
		recordedRequestsHolder.recordRequest(serviceName, operationId, NOT_USED_REQUEST_BODY, queryString2, NOT_USED_RESOURCE_ID);

		Collection<String> recordedRequestParams = recordedRequestsHolder.getRecordedUrlParams(serviceName, operationId);

		assertThat(recordedRequestParams.size(), is(2));
		assertThat(recordedRequestParams, hasItem(queryString1));
		assertThat(recordedRequestParams, hasItem(queryString2));

	}

	@Test
	public void shouldGetRecordedResourceIds() {
		String serviceName = "dummyRestService";
		String operationId = "GET";
		when(configurationHolder.getWebServiceOperation(serviceName, operationId)).thenReturn(new WebserviceOperation());
		String resourceId1 = "id1";
		String resourceId2 = "id2";
		recordedRequestsHolder.recordRequest(serviceName, operationId, NOT_USED_REQUEST_BODY, NOT_USED_REQUEST_PARAM, resourceId1);
		recordedRequestsHolder.recordRequest(serviceName, operationId, NOT_USED_REQUEST_BODY, NOT_USED_REQUEST_PARAM, resourceId2);

		Collection<String> recordedResourceIds = recordedRequestsHolder.getRecordedResourceIds(serviceName, operationId);

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

		when(configurationHolder.getWebServiceOperation(serviceName, operationId)).thenReturn(new WebserviceOperation());
		when(configurationHolder.getWebServiceOperation(serviceName2, operationId2)).thenReturn(new WebserviceOperation());
		when(configurationHolder.getWebServiceOperation(serviceName2, operationId3)).thenReturn(new WebserviceOperation());

		recordedRequestsHolder.recordRequest(serviceName, operationId, request1, NOT_USED_REQUEST_PARAM, NOT_USED_RESOURCE_ID);
		recordedRequestsHolder.recordRequest(serviceName2, operationId2, request2, NOT_USED_REQUEST_PARAM, NOT_USED_RESOURCE_ID);
		recordedRequestsHolder.recordRequest(serviceName2, operationId3, request3, NOT_USED_REQUEST_PARAM, NOT_USED_RESOURCE_ID);

		Collection<String> recordedPrepayRequests = recordedRequestsHolder.getRecordedRequestBodies(serviceName, operationId);
		assertThat(recordedPrepayRequests.size(), is(1));
		assertThat(recordedPrepayRequests, hasItem(request1));

		Collection<String> recordedReserveRequests = recordedRequestsHolder.getRecordedRequestBodies(serviceName2, operationId2);
		assertThat(recordedReserveRequests.size(), is(1));
		assertThat(recordedReserveRequests, hasItem(request2));

		Collection<String> recordedConfirmRequests = recordedRequestsHolder.getRecordedRequestBodies(serviceName2, operationId3);
		assertThat(recordedConfirmRequests.size(), is(1));
		assertThat(recordedConfirmRequests, hasItem(request3));

	}
	
	@Test
	public void shouldClearRecordedRequests() {

		String request1 = "<req1/>";
		String serviceName = "mptu";
		String operationId = "prepayRequest";

		when(configurationHolder.getWebServiceOperation(serviceName, operationId)).thenReturn(new WebserviceOperation());
		recordedRequestsHolder.recordRequest(serviceName, operationId, request1, NOT_USED_REQUEST_PARAM, NOT_USED_RESOURCE_ID);

		Collection<String> recordedPrepayRequests = recordedRequestsHolder.getRecordedRequestBodies(serviceName, operationId);
		assertThat(recordedPrepayRequests.size(), is(1));

		recordedRequestsHolder.clearRecordedRequests(serviceName, operationId);

		recordedPrepayRequests = recordedRequestsHolder.getRecordedRequestBodies(serviceName, operationId);

		assertThat(recordedPrepayRequests.size(), is(0));
	}



}
