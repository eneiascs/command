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

import static java.lang.Long.parseLong;
import static java.lang.Double.parseDouble;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.Instant.now;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import com.google.common.base.Throwables;
import com.google.common.collect.Collections2;
import com.google.common.eventbus.EventBus;

import io.airlift.command.ProcessCpuState;
import io.airlift.command.ProcessMemoryState;
import io.airlift.command.ProcessState;


public class PidStat extends ProcessProbeBase 
{
	private Thread handle;
	
	public PidStat(String id, long pid, ScheduledExecutorService scheduler) 
	{
		super(id, pid, scheduler);
	}
	
	@Override
	public void start(long initialDelay, long period, long duration, TimeUnit unit) 
	{
		processStats();
	}

	@Override
	protected void monitor(final String id, final long pid, final EventBus bus) 
	{
		ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", format("pidstat -p %s -rud -h %s", pid, 1));
        processBuilder.redirectErrorStream(true);
        
        try 
        {
            final Process process = processBuilder.start();
            
           handle =  new Thread(() -> 
           {
            	try(BufferedReader bis = new BufferedReader(new InputStreamReader(process.getInputStream(), UTF_8)))
                {
                	String line;
                	while ((line = bis.readLine()) != null)
                	{
                		if (LOG.isDebugEnabled())
                		{
                			LOG.debug(line);
                		}
                		
                		if (!line.trim().isEmpty() && !line.trim().startsWith("#"))
                    	{
                    		Collection<String> values = Collections2.filter(Arrays.asList(line.trim().split(" ")),  new NonNullAndNonEmptyStringPredicate());
                    		String[] parts = new ArrayList<>(values).toArray(new String[values.size()]);
                    		
                    		if (parts.length == 17)
                    		{
                        		ProcessCpuState cpuState = new ProcessCpuState()
                        				.setDatetime(new Date(Instant.ofEpochSecond(parseLong(parts[0])).toEpochMilli()))
                        				.setPid(parseLong(parts[2]))
                        				.setUser(parseDouble(parts[3]))
                        				.setSys(parseDouble(parts[4]))
                        				.setTotal(parseDouble(parts[6]));
                        		
                        		ProcessMemoryState memState = new ProcessMemoryState()
                        				.setPid(cpuState.getPid())
                        				.setDatetime(cpuState.getDatetime())
                        				.setSize(parseDouble(parts[12]));
                        		
                        		ProcessState data = new ProcessState()
                        				.setCpuState(cpuState)
                        				.setMemoryState(memState)
                        				.setPid(pid)
                        				.setDatetime(now().atZone(ZoneOffset.UTC).toInstant().toEpochMilli())
                        				.setId(id);
                        		
                        		bus.post(data);
                    		}
                    	}
                	}
                }
                catch(IOException exception)
                {
                	LOG.error("Error on reading pidstat results!");
                	LOG.error("The reason is: " + exception.getMessage(), exception);
                }
            });
           
           handle.start();
            
        }
        catch (IOException e) 
        {
            Throwables.propagateIfPossible(e);
        }
	}
	
	@Override
	public void cancel() 
	{
		if (handle != null)
		{
			handle.interrupt();
		}
	}
	
	static class NonNullAndNonEmptyStringPredicate implements Predicate<String>, com.google.common.base.Predicate<String>
	{
		@Override
		public boolean apply(String input) 
		{
			return test(input);
		}

		@Override
		public boolean test(String input) 
		{
			return input != null && !input.trim().isEmpty();
		}
	}
	
	static class PidStatOutputCollector 
	{
	
	}
}
