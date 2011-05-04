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

/**
 * Defines the scope of <code>Instrument</code> annotation.
 * 
 * <code>ALL</code> will instrument all methods of the type marked by the
 * <code>Instrument</code> annotation, <code>PUBLIC</code> will instrument just
 * public methods and <code>PRIVATE</code> will instrument just private methods.
 * 
 * @see sim.instrumentation.annotation.Instrument
 * 
 * @author mcq
 * 
 */
public enum InstrumentationScope {
	ALL, PUBLIC, PRIVATE
}
