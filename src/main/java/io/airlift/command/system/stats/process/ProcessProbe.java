/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.airlift.command.system.stats.process;


import java.util.Optional;
import java.util.concurrent.TimeUnit;

import io.airlift.command.ProcessState;

public interface ProcessProbe 
{
	Optional<ProcessState> processStats();

	/**
	 * @param initialDelay the time to delay first execution
	 * @param period period the period between successive executions
	 * @param unit unit the time unit of the initialDelay and period parameters
	 */
	void start(long initialDelay, long period, TimeUnit unit);
	
	/**
	 * @param initialDelay the time to delay first execution
	 * @param period period the period between successive executions
	 * @param duration maximum duration for execution 
	 * @param unit unit the time unit of the initialDelay and period parameters
	 */
	void start(long initialDelay, long period, long duration, TimeUnit unit);
	
	void cancel();
	
	void stop();
	
	void registerListener(Object listener);
	
}
