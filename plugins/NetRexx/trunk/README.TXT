NetRexx jEdit Plugin readme file

The NetRexx Plugin replaces earlier plugins NetRexxJe, NetRexxDE and NetRexxScript.

NetRexx Plugin feature highlights:

    * Compile and run or interpret NetRexx programs, scripts and macros from jEdit
    * Interpret a currently open NetRexx file including any unsaved changes
    * Dockable console window with command line to view say output or respond to ask input
    * View output with or without trace from the console window to aid in debugging programs
    * Errors are automatically shown in source files, the "Error List", and in the console window
    * A "classpath" option makes Java classes available to NetRexx programs
    * Run compiled NetRexx scripts or Java class files as macros or console commands
    * Includes the "NetRexx at Once" cross reference guide to Java and NetRexx
    * Includes an Ant based IDE for developing Android programs in NetRexx
    * Compile and run simple Java programs
    
Installation: 

The stable 1.0 version is being released to jEdit Plugin Central and should soon be available via the automatic plugin manager installation process.

To install it manually:

    * Download the NetRexxScript.jar file and the NetRexxC.jar file
    * Copy both files to the user account's jEdit jars directory.
    
    https://kenai.com/projects/netrexx-plus/downloads/download/jEdit%20NetRexxPlugin/NetRexxPlugin.jar
    http://netrexx.org/netrexx/netrexxc/trunk/lib/NetRexxC.jar

  On Windows the user's jEdit jar directory could be C:\Documents and Settings\useracct\.jedit\jars or perhaps C:\Documents and Settings\useracct\AppData\Roaming\jEdit\jars
  (Where "useracct" is the actual userid). 
 
Sample macro scripts provided:

    * asktest  --  This script demonstrates console IO  - it requires the NetRexx dockable window
                    Say output displays in the panel of the console and Ask input is read from the cmd line
    * guisample -- a simple NetRexx gui program with a progress bar
    * index  -- This script demonstrates the effect of caching on indexed variables
                 If cached, the second execution will show different results for the second display of s['x']
    * iosample  -- This script demonstrates easy jEdit GUI IO tricks
    * multi  --  This script demonstrates one script calling another in preparse mode - demonstrates parse error if caching option is off
    * test  --   This is the basic test script for the plugin -- It demonstrates say output that contains some trace sections which can be viewed in the trace panel of the plugin console window
    * testbin  --  This is a "binary" NetRexx script - it does not use "Rexx class" variables and runs OK even without the "prefix" option  
    
