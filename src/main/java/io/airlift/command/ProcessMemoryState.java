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
import java.util.Date;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.MoreObjects;

import static java.time.Instant.ofEpochMilli;
import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.SECONDS;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="process-memory-state")
@XmlType(name="process-memory-state")
public class ProcessMemoryState implements Serializable, Cloneable
{
	/**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
	private static final long serialVersionUID = -786126379392097409L;

    @XmlElement(name = "pid")
    private long pid;

    @XmlElement(name = "resident")
    private double resident;

    @XmlElement(name = "page-faults")
    private double pageFaults;

    @XmlElement(name = "major-faults")
    private double majorFaults;

    @XmlElement(name = "share")
    private double share;

    @XmlElement(name = "minor-faults")
    private double minorFaults;

    @XmlElement(name = "virtual")
    private double size;
    
    @XmlElement(name="datetime")
	private Date datetime;
    
    public ProcessMemoryState()
    {
    	super();
    }

    public ProcessMemoryState(long pid, long datetime, double resident, double pageFaults, double majorFaults, double share, double minorFaults, double size)
    {
        this.pid = pid;
        this.datetime = new Date(datetime);
        this.resident = resident;
        this.pageFaults = pageFaults;
        this.majorFaults = majorFaults;
        this.share = share;
        this.minorFaults = minorFaults;
        this.size = size;
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
	public ProcessMemoryState setPid(long pid) 
	{
		this.pid = pid;
		return this;
	}

	/**
	 * @return the resident
	 */
	public double getResident() 
	{
		return resident;
	}

	/**
	 * @param resident the resident to set
	 */
	public ProcessMemoryState setResident(double resident) 
	{
		this.resident = resident;
		return this;
	}

	/**
	 * @return the pageFaults
	 */
	public double getPageFaults() 
	{
		return pageFaults;
	}

	/**
	 * @param pageFaults the pageFaults to set
	 */
	public ProcessMemoryState setPageFaults(double pageFaults) 
	{
		this.pageFaults = pageFaults;
		return this;
	}

	/**
	 * @return the majorFaults
	 */
	public double getMajorFaults() 
	{
		return majorFaults;
	}

	/**
	 * @param majorFaults the majorFaults to set
	 */
	public ProcessMemoryState setMajorFaults(double majorFaults) 
	{
		this.majorFaults = majorFaults;
		return this;
	}

	/**
	 * @return the share
	 */
	public double getShare() 
	{
		return share;
	}

	/**
	 * @param share the share to set
	 */
	public ProcessMemoryState setShare(double share) 
	{
		this.share = share;
		return this;
	}

	/**
	 * @return the minorFaults
	 */
	public double getMinorFaults() 
	{
		return minorFaults;
	}

	/**
	 * @param minorFaults the minorFaults to set
	 */
	public ProcessMemoryState setMinorFaults(double minorFaults) 
	{
		this.minorFaults = minorFaults;
		return this;
	}

	/**
	 * @return the size
	 */
	public double getSize() 
	{
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public ProcessMemoryState setSize(double size) 
	{
		this.size = size;
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
	public ProcessMemoryState setDatetime(Date datetime) 
	{
		this.datetime = datetime;
		return this;
	}

	@Override
    public int hashCode()
    {
		return Objects.hash(
				getPid(), 
				getResident(), 
				getPageFaults(), 
				getMajorFaults(), 
				getShare(),
				getMinorFaults(), 
				getSize(), 
				ofEpochMilli(getDatetime().getTime()).truncatedTo(SECONDS).atZone(UTC));
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
    	
    	ProcessMemoryState other = (ProcessMemoryState)obj;
    	
    	return Objects.equals(getPid(), other.getPid()) &&
    		   Objects.equals(ofEpochMilli(getDatetime().getTime()).truncatedTo(SECONDS).atZone(UTC), ofEpochMilli(other.getDatetime().getTime()).truncatedTo(SECONDS).atZone(UTC)) && 
    		   Objects.equals(getPageFaults(), other.getPageFaults()) &&
    		   Objects.equals(getMajorFaults(), other.getMajorFaults()) &&
    		   Objects.equals(getShare(), other.getShare()) &&
    		   Objects.equals(getMinorFaults(), other.getMinorFaults()) &&
    		   Objects.equals(getSize(), other.getSize());
    }

    @Override
    public String toString()
    {
    	return MoreObjects.toStringHelper(this)
    			          .add("pid", getPid())
    			          .add("resident", getResident())
    			          .add("page-faults", getPageFaults())
    			          .add("major-faults", getMajorFaults())
    			          .add("share", getShare())
    			          .add("minor-faults", getMinorFaults())
    			          .add("size", getSize())
    			          .add("datetime", ofEpochMilli(getDatetime().getTime()).truncatedTo(SECONDS).atZone(UTC))
    			          .omitNullValues()
    			          .toString();
    }
    
    @Override
    public ProcessMemoryState clone()  
    {
    	ProcessMemoryState clone;
    	
		try 
		{
			clone = (ProcessMemoryState) super.clone();
		} 
		catch (CloneNotSupportedException e) 
		{
			clone = new ProcessMemoryState(pid, datetime.getTime(), resident, pageFaults, majorFaults, share, minorFaults, size);
		}
		
    	return clone;
    }
}
