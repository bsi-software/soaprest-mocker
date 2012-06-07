package net.sf.jaceko.mock.model;

public class Request {
	
	public Request(String body, String queryString) {
		super();
		this.body = body;
		this.queryString = queryString;
	}
	private String body;
	private String queryString;

	public String getBody() {
		return body;
	}
	
	public String getQueryString() {
		return queryString;
	}

}
