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

import sim.data.Context;
import sim.instrumentation.data.ContextManager;

/**
 * This abstract aspect provides support for counters per current context.
 * <p>
 * Users should extend this abstract aspect and provide the pointcut where 
 * the counter should be incremented. By default the counter name is the 
 * aspect name. To provide a different name for the counter method 
 * getCounterName() should be overridden.
 * <p>
 * Example to create a counter when method <code>myMethod</code> from 
 * <code>MyClass</code> is executed:
 * <pre>
 * 		public aspect MyCounter extends AbstractContextCounter {
 * 			public pointcut pointcutToIncrementCounter(): execution(* MyClass.myMethod(..));
 * 			protected String getCounterName(JoinPoint jp) {
 * 				return "MyCustomCounterName";
 * 			}
 * 		}
 * </pre>
 * 
 * @author mcq
 *
 */
public abstract aspect AbstractContextCounter {
	private static final Long COUNT_ONE = new Long(1L);
	
	public abstract pointcut pointcutToIncrementCounter();
	
	protected String getCounterName(JoinPoint jp, Context c) {
		return this.getClass().getSimpleName();
	}
	
	@SuppressAjWarnings
	before(): pointcutToIncrementCounter() {
		Context c = ContextManager.getCurrentContext();
		if (c == null)
			return;
		increment(getCounterName(thisJoinPoint, c), c);
	}
	
	private void increment(String key, Context c) {
		Object count = c.get(key);
		if (count == null) {
			c.put(key, COUNT_ONE);
		} else {
			Long v = (Long)count;
			c.put(key, new Long(v.longValue() + 1));
		}
	}
}
