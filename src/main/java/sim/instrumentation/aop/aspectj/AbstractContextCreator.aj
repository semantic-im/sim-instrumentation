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
import org.aspectj.lang.Signature;

import sim.instrumentation.data.ContextManager;

/**
 * Base abstract aspect used to indicate a method or constructor where a 
 * new context should be created for the current execution flow.
 * <p>
 * In order to create a new context for the current execution flow, one 
 * needs to define a concrete aspect of <code>AbstractContextCreator</code> 
 * that implements the <code>methodToCreateNewContext</code> pointcut, 
 * which defines the method or constructor that will create a new context
 * when executed.
 * <p>
 * Optionally, the concrete aspect can also override 
 * <code>getContextNameAndTag</code> method in order to define a custom 
 * name and tag for the new created context. Default implementation returns 
 * the class name of this joint point as the Name of the new context and 
 * the package name as the Tag.
 * <p>
 * Example to create a new context when method <code>myMethod</code> from 
 * <code>MyClass</code> is executed:
 * <pre>
 * 		public aspect MyNewContext extends AbstractContextCreator {
 * 			public pointcut methodToCreateNewContext(): execution(* MyClass.myMethod(..));
 * 			protected String[] getContextNameAndTag(JoinPoint jp) {
 * 				return new String[] {"CustomContextName", "CustomContextTag"};
 * 			}
 * 		}
 * </pre>
 * 
 * @see sim.instrumentation.data.ContextManager
 * 
 * @author mcq
 *
 */
public abstract aspect AbstractContextCreator {
	public abstract pointcut methodToCreateNewContext();
	
	before(): methodToCreateNewContext() {
		beforeInvoke(thisJoinPoint);
	}
	
	after(): methodToCreateNewContext() {
		afterInvoke(thisJoinPoint);
	}
	
	protected  void beforeInvoke(JoinPoint jp) {
		String[] contextNameAndTag = getContextNameAndTag(jp);
		ContextManager.createNewContext(contextNameAndTag[0], contextNameAndTag[1]);
	}
	
	protected  void afterInvoke(JoinPoint jp) {
		ContextManager.destroyCurrentContext();
	}
	
	/**
	 * Returns the Name and Tag to use for the new execution flow context.
	 * Default implementation returns the class name of this joint point as the 
	 * Name of the new context and the package name as the Tag.
	 * 
	 * @param jp	the join point of the executing method
	 * @return		the Name and Tag for the new execution flow context
	 */
	protected String[] getContextNameAndTag(JoinPoint jp) {
		Signature sig = jp.getStaticPart().getSignature();
		String[] result = new String[2];
		result[0] = sig.getDeclaringTypeName();		
		result[1] = sig.getDeclaringType().getPackage().getName();
		return result;
	}
}
