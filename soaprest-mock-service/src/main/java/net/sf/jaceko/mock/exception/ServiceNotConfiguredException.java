package net.sf.jaceko.mock.exception;

public class ServiceNotConfiguredException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ServiceNotConfiguredException() {
		super();
	}
	public ServiceNotConfiguredException(String msg) {
		super(msg);
	}

}
