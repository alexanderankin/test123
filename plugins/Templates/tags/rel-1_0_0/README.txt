README: Templates plugin for jEdit
====================================

About Templates:
-----------------
jEdit is an Open Source, cross platform text editor written in Java.
The Templates plugin is an add-in designed for the jEdit editor.
This plugin can be used to insert code (or text, markup, etc.) templates
into the current buffer. As of this writing, Templates supports the import
of static text files only.

Installing Templates plugin:
----------------------------
Copy the Templates.jar file into your jEdit jars directory.
Refer to the jEdit documentation for further information on the
jars directory.

Usage:
-------
At startup, the Templates plugin scans the user's template directory. By
default, this is a directory called "templates" under the user's
jEdit settings directory (refer to jEdit documentation). 
This directory is user-configurable via the "Templates" tab accessed using
the "Plugin Options" facility.

A new "Templates" submenu is inserted on the "Plugins" menu, and each file
in the templates directory results in a menu item being added to that submenu.
As of version 1.0.0, the text for these menu items can be specified using the 
#ctpragma LABEL (or #ctpragma NAME) directive (see #ctpragma Directives below). 
The plugin will scan subdirectories recursively, and each subdirectory will
result in a new submenu.

To make use of the template files, position the cursor at the point at which
you wish the text inserted, and select the appropriate file from the "Templates"
menu. The full text of the file will be inserted into the current buffer at the
cursor position.

If new templates are added to the templates directory, the "Refresh templates"
menu item may be used to update the templates menu. The templates menu is
automatically refreshed if a new directory is selected under "Plugin Options".

NOTES:
- the Templates plugin will disregard any file which conforms to the jEdit
backup file format (ie. contains the backup prefix and/or suffix defined in
the "Global Options" settings.
- all files in the templates directory which do not match the backup format
described above, are assumed to be template files (this may change in future).
- if the selected templates directory does not exist, it will be created.

#ctpragma Directives
---------------------
The Templates plugin pre-processes template files on import. The behaviour of 
the Templates preprocessor can be influenced through the use of #ctpragma 
directives. These directives use the following format:

        #ctpragma TYPE = Value

where "TYPE" indicates the directive type, and "Value" is a string value.

Any single directive must be contained on a single line, and any given line 
must contain only one directive. A directive type which is not recognized is 
ignored. All lines containing #ctpragma directives are stripped from the 
template when imported.

As of version 1.0.0, Templates supports the following directive types:

- #ctpragma NAME:
- #ctpragma LABEL: These two directives are functionally equivalent. These 
directives indicate to the Templates plugin a label to be used to denote 
the template file in the Plugins->Templates menu. This directive must be in 
the first line of the template file. This avoids the need to process the 
whole file whenever the Templates menu is refreshed. Code samples:
        #ctpgrama LABEL = Java class
        #ctpragma NAME = JavaBean Template

License Info:
--------------
The source code for this plugin is distributed under the Gnu General Public
License. Please refer to the COPYING.txt file included with jEdit in the
"docs" directory.

Known Issues:
--------------
Issue: If both the filename backup prefix and suffix entries in "Global Options" are blank,
the plugin believes that all files in the plugins directory are backup files and 
will not load them. (Reported by Jamie LaScolea on Jan. 4, 2000)
Workaround: Something must be entered into either the prefix or suffix field. I would 
recommend retaining the default of using a tilde (~) as the backup suffix.

More Information:
------------------
Updates to this plugin may be found at:
	http://www.xist.com/sjakob/plugins.html
At some point in the near future, it should also be available from the jEdit
Plugin Central website:
	http://www.icg-online.de/jedit

You may contact the author of this plugin at:
Steve.Jakob@lrcc.on.ca
