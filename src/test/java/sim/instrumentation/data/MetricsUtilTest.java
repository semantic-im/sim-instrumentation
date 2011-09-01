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

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

import org.junit.Assert;
import org.junit.Test;

import sim.data.ApplicationId;
import sim.data.MethodImpl;
import sim.data.MethodMetricsImpl;
import sim.data.PlatformMetricsImpl;

/**
 * @author mcq
 * 
 */
public class MetricsUtilTest {
	private static ApplicationId appId = new ApplicationId("123", "test");

	/**
	 * Test method for WallClockTime calculation by
	 * {@link sim.instrumentation.data.MetricsUtil}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testWallClockTime() throws Exception {
		MethodMetricsImpl mm = new MethodMetricsImpl(new MethodImpl(appId, this.getClass().getName(),
				"testWallClockTime"));
		MetricsUtil.beginReadMethodMetters(mm);
		Assert.assertFalse(0 == mm.getBeginExecutionTime());
		Thread.sleep(100);
		MetricsUtil.endReadMethodMetters(mm);
		Assert.assertTrue(100 <= mm.getWallClockTime());
		Assert.assertEquals(mm.getEndExecutionTime() - mm.getBeginExecutionTime(), mm.getWallClockTime());
	}

	/**
	 * Test method for testThreadWaitTimeAndCount calculation by
	 * {@link sim.instrumentation.data.MetricsUtil}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testThreadWaitTimeAndCount() throws Exception {
		MethodMetricsImpl mm = new MethodMetricsImpl(new MethodImpl(appId, this.getClass().getName(),
				"testThreadWaitTimeAndCount"));
		MetricsUtil.beginReadMethodMetters(mm);
		Assert.assertFalse(0L == mm.getThreadWaitTime());
		Assert.assertFalse(0L == mm.getThreadWaitCount());
		Thread.sleep(50);
		Thread.sleep(50);
		MetricsUtil.endReadMethodMetters(mm);
		Assert.assertTrue(100 <= mm.getThreadWaitTime());
		Assert.assertEquals(2, mm.getThreadWaitCount());
	}

	@Test
	public void testReadPlatformMetrics() throws Exception {
		PlatformMetricsImpl pm = MetricsUtil.readPlatformMetrics(null);
		Assert.assertFalse(0L == pm.getTotalCpuTime());
		Assert.assertFalse(0L == pm.getCpuTime());
		Assert.assertTrue(pm.getTotalCpuTime() == pm.getCpuTime());
		Assert.assertFalse(0L == pm.getCreationTime());
		Assert.assertFalse(0L == pm.getUptime());
		Assert.assertFalse(0L == pm.getAllocatedMemory());
		Assert.assertFalse(0L == pm.getUsedMemory());
		Assert.assertFalse(0L == pm.getFreeMemory());
		Assert.assertFalse(0L == pm.getUnallocatedMemory());
		Assert.assertFalse(0.0d == pm.getAvgCpuUsage());
		Assert.assertFalse(0.0d == pm.getCpuUsage());
		long x = 0;
		for (int i = 0; i < 1000000; i++) {
			x += i;
		}
		System.out.println(x);
		System.gc();
		PlatformMetricsImpl pm2 = MetricsUtil.readPlatformMetrics(pm);
		Assert.assertFalse(0L == pm2.getTotalCpuTime());
		Assert.assertTrue(pm2.getTotalCpuTime() > pm.getTotalCpuTime());
		Assert.assertFalse(0L == pm2.getCpuTime());
		Assert.assertFalse(0L == pm2.getCreationTime());
		Assert.assertFalse(0L == pm2.getUptime());
		Assert.assertFalse(0L == pm.getAllocatedMemory());
		Assert.assertFalse(0L == pm.getUsedMemory());
		Assert.assertFalse(0L == pm.getFreeMemory());
		Assert.assertFalse(0L == pm.getUnallocatedMemory());
		Assert.assertFalse(0.0d == pm2.getAvgCpuUsage());
		Assert.assertFalse(0.0d == pm2.getCpuUsage());
	}

	@Test
	public void testMemoryMetrics() throws Exception {
		System.runFinalization();
		System.gc();
		System.runFinalization();
		System.gc();

		PlatformMetricsImpl pm = MetricsUtil.readPlatformMetrics(null);
		MemoryUsage mu = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		Assert.assertTrue(pm.getAllocatedMemory() == mu.getCommitted());
		Assert.assertTrue(pm.getUsedMemory() == mu.getUsed());
		Assert.assertTrue(pm.getFreeMemory() == mu.getCommitted() - mu.getUsed());
		Assert.assertTrue(pm.getUnallocatedMemory() == mu.getMax() - mu.getCommitted());

		MethodMetricsImpl mm = new MethodMetricsImpl(new MethodImpl(appId, this.getClass().getName(),
				"testMemoryMetrics"));
		MetricsUtil.beginReadMethodMetters(mm);
		MetricsUtil.endReadMethodMetters(mm);
		mu = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		Assert.assertTrue(mm.getAllocatedMemoryBefore() == mu.getCommitted());
		Assert.assertTrue(mm.getUsedMemoryBefore() == mu.getUsed());
		Assert.assertTrue(mm.getFreeMemoryBefore() == mu.getCommitted() - mu.getUsed());
		Assert.assertTrue(mm.getUnallocatedMemoryBefore() == mu.getMax() - mu.getCommitted());
		Assert.assertTrue(mm.getAllocatedMemoryAfter() == mu.getCommitted());
		Assert.assertTrue(mm.getUsedMemoryAfter() == mu.getUsed());
		Assert.assertTrue(mm.getFreeMemoryAfter() == mu.getCommitted() - mu.getUsed());
		Assert.assertTrue(mm.getUnallocatedMemoryAfter() == mu.getMax() - mu.getCommitted());
	}
}
