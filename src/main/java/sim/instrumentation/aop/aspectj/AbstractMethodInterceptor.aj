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

import java.lang.reflect.Method;
import java.util.Deque;
import java.util.LinkedList;

import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

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

	before(): methodExecution() {
		//System.out.println("aop before " + thisJoinPointStaticPart.toLongString());
		MethodSignature sig = (MethodSignature)thisJoinPointStaticPart.getSignature();
		MethodProbe mp = Probe.createMethodProbe(sig.getDeclaringTypeName(), sig.getName());
		mp.start();
		push(mp);
	}

	after() returning: methodExecution() {
		//System.out.println("aop after returning " + thisJoinPointStaticPart.toLongString());
		MethodProbe mp = pop();
		if (mp != null) {
			mp.end();
		}
	}

	after() throwing(Throwable t): methodExecution() {
		//System.out.println("aop after throwing " + thisJoinPointStaticPart.toLongString());
		MethodProbe mp = pop();
		if (mp != null) {
			mp.endWithException(t);
		}
	}

	private static ThreadLocal<Deque<MethodProbe>> tlProbes = new ThreadLocal<Deque<MethodProbe>>();

	private static void push(MethodProbe mp) {
		Deque<MethodProbe> probes = tlProbes.get();
		if (probes == null) {
			probes = new LinkedList<MethodProbe>();
			tlProbes.set(probes);
		}
		probes.push(mp);
	}

	private static MethodProbe pop() {
		Deque<MethodProbe> probes = tlProbes.get();
		if (probes == null) {
			return null;
		}
		if (probes.size() == 0) {
			return null;
		}
		return probes.pop();
	}
}
