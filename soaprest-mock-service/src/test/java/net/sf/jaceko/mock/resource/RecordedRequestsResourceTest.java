package net.sf.jaceko.mock.resource;

import net.sf.jaceko.mock.dom.DocumentImpl;
import net.sf.jaceko.mock.service.RecordedRequestsHolder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RecordedRequestsResourceTest {
	private RestServiceMockVerificatonResource resource = new RestServiceMockVerificatonResource();

	@Mock
	private RecordedRequestsHolder recordedRequestsHolder;

	@Before
	public void before() {
		initMocks(this);
		resource.setRecordedRequestsHolder(recordedRequestsHolder);
	}

	@Test
	public void shouldGetRecordedRequestsForMockedWebService() throws Exception {
		String serviceName = "ticketing";
		String operationId = "reserve";

		String req1 = "<req>dummyRequestContent1</req>";
		String req2 = "<req>dummyRequestContent2</req>";

		List<String> recordedRequests = asList(req1, req2);

		when(recordedRequestsHolder.getRecordedRequestBodies(serviceName, operationId)).thenReturn(recordedRequests);

		String requestsXml = resource.getRecordedRequests(serviceName, operationId, "");

		Document requestsDoc = new DocumentImpl(requestsXml);

		assertThat(requestsDoc, hasXPath("count(/recorded-requests/req)", equalTo("2")));
		assertThat(requestsDoc, hasXPath("/recorded-requests/req[1]", equalTo("dummyRequestContent1")));
		assertThat(requestsDoc, hasXPath("/recorded-requests/req[2]", equalTo("dummyRequestContent2")));
	}


    @Test
    public void shouldGetRecordedRequestsForMockedWebServiceWhenRequestsAreNotXML() throws Exception {
        String serviceName = "ticketing";
        String operationId = "reserve";

        String req1 = "dummyRequestContent1";
        String req2 = "dummyRequestContent2";

        List<String> recordedRequests = asList(req1, req2);

        when(recordedRequestsHolder.getRecordedRequestBodies(serviceName, operationId)).thenReturn(recordedRequests);

        String requestsXml = resource.getRecordedRequests(serviceName, operationId, "");

        Document requestsDoc = new DocumentImpl(requestsXml);

        assertThat(requestsDoc, hasXPath("count(/recorded-requests)", equalTo("1")));
        assertThat(requestsDoc, hasXPath("/recorded-requests", equalTo("\ndummyRequestContent1dummyRequestContent2")));
    }

    @Test
    public void shouldGetRecordedRequestsSurroundedByProvidedElementName() throws Exception {
        String serviceName = "ticketing";
        String operationId = "reserve";
        String elementToSurroundRecordedRequests = "request";

        String req1 = "dummyRequestContent1";
        String req2 = "dummyRequestContent2";

        List<String> recordedRequests = asList(req1, req2);

        when(recordedRequestsHolder.getRecordedRequestBodies(serviceName, operationId)).thenReturn(recordedRequests);

        String requestsXml = resource.getRecordedRequests(serviceName, operationId, elementToSurroundRecordedRequests);

        Document requestsDoc = new DocumentImpl(requestsXml);
        assertThat(requestsDoc, hasXPath("count(/recorded-requests/request)", equalTo("2")));
        assertThat(requestsDoc, hasXPath("/recorded-requests/request[1]", equalTo("dummyRequestContent1")));
        assertThat(requestsDoc, hasXPath("/recorded-requests/request[2]", equalTo("dummyRequestContent2")));
    }

	@Test
	public void shouldReturnEmptyListOfRecordedRequests() throws Exception {

		String serviceName = "ticketing";
		String operationId = "reserveRequest";
		List<String> emptyList = Collections.emptyList();

		when(recordedRequestsHolder.getRecordedRequestBodies(serviceName, operationId)).thenReturn((emptyList));

		String requestsXml = resource.getRecordedRequests(serviceName, operationId, "");
		Document requestsDoc = new DocumentImpl(requestsXml);
		assertThat(requestsDoc, hasXPath("count(/recorded-requests)", equalTo("1")));
		assertThat(requestsDoc, hasXPath("count(/recorded-requests/req)", equalTo("0")));

	}

	@Test
	public void shouldGetRecordedRequestParamsForMockedWebService() throws Exception {
		String serviceName = "billdesk";
		String operationId = "processPayment";

		String reqParams1 = "msg=ABC";
		String reqParams2 = "msg=CDEF&ccno=12334;";

		List<String> recordedRequests = asList(reqParams1, reqParams2);

		when(recordedRequestsHolder.getRecordedUrlParams(serviceName, operationId)).thenReturn(recordedRequests);

		String requestParamsXml = resource.getRecordedUrlParams(serviceName, operationId);

		Document requestsDoc = new DocumentImpl(requestParamsXml);

		assertThat(requestsDoc, hasXPath("count(/recorded-request-params/recorded-request-param)", equalTo("2")));
		assertThat(requestsDoc, hasXPath("/recorded-request-params/recorded-request-param[1]", equalTo(reqParams1)));
		assertThat(requestsDoc, hasXPath("/recorded-request-params/recorded-request-param[2]", equalTo(reqParams2)));
	}

	@Test
	public void shouldGetRecordedResourcesIds() throws ParserConfigurationException, SAXException, IOException {
		String serviceName = "someRESRService";
		String operationId = "GET";

		String resourceId1 = "id1";
		String resourceId2 = "id2";

		List<String> recordedResourceIds = asList(resourceId1, resourceId2);

		when(recordedRequestsHolder.getRecordedResourceIds(serviceName, operationId)).thenReturn(recordedResourceIds);
		String requestParamsXml = resource.getRecordedResourceIds(serviceName, operationId);
		Document requestsDoc = new DocumentImpl(requestParamsXml);

		assertThat(requestsDoc, hasXPath("count(/recorded-resource-ids/recorded-resource-id)", equalTo("2")));
		assertThat(requestsDoc, hasXPath("/recorded-resource-ids/recorded-resource-id[1]", equalTo(resourceId1)));
		assertThat(requestsDoc, hasXPath("/recorded-resource-ids/recorded-resource-id[2]", equalTo(resourceId2)));

	}

}
