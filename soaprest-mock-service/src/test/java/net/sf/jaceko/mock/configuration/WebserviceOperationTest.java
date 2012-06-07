package net.sf.jaceko.mock.configuration;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import net.sf.jaceko.mock.configuration.WebserviceOperation;

import org.junit.Test;


public class WebserviceOperationTest {

	private WebserviceOperation operation = new WebserviceOperation();

	@Test
	public void shouldSetupCustom1stResponse() {
		operation.setCustomResponseText("adsadsa", 1);
		assertThat(operation.getResponseText(1), is("adsadsa"));
	}
	
	@Test
	public void shouldSetupCustomResponseTwice() {
		operation.setCustomResponseText("sadfsdsdf", 1);
		operation.setCustomResponseText("sadfsdsdf2", 1);
		assertThat(operation.getResponseText(1), is("sadfsdsdf2"));
	}

	@Test
	public void shouldReturnDefaultIfResponseNotDefined() {
		operation.setDefaultResponseText("defaultResp");

		operation.setCustomResponseText("sadfsdsdf", 1);
		assertThat(operation.getResponseText(2), is("defaultResp"));
	}
	
	@Test
	public void shouldReturnDefault() {
		operation.setDefaultResponseText("defaultResp");

		operation.setCustomResponseText("sadfsdsdf", 1);
		operation.setCustomResponseText("sadfsdsdf", 1);
		
		assertThat(operation.getResponseText(2), is("defaultResp"));
	}


	@Test
	public void shouldSetupCustom2ndResponse() {
		operation.setCustomResponseText("adsadsa", 2);
		assertThat(operation.getResponseText(2), is("adsadsa"));
	}

	@Test
	public void shouldSetupConsecutiveResponses() {
		operation.setCustomResponseText("sadfsadfsa1", 1);
		operation.setCustomResponseText("sadfsadfsa2", 2);

		assertThat(operation.getResponseText(1), is("sadfsadfsa1"));
		assertThat(operation.getResponseText(2), is("sadfsadfsa2"));
	}
	
	@Test
	public void shouldReturnDefaultResponseOn1stCallAndCustomOn2nd() {
		operation.setDefaultResponseText("defaultResp");
		operation.setCustomResponseText("sadfsadfsa2", 2);
		
		
		assertThat(operation.getResponseText(1), is("defaultResp"));
		assertThat(operation.getResponseText(2), is("sadfsadfsa2"));

	}

	@Test
	public void shouldClearDefaultResponses() {
		operation.setDefaultResponseText("defaultResp");
		operation.setCustomResponseText("customResp123", 1);
		operation.setCustomResponseText("customResp567", 2);
		assertThat(operation.getResponseText(1), is("customResp123"));
		assertThat(operation.getResponseText(2), is("customResp567"));
		operation.init();
		assertThat(operation.getResponseText(1), is("defaultResp"));
		assertThat(operation.getResponseText(2), is("defaultResp"));
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
