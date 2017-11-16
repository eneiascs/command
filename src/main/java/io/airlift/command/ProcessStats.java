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

import java.io.Serializable;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.MoreObjects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "stats")
@XmlType(name = "stats", propOrder = { "pid", "cpu", "memory" })
public class ProcessStats implements Serializable, Cloneable 
{
	/**
	 * Serial code version <code>serialVersionUID</code> for serialization
	 */
	private static final long serialVersionUID = -7001051800380912897L;

	@XmlElement(name = "id")
	private long pid;

	@XmlElement(name = "cpu")
	private ProcessCpuState cpu;

	@XmlElement(name = "memory")
	private ProcessMemoryState memory;
	
	
	public ProcessStats ()
	{
		super();
	}
	
	public ProcessStats(long pid, ProcessCpuState cpu, ProcessMemoryState memory)
	{
		this.pid = pid;
		this.cpu = cpu;
		this.memory = memory;
	}

	/**
	 * @return the pid
	 */
	public long getPid() 
	{
		return pid;
	}

	/**
	 * @param pid the pid to set
	 */
	public ProcessStats setPid(long pid) 
	{
		this.pid = pid;
		return this;
	}

	/**
	 * @return the cpu
	 */
	public ProcessCpuState getCpu() 
	{
		return cpu;
	}

	/**
	 * @param cpu the cpu to set
	 */
	public ProcessStats setCpu(ProcessCpuState cpu) 
	{
		this.cpu = cpu;
		return this;
	}

	/**
	 * @return the memory
	 */
	public ProcessMemoryState getMemory() 
	{
		return memory;
	}

	/**
	 * @param memory the memory to set
	 */
	public ProcessStats setMemory(ProcessMemoryState memory) 
	{
		this.memory = memory;
		return this;
	}
	
	@Override
	public int hashCode() 
	{
		return Objects.hash(getPid(), getCpu(), getMemory());
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
		 
		ProcessStats other = (ProcessStats) obj;
		 
		return Objects.equals(getPid(), other.getPid()) &&
			   Objects.equals(getCpu(), other.getCpu()) &&
			   Objects.equals(getMemory(), other.getMemory());
	}
	 
	@Override
	public String toString() 
	{
		return MoreObjects.toStringHelper(this)
				          .add("pid", getPid())
				          .add("cpu", getCpu())
				          .add("memory", getMemory())
				          .omitNullValues()
				          .toString();
	}
	
	@Override
	protected ProcessStats clone()  
	{
		ProcessStats clone;
		
		try 
		{
			clone = (ProcessStats) super.clone();
		} 
		catch (CloneNotSupportedException e) 
		{
			clone = new ProcessStats()
					    .setPid(getPid())
					    .setMemory(getMemory() != null ? getMemory().clone() : null)
					    .setCpu(getCpu() != null ? getCpu().clone() : null);
		}
		
		return clone;
	}
}
