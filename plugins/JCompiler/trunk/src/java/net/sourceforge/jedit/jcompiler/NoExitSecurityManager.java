/*
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
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */


package net.sourceforge.jedit.jcompiler;

import java.lang.*;
import java.io.FileDescriptor;
import java.util.Hashtable;
import java.net.InetAddress;
import java.lang.reflect.Member;


/**
@author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
@version $Id$
*/
public class NoExitSecurityManager extends SecurityManager {

    private static NoExitSecurityManager psm = null;
    private boolean allowExit = true;

    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public static NoExitSecurityManager getNoExitSM() {
        if (psm != null) {
            return psm;  
        }
        String vmVersion = System.getProperty("java.specification.version");

        if (vmVersion != null && ! vmVersion.startsWith("1.1")) {
            psm =  new NoExitSM2(); 
        } else {
            psm =  new NoExitSecurityManager(); 
        }
        return psm;  
    }

    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    protected NoExitSecurityManager() {
        super();
    }

    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public boolean getAllowExit() {
        return allowExit;
    } 

    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public void setAllowExit(boolean allowExit) {
        this.allowExit = allowExit;
    }

    /**
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public void checkExit(int status) {
        if (allowExit == false) {
            throw new SecurityException("No exit is allowed");
        }
    }

    public void checkCreateClassLoader() { } 
    public void checkAccess(Thread g) { }
    public void checkAccess(ThreadGroup g) { }
    public void checkExec(String cmd) { }
    public void checkLink(String lib) { }
    public void checkRead(FileDescriptor fd) { }
    public void checkRead(String file) { }
    public void checkRead(String file, Object context) { }
    public void checkWrite(FileDescriptor fd) { }
    public void checkWrite(String file) { } 
    public void checkDelete(String file) { }
    public void checkConnect(String host, int port) { }
    public void checkConnect(String host, int port, Object context) { }
    public void checkListen(int port) { }
    public void checkAccept(String host, int port) { }
    public void checkMulticast(InetAddress maddr) { }
    public void checkMulticast(InetAddress maddr, byte ttl) { }
    public void checkPropertiesAccess() { }
    public void checkPropertyAccess(String key) { }
    public void checkPropertyAccess(String key, String def) { }
    public boolean checkTopLevelWindow(Object window) { return true; }
    public void checkPrintJobAccess() { }
    public void checkSystemClipboardAccess() { }
    public void checkAwtEventQueueAccess() { }
    public void checkPackageAccess(String pkg) { }
    public void checkPackageDefinition(String pkg) { }
    public void checkSetFactory() { }
    public void checkMemberAccess(Class clazz, int which) { }
    public void checkSecurityAccess(String provider) { }

    // In JDK 1.1 comment out these two methods!
    //public void checkPermission(java.security.Permission p) { }
    //public void checkPermission(java.security.Permission p, Object o) { }
}   

/**
@author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
@version $Id$
*/
class NoExitSM2 extends NoExitSecurityManager {
    public void checkPermission(java.security.Permission p) { }
    public void checkPermission(java.security.Permission p, Object o) { } 
}

