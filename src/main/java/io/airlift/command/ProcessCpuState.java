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
import java.util.Date;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.MoreObjects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "process-cpu-state")
@XmlType(name="process-cpu-state")
public class ProcessCpuState implements Serializable, Cloneable
{
	/**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
	private static final long serialVersionUID = -7749906267810891780L;

    @XmlElement(name="pid")
    private long pid;
    
    @XmlElement(name="datetime")
    private Date datetime;
    
    @XmlElement(name="user")
    private double user;
    
    @XmlElement(name="last-time")
    private long lastTime;
    
    @XmlElement(name="percent")
    private double percent;
    
    @XmlElement(name="start-time")
    private long startTime;
    
    @XmlElement(name="total")
    private double total;
    
    @XmlElement(name="sys")
    private double sys;
    
    public ProcessCpuState()
    {
    	super();
    }

    public ProcessCpuState(long processId, Date datetime, double user, long lastTime, double percent, long startTime, double total, double sys)
    {
        this.pid = processId;
        this.datetime = datetime;
        this.user = user;
        this.lastTime = lastTime;
        this.percent = percent;
        this.startTime = startTime;
        this.total = total;
        this.sys = sys;
    }

    /**
	 * @return the processId
	 */
	public long getPid() {
		return pid;
	}

	/**
	 * @param processId the processId to set
	 */
	public ProcessCpuState setPid(long processId) 
	{
		this.pid = processId;
		return this;
	}

	/**
	 * @return the datetime
	 */
	public Date getDatetime() 
	{
		return datetime;
	}

	/**
	 * @param datetime the datetime to set
	 */
	public ProcessCpuState setDatetime(Date datetime) 
	{
		this.datetime = datetime;
		return this;
	}

	/**
	 * @return the user
	 */
	public double getUser() 
	{
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public ProcessCpuState setUser(double user) 
	{
		this.user = user;
		return this;
	}

	/**
	 * @return the lastTime
	 */
	public long getLastTime() 
	{
		return lastTime;
	}

	/**
	 * @param lastTime the lastTime to set
	 */
	public ProcessCpuState setLastTime(long lastTime) 
	{
		this.lastTime = lastTime;
		return this;
	}

	/**
	 * @return the percent
	 */
	public double getPercent() 
	{
		return percent;
	}

	/**
	 * @param percentUsage the percentUsage to set
	 */
	public ProcessCpuState setPercent(double percentUsage) 
	{
		this.percent = percentUsage;
		return this;
	}

	/**
	 * @return the startTime
	 */
	public long getStartTime() 
	{
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public ProcessCpuState setStartTime(long startTime) 
	{
		this.startTime = startTime;
		return this;
	}

	/**
	 * @return the cpuTime
	 */
	public double getTotal() 
	{
		return total;
	}

	/**
	 * @param cpuTime the cpuTime to set
	 */
	public ProcessCpuState setTotal(double cpuTime) 
	{
		this.total = cpuTime;
		return this;
	}

	/**
	 * @return the sys
	 */
	public double getSys() 
	{
		return sys;
	}

	/**
	 * @param kernelTime the kernelTime to set
	 */
	public ProcessCpuState setSys(double kernelTime) 
	{
		this.sys = kernelTime;
		return this;
	}

	@Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
        		  .add("pid", getPid())
        		  .add("datetime", getDatetime() != null ? ofEpochMilli(getDatetime().getTime()).truncatedTo(SECONDS).atZone(UTC) : null)
        		  .add("start-time", getStartTime())
        		  .add("total", getTotal())
        		  .add("sys", getSys())
        		  .add("percent", getPercent())
        		  .omitNullValues()
        		  .toString();
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
    	
    	ProcessCpuState other = (ProcessCpuState)obj;
    	
        return Objects.equals(getPid(), other.getPid()) &&
        	   Objects.equals(getTotal(), other.getTotal()) &&
        	   Objects.equals(ofEpochMilli(getDatetime().getTime()).truncatedTo(SECONDS).atZone(UTC), ofEpochMilli(other.getDatetime().getTime()).truncatedTo(SECONDS).atZone(UTC)) &&
        	   Objects.equals(getSys(), other.getSys()) &&
        	   Objects.equals(getLastTime(), other.getLastTime()) &&
        	   Objects.equals(getPercent(), other.getPercent()) &&
        	   Objects.equals(getStartTime(), other.getStartTime()) && 
        	   Objects.equals(getUser(), other.getUser());
        
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(
        		getPid(), 
        		getTotal(), 
        		getSys(), 
        		getLastTime(), 
        		getPercent(), 
        		getStartTime(), 
        		getUser(), 
        		ofEpochMilli(getDatetime().getTime()).truncatedTo(SECONDS).atZone(UTC));
    }
    
    @Override
    protected ProcessCpuState clone() 
    {
    	ProcessCpuState clone;
    	
		try 
		{
			clone = (ProcessCpuState) super.clone();
		} 
		catch (CloneNotSupportedException e) 
		{
			clone = new ProcessCpuState(getPid(), datetime, user, lastTime, percent, startTime, total, sys);
		}
    	
    	return clone;
    }
}
