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

import org.junit.Test;

import sim.data.Context;
import sim.instrumentation.annotation.CreateContext;
import sim.instrumentation.data.ContextManager;

/**
 * @author mcq
 * 
 */
public class AnnotationContextCreatorTest {

	@Test
	@CreateContext(name = "context1", tag = "tag1")
	public void test1() {
		assertEquals("context1", ContextManager.getCurrentContext().getName());
		assertEquals("tag1", ContextManager.getCurrentContext().getTag());
		method1(ContextManager.getCurrentContext());
	}

	@CreateContext(name = "context2", tag = "tag2")
	private void method1(Context parentContext) {
		assertEquals("context2", ContextManager.getCurrentContext().getName());
		assertEquals("tag2", ContextManager.getCurrentContext().getTag());
		assertEquals(parentContext.getId(), ContextManager.getCurrentContext().getParentContextId());
	}
}
