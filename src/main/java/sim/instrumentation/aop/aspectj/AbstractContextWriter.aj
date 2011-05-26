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

import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.aspectj.lang.reflect.CatchClauseSignature;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.FieldSignature;
import org.aspectj.lang.reflect.MethodSignature;

import sim.instrumentation.data.ContextManager;

/**
 * Base abstract aspect used to indicate a join point where values
 * that are available at that join point should be written/published
 * to the context of the current execution flow. 
 * <p>
 * <code>placeToTriggerTheContextWrite</code> pointcut defines the 
 * joint point where the reading of the values we want to publish to 
 * the context should happen. It can be a method or a constructor 
 * execution, a field get or set or an exception handler.
 * <p>
 * In order to publish values to the current context of the execution 
 * flow, one needs to define a concrete aspect of 
 * <code>AbstractContextWriter</code> that implements the 
 * <code>placeToTriggerTheContextWrite</code> pointcut.
 * <p>
 * Optionally, the concrete aspect can also override 
 * <code>readValuesBefore</code> and/or <code>readValuesAfter</code>
 * methods in order to publish custom values to the context. 
 * <p>
 * Example to publish values to the context when method <code>myMethod</code> 
 * from <code>MyClass</code> is executed:
 * <pre>
 * 		public aspect MyContextWriter extends AbstractContextWriter {
 * 			public pointcut placeToTriggerTheContextWrite(): execution(* MyClass.myMethod(..));
 * 		}
 * </pre>
 * 
 * @see sim.instrumentation.data.ContextManager
 * 
 * @author mcq
 *
 */
public abstract aspect AbstractContextWriter {
	public abstract pointcut placeToTriggerTheContextWrite();
	
	@SuppressAjWarnings
	before(): placeToTriggerTheContextWrite() {
		beforeInvoke(thisJoinPoint);
	}
	
	@SuppressAjWarnings
	after() returning(Object result): placeToTriggerTheContextWrite() {
		afterReturningInvoke(thisJoinPoint, result);
	}
	
	@SuppressAjWarnings
	after(): placeToTriggerTheContextWrite() {
		afterInvoke(thisJoinPoint);
	}
	
	protected  void beforeInvoke(JoinPoint jp) {
		ContextManager.getCurrentContext().putAll(readValuesBeforeExecution(jp));
	}
	
	protected  void afterReturningInvoke(JoinPoint jp, Object result) {
		ContextManager.getCurrentContext().putAll(readValuesAfterReturningExecution(jp, result));
	}
	
	protected  void afterInvoke(JoinPoint jp) {}

	/**
	 * Returns the values to be written to the current context of the current 
	 * execution flow context before the execution of the pointcut 
	 * <code>placeToTriggerTheContextWrite</code>.
	 * <p> 
	 * Default implementation returns the arguments (if there are any) in case 
	 * the executing joint point is a method or constructor, the field value in 
	 * case of a field set and the exception message in case of exception.
	 * 
	 * @param jp the join point to trigger the reading of values
	 * @return a hash map of the values read
	 */
	protected Map<String, Object> readValuesBeforeExecution(JoinPoint jp) {
		Map<String, Object> result = new HashMap<String, Object>();
		Signature sig = jp.getStaticPart().getSignature();
		Object[] args = jp.getArgs();
		String name = sig.getDeclaringTypeName() + "." + sig.getName();
		if (sig instanceof CatchClauseSignature) {
			result.put(name + ".exception", args[0]);
		} else if (sig instanceof FieldSignature) {
			if (jp.getKind().equals(JoinPoint.FIELD_SET))
				result.put(name + ".value", args[0]);
		} else if (sig instanceof CodeSignature) {
			CodeSignature csig = (CodeSignature)sig;
			String[] names = csig.getParameterNames();
			for (int i = 0; i < Math.min(names.length, args.length); i++) {
				result.put(name + ".params." + names[i], args[i]);
			}
		}
		return result;
	}
	
	/**
	 * Returns the values to be written to the current context of the current 
	 * execution flow context after the execution of the pointcut 
	 * <code>placeToTriggerTheContextWrite</code>.
	 * <p> 
	 * Default implementation returns the return value (if there is any) in case 
	 * the executing joint point is a method and the field value in case of a 
	 * field get.
	 * 
	 * @param jp the join point to trigger the reading of values
	 * @param jpResult the join point result
	 * @return a hash map of the values read
	 */
	protected Map<String, Object> readValuesAfterReturningExecution(JoinPoint jp, Object jpResult) {
		Map<String, Object> result = new HashMap<String, Object>();
		Signature sig = jp.getStaticPart().getSignature();
		String name = sig.getDeclaringTypeName() + "." + sig.getName();
		if (sig instanceof MethodSignature) {
			result.put(name + ".result", jpResult);
		} else if (sig instanceof FieldSignature) {			
			result.put(name + ".value", jpResult);
		}
		return result;
	}
}
