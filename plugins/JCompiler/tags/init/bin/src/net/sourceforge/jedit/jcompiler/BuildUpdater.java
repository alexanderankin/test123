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

import net.sourceforge.jedit.buildtools.*;
import org.gjt.sp.util.*;
 
public class BuildUpdater extends Thread {

    private int         progress;
    private String      message;
    
    
    public BuildUpdater(int progress, String message) {
        this.progress = progress;
        this.message = message;    

    }

    public void run() {
        Log.log( Log.DEBUG, "JCompiler", " setting progress to: "  + progress);
        JCompilerPlugin.progress.setValue(progress);             
    }

    /*



    //BuildProgressListener
    public void reportError(String file, int line, String errorMessage) {}
    public void reportMessage(String message){}

    public void reportStatus(int progress, String message) {
        Log.log( Log.DEBUG, "JCompiler", " reportProgress()" );
        this.notify(); //now tell the thread that it has been updated.
    }
    

    public void run() {
            
        try {
            while(true) {
                wait();            
                Log.log( Log.DEBUG, "JCompiler", " run()" );
                JCompilerPlugin.progress.setValue(progress);            
            }
        } catch (InterruptedException e) {
        
        }
    }

    */
    
}
