package sim.instrumentation.data;

import java.util.HashMap;
import java.util.Map;

public class EventProbe {
	private String name;
	private Map<String, Object> context = new HashMap<String, Object>();

	EventProbe(String name) {
		this.name = name;
	}

	public EventProbe set(String key, Object value) {
		context.put(key, value);
		return this;
	}

	public void fire() {}
}