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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.MoreObjects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="process-time")
public final class ProcessTime implements Serializable, Cloneable
{
	/**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
	private static final long serialVersionUID = -8129914808838514146L;

	@XmlElement(name = "pid")
    private long pid;
	
	@XmlElement(name = "datetime")
	private long datetime;

	@XmlElement(name = "user")
    private long user;

	@XmlElement(name = "start-time")
    private long startTime;

	@XmlElement(name = "total")
    private long total;

	@XmlElement(name = "sys")
    private long sys;
	
	public ProcessTime()
	{
		super();
	}

    public ProcessTime(long pid, long datetime, long user, long startTime, long total, long sys)
    {
        this.pid = pid;
        this.datetime = datetime;
        this.user = user;
        this.startTime = startTime;
        this.total = total;
        this.sys = sys;
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
	public ProcessTime setPid(long pid) 
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
	public ProcessTime setDatetime(long datetime) 
	{
		this.datetime = datetime;
		return this;
	}

	/**
	 * @return the user
	 */
	public long getUser() 
	{
		return user;
	}


	/**
	 * @param user the user to set
	 */
	public ProcessTime setUser(long user) 
	{
		this.user = user;
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
	public ProcessTime setStartTime(long startTime) 
	{
		this.startTime = startTime;
		return this;
	}


	/**
	 * @return the total
	 */
	public long getTotal() 
	{
		return total;
	}


	/**
	 * @param total the total to set
	 */
	public ProcessTime setTotal(long total) 
	{
		this.total = total;
		return this;
	}


	/**
	 * @return the sys
	 */
	public long getSys() 
	{
		return sys;
	}


	/**
	 * @param sys the sys to set
	 */
	public ProcessTime setSys(long sys) 
	{
		this.sys = sys;
		return this;
	}



	@Override
    public int hashCode()
    {
        return Objects.hash(getPid(), ofEpochMilli(getDatetime()).truncatedTo(SECONDS).atZone(UTC), 
        	                getUser(), getStartTime(), getTotal(), getSys());
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
    	
    	ProcessTime other = (ProcessTime)obj;
    	
        return Objects.equals(getPid(), other.getPid()) && 
        	   Objects.equals(ofEpochMilli(getDatetime()).truncatedTo(SECONDS).atZone(UTC), ofEpochMilli(other.getDatetime()).truncatedTo(SECONDS).atZone(UTC)) &&
        	   Objects.equals(getUser(), other.getUser()) &&
        	   Objects.equals(getStartTime(), other.getStartTime()) && 
        	   Objects.equals(getTotal(), other.getTotal()) &&
        	   Objects.equals(getSys(), other.getSys());
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
        		          .add("pid", getPid())
        		          .add("datetime", ofEpochMilli(getDatetime()).truncatedTo(SECONDS).atZone(UTC))
        		          .omitNullValues()
        		          .toString();
    }
    
    @Override
    protected ProcessTime clone() 
    {
    	ProcessTime clone;
    	
		try 
		{
			clone = (ProcessTime) super.clone();
		} 
		catch (CloneNotSupportedException e) 
		{
			clone = new ProcessTime(pid, datetime, user, startTime, total, sys);
		}
		
    	return clone;
    }
}
