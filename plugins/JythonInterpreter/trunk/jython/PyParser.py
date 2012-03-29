"""
PyParser - A pyclbr-based Python parser for the SideKick
plugin for jEdit.

Copyright (C) 2001-2003 Ollie Rutherfurd <oliver@rutherfurd.net>

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

$Id: PyParser.py,v 1.2 2003/05/27 21:26:15 tibu Exp $
"""

# java imports
from javax.swing.tree import DefaultMutableTreeNode

# plugin imports
import sidekick

# python imports
import os
import pyclbr

from org.gjt.sp.util import Log


class PyAsset(sidekick.Asset):

    """
    Base-class for Python Assets
    """

    def __init__(self, name, lineno, buffer):
        sidekick.Asset.__init__(self, name)

    def createPositions(self, buffer, lineno, next_start):
        start_offset = buffer.getLineStartOffset(lineno-1)
        end_offset = buffer.getLength() # default to end of buffer
        # if we know where the next asset begins, use that to
        # determine the end position of this asset
        if next_start:
            # use end of line before the start of the next asset
            # as the end offset for this asset
            end_offset = buffer.getLineEndOffset(next_start-2) - 1

        #Log.log(Log.DEBUG, self.__class__, self.name)
        #Log.log(Log.DEBUG, self.__class__, 'start: ' + str(start_offset))
        #Log.log(Log.DEBUG, self.__class__, 'end: ' + str(end_offset))

        self.start = buffer.createPosition(start_offset)
        self.end = buffer.createPosition(end_offset)

    def getShortString(self):
        """@sig String getShortString()"""
        return self.name

    def getLongString(self):
        """@sig String getLongString()"""
        return self.name

    def getIcon(self):
        """@sig javax.swing.Icon getIcon()"""
        return None


class ClassAsset(PyAsset):

    def __init__(self, klass, parent, buffer, next_start):
        bases = []
        if len(klass.super):
            for superclass in klass.super:
                if isinstance(superclass, pyclbr.Class):
                    bases.append(superclass.name)
                else:
                    bases.append(superclass)
        name = 'class ' + klass.name + '(' + ','.join(bases) + '):'
        PyAsset.__init__(self, name, klass.lineno, buffer)
        self.createPositions(buffer, klass.lineno, next_start)
        node = DefaultMutableTreeNode(self)
        parent.add(node)
        methods = klass.methods.items()
        # sort by line numbers (name,line)
        methods.sort(lambda (name1,line1),(name2,line2): line1 - line2)
        for i in range(len(methods)):
            name,lineno = methods[i]
            _next_start = next_start
            try:
                _next_start = methods[i+1][-1]
            except IndexError:
                pass
            MethodAsset(name,lineno,node,buffer,_next_start)


class MethodAsset(PyAsset):

    def __init__(self, name, lineno, parent, buffer, next_start):
        PyAsset.__init__(self, 'def ' + name + '():', lineno, buffer)
        self.createPositions(buffer, lineno, next_start)
        parent.add(DefaultMutableTreeNode(self))


class FunctionAsset(PyAsset):

    def __init__(self, klass, parent, buffer, next_start):
        PyAsset.__init__(self, 'def ' + klass.name + '():', klass.lineno, buffer)
        self.createPositions(buffer, klass.lineno, next_start)
        parent.add(DefaultMutableTreeNode(self))

def _parse(buffer, errorsource):
    if buffer.mode.name == "python":
        tree = sidekick.SideKickParsedData(buffer.getName())
        pyclbr._modules = {}    # HACK: dump pyclbr module cache
        modname = os.path.splitext(buffer.getName())[0]
        moddir = os.path.split(buffer.getPath())[0]
        items = pyclbr.readmodule_ex(modname, [moddir]).items()
        # sort items by lineno
        items.sort(lambda a,b: a[1].lineno - b[1].lineno)
        for i in range(len(items)):
            name,klass = items[i]
            next_start = None
            try:
                next_start = items[i+1][-1].lineno   # next 'klass'
            except IndexError:
                pass
            if isinstance(klass, pyclbr.Function):
                 node = FunctionAsset(klass, tree.root, buffer, next_start)
            else:
                node = ClassAsset(klass, tree.root, buffer, next_start)
        return tree
    else:
        return None

def __registerParser():
    """
    registers Python parser with SideKick plugin.
    """
    try:
        parser = PythonParser()
        sidekick.SideKickPlugin.registerParser(parser)
    except Exception,e:
        print str(e)


def __unregisterParser():
    """
    unregisters Python parser with SideKick plugin.
    """
    try:
        parser = PythonParser()
        sidekick.SideKickPlugin.unregisterParser(parser)
    except Exception,e:
        print str(e)


if __name__ in ('__main__','main'):
    parser = PythonParser()
    try:
        sidekick.SideKickPlugin.unregisterParser(parser)
    except:
        pass
    sidekick.SideKickPlugin.registerParser(parser)

# :indentSize=4:lineSeparator=\n:noTabs=true:tabSize=4:
