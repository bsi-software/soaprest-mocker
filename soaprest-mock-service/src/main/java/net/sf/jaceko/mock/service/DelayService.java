package net.sf.jaceko.mock.service;

import java.text.MessageFormat;

import org.apache.log4j.Logger;

public class DelayService {
	private static final Logger LOG = Logger.getLogger(DelayService.class);
	private static final int MILIS_IN_SEC = 1000;

	public void delaySec(int sec) {
		
		if (sec == 0)
			return;
		LOG.debug(MessageFormat.format("Delaying request for {0} seconds.", sec));
		try {
			Thread.sleep(sec * MILIS_IN_SEC);
		} catch (InterruptedException e) {
			// do nothing
		}
	}

}
