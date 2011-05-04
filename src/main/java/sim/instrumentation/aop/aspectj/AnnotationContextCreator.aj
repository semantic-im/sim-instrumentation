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

import java.lang.reflect.AnnotatedElement;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;

import sim.instrumentation.annotation.CreateContext;

/**
 * Aspect that creates a new execution flow context on any method or constructor
 * marked by {@link sim.instrumentation.annotation.CreateContext @CreateContext} 
 * annotation.
 * 
 * @see sim.instrumentation.aop.aspectj.AbstractContextCreator
 * @see sim.instrumentation.annotation.CreateContext
 * 
 * @author mcq
 *
 */
final aspect AnnotationContextCreator extends AbstractContextCreator {
	public pointcut methodToCreateNewContext(): execution(@sim.instrumentation.annotation.CreateContext * *(..)) || 
												execution(@sim.instrumentation.annotation.CreateContext *.new(..));

	@Override
	protected String[] getContextNameAndTag(JoinPoint jp) {
		Signature sig = jp.getStaticPart().getSignature();
		AnnotatedElement ae = null;
		if (sig instanceof ConstructorSignature) {
			ae = ((ConstructorSignature)sig).getConstructor();
		} else if (sig instanceof MethodSignature) {
			ae = ((MethodSignature)sig).getMethod();
		}
		CreateContext c = ae.getAnnotation(CreateContext.class);
		return new String[] {c.name(), c.tag()};
	}
}
