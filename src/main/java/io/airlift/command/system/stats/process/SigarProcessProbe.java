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

import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Singleton;

import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.ProcFd;
import org.hyperic.sigar.ProcMem;
import org.hyperic.sigar.ProcState;
import org.hyperic.sigar.ProcTime;
import org.hyperic.sigar.SigarException;

import com.google.common.eventbus.EventBus;

import io.airlift.command.ProcessCpuState;
import io.airlift.command.ProcessMemoryState;
import io.airlift.command.ProcessState;
import io.airlift.command.ProcessTime;
import io.airlift.command.system.stats.SigarService;

import static java.util.Objects.requireNonNull;

import java.time.Instant;
import java.time.ZoneOffset;

import static java.time.Instant.now;

@ThreadSafe
@Singleton
public class SigarProcessProbe extends ProcessProbeBase
{
	private final SigarService sigarService;
	
	public SigarProcessProbe (String id, long pid, SigarService sigarService, ScheduledExecutorService scheduler)
	{
		super(id, pid, scheduler);
		this.sigarService = requireNonNull(sigarService, "sigar service is null");
	}

	@Override
	protected void monitor(final String id, final long pid, EventBus subscribers) 
	{
		final Instant instant = now().atZone(ZoneOffset.UTC).toInstant();
		
		if (sigarService.isReady())
		{
			try 
			{
				ProcState procState = sigarService.sigar().getProcState(pid);
				ProcCpu cpuState = sigarService.sigar().getProcCpu(pid);
				ProcMem mem = sigarService.sigar().getProcMem(pid);
				ProcTime time = sigarService.sigar().getProcTime(pid);
				ProcFd procFd = sigarService.sigar().getProcFd(pid);
				

				ProcessMemoryState memoryState = new ProcessMemoryState()
						.setPid(pid)
						.setDatetime(new Date(instant.toEpochMilli()))
						.setResident(mem.getResident())
						.setPageFaults(mem.getPageFaults())
						.setMajorFaults(mem.getMajorFaults())
						.setShare(mem.getShare())
						.setMajorFaults(mem.getMinorFaults())
						.setSize(mem.getSize());
				

				ProcessCpuState processCpuState = new ProcessCpuState()
						.setPid(pid)
						.setDatetime(new Date(instant.toEpochMilli()))
						.setUser(cpuState.getUser())
						.setLastTime(cpuState.getLastTime())
						.setPercent(cpuState.getPercent() * 100)
						.setStartTime(cpuState.getStartTime())
						.setTotal(cpuState.getTotal())
						.setSys(cpuState.getSys());

				ProcessTime processTime = new ProcessTime()
						.setPid(pid)
						.setDatetime(instant.toEpochMilli())
						.setUser(time.getUser())
						.setStartTime(time.getStartTime())
						.setTotal(time.getTotal())
						.setSys(time.getSys());

				ProcessState data = new ProcessState()
						.setPid(pid)
						.setNumberOfActiveThreads(procState.getThreads())
						.setTty(procState.getTty())
						.setProcessor(procState.getProcessor())
						.setPriority(procState.getPriority())
						.setNice(procState.getNice())
						.setFd(procFd.getTotal())
						.setKernelSchedulingPriority(procState.getPriority())
						.setState(ProcessState.ProcState.valueOf(procState.getState()))
						.setCpuState(processCpuState)
						.setMemoryState(memoryState)
						.setProcessTime(processTime)
						.setId(id);

				subscribers.post(data);

			} 
			catch (SigarException exception) 
			{
				LOG.info("Error on collecting stats of process [{}]", pid);
				LOG.error("The reason for sigar's probe failure is", exception);
			}
		}
	}
}
