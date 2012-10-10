NetRexx Script readme file

NetRexxScript feature highlights:

    * Efficient execution of NetRexx scripts and macros in jEdit
    * Optional caching of parsed scripts to improve performance
    * Option to "preparse" all available scripts as a unit for performance (aka "greased lightning mode") and cross-script access support
    * Option to suppress or allow trace output
    * Options to execute a currently open NetRexx file including unsaved changes
    * Optional dockable console window with command line to run scripts with parms and view say output or respond to ask input
    * View say output with or without trace from the console window to aid debugging
    * Option to "prefix" scripts automatically adds access to jEdit functions and variables
    * Script parse errors are automatically highlighted in script source files and listed in the "Error List" as well as displayed in a console window
    * User defined "classpath" option for making classes available to NetRexx scripts
    * User defined "scriptpath" option for access to scripts outside of jEdit macro libraries
    * Support for executing compiled NetRexx scripts or any "main" type Java class file as a macro or console command
    * Includes an updated version of the "NetRexx at Once" cross reference guide to Java and NetRexx
    
Installation:

You must install the plugin manually at this time. 

When it reaches a stable 1.0 version and gets packaged for distribution from the jEdit site, you will be able to use the automatic installation process, but for now -

To install it manually:

    * Download the NetRexxScript.jar file and the NetRexxC.jar file or download and unzip the zip file which contains both
    * Copy both files to the user account's jEdit jars directory or the main jEdit jars directory.

  On Windows XP the user's jEdit home directory could be C:\Documents and Settings\useracct\.jedit\jars (Where "useracct" is the actual userid.)
  The jEdit main jar directory may be C:\Program Files\jEdit\jars

  If you have the NetRexxJe plugin installed, NetRexxC.jar is installed with it, so just copy NetRexxScript.jar to the same directory.
 
  
  
Sample Scripts provided with the NetRexxScript plugin:

    * asktest  --  This script demonstrates console IO to the user - it requires the NetRexxScript dockable window to work
                    Say output displays in the OUT panel of the console and ask input is read from the command line
    * index  -- This script demonstrates the effect of caching on indexed variables
                 If cached, the second execution will show different results for the second display of s['x']
    * iosample  -- This script demonstrates easy GUI IO to the end user
    * multi  --  This script demonstrates one script calling another in preparse mode - demonstrates parse error if caching option is off
    * test  --   This is the basic test script for the plugin -- It demonstrates say output that contains some trace sections
                  which can be viewed in the trace panel of the plugin console window
    * testbin  --  This is a "binary" NetRexx script - it does not use "Rexx class" variables and runs OK even without the "prefix" option  
    
