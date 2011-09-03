package sim.instrumentation.data;

import java.util.HashMap;
import java.util.Map;

import sim.data.Context;

public class EventProbe {
	private String name;
	private Map<String, Object> values = new HashMap<String, Object>();

	EventProbe(String name) {
		this.name = name;
	}

	public EventProbe set(String key, Object value) {
		values.put(key, value);
		return this;
	}

	public void fire() {
		Context c = ContextManager.createNewContext(name, name);
		c.putAll(values);
		ContextManager.destroyCurrentContext();
		Collector.addMeasurement(c);
	}
}