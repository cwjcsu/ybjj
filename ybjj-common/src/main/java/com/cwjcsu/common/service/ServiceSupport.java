package com.cwjcsu.common.service;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public abstract class ServiceSupport implements Service {

	private static final Logger LOG = Logger.getLogger(ServiceSupport.class
			.getName());

	private AtomicBoolean started = new AtomicBoolean(false);
	private AtomicBoolean stopping = new AtomicBoolean(false);
	private AtomicBoolean stopped = new AtomicBoolean(false);

	protected boolean debug = true;

	public static void dispose(Service service) {
		try {
			service.stop();
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Could not stop service: " + service
					+ ". Reason: " + e, e);
		}
	}

	protected void init() throws Exception {

	}

	@PostConstruct
	public void start() throws Exception {
		if (started.compareAndSet(false, true)) {
			boolean success = false;
			try {
				init();
				doStart();
				success = true;
			} finally {
				started.set(success);
			}
		}
	}

	protected static void start(Object service) throws Exception {
		if (service instanceof Service) {
			((Service) service).start();
		}
	}

	protected static void stop(Object service) throws Exception {
		if (service instanceof Service) {
			((Service) service).stop();
		}
	}

	protected void checkStarted() {
		if (!isStarted())
			throw new IllegalStateException("Service not started");
		if (isStopping()) {
			throw new IllegalStateException("Service is stopping");
		}
	}

	@PreDestroy
	public void stop() throws Exception {
		if (stopped.compareAndSet(false, true)) {
			stopping.set(true);
			try {
				doStop();
			} catch (Exception e) {
				stopped.set(false);
				stopping.set(false);
				throw e;
			}
			stopped.set(true);

			stopping.set(false);
		}
	}

	/**
	 * @return true if this service has been started
	 */
	public boolean isStarted() {
		return started.get();
	}

	/**
	 * @return true if this service is in the process of closing
	 */
	public boolean isStopping() {
		return stopping.get();
	}

	/**
	 * @return true if this service is closed
	 */
	public boolean isStopped() {
		return stopped.get();
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean isDebug() {
		return debug;
	}

	protected abstract void doStop() throws Exception;

	protected abstract void doStart() throws Exception;

}
