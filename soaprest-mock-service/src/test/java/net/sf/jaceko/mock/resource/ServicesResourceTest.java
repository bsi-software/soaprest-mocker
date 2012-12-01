package net.sf.jaceko.mock.resource;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import net.sf.jaceko.mock.application.enums.ServiceType;
import net.sf.jaceko.mock.dto.OperationRefDto;
import net.sf.jaceko.mock.dto.WebServiceDto;
import net.sf.jaceko.mock.dto.WebServicesDto;
import net.sf.jaceko.mock.model.webservice.WebService;
import net.sf.jaceko.mock.model.webservice.WebserviceOperation;
import net.sf.jaceko.mock.service.MockConfigurationHolder;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class ServicesResourceTest {
	private ServicesResource resource = new ServicesResource();

	@Mock
	private MockConfigurationHolder mockConfigurationService;
	
	@Mock
	private HttpServletRequest context;
	
	@Before
	public void before() {
		initMocks(this);
		resource.setMockConfigurationService(mockConfigurationService);
	}
	
	@Test
	public void shouldReturnWebServicesDtoContainingSoapService() throws URISyntaxException {
		WebService service = new WebService();
		service.setServiceType(ServiceType.SOAP);
		service.setName("dummyService1");
		
		
		Collection<WebService> servicesCollection = asList(service);
		when(mockConfigurationService.getWebServices()).thenReturn(servicesCollection);
		when(context.getServerName()).thenReturn("someserver");
		when(context.getServerPort()).thenReturn(1234);
		
		WebServicesDto expectedWebServicesDto = new WebServicesDto();
		WebServiceDto webServiceDto1 = new WebServiceDto();
		webServiceDto1.setName("dummyService1");
		webServiceDto1.setType(ServiceType.SOAP);
		webServiceDto1.setWsdlUri("http://someserver:1234/mock/services/SOAP/dummyService1/wsdl");
		webServiceDto1.setEndpointUri("http://someserver:1234/mock/services/SOAP/dummyService1/endpoint");
		expectedWebServicesDto.getWebservicesList().add(webServiceDto1);

		assertThat(resource.getWebServices(context), is(expectedWebServicesDto));
	}
	
	@Test
	public void shouldReturnWebServicesDtoContainingRestService() throws URISyntaxException {
		WebService service = new WebService();
		service.setServiceType(ServiceType.REST);
		service.setName("dummyService2");
		
		
		Collection<WebService> servicesCollection = asList(service);
		when(mockConfigurationService.getWebServices()).thenReturn(servicesCollection);
		when(context.getServerName()).thenReturn("server");
		when(context.getServerPort()).thenReturn(5678);
		
		WebServicesDto expectedWebServicesDto = new WebServicesDto();
		WebServiceDto webServiceDto = new WebServiceDto();
		webServiceDto.setName("dummyService2");
		webServiceDto.setType(ServiceType.REST);
		webServiceDto.setEndpointUri("http://server:5678/mock/services/REST/dummyService2/endpoint");
		expectedWebServicesDto.getWebservicesList().add(webServiceDto);

		assertThat(resource.getWebServices(context), is(expectedWebServicesDto));
	}
	
	@Test
	public void shouldReturnEmptyWebservicesDtoObject() {
		when(mockConfigurationService.getWebServices()).thenReturn(Collections.<WebService> emptyList());
		assertThat(resource.getWebServices(context), is(new WebServicesDto()));
	}

	@Test
	public void shouldReturn3Serices() {

		WebService service1 = new WebService();
		service1.setName("dummyService1");
		
		WebService service2 = new WebService();
		service2.setName("dummyService2");

		WebService service3 = new WebService();
		service3.setName("dummyService3");

		Collection<WebService> servicesCollection = asList(service1, service2, service3);
		when(mockConfigurationService.getWebServices()).thenReturn(servicesCollection);

		assertThat(resource.getWebServices(context).getWebservicesList().size(), is(3));
		assertThat(resource.getWebServices(context).getWebservicesList().get(0).getName(), is("dummyService1"));
		assertThat(resource.getWebServices(context).getWebservicesList().get(1).getName(), is("dummyService2"));
		assertThat(resource.getWebServices(context).getWebservicesList().get(2).getName(), is("dummyService3"));

	}

	
	
	@Test
	public void shouldReturnWebserviceWithOperationRefernces() {
		WebService service = new WebService();
		service.setServiceType(ServiceType.SOAP);
		service.setName("dummyService");
		WebserviceOperation operation = new WebserviceOperation();
		operation.setOperationName("dummyRequest");
		service.addOperation(0, operation);
		WebserviceOperation operation2 = new WebserviceOperation();
		operation2.setOperationName("otherRequest");
		service.addOperation(1, operation2);

		
		
		Collection<WebService> servicesCollection = asList(service);
		when(mockConfigurationService.getWebServices()).thenReturn(servicesCollection);
		when(context.getServerName()).thenReturn("localhost");
		when(context.getServerPort()).thenReturn(9876);

		OperationRefDto operationRef = new OperationRefDto();
		operationRef.setName("dummyRequest");
		operationRef.setUri("http://localhost:9876/mock/services/SOAP/dummyService/operations/dummyRequest");

		OperationRefDto operationRef2 = new OperationRefDto();
		operationRef2.setName("otherRequest");
		operationRef2.setUri("http://localhost:9876/mock/services/SOAP/dummyService/operations/otherRequest");
		
		assertThat(resource.getWebServices(context).getWebservicesList().get(0).getOperationRefs(), contains(operationRef, operationRef2));

		
	}


}
