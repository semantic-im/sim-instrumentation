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

import sim.instrumentation.data.MethodProbe;
import sim.instrumentation.data.Probe;

/**
 * Internal utility class that implements all the logic for performing
 * instrumentation for a method execution.
 * <p>
 * When a method execution needs to be instrumented <code>beforeInvoke</code>
 * and <code>afterInvoke</code> is being called before and after the method
 * execution.
 * 
 * @see sim.instrumentation.aop.aspectj.AbstractMethodInstrumentation
 * 
 * @author mcq
 * 
 */
public final class MethodInstrumentationUtil {
	private static final ThreadLocal<Deque<MethodProbe>> tlProbes = new ThreadLocal<Deque<MethodProbe>>();

	static void beforeInvoke(JoinPoint jp) {
		Signature sig = jp.getStaticPart().getSignature();
		MethodProbe mp = Probe.createMethodProbe(sig.getDeclaringTypeName(), sig.getName());
		mp.start();
		push(mp);
	}

	static void afterInvoke(Throwable t) {
		MethodProbe mp = pop();
		if (mp != null) {
			if (t == null)
				mp.end();
			else
				mp.endWithException(t);
		}
	}

	/**
	 * Used by testing code to find out if a certain method is actually
	 * instrumented. The method needs to be executed in order for this test to
	 * work.
	 * 
	 * @param className the class name of the tested method
	 * @param methodName the name of the tested method
	 * @return true if the tested method is instrumented, false otherwise
	 */
	public static boolean isMethodInstrumented(String className, String methodName) {
		Deque<MethodProbe> probes = tlProbes.get();
		if (probes == null) {
			return false;
		}
		MethodProbe mp = probes.peekFirst();
		if (mp == null) {
			return false;
		}
		return mp.getName().equals(className + "." + methodName);
	}

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
