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

import net.sourceforge.jedit.buildtools.StaticLogger;
import java.util.StringTokenizer;
import java.io.File;

public class BuildMessage {

    public final static int TYPE_UNKNOWN    = -1;
    public final static int TYPE_EXCEPTION  = 1;
    public final static int TYPE_BUILD      = 2;

    private String  message     = "";
    private String  target      = "";
    private int     type        = 0;
    private int     lineNumber  = 0;    

    private BuildMessage( String message,
                          int type ) {
        this.message = message;
        this.type = type;
    }

    public BuildMessage( String message,
                         String target,
                         int type,
                         int lineNumber ) {

        this.message = message;
        this.target = target;
        this.type = type;
        this.lineNumber = lineNumber;
                             
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public String getTarget() {
        return this.target;
    }

    public String getMessage() {
        return this.message;
    }

    public int getType() {
        return this.type;
    }

    /**
    Given a string... try to determine a build message and then return it.
    */
    public static BuildMessage getBuildMessage(String message) {
     
        BuildMessage buildMessage = null;
        switch (getMessageType(message) ) {
            
            case TYPE_EXCEPTION:
                buildMessage = getTypeExceptionMessage(message);
            break;
            
            case TYPE_BUILD:
                buildMessage = getTypeBuildMessage(message);
            break;
            
            case TYPE_UNKNOWN:
                buildMessage = new BuildMessage( message, TYPE_UNKNOWN );
            break;
            
        }
 
        return buildMessage;
    }
    
    
    private static BuildMessage getTypeExceptionMessage(String message) {

        
        /*
        StringTokenizer tokens = new StringTokenizer( message, ":" );

        if ( tokens.hasMoreElements() 
        tokens.nextToken();
        String lineNumber = tokens.nextToken();
        */
        
        int lineNumber = 0;

        int lineStart = message.lastIndexOf(":");
        int lineEnd = message.lastIndexOf(")");
        
        if ( lineStart > -1 && lineEnd > -1 ) {
            lineNumber = Integer.parseInt( message.substring( lineStart + 1, lineEnd ) );
        }
        
        String trimmed = message.trim();

        int start = trimmed.indexOf(" ") + 1;
        int end = trimmed.indexOf("(");

        String classname = trimmed.substring( start, end );
        classname = classname.substring( 0, classname.lastIndexOf(".") );

        //remove any reference to an inner class
        if (classname.indexOf("$") > -1) {
            classname = classname.substring( 0, classname.indexOf("$") );
        }
        
        //ok... now use EditBus to request that a file get decompiled.

        String target = classname;

        return new BuildMessage( message,
                                 target, 
                                 TYPE_EXCEPTION,
                                 lineNumber );


    }

    private static BuildMessage getTypeBuildMessage(String message) {

        
        int start = message.indexOf(".java:");
        if (start > 0) {
            start = start + 5;
        }

        int end = message.indexOf(':', start + 1);

        if (start < 0 || end < 0 || end - start <= 0) {
            return null;
        }

        String lineNumStr = message.substring(start+1, end);
        int lineNum = Integer.parseInt(lineNumStr);
        String errFile = message.substring(0, start);
        

        return new BuildMessage( message,
                                 errFile, 
                                 TYPE_BUILD,
                                 lineNum );
    }
    
    
    private static int getMessageType( String message ) {


        //if the string begins with "at" return 

        
        if ( message.trim().length() >= 2 && message.trim().substring(0, 2).equals("at") ) {
            return TYPE_EXCEPTION;
        }


        //exceptions begin with a file separated by a ":" so if this file exists 
        //it is a build type
        StringTokenizer tokens = new StringTokenizer( message, ":" ) ;
        
        if ( tokens.hasMoreTokens() ) {

            if ( new File( tokens.nextToken() ).exists() ) {
                return TYPE_BUILD;
            }
        }
        
        
        
        return TYPE_UNKNOWN;
        
    }
    
    
    public void dump() {
        StaticLogger.log( "Message:     " + this.getMessage() );
        StaticLogger.log( "Target:      " +  this.getTarget());
        StaticLogger.log( "Type:        " +  this.getType());
        StaticLogger.log( "LineNumber:  " +  this.getLineNumber());
    }


}


