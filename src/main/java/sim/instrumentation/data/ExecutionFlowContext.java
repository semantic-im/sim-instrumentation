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

package sim.instrumentation.data;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import sim.data.Context;

/**
 * Global thread safe storage for keeping track of execution flow context data.
 * 
 * @author mcq
 * 
 */
public final class ExecutionFlowContext {
	private static final InheritableThreadLocal<Deque<Context>> storage = new InheritableThreadLocal<Deque<Context>>();

	public static Context createNewContext() {
		Deque<Context> s = storage.get();
		if (s == null) {
			s = new ArrayDeque<Context>();
			storage.set(s);
		}
		Context m = new Context();
		s.addFirst(m);
		return m;
	}

	public static Context destroyCurrentContext() {
		Deque<Context> s = storage.get();
		if (s == null) {
			return null;
		} else {
			return s.pollFirst();
		}
	}

	public static Context getCurrentContext() {
		Deque<Context> s = storage.get();
		if (s == null) {
			return null;
		} else {
			return s.peekFirst();
		}
	}

	public static Context getFullContext() {
		Deque<Context> s = storage.get();
		if (s == null) {
			return null;
		} else {
			if (s.isEmpty()) {
				return null;
			} else {
				Context fullContext = new Context();
				for (Iterator<Context> it = s.descendingIterator(); it.hasNext();) {
					Context c = it.next();
					fullContext.putAll(c);
				}
				return fullContext;
			}
		}
	}

	private ExecutionFlowContext() {
		super();
	}
}
