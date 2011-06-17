package sim.instrumentation.data;

import sim.data.Context;
import sim.data.MethodImpl;
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
		mm = new MethodMetricsImpl(new MethodImpl(ConfigParams.APPLICATION_ID, className, methodName));
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

	public String getName() {
		return mm.getMethod().getClassName() + "." + mm.getMethod().getMethodName();
	}

	private void beginReadMetters(MethodMetricsImpl measurement) {
		MetricsUtil.beginReadMethodMetters(measurement);
	}

	private void endReadMetters(MethodMetricsImpl measurement) {
		MetricsUtil.endReadMethodMetters(measurement);
		Context c = ContextManager.getCurrentContext();
		if (c != null)
			mm.setContextId(c.getId());
	}

	private void publishMeasurement(MethodMetrics measurement) {
		Collector.addMeasurement(measurement);
	}
}