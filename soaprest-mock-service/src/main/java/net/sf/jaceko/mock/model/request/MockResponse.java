/**
 *
 *     Copyright (C) 2012 Jacek Obarymski
 *
 *     This file is part of SOAP/REST Mock Service.
 *
 *     SOAP/REST Mock Service is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License, version 3
 *     as published by the Free Software Foundation.
 *
 *     SOAP/REST Mock Service is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with SOAP/REST Mock Service; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.sf.jaceko.mock.model.request;

import javax.ws.rs.core.MediaType;

public class MockResponse {
	private String body;
	private int code = 200;
	private int delaySec;
	private MediaType contentType;

	public static MockResponseBuilder body(String body) {
		MockResponseBuilder builder = MockResponseBuilder.getInstance();
		builder.body(body);
		return builder.body(body);
	}

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

	public MockResponse() {
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

	public void setZeroCodeTo(int code) {
		if (this.code == 0) {
			setCode(code);
		}
	}

	public int getDelaySec() {
		return delaySec;
	}

	public MediaType getContentType() {
		return contentType;
	}

	public static class MockResponseBuilder {
		private String body;
		private int code = 200;
		private int delaySec;
		private MediaType contentType;

		public static MockResponseBuilder getInstance() {
			return new MockResponseBuilder();
		}

		public MockResponseBuilder body(String body) {
			this.body = body;
			return this;
		}

		public MockResponseBuilder code(int code) {
			this.code = code;
			return this;
		}

		public MockResponseBuilder delaySec(int delaySec) {
			this.delaySec = delaySec;
			return this;
		}

		public MockResponseBuilder contentType(MediaType contentType) {
			this.contentType = contentType;
			return this;
		}

		public MockResponse build() {
			MockResponse mockResponse = new MockResponse();
			mockResponse.setBody(body);
			mockResponse.setCode(code);
			mockResponse.setContentType(contentType);
			mockResponse.setDelaySec(delaySec);
			return mockResponse;
		}

	}

	void setBody(String body) {
		this.body = body;
	}

	void setDelaySec(int delaySec) {
		this.delaySec = delaySec;
	}

	void setContentType(MediaType contentType) {
		this.contentType = contentType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + code;
		result = prime * result + ((contentType == null) ? 0 : contentType.hashCode());
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
		if (contentType == null) {
			if (other.contentType != null)
				return false;
		} else if (!contentType.equals(other.contentType))
			return false;
		if (delaySec != other.delaySec)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("MockResponse [body=%s, code=%s, delaySec=%s, contentType=%s]", body, code, delaySec, contentType);
	}

}
