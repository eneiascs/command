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

import static java.time.Instant.ofEpochMilli;
import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.SECONDS;

import java.io.Serializable;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.MoreObjects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="process-state")
@XmlType(name="process-state")
public class ProcessState implements Serializable, Cloneable
{
	/**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
	private static final long serialVersionUID = 3717008105003669282L;

    public static enum ProcState
    {
        /**
         * for sleeping (idle).
         */
        S,

        /**
         * Process running.
         */
        R,

        /**
         * for disk sleep (uninterruptible).
         */
        D,

        /**
         * for zombie (waiting for parent to read it's exit status).
         */
        Z,

        /**
         * for traced or suspended (e.g by SIGTSTP).
         */
        T,

        /**
         * for paging.
         */
        W, ;

        public static ProcState valueOf(char state)
        {
            return valueOf(String.valueOf(state));
        }
    }

    /**
     * 
     */
    private String id;
    
    /**
     * The process that is monitored.
     */
    private long pid;
    
    private long datetime;

    /**
     * Number of active threads.
     */
    private long numberOfActiveThreads;

    /**
     * Kernel scheduling priority
     */
    private int kernelSchedulingPriority;

    /**
     * The controlling terminal of the process.
     */
    private int tty;

    /**
     * The percentage of the CPU time that the process is currently using.
     */
    private int processor;

    /**
     * Process priority
     */
    private int priority;

    /**
     * The nice value of a process, from 19 (low priority) to -20 (high priority). A high value means the process is being nice, letting others have a
     * higher relative priority. Only root can lower the value/
     */
    private int nice;

    /**
     * 
     */
    private long fd;

    /**
     * 
     */
    private ProcessCpuState cpuState;

    /**
     * 
     */
    private ProcessMemoryState memoryState;

    /**
     * 
     */
    private ProcessTime processTime;

    /**
     * 
     */
    private ProcState state;
    
    public ProcessState()
    {
    	super();
    }

    public ProcessState(String id, long processId, long datetime, long threads, int tty, int processor, int priority, int nice, long fd, int kernelSchedulingPriority,
            ProcState state, ProcessCpuState cpuState, ProcessMemoryState memoryState, ProcessTime processTime)
    {

    	this.id = id;
        this.pid = processId;
        this.datetime = datetime;
        this.numberOfActiveThreads = threads;
        this.tty = tty;
        this.processor = processor;
        this.priority = priority;
        this.nice = nice;
        this.fd = fd;
        this.kernelSchedulingPriority = kernelSchedulingPriority;
        this.state = state;
        this.cpuState = cpuState;
        this.memoryState = memoryState;
        this.processTime = processTime;
    }
    

    /**
	 * @return the id
	 */
	public String getId() 
	{
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public ProcessState setId(String id) 
	{
		this.id = id;
		return this;
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
	public ProcessState setPid(long pid) 
	{
		this.pid = pid;
		return this;
	}
	

	/**
	 * @return the datetime
	 */
	public long getDatetime() 
	{
		return datetime;
	}


	/**
	 * @param datetime the datetime to set
	 */
	public ProcessState setDatetime(long datetime) 
	{
		this.datetime = datetime;
		return this;
	}


	/**
	 * @return the numberOfActiveThreads
	 */
	public long getNumberOfActiveThreads() 
	{
		return numberOfActiveThreads;
	}


	/**
	 * @param numberOfActiveThreads the numberOfActiveThreads to set
	 */
	public ProcessState setNumberOfActiveThreads(long numberOfActiveThreads) 
	{
		this.numberOfActiveThreads = numberOfActiveThreads;
		return this;
	}


	/**
	 * @return the kernelSchedulingPriority
	 */
	public int getKernelSchedulingPriority() 
	{
		return kernelSchedulingPriority;
	}

	/**
	 * @param kernelSchedulingPriority the kernelSchedulingPriority to set
	 */
	public ProcessState setKernelSchedulingPriority(int kernelSchedulingPriority) 
	{
		this.kernelSchedulingPriority = kernelSchedulingPriority;
		return this;
	}


	/**
	 * @return the tty
	 */
	public int getTty() 
	{
		return tty;
	}


	/**
	 * @param tty the tty to set
	 */
	public ProcessState setTty(int tty) 
	{
		this.tty = tty;
		return this;
	}


	/**
	 * @return the processor
	 */
	public int getProcessor() 
	{
		return processor;
	}


	/**
	 * @param processor the processor to set
	 */
	public ProcessState setProcessor(int processor) 
	{
		this.processor = processor;
		return this;
	}


	/**
	 * @return the priority
	 */
	public int getPriority() 
	{
		return priority;
	}


	/**
	 * @param priority the priority to set
	 */
	public ProcessState setPriority(int priority) 
	{
		this.priority = priority;
		return this;
	}


	/**
	 * @return the nice
	 */
	public int getNice() 
	{
		return nice;
	}

	/**
	 * @param nice the nice to set
	 */
	public ProcessState setNice(int nice) 
	{
		this.nice = nice;
		return this;
	}


	/**
	 * @return the fd
	 */
	public long getFd() {
		return fd;
	}


	/**
	 * @param fd the fd to set
	 */
	public ProcessState setFd(long fd) {
		this.fd = fd;
		return this;
	}


	/**
	 * @return the cpuState
	 */
	public ProcessCpuState getCpuState() 
	{
		return cpuState;
	}


	/**
	 * @param cpuState the cpuState to set
	 */
	public ProcessState setCpuState(ProcessCpuState cpuState) 
	{
		this.cpuState = cpuState;
		return this;
	}

	/**
	 * @return the memoryState
	 */
	public ProcessMemoryState getMemoryState() 
	{
		return memoryState;
	}

	/**
	 * @param memoryState the memoryState to set
	 */
	public ProcessState setMemoryState(ProcessMemoryState memoryState) 
	{
		this.memoryState = memoryState;
		return this;
	}


	/**
	 * @return the processTime
	 */
	public ProcessTime getProcessTime() 
	{
		return processTime;
	}


	/**
	 * @param processTime the processTime to set
	 */
	public ProcessState setProcessTime(ProcessTime processTime) 
	{
		this.processTime = processTime;
		return this;
	}


	/**
	 * @return the state
	 */
	public ProcState getState() 
	{
		return state;
	}
	
	/**
	 * @param state the state to set
	 */
	public ProcessState setState(ProcState state) 
	{
		this.state = state;
		return this;
	}


	@Override
    public int hashCode()
    {
    	return Objects.hash(getId(), getPid(), ofEpochMilli(getDatetime()).truncatedTo(SECONDS).atZone(UTC),
    			getNumberOfActiveThreads(), getKernelSchedulingPriority(), getTty(), getProcessor(),
    			getPriority(), getNice(), getFd(), getCpuState(), getMemoryState(), getProcessTime(), getState());
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
    	
    	ProcessState other = (ProcessState)obj;
    	
        return Objects.equals(getId(), other.getId()) &&
        	   Objects.equals(getPid(), other.getPid()) && 
        	   Objects.equals(ofEpochMilli(getDatetime()).truncatedTo(SECONDS).atZone(UTC), ofEpochMilli(other.getDatetime()).truncatedTo(SECONDS).atZone(UTC)) &&
        	   Objects.equals(getNumberOfActiveThreads(), other.getNumberOfActiveThreads()) && 
        	   Objects.equals(getKernelSchedulingPriority(), other.getKernelSchedulingPriority()) && 
        	   Objects.equals(getTty(), other.getTty()) &&
        	   Objects.equals(getProcessor(), other.getProcessor()) && 
        	   Objects.equals(getPriority(), other.getPriority()) &&
        	   Objects.equals(getNice(), other.getNice()) &&
        	   Objects.equals(getFd(), other.getFd()) &&
        	   Objects.equals(getCpuState(), other.getCpuState()) &&
        	   Objects.equals(getMemoryState(), other.getMemoryState()) &&
        	   Objects.equals(getProcessTime(), other.getProcessTime()) &&
        	   Objects.equals(getState(), other.getState());
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
        		          .add("id", getId())
        		          .add("pid", getPid())
        		          .add("datetime", ofEpochMilli(getDatetime()).truncatedTo(SECONDS).atZone(UTC))
        		          .add("numberOfActiveThreads", getNumberOfActiveThreads())
        		          .add("kernelSchedulingPriority", getKernelSchedulingPriority())
        		          .add("tty", getTty())
        		          .add("processor", getProcessor())
        		          .add("priority", getPriority())
        		          .add("nice", getNice())
        		          .add("fd", getFd())
        		          .add("cpuState", getCpuState())
        		          .add("memoryState", getMemoryState())
        		          .add("processTime", getProcessTime())
        		          .add("state", getState())
        		          .omitNullValues()
        		          .toString();
    }
    
    @Override
    public ProcessState clone() 
    {
    	ProcessState clone;
    	
		try 
		{
			clone = (ProcessState) super.clone();
		} 
		catch (CloneNotSupportedException e) 
		{
			clone = new ProcessState(id, pid, datetime, numberOfActiveThreads, tty, processor, priority, nice, fd, kernelSchedulingPriority, state, cpuState !=  null ? cpuState.clone() : null, memoryState != null ? memoryState.clone() : null, processTime);
		}
		
    	return clone;
    }
}
