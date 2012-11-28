package net.sf.jaceko.mock.configuration;

import static org.junit.Assert.assertThat;

import net.sf.jaceko.mock.model.webservice.WebService;
import net.sf.jaceko.mock.model.webservice.WebserviceOperation;

import org.junit.Test;


import static org.hamcrest.core.Is.*;

public class WebServiceTest {

	private WebService service = new WebService();

	
	@Test
	public void shouldGetOperationByIndx() {
		WebserviceOperation operation = new WebserviceOperation();
		int indx = 1;
		service.addOperation(indx, operation);

		assertThat(service.getOperation(indx), is(operation));
	}

}
