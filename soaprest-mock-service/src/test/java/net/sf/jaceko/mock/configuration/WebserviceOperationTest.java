package net.sf.jaceko.mock.configuration;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import net.sf.jaceko.mock.configuration.WebserviceOperation;
import net.sf.jaceko.mock.model.MockResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.junit.Before;
import org.junit.Test;


public class WebserviceOperationTest {

	private WebserviceOperation operation;

	@Before
	public void before() {
		operation = new WebserviceOperation();
	}
	
	
	
	@Test
	public void shouldSetupCustom1stResponse() {
		operation.setCustomResponse(new MockResponse("adsadsa"), 1);
		assertThat(operation.getResponse(1).getBody(), is("adsadsa"));
	}
	
	@Test
	public void shouldSetupCustomResponseTwice() {
		operation.setCustomResponse(new MockResponse("sadfsdsdf"), 1);
		MockResponse secondCustomResponse = new MockResponse("sadfsdsdf2");
		operation.setCustomResponse(secondCustomResponse, 1);
		assertThat(operation.getResponse(1), is(secondCustomResponse));
	}

	@Test
	public void shouldReturnDefaultIf2ndResponseNotDefined() {
		operation.setDefaultResponseText("defaultResp");
		operation.setDefaultResponseCode(HttpStatus.SC_CREATED);
		
		operation.setCustomResponse(new MockResponse("sadfsdsdf"), 1);
		
		assertThat(operation.getResponse(2).getBody(), is("defaultResp"));
		assertThat(operation.getResponse(2).getCode(), is(HttpStatus.SC_CREATED));
	}
	
	@Test
	public void shouldReturnDefaultResponse() {
		operation.setDefaultResponseText("defaultResp");
		operation.setDefaultResponseCode(HttpStatus.SC_OK);
		
		operation.setCustomResponse(new MockResponse("sadfsdsdf"), 1);
		operation.setCustomResponse(new MockResponse("sadfsdsdf"), 1);
		
		assertThat(operation.getResponse(2).getBody(), is("defaultResp"));
		assertThat(operation.getResponse(2).getCode(), is(HttpStatus.SC_OK));
	}
	
	@Test
	public void shouldReturnDefaultResponseCodeIfCodeNodeDefinedInCustomResponse() {
		operation.setDefaultResponseCode(HttpStatus.SC_OK);

		int code = 0;
		operation.setCustomResponse(new MockResponse("dummy", code), 1);
		assertThat(operation.getResponse(1).getCode(), is(HttpStatus.SC_OK));

	}



	@Test
	public void shouldSetupCustom2ndResponse() {
		MockResponse customResponse = new MockResponse("adsadsa", 201);
		operation.setCustomResponse(customResponse, 2);
		assertThat(operation.getResponse(2), is(customResponse));
	}

	@Test
	public void shouldSetupConsecutiveResponses() {
		MockResponse customResponse1 = new MockResponse("sadfsadfsa1", 201);
		MockResponse customResponse2 = new MockResponse("sadfsadfsa2", 200);
		operation.setCustomResponse(customResponse1, 1);
		operation.setCustomResponse(customResponse2, 2);

		assertThat(operation.getResponse(1), is(customResponse1));
		assertThat(operation.getResponse(2), is(customResponse2));
	}
	
	@Test
	public void shouldReturnDefaultResponseOn1stCallAndCustomOn2nd() {
		operation.setDefaultResponseText("defaultResp");
		operation.setCustomResponse(new MockResponse("sadfsadfsa2"), 2);
		
		
		assertThat(operation.getResponse(1).getBody(), is("defaultResp"));
		assertThat(operation.getResponse(2).getBody(), is("sadfsadfsa2"));

	}

	@Test
	public void shouldClearDefaultResponses() {
		operation.setDefaultResponseText("defaultResp");
		operation.setCustomResponse(new MockResponse("customResp123"), 1);
		operation.setCustomResponse(new MockResponse("customResp567"), 2);
		assertThat(operation.getResponse(1).getBody(), is("customResp123"));
		assertThat(operation.getResponse(2).getBody(), is("customResp567"));
		operation.init();
		assertThat(operation.getResponse(1).getBody(), is("defaultResp"));
		assertThat(operation.getResponse(2).getBody(), is("defaultResp"));
	}
	
	@Test
	public void shouldClearDelay() {
		operation.setCustomDelaySec(5);
		assertThat(operation.getCustomDelaySec(), is(5));

		operation.init();
		assertThat(operation.getCustomDelaySec(), is(0));
		
	}
	
	@Test
	public void shouldClearInvocationCount() {
		assertThat(operation.getNextInvocationNumber(), is(1));
		assertThat(operation.getNextInvocationNumber(), is(2));
		assertThat(operation.getNextInvocationNumber(), is(3));

		operation.init();
		assertThat(operation.getNextInvocationNumber(), is(1));
		assertThat(operation.getNextInvocationNumber(), is(2));
		assertThat(operation.getNextInvocationNumber(), is(3));
		
	}

	
	@Test
	public void shouldGetNextInvocationCount() {
		assertThat(operation.getNextInvocationNumber(), is(1));
		assertThat(operation.getNextInvocationNumber(), is(2));
		assertThat(operation.getNextInvocationNumber(), is(3));
		assertThat(operation.getNextInvocationNumber(), is(4));
	}

}
