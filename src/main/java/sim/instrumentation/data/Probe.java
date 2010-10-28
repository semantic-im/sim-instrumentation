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

/**
 * Builder for probes. A probe is an object that when activated takes specific
 * probe measurements and sends these measurements to the collector.
 * 
 * @author mcq
 * 
 */
public class Probe {

	public static MethodProbe createMethodProbe(String className,
			String methodName) {
		return new MethodProbe(className, methodName);
	}

	public static EventProbe createEventProbe(String name) {
		return new EventProbe(name);
	}
}
