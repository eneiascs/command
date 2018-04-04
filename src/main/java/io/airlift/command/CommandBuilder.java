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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import io.airlift.units.Duration;

import static com.google.common.base.Preconditions.checkArgument;

public final class CommandBuilder 
{
	private String id;
	private List<String> command = new ArrayList<>();
    private Set<Integer> successfulExitCodes = new HashSet<>(ImmutableSet.of(0));
    private File directory;
    private Map<String, String> environment = new HashMap<>();
    private Duration timeLimit;
    private List<Object> listeners = new ArrayList<>();
    private boolean includeSystemEnvVariables = true;
    
    private CommandBuilder(){}
    
    public static CommandBuilder newCommandBuilder()
    {
    	return new CommandBuilder();
    }
    
//    public static CommandBuilder newNullCommandBuilder()
//    {
//    	return new CommandBuilder();
//    }
    
    public CommandBuilder setId(UUID id)
    {
    	if (id != null )
    	{
    		this.id = id.toString();
    	}
    	
    	return this;
    }
    
    public CommandBuilder setId(String id)
    {
    	this.id = id;
    	return this;
    }
    
    public CommandBuilder setDirectory(File directory)
    {
    	this.directory = directory;
    	return this;
    }
    
    public CommandBuilder setCommands(String ... commands)
    {
    	this.command = Arrays.asList(commands);
    	return this;
    }
    
    public CommandBuilder setCommands(List<String> commands)
    {
    	if (commands != null)
    	{
        	this.command.addAll(commands);
    	}
    	
    	return this;
    }
    
    public CommandBuilder setTimeout(Duration duration)
    {
    	this.timeLimit = duration;
    	return this;
    }
    
    public CommandBuilder setTimeout(Long value, TimeUnit timeUnit)
    {    	  
		return setTimeout(new Duration(value, timeUnit));
    }
    
    public CommandBuilder setSuccessfulExitCodes(Integer ... successfulExitCodes)
    {
    	if (successfulExitCodes != null)
    	{
    		for (Integer code: successfulExitCodes)
        	{
        		this.successfulExitCodes.add(code);
        	}
    	}
    	return this;
    }
    
    public CommandBuilder addEnviromentVariable(String name, String value)
    {
    	checkArgument(!Strings.isNullOrEmpty(name), "Environment variable's name is null");
    	
    	environment.put(name, value);
    	return this;
    }
    
    public  CommandBuilder registerListeners(Object ... listeners)
    {
    	if (listeners != null)
    	{
    		for (Object listener: listeners)
    		{
    			this.listeners.add(listener);
    		}
    	}
    	
    	return this;
    }
    
    public CommandBuilder includeEnvVariables()
    {
    	includeSystemEnvVariables = true;
    	return this;
    }
    
    public CommandBuilder removeEnvVariables()
    {
    	includeSystemEnvVariables = false;
    	return this;
    }
    
    public Command build()
    {
    	if (directory == null)
    	{
    		directory = new File(".").getAbsoluteFile();
    	}
    	
    	if (timeLimit == null)
    	{
    		this.timeLimit = new Duration(365, TimeUnit.DAYS);
    	}
    	
		return new Command(id, command, successfulExitCodes, directory, environment, timeLimit, listeners, includeSystemEnvVariables);
    }
}
