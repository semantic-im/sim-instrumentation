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
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import sim.data.MethodMetricsImpl;
import sim.data.PlatformMetricsImpl;

/**
 * Provides static methods for reading JVM statistics.
 * 
 * @author mcq
 * 
 */
class MetricsUtil {
	private static final AtomicBoolean canReadProcessTotalTime = new AtomicBoolean(true);

	static {
		ThreadMXBean t = ManagementFactory.getThreadMXBean();
		if (t.isThreadCpuTimeSupported() && !t.isThreadCpuTimeEnabled())
			t.setThreadCpuTimeEnabled(true);
		if (t.isThreadContentionMonitoringSupported() && !t.isThreadContentionMonitoringEnabled())
			t.setThreadContentionMonitoringEnabled(true);
	}

	static void beginReadMethodMetters(MethodMetricsImpl mm) {
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
		mm.setProcessTotalCpuTime(readProcessTotalCpuTime());
		// gcc metrics
		DoubleLongResult gcc = readGccMetrics();
		mm.setThreadGccCount(gcc.long1);
		mm.setThreadGccTime(gcc.long2);
	}

	static void endReadMethodMetters(MethodMetricsImpl mm) {
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
		mm.setProcessTotalCpuTime((readProcessTotalCpuTime() - mm.getProcessTotalCpuTime()) / 1000000);
		// gcc metrics
		DoubleLongResult gcc = readGccMetrics();
		mm.setThreadGccCount(gcc.long1 - mm.getThreadGccCount());
		mm.setThreadGccTime(gcc.long2 - mm.getThreadGccTime());
	}

	static PlatformMetricsImpl readPlatformMetrics(PlatformMetricsImpl previous) {
		PlatformMetricsImpl result = new PlatformMetricsImpl(ConfigParams.APPLICATION_ID);
		// gcc metrics
		DoubleLongResult gcc = readGccMetrics();
		result.setTotalGccCount(gcc.long1);
		result.setTotalGccTime(gcc.long2);
		long oldTotalGccCount = 0;
		long oldTotalGccTime = 0;
		if (previous != null) {
			oldTotalGccCount = previous.getTotalGccCount();
			oldTotalGccTime = previous.getTotalGccTime();
		}
		result.setGccCount(gcc.long1 - oldTotalGccCount);
		result.setGccTime(gcc.long2 - oldTotalGccTime);
		// uptime
		RuntimeMXBean r = ManagementFactory.getRuntimeMXBean();
		long uptime = r.getUptime();
		result.setUptime(uptime);
		// cpu total time
		long totalCpuTime = readProcessTotalCpuTime() / 1000000;
		result.setTotalCpuTime(totalCpuTime);
		// avg cpu usage
		double avgCpu = ((double) (totalCpuTime) / (double) uptime);
		result.setAvgCpuUsage(avgCpu);
		// cpu usage
		long oldTotalCpuTime = 0;
		long oldCreationTime = 0;
		if (previous != null) {
			oldTotalCpuTime = previous.getTotalCpuTime();
			oldCreationTime = previous.getCreationTime();
		}
		double cpuUsage = (double) (totalCpuTime - oldTotalCpuTime) / (double) (result.getCreationTime() - oldCreationTime);
		result.setCpuUsage(cpuUsage);
		// cpu time
		result.setCpuTime(totalCpuTime - oldTotalCpuTime);
		// memory used
		result.setUsedMemory(readUsedMemory());
		// free memory
		result.setFreeMemory(Runtime.getRuntime().freeMemory());
		return result;
	}

	private static long readProcessTotalCpuTime() {
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

	private static long readUsedMemory() {
		MemoryMXBean m = ManagementFactory.getMemoryMXBean();
		long result = m.getHeapMemoryUsage().getUsed() + m.getNonHeapMemoryUsage().getUsed();
		return result;
	}
}
