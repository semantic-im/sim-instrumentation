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

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import sim.data.MethodMetricsImpl;

/**
 * Provides static methods for reading JVM statistics.
 * 
 * @author mcq
 * 
 */
class MetricsUtil {
	private static AtomicBoolean canReadProcessTotalTime = new AtomicBoolean(true);

	static {
		ThreadMXBean t = ManagementFactory.getThreadMXBean();
		if (t.isThreadCpuTimeSupported() && !t.isThreadCpuTimeEnabled())
			t.setThreadCpuTimeEnabled(true);
		if (t.isThreadContentionMonitoringSupported() && !t.isThreadContentionMonitoringEnabled())
			t.setThreadContentionMonitoringEnabled(true);
	}

	public static void beginReadMethodMetters(MethodMetricsImpl mm) {
		// wall clock time
		mm.setBeginExecutionTime(System.currentTimeMillis());
		// thread metrics
		ThreadMXBean t = ManagementFactory.getThreadMXBean();
		mm.setThreadTotalCpuTime(t.getCurrentThreadCpuTime());
		mm.setThreadUserCpuTime(t.getCurrentThreadUserTime());
		mm.setThreadCount(t.getTotalStartedThreadCount());
		ThreadInfo ti = t.getThreadInfo(Thread.currentThread().getId());
		mm.setThreadBlockCount(ti.getBlockedCount());
		mm.setThreadBlockTime(ti.getBlockedTime());
		mm.setThreadWaitCount(ti.getWaitedCount());
		mm.setThreadWaitTime(ti.getWaitedTime());
		// process time
		mm.setProcessTotalCpuTime(readProcessCpuTime());
		// gcc metrics
		DoubleLongResult gcc = readGccMetrics();
		mm.setThreadGccCount(gcc.long1);
		mm.setThreadGccTime(gcc.long2);
	}

	public static void endReadMethodMetters(MethodMetricsImpl mm) {
		// wall clock time
		long endTime = System.currentTimeMillis();
		mm.setEndExecutionTime(endTime);
		mm.setWallClockTime(endTime - mm.getBeginExecutionTime());
		// thread metrics
		ThreadMXBean t = ManagementFactory.getThreadMXBean();
		mm.setThreadTotalCpuTime((t.getCurrentThreadCpuTime() - mm.getThreadTotalCpuTime()) / 1000000);
		mm.setThreadUserCpuTime((t.getCurrentThreadUserTime() - mm.getThreadUserCpuTime()) / 1000000);
		mm.setThreadSystemCpuTime(mm.getThreadTotalCpuTime() - mm.getThreadUserCpuTime());
		mm.setThreadCount(t.getTotalStartedThreadCount() - mm.getThreadCount());
		ThreadInfo ti = t.getThreadInfo(Thread.currentThread().getId());
		mm.setThreadBlockCount(ti.getBlockedCount() - mm.getThreadBlockCount());
		mm.setThreadBlockTime(ti.getBlockedTime() - mm.getThreadBlockTime());
		mm.setThreadWaitCount(ti.getWaitedCount() - mm.getThreadWaitCount());
		mm.setThreadWaitTime(ti.getWaitedTime() - mm.getThreadWaitTime());
		// process time
		mm.setProcessTotalCpuTime((readProcessCpuTime() - mm.getProcessTotalCpuTime()) / 1000000);
		// gcc metrics
		DoubleLongResult gcc = readGccMetrics();
		mm.setThreadGccCount(gcc.long1 - mm.getThreadGccCount());
		mm.setThreadGccTime(gcc.long2 - mm.getThreadGccTime());
	}

	private static long readProcessCpuTime() {
		long processCpuTime = 0;
		if (canReadProcessTotalTime.get()) {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			try {
				ObjectName os = new ObjectName(ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME);
				processCpuTime = ((Long) mbs.getAttribute(os, "ProcessCpuTime")).longValue();
			} catch (Exception e) {
				canReadProcessTotalTime.set(false);
			}
		}
		return processCpuTime;
	}

	private static class DoubleLongResult {
		private long long1;
		private long long2;
	}

	private static DoubleLongResult readGccMetrics() {
		List<GarbageCollectorMXBean> gccList = ManagementFactory.getGarbageCollectorMXBeans();
		long gccCount = 0;
		long gccTime = 0;
		for (GarbageCollectorMXBean gcc : gccList) {
			gccCount += gcc.getCollectionCount();
			gccTime += gcc.getCollectionTime();
		}
		DoubleLongResult result = new DoubleLongResult();
		result.long1 = gccCount;
		result.long2 = gccTime;
		return result;
	}
}
