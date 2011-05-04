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

import static org.junit.Assert.assertTrue;
import static sim.instrumentation.aop.aspectj.MethodInstrumentationUtil.isMethodInstrumented;

import org.junit.Test;

import sim.instrumentation.annotation.Instrument;
import sim.instrumentation.annotation.InstrumentationScope;

/**
 * @author mcq
 * 
 */
@Instrument(InstrumentationScope.ALL)
public class AnnotationTypeInstrumentationTest {
	private static final String className = AnnotationTypeInstrumentationTest.class.getName();

	@Test
	public void testMethod() {
		assertTrue(isMethodInstrumented(className, "testMethod"));
	}

	public AnnotationTypeInstrumentationTest() {
		assertTrue(isMethodInstrumented(className, "<init>"));
	}

	@Test
	public void testMethod2() {
		assertTrue(isMethodInstrumented(className, "testMethod2"));
		this.testMethod3();
		this.testMethod4();
		AnnotationTypeInstrumentationTest.testMethod5();
		AnnotationTypeInstrumentationTest.testMethod6();
	}

	public void testMethod3() {
		assertTrue(isMethodInstrumented(className, "testMethod3"));
	}

	private void testMethod4() {
		assertTrue(isMethodInstrumented(className, "testMethod4"));
	}

	public static void testMethod5() {
		assertTrue(isMethodInstrumented(className, "testMethod5"));
	}

	private static void testMethod6() {
		assertTrue(isMethodInstrumented(className, "testMethod6"));
	}
}
