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

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.SuppressAjWarnings;

/**
 * Base abstract aspect used to instrument code indicated by pointcut 
 * <code>methodsToInstrument</code>.
 * <p>
 * In order to instrument methods or constructors one needs to define 
 * a concrete aspect of <code>AbstractMethodInstrumentation</code> 
 * that implements the <code>methodsToInstrument</code> pointcut.
 * <code>methodsToInstrument</code> is the place to express the join 
 * points we are interested to instrument.
 * <p>
 * For example instrumenting <code>myMethod</code> from <code>MyClass</code> 
 * could be done like this:
 * <pre>
 * 		public aspect MyMethodInstrumentation extends AbstractMethodInstrumentation {
 * 			public pointcut methodsToInstrument(): execution(* MyClass.myMethod(..));
 * 		}
 * </pre>
 * 
 * @see sim.instrumentation.aop.aspectj.MethodInstrumentationUtil
 * @see sim.instrumentation.aop.aspectj.AnnotationMethodInstrumentation
 * @see sim.instrumentation.aop.aspectj.AnnotationTypeInstrumentation
 * 
 * @author mcq
 * 
 */
public abstract aspect AbstractMethodInstrumentation {
	public abstract pointcut methodsToInstrument();
	
	@SuppressAjWarnings
	before(): methodsToInstrument() {
		beforeInvoke(thisJoinPoint);
	}

	@SuppressAjWarnings
	after() returning: methodsToInstrument() {
		afterInvoke(thisJoinPoint, null);
	}

	@SuppressAjWarnings
	after() throwing(Throwable t): methodsToInstrument() {
		afterInvoke(thisJoinPoint, t);
	}
	
	protected  void beforeInvoke(JoinPoint jp) {
		MethodInstrumentationUtil.beforeInvoke(jp);
	}
	
	protected  void afterInvoke(JoinPoint jp, Throwable t) {
		MethodInstrumentationUtil.afterInvoke(t);
	}
}
