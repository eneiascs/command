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
package io.airlift.command;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;

import javax.annotation.concurrent.Immutable;

@Immutable
public class CommandResult
{
	private final String id;
	private final Long pid;
    private final Integer exitCode;
    private final String commandOutput;
	private final ImmutableList<ProcessState> stats;
	private final Long elapsedTime;

    public CommandResult(String id, Long pid, Integer exitCode, String commandOutput, Long elapsedTime)
    {
        this(id, pid, exitCode, commandOutput, elapsedTime, ImmutableList.of());
    }
    
    public CommandResult(String id, Long pid, Integer exitCode, String commandOutput, Long elapsedTime, List<ProcessState> monitorData)
    {
    	this.id = id;
    	this.pid = pid;
    	this.exitCode = exitCode;
    	this.elapsedTime = elapsedTime;
        this.commandOutput = requireNonNull(commandOutput, "commandOutput is null");
        this.stats = monitorData == null ? ImmutableList.of() : ImmutableList.copyOf(monitorData);
    }
    
    /**
	 * @return the id
	 */
	public String getId() 
	{
		return id;
	}

	public Integer getExitCode()
    {
        return exitCode;
    }

    public String getCommandOutput()
    {
        return commandOutput;
    }
    
    
    /**
	 * @return the elapsedTime
	 */
	public Long getElapsedTime() 
	{
		return elapsedTime;
	}

	public ImmutableList<ProcessState> getProcessStats()
    {
    	return stats;
    }

	/**
	 * @return the pid
	 */
	public Long getPid() 
	{
		return pid;
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
		{
			return true;
		}
		
		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}
		
		CommandResult other = (CommandResult)obj;
		
		return Objects.equals(getId(), other.getId()) &&
			   Objects.equals(getPid(), other.getPid()) &&
			   Objects.equals(getExitCode(), other.getExitCode()) &&
			   Objects.equals(getCommandOutput(), other.getCommandOutput());
	}
	
	@Override
	public int hashCode() 
	{
		return Objects.hash(getId(), getPid(), getExitCode(), getCommandOutput());
	}
	
	@Override
	public String toString() 
	{
		return MoreObjects.toStringHelper(this)
				.add("pid", getPid())
				.add("exitcode", getExitCode())
				.add("output", getCommandOutput())
				.omitNullValues()
				.toString();
	}
}
