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
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

import io.airlift.command.ProcessState;

import static java.lang.String.*;

public abstract class ProcessProbeBase implements ProcessProbe 
{
	protected final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());
	private final AtomicBoolean started = new AtomicBoolean(false);
	
	private final ScheduledExecutorService executor;
	private final String id;
	private final long pid;
	private final EventBus subscribers;
	
	private Future<?> monitorHandle;
	
	public ProcessProbeBase(String id, long pid, ScheduledExecutorService scheduler)
	{
		this.id = id;
		this.pid = pid;
		this.executor = scheduler;
		this.subscribers = new EventBus(format("probe-pid-%s", pid));
	}


	@Override
	public void start(long initialDelay, long period, TimeUnit unit)
	{
		start(initialDelay, period, Integer.MAX_VALUE, unit);
	}

	@Override
	public void start(long initialDelay, long period, long duration, TimeUnit unit)
	{
		if (started.compareAndSet(false, true))
		{
			monitorHandle = executor.scheduleAtFixedRate(() -> processStats(), initialDelay, period, unit);
			
			if (duration > 0 && duration != Integer.MAX_VALUE)
			{
				executor.schedule(() -> cancel(), duration, unit);
			}
		}
	}


	@Override
	public void cancel() 
	{
		if (started.compareAndSet(true, false))
		{
			monitorHandle.cancel(true);
		}
	}

	@Override
	public void stop() 
	{
		cancel();
	}

	@Override
	public void registerListener(Object listener) 
	{
		if (listener != null)
		{
			subscribers.register(listener);
		}
	}


	@Override
	public Optional<ProcessState> processStats() 
	{
		monitor(id, pid, subscribers);
		return Optional.empty();
	}
	
	
	protected abstract void monitor(String id, long pid, EventBus bus);
}
