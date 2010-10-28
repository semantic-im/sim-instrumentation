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

import java.util.HashMap;
import java.util.Map;

import sim.data.MethodMetrics;

/**
 * Implementation for {@link MethodMeasurement}.
 * 
 * @author mcq
 * 
 */
class MethodMetricsImpl implements MethodMetrics {
	private static final long serialVersionUID = 1L;
	private String methodName;
	private String className;
	private Map<String, Object> context = new HashMap<String, Object>();
	private String exception;
	private boolean endedWithError = false;
	private long beginExecutionTime;
	private long endExecutionTime;
	private long wallClockTime;
	private long threadUserCpuTime;
	private long threadSystemCpuTime;
	private long threadTotalCpuTime;
	private long threadCount;
	private long threadBlockCount;
	private long threadBlockTime;
	private long threadWaitCount;
	private long threadWaitTime;
	private long threadGccCount;
	private long threadGccTime;
	private long processTotalCpuTime;

	public MethodMetricsImpl(String className, String methodName) {
		this.methodName = methodName;
		this.className = className;
	}

	public void set(String key, Object value) {
		context.put(key, value);
	}

	@Override
	public String getMethodName() {
		return methodName;
	}

	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public Map<String, Object> getContext() {
		return context;
	}

	@Override
	public String getException() {
		return exception;
	}

	@Override
	public boolean endedWithError() {
		return endedWithError;
	}

	@Override
	public long getBeginExecutionTime() {
		return beginExecutionTime;
	}

	@Override
	public long getEndExecutionTime() {
		return endExecutionTime;
	}

	@Override
	public long getWallClockTime() {
		return wallClockTime;
	}

	@Override
	public long getThreadUserCpuTime() {
		return threadUserCpuTime;
	}

	@Override
	public long getThreadSystemCpuTime() {
		return threadSystemCpuTime;
	}

	@Override
	public long getThreadTotalCpuTime() {
		return threadTotalCpuTime;
	}

	@Override
	public long getThreadCount() {
		return threadCount;
	}

	@Override
	public long getThreadBlockCount() {
		return threadBlockCount;
	}

	@Override
	public long getThreadBlockTime() {
		return threadBlockTime;
	}

	@Override
	public long getThreadWaitCount() {
		return threadWaitCount;
	}

	@Override
	public long getThreadWaitTime() {
		return threadWaitTime;
	}

	@Override
	public long getThreadGccCount() {
		return threadGccCount;
	}

	@Override
	public long getThreadGccTime() {
		return threadGccTime;
	}

	@Override
	public long getProcessTotalCpuTime() {
		return processTotalCpuTime;
	}

	void setException(String exception) {
		this.exception = exception;
	}

	void setEndedWithError(boolean endedWithError) {
		this.endedWithError = endedWithError;
	}

	void setBeginExecutionTime(long beginExecutionTime) {
		this.beginExecutionTime = beginExecutionTime;
	}

	void setEndExecutionTime(long endExecutionTime) {
		this.endExecutionTime = endExecutionTime;
	}

	void setWallClockTime(long wallClockTime) {
		this.wallClockTime = wallClockTime;
	}

	void setThreadUserCpuTime(long threadUserCpuTime) {
		this.threadUserCpuTime = threadUserCpuTime;
	}

	void setThreadSystemCpuTime(long threadSystemCpuTime) {
		this.threadSystemCpuTime = threadSystemCpuTime;
	}

	void setThreadTotalCpuTime(long threadTotalCpuTime) {
		this.threadTotalCpuTime = threadTotalCpuTime;
	}

	void setThreadCount(long threadCount) {
		this.threadCount = threadCount;
	}

	void setThreadBlockCount(long threadBlockCount) {
		this.threadBlockCount = threadBlockCount;
	}

	void setThreadBlockTime(long threadBlockTime) {
		this.threadBlockTime = threadBlockTime;
	}

	void setThreadWaitCount(long threadWaitCount) {
		this.threadWaitCount = threadWaitCount;
	}

	void setThreadWaitTime(long threadWaitTime) {
		this.threadWaitTime = threadWaitTime;
	}

	void setThreadGccCount(long threadGccCount) {
		this.threadGccCount = threadGccCount;
	}

	void setThreadGccTime(long threadGccTime) {
		this.threadGccTime = threadGccTime;
	}

	void setProcessTotalCpuTime(long processTotalCpuTime) {
		this.processTotalCpuTime = processTotalCpuTime;
	}

}
