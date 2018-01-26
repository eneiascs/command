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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.hyperic.sigar.Sigar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

@Singleton
public final class SigarService
{
	private static final Logger LOG = LoggerFactory.getLogger(SigarService.class);  
	
    static
    {
        final String name = "libsigar-amd64-linux.so";

        final String path = SigarService.class.getName().replace('.', '/').trim();
        String fullPath = SigarService.class.getResource("/" + path + ".class").toString();

        boolean injar = fullPath.startsWith("jar:");

        if (injar)
        {
            InputStream in = getDefaultClassLoader().getResourceAsStream(name);
            File sigarHome = new File(System.getProperty("user.home"), String.format("/.sigarLib/%s", name));
            
            if (!sigarHome.exists())
            {
            	try
            	{
            		Files.createParentDirs(sigarHome);
            		
					try (FileOutputStream fos = new FileOutputStream(sigarHome))
                    {
                        copyLarge(in, fos, new byte[1024 * 4]);
                    }
            	}
            	catch (IOException e)
                {
            		e.printStackTrace();
//            		LOG.error("Error on copying sigar native libraries");
//            		LOG.error("The reason for failing the copy is", e);
                }
            }
            
            System.setProperty("java.library.path", System.getProperty("java.library.path") + File.pathSeparator + sigarHome.getPath().replaceAll(name, ""));
        }
        else
        {
        	URL resource = getDefaultClassLoader().getResource(name);
        	if (resource != null)
        	{
        		String libPath = resource.getPath();
                libPath = libPath.substring(0, libPath.indexOf(name) - 1);
                System.setProperty("java.library.path", System.getProperty("java.library.path") + File.pathSeparator + libPath);
        	}
        }
        
        System.out.println(">======= java.library.path =======>" + System.getProperty("java.library.path"));
    }

    private final Sigar sigar;
    
    
    @Inject
    public SigarService()
    {
    	Sigar sigar = null;
    	
    	try
    	{
    		sigar = new Sigar();
    		Sigar.load();
    		
    		LOG.debug("Successfully loaded SIGAR [{}]", Sigar.VERSION_STRING);
    	}
    	catch(Throwable t)
    	{
    		LOG.debug("Failed to load SIGAR"); 
            LOG.debug("The reason for SIGAR to have failed is", t);
            
    		if (sigar != null)
    		{
    			try
    			{
    				sigar.close();
    			}
    			catch(Throwable ignore)
    			{
    			}
    			finally
    			{
    				sigar = null;
    			}
    		}
    	}
    	
    	this.sigar = sigar;
    }
    
	public boolean isReady() 
	{
		return null != sigar;
	}

	public Sigar sigar() 
	{
		return sigar;
	}

    /**
     * Return the default ClassLoader to use: typically the thread context ClassLoader, if available; the ClassLoader that loaded the ClassUtils class
     * will be used as fallback.
     * 
     * @return the default ClassLoader (never <code>null</code>)
     * @see java.lang.Thread#getContextClassLoader()
     */
    private static ClassLoader getDefaultClassLoader()
    {
        ClassLoader cl = null;
        
        try
        {
            cl = Thread.currentThread().getContextClassLoader();
        }
        catch (Throwable ex)
        {
        }
        
        if (cl == null)
        {
            cl = SigarService.class.getClassLoader();
        }
        return cl;
    }

    private static final int EOF = -1;

    /**
     * Copy bytes from a large (over 2GB) <code>InputStream</code> to an <code>OutputStream</code>.
     * <p>
     * This method uses the provided buffer, so there is no need to use a <code>BufferedInputStream</code>.
     * <p>
     * 
     * @param input the <code>InputStream</code> to read from
     * @param output the <code>OutputStream</code> to write to
     * @param buffer the buffer to use for the copy
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException if an I/O error occurs
     * @since 2.2
     * Copied from Apache org.apache.commons.io.IOUtils. 
     */
    private static long copyLarge(InputStream input, OutputStream output, byte[] buffer) throws IOException
    {
        long count = 0;
        int n = 0;
        while (EOF != (n = input.read(buffer)))
        {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}
