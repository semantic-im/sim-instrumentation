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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import sim.instrumentation.annotation.CreateContext;
import sim.instrumentation.annotation.WriteToContext;
import sim.instrumentation.data.ContextManager;

/**
 * @author mcq
 * 
 */
public class AnnotationContextWriterTest {

	@Test
	@CreateContext(name = "context1", tag = "tag1")
	public void test1() {
		String result = method1("param1", 5);
		String resultName = this.getClass().getName() + ".method1.result";
		assertEquals(result, ContextManager.getCurrentContext().get(resultName));
	}

	@WriteToContext
	private String method1(String p1, int p2) {
		String param1Name = this.getClass().getName() + ".method1.params.p1";
		String param2Name = this.getClass().getName() + ".method1.params.p2";
		assertTrue(ContextManager.getCurrentContext().containsKey(param1Name));
		assertTrue(ContextManager.getCurrentContext().containsKey(param2Name));
		assertEquals(p1, ContextManager.getCurrentContext().get(param1Name));
		assertEquals(p2, ContextManager.getCurrentContext().get(param2Name));
		return "return-value";
	}

}
