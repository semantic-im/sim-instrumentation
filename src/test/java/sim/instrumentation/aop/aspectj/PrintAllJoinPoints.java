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

import java.io.PrintStream;

/**
 * @author mcq
 * 
 */
public class PrintAllJoinPoints {
	String var1 = "aaa";
	String var2;
	int var3 = 5;

	public PrintAllJoinPoints() {
		var2 = "bbb";
	}

	public static void main(String[] args) {
		new PrintAllJoinPoints().test1();
	}

	public void test1() {
		String varx = var2;
		PrintStream ps = System.out;
		ps.println(calculateVar(var1));
		ps.println(calculateVar(var2));
		ps.println(calculateVar(var3));
		ps.println(calculateVar(varx));
		manyParams("v1", 2, 3L);
		try {
			exception();
		} catch (Exception e) {
			ps.println("catch exception: " + e.toString());
		}
	}

	private String calculateVar(Object value) {
		String rezult = "rezult=" + value;
		return rezult;
	}

	private void manyParams(String p1, int p2, Long p3) {

	}

	private void exception() throws Exception {
		throw new Exception("exception message");
	}

}
