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
import java.lang.management.MemoryUsage;
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
		GccMetrics gcc = readGccMetrics();
		mm.setThreadGccCount(gcc.gccCount);
		mm.setThreadGccTime(gcc.gccTime);
		// memory metrics
		MemoryMetrics mem = readMemoryMetrics();
		mm.setAllocatedMemoryBefore(mem.allocated);
		mm.setUsedMemoryBefore(mem.used);
		mm.setFreeMemoryBefore(mem.free);
		mm.setUnallocatedMemoryBefore(mem.unallocated);
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
		GccMetrics gcc = readGccMetrics();
		mm.setThreadGccCount(gcc.gccCount - mm.getThreadGccCount());
		mm.setThreadGccTime(gcc.gccTime - mm.getThreadGccTime());
		// memory metrics
		MemoryMetrics mem = readMemoryMetrics();
		mm.setAllocatedMemoryAfter(mem.allocated);
		mm.setUsedMemoryAfter(mem.used);
		mm.setFreeMemoryAfter(mem.free);
		mm.setUnallocatedMemoryAfter(mem.unallocated);
	}

	static PlatformMetricsImpl readPlatformMetrics(PlatformMetricsImpl previous) {
		PlatformMetricsImpl result = new PlatformMetricsImpl(ConfigParams.APPLICATION_ID);
		// read old values
		long oldTotalGccCount = 0;
		long oldTotalGccTime = 0;
		long oldTotalCpuTime = 0;
		long oldCreationTime = 0;
		long oldTotalThreadsStarted = 0;
		if (previous != null) {
			oldTotalGccCount = previous.getTotalGccCount();
			oldTotalGccTime = previous.getTotalGccTime();
			oldTotalCpuTime = previous.getTotalCpuTime();
			oldCreationTime = previous.getCreationTime();
			oldTotalThreadsStarted = previous.getTotalThreadsStarted();
		}
		// gcc metrics
		GccMetrics gcc = readGccMetrics();
		result.setTotalGccCount(gcc.gccCount);
		result.setTotalGccTime(gcc.gccTime);
		result.setGccCount(gcc.gccCount - oldTotalGccCount);
		result.setGccTime(gcc.gccTime - oldTotalGccTime);
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
		double cpuUsage = (double) (totalCpuTime - oldTotalCpuTime) / (double) (result.getCreationTime() - oldCreationTime);
		result.setCpuUsage(cpuUsage);
		// cpu time
		result.setCpuTime(totalCpuTime - oldTotalCpuTime);
		// memory metrics
		MemoryMetrics mem = readMemoryMetrics();
		result.setAllocatedMemory(mem.allocated);
		result.setUsedMemory(mem.used);
		result.setFreeMemory(mem.free);
		result.setUnallocatedMemory(mem.unallocated);
		// thread metrics
		ThreadMXBean t = ManagementFactory.getThreadMXBean();
		result.setThreadsCount(t.getThreadCount());
		result.setTotalThreadsStarted(t.getTotalStartedThreadCount());
		result.setThreadsStarted(t.getTotalStartedThreadCount() - oldTotalThreadsStarted);
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

	private static class GccMetrics {
		private long gccCount;
		private long gccTime;
	}

	private static GccMetrics readGccMetrics() {
		List<GarbageCollectorMXBean> gccList = ManagementFactory.getGarbageCollectorMXBeans();
		long gccCount = 0;
		long gccTime = 0;
		for (GarbageCollectorMXBean gcc : gccList) {
			gccCount += gcc.getCollectionCount();
			gccTime += gcc.getCollectionTime();
		}
		GccMetrics result = new GccMetrics();
		result.gccCount = gccCount;
		result.gccTime = gccTime;
		return result;
	}

	private static class MemoryMetrics {
		private long allocated;
		private long used;
		private long free;
		private long unallocated;
	}

	private static MemoryMetrics readMemoryMetrics() {
		MemoryMXBean m = ManagementFactory.getMemoryMXBean();
		MemoryMetrics result = new MemoryMetrics();
		MemoryUsage mu = m.getHeapMemoryUsage();
		result.allocated = mu.getCommitted();
		result.used = mu.getUsed();
		result.free = mu.getCommitted() - mu.getUsed();
		result.unallocated = mu.getMax() - mu.getCommitted();
		return result;
	}
}
