/*
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

package buildtools.msg;

import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;


/**
Represents a way to decompile a class.

@author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
@version $Id$
*/    
public class DecompileClassMessage extends EBMessage.NonVetoable {

    private EBComponent     source      = null;
    private String          classname   = null;
    private String          filename    = null;
    private Object          message     = new Object();

    /**
    Create an instance with the source and the classname you want to decompile.
    Also tack on a "message" object that can be used to convey further 
    information
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */    
	public DecompileClassMessage(   EBComponent   source, 
                                    String        classname, 
                                    Object        message) {
        this(source, classname);
        this.message = message;
                                    
    }

    

    /**
    Create an instance with the source and the classname you want to decompile.
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */    
	public DecompileClassMessage(EBComponent source, 
                                 String classname) {
        super(source);
        this.source = source;
        this.classname = classname; 
	}

    /**
    Get's the classname.
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */    
    public String getClassName() {
        return this.classname;
    }

    /**
    Return's the filename from this class after it was compiled.
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */    
    public String getFileName() {
        return this.filename;
    }
    
    public void setFileName(String filename) {
        this.filename = filename;
    }

    /**
    Decompile events can contain message's as object.  You can enhance your 
    communication with a decompiler by adding a message for it.
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>    
    @version $Id$
    */    
    public Object getMessage() {
        return this.message;
    }
    
}


/*
$Log$
Revision 1.2  2000/11/03 05:25:41  akaplan
Removed dead code, doc update

Revision 1.1.1.1  2000/10/29 15:12:49  andre
JavaInsight initial import

Revision 1.1.1.1  2000/01/17 03:41:15  burtonator
init

Revision 1.3  2000/01/09 09:26:36  burton
...

Revision 1.2  2000/01/09 06:10:53  burton
added an option to add an object to DecompileClassMessage

Revision 1.1  2000/01/08 10:09:24  burton
init


*/
