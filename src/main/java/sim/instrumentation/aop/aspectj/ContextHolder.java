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

import sim.data.Context;

/**
 * Signature for classes that will hold Context.
 * 
 * This is used to do Context propagation through Thread/Process/Computer
 * boundaries.
 * 
 * @author mcq
 * 
 */
public interface ContextHolder {
	public Context getContext();

	public void setContext(Context c);
}
