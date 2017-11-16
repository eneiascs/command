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
package io.airlift.command.system.stats;

import java.util.concurrent.Executor;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import static java.util.Objects.requireNonNull;

import io.airlift.command.system.stats.process.ProcessProbe;

@Singleton
public class StatsService 
{
	private final ProcessProbe processProbe;
	private final Executor statsExecutor;

	@Inject
	public StatsService(ProcessProbe processProbe, Executor executor)
	{
		this.processProbe = requireNonNull(processProbe, "processProbe is null");
		this.statsExecutor = requireNonNull(executor, "statsExecutor is null");
	}
	
	@Nonnull
	public ProcessProbe processProbe()
	{
		return this.processProbe;
	}
	
	@Nonnull
	public Executor statsExecutor()
	{
		return statsExecutor;
	}
}
