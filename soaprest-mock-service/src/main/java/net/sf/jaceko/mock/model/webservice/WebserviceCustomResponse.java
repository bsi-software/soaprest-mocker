package net.sf.jaceko.mock.model.webservice;

import static java.lang.String.format;


public class WebserviceCustomResponse {

	private String searchString;
	private String responseFile;
	private String responseText;

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public String getSearchString() {
		return searchString;
	}

	public void setResponseFile(String responseFile) {
		this.responseFile = responseFile;
	}

	public String getResponseFile() {
		return responseFile;
	}

	public void setResponseText(String responseText) {
		this.responseText = responseText;
	}

	public String getResponseText() {
		return responseText;
	}

	@Override
	public String toString() {
		return format(
				"WebserviceCustomResponse [searchString=%s, responseFile=%s, responseText=%s]",
				searchString, responseFile, responseText);
	}
}
