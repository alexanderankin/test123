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



package buildtools;


public interface BuildProgressListener extends java.util.EventListener {
 

    public void reportNewBuild();
    

    /**
     *  reportError is called when the compiler finds an error in a file
     */
    public void reportError( String file, int line, String errorMessage );

    
    /**
     *  tell the build progress listener when the build is all done.
     */
    public void reportBuildDone( boolean success );

     
    
    /**
     *  JJ... we should not use reportMessage but should use reportStatus.
     *  progress is in 0...100
     */
    public void reportStatus( int progress, String message );
}



