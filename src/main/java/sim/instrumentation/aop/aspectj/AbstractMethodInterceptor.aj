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
package sim.instrumentation.aop.aspectj;

import java.util.ArrayDeque;
import java.util.Deque;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import sim.instrumentation.data.ExecutionFlowContext;
import sim.instrumentation.data.MethodProbe;
import sim.instrumentation.data.Probe;

/**
 * Aspect responsible for triggering events when methods we are interested in
 * are executed.
 * 
 * @author mcq
 * 
 */
public abstract aspect AbstractMethodInterceptor {
	public abstract pointcut methodExecution();
	
	protected void beforeInvoke(JoinPoint jp) {}
	
	protected void afterInvoke() {}
	
	before(): methodExecution() {
		//System.out.println("aop before " + thisJoinPointStaticPart.toLongString());
		beforeInvoke(thisJoinPoint);
		Signature sig = thisJoinPointStaticPart.getSignature();
		MethodProbe mp = Probe.createMethodProbe(sig.getDeclaringTypeName(), sig.getName());
		mp.start();
		push(mp);		
	}

	after() returning: methodExecution() {
		//System.out.println("aop after returning " + thisJoinPointStaticPart.toLongString());
		MethodProbe mp = pop();				
		if (mp != null) {
			copyContext(mp);
			mp.end();
		}
		afterInvoke();
	}

	after() throwing(Throwable t): methodExecution() {
		//System.out.println("aop after throwing " + thisJoinPointStaticPart.toLongString());
		MethodProbe mp = pop();
		if (mp != null) {
			copyContext(mp);
			mp.endWithException(t);
		}
		afterInvoke();
	}

	private void copyContext(MethodProbe mp) {
		mp.addToContext(ExecutionFlowContext.getFullContext());
	}

	private static final ThreadLocal<Deque<MethodProbe>> tlProbes = new ThreadLocal<Deque<MethodProbe>>();

	private static void push(MethodProbe mp) {
		Deque<MethodProbe> probes = tlProbes.get();
		if (probes == null) {
			probes = new ArrayDeque<MethodProbe>();
			tlProbes.set(probes);
		}
		probes.addFirst(mp);
	}

	private static MethodProbe pop() {
		Deque<MethodProbe> probes = tlProbes.get();
		if (probes == null) {
			return null;
		}		
		return probes.pollFirst();
	}
}
