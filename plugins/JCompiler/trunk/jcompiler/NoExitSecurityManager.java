/*
 * NoExitSecurityManager - a security manager that disallows exits
 * (c) 1999, 2000 Kevin A. Burton and Aziz Sharif
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
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */


//
// !!! IMPORTANT NOTE:
// !!! THIS CLASS ONLY COMPILES ON JAVA2!
// !!! It runs on an JRE 1.x, though.
//


package jcompiler;


/**
 * A <code>SecurityManager</code> that disallows VM exits, but allows
 * everything else.
 */
public class NoExitSecurityManager extends SecurityManager
{

    private static NoExitSecurityManager sm = null;


    public static NoExitSecurityManager getInstance() {
        if (sm != null) return sm;
        String vmVersion = System.getProperty("java.specification.version");
        if (vmVersion != null && vmVersion.startsWith("1.1")) {
            sm =  new NoExitSecurityManager();
        } else {
            sm =  new NoExitSecurityManager2();
        }
        return sm;
    }


    // the default is to allow exits
    private boolean allowExit = true;


    protected NoExitSecurityManager() {
        super();
    }


    public void checkExit(int status) {
        if (!allowExit) {
            throw new SecurityException("VM Exit is not allowed currently.");
        }
    }


    public void setAllowExit(boolean allowExit) {
        this.allowExit = allowExit;
    }


    public void checkCreateClassLoader() { }
    public void checkAccess(Thread g) { }
    public void checkAccess(ThreadGroup g) { }
    public void checkExec(String cmd) { }
    public void checkLink(String lib) { }
    public void checkRead(java.io.FileDescriptor fd) { }
    public void checkRead(String file) { }
    public void checkRead(String file, Object context) { }
    public void checkWrite(java.io.FileDescriptor fd) { }
    public void checkWrite(String file) { }
    public void checkDelete(String file) { }
    public void checkConnect(String host, int port) { }
    public void checkConnect(String host, int port, Object context) { }
    public void checkListen(int port) { }
    public void checkAccept(String host, int port) { }
    public void checkMulticast(java.net.InetAddress maddr) { }
    public void checkMulticast(java.net.InetAddress maddr, byte ttl) { }
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
}

