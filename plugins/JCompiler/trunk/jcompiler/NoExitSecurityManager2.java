/*
 * NoExitSecurityManager2 - a no-exit security manager for Java2
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


package jcompiler;


/**
 * <p>Security manager for Java2 platform.</p>
 *
 * <p><b>
 * !!!!NOTE!!!!  THIS CLASS MUST BE COMPILED WITH JAVA2!!!!
 * </b><p>
 */ 
class NoExitSecurityManager2 extends NoExitSecurityManager {
    public void checkPermission(java.security.Permission p) { }
    public void checkPermission(java.security.Permission p, Object o) { } 
}

