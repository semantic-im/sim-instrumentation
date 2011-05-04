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

/**
 * Aspect that instruments all methods or constructors marked by 
 * {@link sim.instrumentation.annotation.Instrument @Instrument} annotation.
 * 
 * @see sim.instrumentation.aop.aspectj.AbstractMethodInstrumentation
 * @see sim.instrumentation.annotation.Instrument
 * 
 * @author mcq
 * 
 */
final aspect AnnotationMethodInstrumentation extends AbstractMethodInstrumentation {
	public pointcut methodsToInstrument(): execution(@sim.instrumentation.annotation.Instrument * *(..)) || 
											execution(@sim.instrumentation.annotation.Instrument *.new(..));
}
