package net.sf.jaceko.mock.model;

public class MockResponse {
	private String body;
	private int code = 200;
	private int delaySec;

	public MockResponse(String body, int code, int delaySec) {
		super();
		this.body = body;
		this.code = code;
		this.delaySec = delaySec;
	}

	public MockResponse(int delaySec) {
		super();
		this.delaySec = delaySec;
	}

	public MockResponse(String body) {
		super();
		this.body = body;
	}

	public MockResponse(String body, int code) {
		this.body = body;
		this.code = code;
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

	public int getDelaySec() {
		return delaySec;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + code;
		result = prime * result + delaySec;
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
		if (delaySec != other.delaySec)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("MockResponse [body=%s, code=%s, delaySec=%s]", body, code, delaySec);
	}


}
