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

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import net.vidageek.mirror.dsl.Mirror;

class ProcessCallable implements Callable<CommandResult>
{
    private final Command command;
    private final Executor executor;
    
    private final EventBus eventbus;

    public ProcessCallable(Command command, Executor executor, List<Object> listeners)
    {
        this.command = requireNonNull(command, "command is null");
        this.executor = requireNonNull(executor, "executor is null");
        this.eventbus = new EventBus();
        
        if (listeners != null)
        {
        	listeners.forEach(eventbus::register);
        }        
    }
    

    @Override
    public CommandResult call() throws CommandFailedException, InterruptedException
    {
        ProcessBuilder processBuilder = new ProcessBuilder(command.getCommand());
        processBuilder.directory(command.getDirectory());
        processBuilder.redirectErrorStream(true);
        
        if (!command.isIncludeEnvironmentVariables())
        {
        	processBuilder.environment().clear();
        }
        
        processBuilder.environment().putAll(command.getEnvironment());
        
        // start the process
        Process process;
        
        final long startTime;
        
        final int pid;
        try 
        {
            process = processBuilder.start();
            startTime = System.nanoTime();
            pid = (int) new Mirror().on(process).get().field("pid");
            System.out.printf("Running without probe. PID is: %s\n", pid);
           
        }
        catch (IOException e) 
        {
            throw new CommandFailedException(command, "failed to start", e);
        }
        
        OutputProcessor outputProcessor = null;
        

        ProcessStatsListener listener = new ProcessStatsListener();

        
        try 
        {
            // start the output processor
            outputProcessor = new OutputProcessor(process, executor);
            outputProcessor.start();
            

            // wait for command to exit
            int exitCode = process.waitFor();
            
            long elapsedTime = System.nanoTime() - startTime;
            
            String out = outputProcessor.getOutput();
            
            // validate exit code
            if (!command.getSuccessfulExitCodes().contains(exitCode)) 
            {
                throw new CommandFailedException(command, exitCode, pid, out);
            }
            
            return new CommandResult(command.getId(), Long.valueOf(pid), exitCode, out, elapsedTime, listener.stats);
        }
        finally 
        {
            try 
            {
                process.destroy();
            }
            finally 
            {
                if (outputProcessor != null) 
                {
                    outputProcessor.destroy();
                }
                

            }
        }
    }
    
  
	class ProcessStatsListener 
    {
    	private List<ProcessState> stats = new ArrayList<>();
    	
    	@Subscribe
    	public void addProcessState(ProcessState state)
    	{
    		eventbus.post(state);
    	}
    }
}

