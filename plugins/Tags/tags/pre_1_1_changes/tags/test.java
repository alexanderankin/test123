import java.io.*;
import java.lang.*;
import java.lang.System.*;
import java.util.*;

// class test {

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.textarea.*;

class test {
  
  public void foo() {} // This is a comment
  public void foo(int foo) {} // another comment
  
  /***************************************************************************/
  static public void main(String args[]) {
    /* When tagging do all methods...
        - follow tag same file
        - follow tag, new window
        - enter and follow tag
        - enter and follow tag, new window
        - highlight tag same file
        - highlight tag, new file
        
        - Also do on known collisions
           - both popup and dialog
           - test escape
           - arrow keys
           - number keys
    */

    // Tag on   nothing...

    // Tag on space  "      "
    
    // Tag at end of line.
    
    // Tags.

    //   .main

    // Tag to Buffer.shiftIndentLeft
    // This has [] in the tag search line which must be /'ed for '[' and ']'
    
    // Tag to another window
    // MiscUtilities.getLeadingWhiteSpaceWidth();
    // ^ cursor
    
    // tag to something known to not be found.  Make sure item is unhighlighted

    // make sure this goes to the right place
    // test
    
    // main
    
    // (main())
    
    // tag to this:                                       main
 
    // view_

    // view
    
    // jEdit.openFile
 
    // jEdit.java
 
    // getFirstBuffer
    
    // Test that \/\/ is displayed as // in search string
    // foo
 
    // Should not be in any tag index file (famous last words)
    // zzz
    
    // Test that \/\/ is displayed as // in search string
    // foo
 
    // Test that popup is in correct location w/ folding and wrapping on... 
    // offsetToX

 
 
 
 
 
 
 
 
 
 
 
 
 
  }
}
