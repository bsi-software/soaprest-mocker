package net.sf.jaceko.mock.service;

public class DelayService {

	private static final int MILIS_IN_SEC = 1000;

	public void delaySec(int sec) {
		try {
			Thread.sleep(sec * MILIS_IN_SEC);
		} catch (InterruptedException e) {
			//do nothing
		}
	}

}
