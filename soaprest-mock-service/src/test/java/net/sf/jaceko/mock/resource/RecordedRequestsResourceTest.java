package net.sf.jaceko.mock.resource;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Collections;
import java.util.List;

import net.sf.jaceko.mock.helper.XmlParser;
import net.sf.jaceko.mock.resource.RecordedRequestsResource;
import net.sf.jaceko.mock.service.WebserviceMockSvcLayer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.w3c.dom.Document;



public class RecordedRequestsResourceTest {
	private RecordedRequestsResource resource = new RecordedRequestsResource();

	@Mock
	private WebserviceMockSvcLayer service;

	@Before
	public void before() {
		initMocks(this);
		resource.setWebserviceMockService(service);
	}
	
	@Test
	public void shouldGetRecordedRequestsForMockedWebService() throws Exception {
		String serviceName = "ticketing";
		String operationId = "reserve";

		String req1 = "<req>dummyRequestContent1</req>";
		String req2 = "<req>dummyRequestContent2</req>";

		List<String> recordedRequests = asList(req1, req2);

		when(service.getRecordedRequestBodies(serviceName, operationId)).thenReturn(
				recordedRequests);

		String requestsXml = resource.getRecordedRequests(serviceName, operationId);

		Document requestsDoc = XmlParser.parse(requestsXml, false);

		assertThat(requestsDoc, hasXPath("count(/requests/req)", equalTo("2")));
		assertThat(requestsDoc, hasXPath("/requests/req[1]", equalTo("dummyRequestContent1")));
		assertThat(requestsDoc, hasXPath("/requests/req[2]", equalTo("dummyRequestContent2")));
	}

	@Test
	public void shouldReturnEmptyListOfRecordedRequests() throws Exception {

		String serviceName = "ticketing";
		String operationId = "reserveRequest";
		List<String> emptyList = Collections.emptyList();

		when(service.getRecordedRequestBodies(serviceName, operationId)).thenReturn((emptyList));

		String requestsXml = resource.getRecordedRequests(serviceName, operationId);
		Document requestsDoc = XmlParser.parse(requestsXml, false);
		assertThat(requestsDoc, hasXPath("count(/requests)", equalTo("1")));
		assertThat(requestsDoc, hasXPath("count(/requests/req)", equalTo("0")));

	}
	
	@Test
	public void shouldGetRecordedRequestParamsForMockedWebService() throws Exception {
		String serviceName = "billdesk";
		String operationId = "processPayment";

		String reqParams1 = "msg=ABC";
		String reqParams2 = "msg=CDEF&ccno=12334;";

		List<String> recordedRequests = asList(reqParams1, reqParams2);

		when(service.getRecordedUrlParams(serviceName, operationId)).thenReturn(
				recordedRequests);

		String requestParamsXml = resource.getRecordedUrlParams(serviceName, operationId);
		System.out.println(requestParamsXml);

		Document requestsDoc = XmlParser.parse(requestParamsXml, false);

		assertThat(requestsDoc, hasXPath("count(/urlRequestParams/queryString)", equalTo("2")));
		assertThat(requestsDoc, hasXPath("/urlRequestParams/queryString[1]", equalTo(reqParams1)));
		assertThat(requestsDoc, hasXPath("/urlRequestParams/queryString[2]", equalTo(reqParams2)));
	}


}
