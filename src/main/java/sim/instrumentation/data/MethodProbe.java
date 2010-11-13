package sim.instrumentation.data;

import sim.data.MethodMetrics;
import sim.data.MethodMetricsImpl;

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

	public void start() {
		if (ended)
			throw new IllegalStateException("Method probe already ended!");
		beginReadMetters(mm);
		started = true;
	}

	public void end() {
		if (ended)
			throw new IllegalStateException("Method probe already ended!");
		if (!started)
			throw new IllegalStateException("Method probe not started!");
		endReadMetters(mm);
		ended = true;
		publishMeasurement(mm);
	}

	public void endWithException(Throwable t) {
		if (ended)
			throw new IllegalStateException("Method probe already ended!");
		mm.setEndedWithError(true);
		mm.setException(t.toString());
		endReadMetters(mm);
		ended = true;
		publishMeasurement(mm);
	}

	private void beginReadMetters(MethodMetricsImpl measurement) {
		Metrics.beginReadMethodMetters(measurement);
	}

	private void endReadMetters(MethodMetricsImpl measurement) {
		Metrics.endReadMethodMetters(measurement);
	}

	private void publishMeasurement(MethodMetrics measurement) {
		Collector.addMeasurement(measurement);
	}
}