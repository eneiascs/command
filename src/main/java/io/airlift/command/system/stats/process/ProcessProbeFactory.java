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

import static io.airlift.concurrent.Threads.daemonThreadsNamed;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

import java.util.concurrent.ScheduledExecutorService;

import io.airlift.command.system.stats.SigarService;

public class ProcessProbeFactory 
{
	public static ProcessProbe getProcessProbe(String id, long pid)
	{
		String name = String.format("pidstat-%s", pid);
		return getProcessProbe(id, pid, newSingleThreadScheduledExecutor(daemonThreadsNamed(name + "%s")));
	}
	
	public static ProcessProbe getProcessProbe(String id, long pid, ScheduledExecutorService executor)
	{
		SigarService sigar = new SigarService();
		return sigar.isReady() ? new SigarProcessProbe(id, pid, new SigarService(), executor) : new PidStat(id, pid, executor); 
	}
}
