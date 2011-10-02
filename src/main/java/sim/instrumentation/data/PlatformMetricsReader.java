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

import sim.data.PlatformMetricsImpl;

/**
 * Starts a thread that every 5 seconds reads the platform metrics and sends
 * them to the collector.
 * 
 * @author mcq
 * 
 */
class PlatformMetricsReader {

	static void start() {
		PlatformMetricsReaderThread pmrt = new PlatformMetricsReaderThread();
		pmrt.start();
	}

	private static class PlatformMetricsReaderThread extends Thread {

		public PlatformMetricsReaderThread() {
			super("SIM - PlatformMetricsReaderThread");
			setDaemon(true);
		}

		@Override
		public void run() {
			PlatformMetricsImpl pm = null;
			while (true) {
				try {
					sleep(ConfigParams.PLATFORM_METRICS_COLLECT_INTERVAL);
				} catch (InterruptedException e) {
					break;
				}
				pm = MetricsUtil.readPlatformMetrics(pm);
				Collector.addMeasurement(pm);
			}
		}

	}
}
