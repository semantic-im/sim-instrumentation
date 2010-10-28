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
 * Aspect responsible for triggering events when methods we are interested in
 * are executed.
 * 
 * @author mcq
 * 
 */
public abstract aspect AbstractMethodInterceptor {
	abstract pointcut methodExecution();

	before(): methodExecution() {
		// fire event before method execution
	}

	after() returning: methodExecution() {
		// fire event after method execution which completed successfully
	}

	after() throwing(Throwable t): methodExecution() {
		// fire event after method execution which completed with an exception
	}

	after(): methodExecution() {
		// fire event after method execution
	}
}
