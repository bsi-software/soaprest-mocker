package net.sf.jaceko.mock.model;

public class MockResponse {
	String body;
	int code = 200;
	public MockResponse(String body, int code) {
		super();
		this.body = body;
		this.code = code;
	}
	public MockResponse(String body) {
		super();
		this.body = body;
	}
	public String getBody() {
		return body;
	}
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return String.format("MockResponse [body=%s, code=%s]", body, code);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + code;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MockResponse other = (MockResponse) obj;
		if (body == null) {
			if (other.body != null)
				return false;
		} else if (!body.equals(other.body))
			return false;
		if (code != other.code)
			return false;
		return true;
	}

}
