/*
 *  ClasspathChangeListener.java - Interface describing a class which will learn
 *  about changes to the classpath.  
 *  Copyright (C) 2002  Matthew Flower (MattFlower@yahoo.com)
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package jimporter.classpath;

import java.util.EventListener;

/**
 * This interface is designed to be implemented by classes that wish to be 
 * notified when the source of the classpath, or a classpath itself is modified.
 * Currently, classes will not be notified when a SpeedJava or JCompiler classpath
 * is modified.
 *
 * @author Matthew Flower
 */
public interface ClasspathChangeListener extends EventListener {
    /**
     * This method is called whenever the source of the classpath is modified or
     * when the classpath typed into "Use this classpath" is changed.
     *
     * @param oldClasspath a <code>Classpath</code> object which represents the
     * classpath source before the change.
     * @param newClasspath a <code>Classpath</code> object which represents the
     * classpath source after the change.
     */
    public void classpathChanged(Classpath oldClasspath, Classpath newClasspath);
}
