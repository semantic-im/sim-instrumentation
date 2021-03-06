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
package sim.instrumentation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark types (classes, interfaces), methods or constructors
 * that will be instrumented.
 * <p>
 * When it is used on a type, the <code>value</code> property indicates what
 * methods should be instrumented. By default all methods are instrumented (
 * <code>InstrumentationScope.ALL</code>). To instrument just public methods
 * <code>InstrumentationScope.PUBLIC</code> should be used. To instrument just
 * private methods <code>InstrumentationScope.PRIVATE</code> should be used
 * instead. When the annotation is used on a method or constructor then the
 * <code>value</code> property is ignored.
 * 
 * @see sim.instrumentation.annotation.InstrumentationScope
 * @see sim.instrumentation.aop.aspectj.AnnotationMethodInstrumentation
 * @see sim.instrumentation.aop.aspectj.AnnotationTypeInstrumentation
 * 
 * @author mcq
 * 
 */
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Instrument {
	InstrumentationScope value() default InstrumentationScope.ALL;
}
