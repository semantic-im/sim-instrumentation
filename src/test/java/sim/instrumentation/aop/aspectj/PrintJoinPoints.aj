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

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;

/**
 * @author mcq
 *
 */
public aspect PrintJoinPoints {
	pointcut joinpoints(): !within(PrintJoinPoints) &&
							within(sim.instrumentation.aop.aspectj.PrintAllJoinPoints) &&
							execution(sim.instrumentation.aop.aspectj.PrintAllJoinPoints.new(..));
	
	before(): joinpoints() {
		print(thisJoinPoint, null);
	}
	
	after() returning(Object result): joinpoints() {
		print(thisJoinPoint, result);
	}
	
	void print(JoinPoint jp, Object result) {
		System.out.println("------------------------------>");
		System.out.println(jp.toLongString());
		System.out.println(jp.getKind());
		System.out.println(jp.getSignature().getName());
		if (jp.getSignature() instanceof CodeSignature)
			System.out.println(Arrays.toString(((CodeSignature)jp.getSignature()).getParameterNames()));
		System.out.println(Arrays.toString(jp.getArgs()));
		System.out.println(result);
		System.out.println("<------------------------------");
	}
}
