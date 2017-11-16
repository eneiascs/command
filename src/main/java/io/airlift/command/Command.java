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

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Ints;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;

import io.airlift.units.Duration;

import javax.annotation.concurrent.Immutable;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

@Immutable
public class Command
{
    private static final ImmutableSet<Integer> DEFAULT_SUCCESSFUL_EXIT_CODES = ImmutableSet.of(0);
    private static final File DEFAULT_DIRECTORY = new File(".").getAbsoluteFile();
    private static final Duration DEFAULT_TIME_LIMIT = new Duration(365, TimeUnit.DAYS);

    private final String id;
    private final List<String> command;
    private final Set<Integer> successfulExitCodes;
    private final File directory;
    private final Map<String, String> environment;
    private final Duration timeLimit;
    private final List<Object> listeners;

    public Command(String id, String... command)
    {
        this(id, ImmutableList.copyOf(command), DEFAULT_SUCCESSFUL_EXIT_CODES, DEFAULT_DIRECTORY, ImmutableMap.<String, String>of(), DEFAULT_TIME_LIMIT, Collections.emptyList());
    }

    public Command(String id, List<String> command, Set<Integer> successfulExitCodes, File directory, Map<String, String> environment, Duration timeLimit, List<Object> listeners)
    {
        requireNonNull(command, "command is null");
        checkArgument(!command.isEmpty(), "command is empty");
        requireNonNull(successfulExitCodes, "successfulExitCodes is null");
        checkArgument(!successfulExitCodes.isEmpty(), "successfulExitCodes is empty");
        requireNonNull(directory, "directory is null");
        requireNonNull(timeLimit, "timeLimit is null");
        
        checkArgument(!Strings.isNullOrEmpty(id), "ID is null or empty");

        this.id = id;
        this.command = ImmutableList.copyOf(command);
        // exit codes have a default and thus are required
        this.successfulExitCodes = ImmutableSet.copyOf(successfulExitCodes);
        this.directory = directory;
        this.environment = ImmutableMap.copyOf(environment);
        this.timeLimit = timeLimit;
        
        this.listeners = listeners != null ? ImmutableList.copyOf(listeners) : ImmutableList.of().asList();
    }
    
    /**
	 * @return the id
	 */
	public String getId() 
	{
		return id;
	}

	public List<String> getCommand()
    {
        return command;
    }

    public Command addArgs(String... args)
    {
        requireNonNull(args, "args is null");
        return addArgs(ImmutableList.copyOf(args));
    }

    public Command addArgs(Iterable<String> args)
    {
        requireNonNull(args, "args is null");
        ImmutableList.Builder<String> command = ImmutableList.<String>builder().addAll(this.command).addAll(args);
        return new Command(id, command.build(), successfulExitCodes, directory, environment, timeLimit, listeners);
    }
    
    public Command registerListeners(List<Object> listeners)
    {
    	return new Command(id, command, successfulExitCodes, directory, environment, timeLimit, listeners);
    }

    public Map<String, String> getEnvironment()
    {
        return environment;
    }

    public Command addEnvironment(String name, String value)
    {
        requireNonNull(name, "name is null");
        requireNonNull(value, "value is null");
        ImmutableMap.Builder<String, String> builder = ImmutableMap.<String, String>builder().putAll(this.environment).put(name, value);
        return new Command(id, command, successfulExitCodes, directory, builder.build(), timeLimit, listeners);
    }

    public Command addEnvironment(Map<String, String> environment)
    {
        requireNonNull(environment, "environment is null");
        ImmutableMap.Builder<String, String> builder = ImmutableMap.<String, String>builder().putAll(this.environment).putAll(environment);
        return new Command(id, command, successfulExitCodes, directory, builder.build(), timeLimit, listeners);
    }

    public Set<Integer> getSuccessfulExitCodes()
    {
        return successfulExitCodes;
    }

    public Command setSuccessfulExitCodes(int... successfulExitCodes)
    {
        requireNonNull(successfulExitCodes, "successfulExitCodes is null");
        return setSuccessfulExitCodes(ImmutableSet.copyOf(Ints.asList(successfulExitCodes)));
    }

    public Command setSuccessfulExitCodes(Set<Integer> successfulExitCodes)
    {
        requireNonNull(successfulExitCodes, "successfulExitCodes is null");
        checkArgument(!successfulExitCodes.isEmpty(), "successfulExitCodes is empty");
        return new Command(id, command, successfulExitCodes, directory, environment, timeLimit, listeners);
    }

    public File getDirectory()
    {
        return directory;
    }

    public Command setDirectory(String directory)
    {
        requireNonNull(directory, "directory is null");
        return setDirectory(new File(directory));
    }

    public Command setDirectory(File directory)
    {
        requireNonNull(directory, "directory is null");
        return new Command(id, command, successfulExitCodes, directory, environment, timeLimit, listeners);
    }

    public Duration getTimeLimit()
    {
        return timeLimit;
    }

    public Command setTimeLimit(double value, TimeUnit timeUnit)
    {
        return setTimeLimit(new Duration(value, timeUnit));
    }

    public Command setTimeLimit(Duration timeLimit)
    {
        requireNonNull(timeLimit, "timeLimit is null");
        return new Command(id, command, successfulExitCodes, directory, environment, timeLimit, listeners);
    }

    public CommandResult execute(Executor executor) throws CommandFailedException
    {
        ProcessCallable processCallable = new ProcessCallable(this, executor, listeners);
        Future<CommandResult> future = submit(executor, processCallable);

        try 
        {
            return future.get(timeLimit.toMillis(), TimeUnit.MILLISECONDS);
        }
        catch (ExecutionException e) 
        {
            Throwables.propagateIfPossible(e.getCause(), CommandFailedException.class);
            throw new CommandFailedException(this, "unexpected exception", e.getCause());
        }
        catch (InterruptedException e) 
        {
            Thread.currentThread().interrupt();
            throw new CommandFailedException(this, "interrupted", e);
        }
        catch (TimeoutException e) 
        {
            throw new CommandTimeoutException(this);
        }
        finally 
        {
            future.cancel(true);
        }
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
        
        Command o = (Command) obj;
        return Objects.equals(this.command, o.command) &&
               Objects.equals(this.successfulExitCodes, o.successfulExitCodes) &&
               Objects.equals(this.directory, o.directory) &&
               Objects.equals(this.timeLimit, o.timeLimit);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(command, successfulExitCodes, directory, timeLimit);
    }

    @Override
    public String toString()
    {
        return toStringHelper(this)
                .add("command", command)
                .add("successfulExitCodes", successfulExitCodes)
                .add("directory", directory)
                .add("timeLimit", timeLimit)
                .toString();
    }

    static <T> ListenableFuture<T> submit(Executor executor, Callable<T> task)
    {
        ListenableFutureTask<T> future = ListenableFutureTask.create(task);
        executor.execute(future);
        return future;
    }
}
