package net.sf.jaceko.mock.model;

public class Request {
	
	private String resourceId;
	private String body;
	private String queryString;

	public Request(String body, String queryString, String resourceId) {
		super();
		this.body = body;
		this.queryString = queryString;
		this.resourceId = resourceId;
	}

	public String getBody() {
		return body;
	}
	
	public String getResourceId() {
		return resourceId;
	}

	public String getQueryString() {
		return queryString;
	}

}
