=====================
New jEdit Launcher
=====================

:Author: Ollie Rutherfurd
:Contact: oliver@jedit.org
:Abstract: A set of replacements for the jEditLauncher components which, 
			as of jEdit 4.2, are no longer distributed with jEdit.

.. WARNING::

	These components are in alpha state.  Using `jeditext.dll` may
	crash your shell (explorer.exe).  If that happens, and it doesn't
	automatically restart, type `CTRL+SHIFT+ESC`, then `File` > 
	`New Task (Run...)` and enter "explorer.exe".

========
Overview
========

jedit.exe:

	Native jEdit launcher.  Can be used to open 0 or more files in a running
	instance of jEdit, or in a new instance of jEdit (if not running).

jdiff.exe:

	Invokes JDiff plugin to compare 2 files in jEdit.  Without the JDiff
	plugin installed, you won't get much mileage out of this.

jeditext.dll:

	Context menu shell extension.
	
	* Open with jEdit (if any files selected)
	* Diff with jEdit (if 2 files selected)
	* Open \*.<selected file extension> with jEdit (if 1 file selected)


==========
Installing
==========

"jeditexe.dll" is a COM server, so it must be registered.  To register it,
run::

	regsvr32 jeditext.dll

The following registry entries should be created (though paths will be different)::

	[HKEY_CLASSES_ROOT\CLSID\{F1763C8F-4F26-4a15-943F-2CA281BD3385}]
	   @="jEdit Shell Extension"
	[HKEY_CLASSES_ROOT\CLSID\{F1763C8F-4F26-4a15-943F-2CA281BD3385}\InProcServer32]
	   @="C:\\Sandbox\\jedit.exe\\jeditext.dll"
	   "ThreadingModel"="Apartment"
	
	[HKEY_CLASSES_ROOT\*\shellex\ContextMenuHandlers\jedit]
	   @="{F1763C8F-4F26-4a15-943F-2CA281BD3385}"
	
	[HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows\CurrentVersion\Shell Extensions\Approved]
	   "{F1763C8F-4F26-4a15-943F-2CA281BD3385}"="jEdit Shell Extension"

See `Configuration`_ for additional steps.

Removing Context Menu Entries Created by 4.2 Installer
------------------------------------------------------

Once you've installed the context menu handler, you may wish to remove
the context menu entry created by the jEdit 4.2 installer.  To do so,
remove the following registry key::

	HKEY_CLASSES_ROOT\*\Shell\Open with jEdit\command

=============
Configuration
=============

To make migration easier for users of jEditLauncher, these programs use 
jEditLauncher's registry settings, which are located at::

	HKEY_CURRENT_USER\Software\www.jedit.org\jEditLauncher\4.0

The settings used are:

`Java Executable`
	Path to "javaw.exe" or "java.exe"

`Java Options`
	Options to pass to JVM when launching jEdit.

`jEdit Target`
	Path to "jedit.jar".

`jEdit Options`
	Options to pass to jEdit, when launching.

`jEdit Working Directory`
	Path to jEdit directory (where "jedit.jar") is located.

If you don't have these settings, you must add them, or nothing will work.

See "config\launcher.reg" for a template you can edit to create these settings.

===============
Troubleshooting
===============

If you run into any errors, please e-mail me "jeditlauncher.log" from your
home directory.  Please provide as much additional information, such as
you verison of jEdit, your version of windows, what you where doing, etc...

Note that you may wish to delete "jeditlauncher.log" every so often, as
it will just continue to grow.


============
Uninstalling
============

The only component that needs uninstalling is `jeditext.dll`, the context
menu shell extension.  To unregister the component, run::

	regsvr32 /u jeditext.dll


=========
Compiling
=========

I use `SCons`_ to compile.  There's a Makefile in the source dir, but I haven't
updated it recently and it's not setup to compile "jeditext.dll".  If you're
famliar with NMake syntax, and would rather use it, please update it and send
me patches.

.. _SCons: http://www.scons.org/

=======
License
=======

To be determined...

"gvimext.dll" (by Tianmiao Hu) was used as a base for "jeditext.dll" (jeditext.h
& jeditext.cpp). I'm not sure exactly how this affects licensing, as this isn't
a modification of Vim, but rather using a portion of the code as a base.

I also got ideas from Brian Hawkins and John Gellene's jEditLaucher.

Once I sort out the issues, I'll slap a license on it.  I don't really care how 
it's licensed provided I don't step on any toes and no one tries to take me to
court if their machine goes up in a ball of flames as a result of using it.

.. :mode=rest:
