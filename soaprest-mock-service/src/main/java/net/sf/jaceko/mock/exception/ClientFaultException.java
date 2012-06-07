package net.sf.jaceko.mock.exception;

public class ClientFaultException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	public ClientFaultException(String msg) {
		super(msg);
	}
	
	public ClientFaultException(String msg, Exception e) {
		super(msg, e);
	}


}
