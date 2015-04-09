package com.harvest.shared;

import java.util.concurrent.ThreadFactory;

/**
 * Factory used to create daemon threads; used by thread pools
 *
 */
public class DaemonThreadFactory implements ThreadFactory {
	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(r);
		t.setDaemon(true);
		return t;
	}
}
