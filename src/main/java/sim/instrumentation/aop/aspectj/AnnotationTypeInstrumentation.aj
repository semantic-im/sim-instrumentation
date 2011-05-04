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

import java.lang.reflect.Modifier;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;

import sim.instrumentation.annotation.Instrument;
import sim.instrumentation.annotation.InstrumentationScope;

/**
 * Aspect that instruments all types (classes, interfaces) marked by 
 * {@link sim.instrumentation.annotation.Instrument @Instrument} annotation.
 * 
 * @see sim.instrumentation.aop.aspectj.AbstractMethodInstrumentation
 * @see sim.instrumentation.annotation.Instrument
 * 
 * @author mcq
 *
 */
final aspect AnnotationTypeInstrumentation extends AbstractMethodInstrumentation {
	public pointcut methodsToInstrument(): within(@sim.instrumentation.annotation.Instrument *) && 
											(execution(* *(..)) || execution(*.new(..)));
	@Override
	protected void beforeInvoke(JoinPoint jp) {
		if (checkScope(jp))
			super.beforeInvoke(jp);
	}

	@Override
	protected void afterInvoke(JoinPoint jp, Throwable t) {
		if (checkScope(jp))
			super.afterInvoke(jp, t);
	}
	
	private static boolean checkScope(JoinPoint jp) {
		Signature sig = jp.getStaticPart().getSignature();
		@SuppressWarnings("unchecked")
		Instrument i = (Instrument) sig.getDeclaringType().getAnnotation(Instrument.class);
		if (i.value() == InstrumentationScope.ALL)
			return true;
		int mod = sig.getModifiers();
		if (Modifier.isPublic(mod) && (i.value() == InstrumentationScope.PUBLIC))
			return true;
		if (Modifier.isPrivate(mod) && (i.value() == InstrumentationScope.PRIVATE))
			return true;
		return false;		
	}
}
