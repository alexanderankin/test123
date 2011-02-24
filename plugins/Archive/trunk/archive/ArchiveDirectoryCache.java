/*
 * ArchiveDirectoryCache.java - Caches remote directories to improve performance
 *
 * :tabSize=4:indentSize=4:noTabs=true:
 *
 * Copyright (c) 2000, 2004 Slava Pestov
 * Copyright (c) 2001, 2002 Andre Kaplan
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DirectoryCache.class program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */


package archive;

import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;

import org.gjt.sp.jedit.io.*;
import org.gjt.sp.util.IOUtilities;
import org.gjt.sp.util.Log;


public class ArchiveDirectoryCache
{
    /**
     * Returns the specified cached directory listing, or null if
     * it is not in the cache.
     * @param url The URL
     */
    public static VFSFile[] getCachedDirectory(String url)
    {
        url = canon(url);

        synchronized(lock)
        {
            String path = (String)urlToCacheFileHash.get(url);
            if(path != null)
            {
                ObjectInputStream in = null;
                try
                {
                    in = new ObjectInputStream(
                        new BufferedInputStream(
                        new FileInputStream(path)));
                    return (VFSFile[])in.readObject();
                }
                catch(Exception e)
                {
                    Log.log(Log.ERROR,ArchiveDirectoryCache.class,e);
                    return null;
                }
                finally
                {
                    IOUtilities.closeQuietly(in);
                }
            }
            else
                return null;
        }
    }

    /**
     * Caches the specified directory listing.
     * @param url The URL
     * @param directory The directory listing
     */
    public static void setCachedDirectory(String url, VFSFile[] directory)
    {
        url = canon(url);

        synchronized(lock)
        {
            String path = ArchivePlugin.tempFileName();
            if(path == null)
                return;

            ObjectOutputStream out = null;
            try
            {
                out = new ObjectOutputStream(
                    new BufferedOutputStream(
                    new FileOutputStream(path)));
                out.writeObject(directory);

                Log.log(Log.DEBUG,ArchiveDirectoryCache.class,"Cached "
                    + url + " to " + path);

                urlToCacheFileHash.put(url,path);
            }
            catch(Exception e)
            {
                Log.log(Log.ERROR,ArchiveDirectoryCache.class,e);
            }
            finally
            {
                IOUtilities.closeQuietly(out);
            }
        }
    }

    /**
     * Removes the cached listing of the specified directory.
     * @param url The URL
     * @since jEdit 2.6pre5
     */
    public static void clearCachedDirectory(String url)
    {
        url = canon(url);

        synchronized(lock)
        {
            String path = (String)urlToCacheFileHash.remove(url);
            if(path == null)
                return;
            else
            {
                Log.log(Log.DEBUG,ArchiveDirectoryCache.class,"Deleting " + path);
                new File(path).delete();
            }
        }
    }

    /**
     * Removes all cached directory listings.
     * @since jEdit 2.6pre5
     */
    public static void clearAllCachedDirectories()
    {
        synchronized(lock)
        {
            Enumeration files = urlToCacheFileHash.elements();
            while(files.hasMoreElements())
            {
                String path = (String)files.nextElement();
                Log.log(Log.DEBUG,ArchiveDirectoryCache.class,"Deleting " + path);
                new File(path).delete();
            }
            urlToCacheFileHash.clear();
        }
    }

    // private members
    private static Object lock = new Object();
    private static Hashtable urlToCacheFileHash;

    private ArchiveDirectoryCache() {}

    /* This method exists so that foo/ and foo will both be cached
     * as the same URL. When the VFSPath class arrives, will get rid
     * of this kludge */
    private static String canon(String url)
    {
        if(url.length() != 0 && (url.endsWith("/")
            || url.endsWith(File.separator)))
            return url.substring(0,url.length() - 1);
        else
            return url;
    }

    static
    {
        urlToCacheFileHash = new Hashtable();
    }
}
