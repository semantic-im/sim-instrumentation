/*
 * Copyright 2010 Softgress - http://www.softgress.com/
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sim.instrumentation.data;

import java.util.UUID;

import sim.data.ApplicationId;

/**
 * One place where to store configurations parameters.
 * 
 * @author mcq
 * 
 */
class ConfigParams {
	static final ApplicationId APPLICATION_ID;
	static final long MEASURABLE_THRESHOLD;
	static final String AGENT_LOCATION;
	static final long COLLECT_INTERVAL;
	static final long MAX_METRICS_WRITE;
	static final int TIMEOUT;

	static {
		String appName = System.getProperty("sim.application.name", "not set");
		String appId = UUID.randomUUID().toString();
		APPLICATION_ID = new ApplicationId(appId, appName);
		MEASURABLE_THRESHOLD = Long.parseLong(System.getProperty("sim.measurable.threshold", "100"));
		AGENT_LOCATION = System.getProperty("sim.agent.location", "http://localhost:8088/agent");
		COLLECT_INTERVAL = Long.parseLong(System.getProperty("sim.collect.interval", "5000"));
		MAX_METRICS_WRITE = Long.parseLong(System.getProperty("sim.max.metrics.write", "1000"));
		TIMEOUT = Integer.parseInt(System.getProperty("sim.agent.timeout", "5000"));
	}

}
