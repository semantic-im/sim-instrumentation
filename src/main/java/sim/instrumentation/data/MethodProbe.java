package sim.instrumentation.data;


/**
 * Probe for making method specific measurements.
 * 
 * @author mcq
 * 
 */
public class MethodProbe {

	private boolean started = false;
	private boolean ended = false;
	private MethodMetricsImpl mm;

	MethodProbe(String className, String methodName) {
		mm = new MethodMetricsImpl(className, methodName);
	}

	public MethodProbe set(String key, Object value) {
		if (ended)
			throw new IllegalStateException("Method probe already ended!");
		mm.set(key, value);
		return this;
	}

	public void begin() {
		if (ended)
			throw new IllegalStateException("Method probe already ended!");
		started = true;
		beginReadMetters();
	}

	public void end() {
		if (ended)
			throw new IllegalStateException("Method probe already ended!");
		if (!started)
			throw new IllegalStateException("Method probe not started!");
		endReadMetters();
		ended = true;
	}

	public void endWithException(Throwable t) {
		if (ended)
			throw new IllegalStateException("Method probe already ended!");
		mm.setEndedWithError(true);
		mm.setException(t.toString());
		endReadMetters();
		ended = true;
	}

	private void beginReadMetters() {
		Metrics.beginReadMethodMetters(mm);
	}

	private void endReadMetters() {
		Metrics.endReadMethodMetters(mm);
	}
}