#!/usr/bin/env python
"""
pjolib.py - pjo utility functions and classes.

$Id$
"""

from cStringIO import StringIO
import fnmatch
import logging
import md5
import os
import re
import zipfile
from properties import Properties

# ---------------------------------------------------------
# constants

BATCH_RE = re.compile(r'\d{4}-\d{2}-\d{2}')
CHECKSUMS = 'PACKAGE-CHECKSUMS'
PACKAGE_EXTS = ('.tgz','.zip','-bin.tgz','-bin.zip')

# source file extensions to ignore
SRC_SKIP_EXTS = ('~','.pyc','.jar','.marks','.tgz','.zip','.class',)
# source files to ignore
SRC_SKIP_FILES = ('.cvsignore', 'Entries', 'Entries.Extra',
              'Entries.Log', 'Repository', 'Root', 'Tag',
              '.DS_Store', 'Thumbs.db', CHECKSUMS)
# source directories to ignore
SRC_SKIP_DIRS = ('.svn', 'CVS', '.bzr', '.hg', '_darcs', '.git')

# ---------------------------------------------------------
# module vars

_logger = None

# ---------------------------------------------------------
# logging

def get_log():
    """get_log() -> logging.Logger

    Creates and returns the module logger.
    """
    global _logger
    if _logger is None:
        _logger = logging.getLogger('pjolib')
        hdlr = logging.StreamHandler()
        formatter = logging.Formatter('%(levelname)s: %(message)s')
        hdlr.setFormatter(formatter)
        _logger.addHandler(hdlr)
        _logger.setLevel(logging.INFO) # DEBUG | INFO | WARN
    return _logger

def debug(msg):
    """debug(msg:str)
    Logs a debug message"""
    get_log().debug(msg)

def info(msg):
    """info(msg:str)
    Logs an info message"""
    get_log().info(msg)

def warn(msg):
    """warn(msg:str)
    Logs a warn message"""
    get_log().warn(msg)

def error(msg):
    """error(msg:str)
    Logs an error message"""
    get_log().error(msg)

# ---------------------------------------------------------
# exceptions

class PJOException(Exception):
    """Base-class for all exceptions"""
    pass
class CorruptArchive(PJOException):
    """Invalid archive file"""
    pass
class PluginError(PJOException):
    """Plugin-related error"""
    pass
class CheckFailure(PJOException):
    """Exception thrown when a check fails"""
    pass

# ---------------------------------------------------------
# PluginProps

class PluginProps(object):
    """
    Container for Plugin properties.

    This class takes care of extracting name, version, author, etc...
    from the plugin's properties.
    """
    def __init__(self,properties):
        """
        properties: properties.Properties
            plugin's properties
        """
        self.properties = properties
        self.version = None
        self.name = None
        self.author = None
        self.docs = None
        self.jars = []
        self.activate = None
        self.jedit_version = None
        self.jdk_version = None
        self.required_plugins = []
        self.optional_plugins = []

        for n in self.properties.keys():
            if n.startswith('plugin.'):
                if n.endswith('.activate'):
                    self.activate = self[n]
                    info('activate is set to %s' % self.activate)
                elif n.endswith('.author'):
                    self.author = self[n]
                elif n.endswith('.docs'):
                    self.docs = self[n]
                elif n.endswith('.jars'):
                    self.jars = self[n].split()
                elif n.endswith('.name'):
                    self.name = self[n]
                elif n.endswith('.version'):
                    self.version = self[n]
                    info('version is set to %s' % self.version)
                elif '.depend.' in n:
                    v = self[n].strip()
                    if v.startswith('jedit'):
                        # splitting and discarding first part
                        # to ensure there are only 2 parts
                        __,self.jedit_version = v.split()
                        info('Requires jEdit %s' % self.jedit_version)
                    elif v.startswith('jdk'):
                        # splitting and discarding first part
                        # to ensure there are only 2 parts
                        __,self.jdk_version = v.split()
                        info('Requires Java %s' % self.jdk_version)
                    elif v.startswith('plugin'):
                        plugin = v.split()[1:]  # discard 'plugin'
                        if len(plugin) == 2:
                            self.required_plugins.append(tuple(plugin))
                        else:
                            warn('unexpected dependency: %s=%s' % (n,v))
                        info('Required plugins %s' % self.required_plugins )
                    elif v.startswith('optional'):
                        plugin = v.split()[2:]  # discard 'optional','plugin'
                        if len(plugin) == 2:
                            self.optional_plugins.append(tuple(plugin))
                        else:
                            warn('unexpected dependency: %s=%s' % (n,v))
                        info('Optional plugins: %s' % self.optional_plugins )
                    else:
                        warn('unexpected dependency: %s=%s' % (n,v))

    def __getitem__(self,key):
        return self.properties[key]


# ---------------------------------------------------------
# JarFile

class JarFile(object):
    """
    JarFile - Read-only helper class
    for accessing contents of a jar file.
    """
    def __init__(self,path):
        self.path = path
        self.zipfile = zipfile.ZipFile(path)
        try:
            if not self.zipfile.testzip() is None:
                raise CorruptArchive('%s is corrupt' % path)
        except zipfile.BadZipfile:
            raise CorruptArchive('%s is corrupt' % path)
        self._files = None
        self._dirs = None

    def _get_files(self):
        if self._files is None:
            self._read_names()
        return self._files
    def _get_dirs(self):
        if self._dirs is None:
            self._read_names()
        return self._dirs

    def _read_names(self):
        self._dirs = []
        self._files = []
        names = self.zipfile.namelist()
        for n in names:
            if n.endswith('/'):
                self._dirs.append(n)
            else:
                self._files.append(n)

    files = property(_get_files,None,None,'names of files in archive')
    dirs = property(_get_dirs,None,None,'names of directories in archive')

    def read(self,filename):
        """read(filename:str) -> str

        Returns the contents of a file in the jar.
        """
        return self.zipfile.read(filename)

# ---------------------------------------------------------
# Plugin

class Plugin(object):
    """
    Abstract base class for JarPlugin and SourcePlugin.

    Sub-classes must provide a `files` attribute which
    contains the name of the source files in the plugin.
    """

    PACKAGE_EXTS = ('.tgz','.zip','-bin.tgz','-bin.zip')

    def __init__(self,name,path):
        self.name = name
        self.path = path
        self.properties = None
        # directory containing plugin
        if path.endswith('.jar'):
            self._directory = os.path.split(os.path.abspath(path))[0]
        else:
            self._directory = os.path.abspath(path) # ??? abspath needed?
        debug('plugin files: %s' % `self.files`)
        self._load_properties()
        if not os.path.isdir(path):
            path = os.path.split(path)[0]
        self.packages = ['%s-%s%s' % (self.name,self.version,ext)
                         for ext in PACKAGE_EXTS]

    def _get_version(self):
        if self.properties:
            return self.properties.version
        return '???'
    version = property(_get_version,None,None,'plugin version')

    def _get_directory(self):
        return self._directory
    directory = property(_get_directory,None,None,'plugin directory')

    def _get_jars(self):
        return self.properties.jars
    jars = property(_get_jars,None,None,'plugin library jars')

    def _load_properties(self):
        debug('loading properties')
        properties = Properties()
        files = fnmatch.filter(self.files,'*.props')
        debug('props files: %s' % `files`)
        if len(files) == 0:
            raise PluginError('no props files found for %s' % (self.path,))
        elif len(files) > 1:
            warn('expected 1 props file, but found %d: %s' % (len(files), `files`))
        for filename in files: #files[:-1]:
            info('loading properties from %s' % filename)
            stream = StringIO(self.read(filename))
            properties.load(stream)
            stream.close()
        self.properties = PluginProps(properties)

    def read(self,path):
        """read(path:str) -> str

        Returns the contents of a source file in the plugin.

        This must be implemented by sub-classes so files can
        be read from either the filesystem or inside an archive.
        """
        raise NotImplementedError

    def __contains__(self,filename):
        """Returns True if the plugin contains `filename`."""
        return filename in self.files

    def __getitem__(self,filename):
        """Alias for `read`."""
        return self.read(filename)

    def __str__(self):
        return '%s-%s' % (self.name,self.version)


class SourcePlugin(Plugin):
    """
    Plugin source.

    This is used to package, inspect source, etc...
    """

    def __init__(self,name,path):
        """
        name: str
            Plugin's name
        path: str
            Path to parent directory containing a directory
            with the plugin's source.  For example for
            SendBuffer-1.0.3, it might be::
                ``<foo>/SendBuffer-1.0.3
            which contains:
                ``SendBuffer``
        """
        if not os.path.exists(path):
            raise PluginError('`%s` not found' % (path,))
        self._jar = None
        self.name = name
        self.path = path
        self.src_dir = self.find_src_dir()
        self.files = find_src_files(os.path.join(self.path,self.src_dir))
        Plugin.__init__(self,name,path)

    def find_src_dir(self):
        """find_src_dir() -> path:str

        Locates plugin's source directory.  This is to deal with
        plugins like 'GesturePlugin', whose code is in 'Gestures'.
        """
        dirs = filter(os.path.isdir,[os.path.join(self.path,x) for x in os.listdir(self.path)])
        if len(dirs) == 0:
            raise PluginError('unable to find source directory for %s in %s' % (self.name,self.path))
        if len(dirs) == 1:
            return os.path.split(dirs[0])[-1]
        for d in dirs:
            name = os.path.split(d)[-1]
            if name in SRC_SKIP_DIRS:
                continue
            if name == self.name:
                return name
        raise PluginError('unable to find source directory for %s in %s' % (self.name,self.path))

    def read(self,filename):
        path = os.path.join(self.path,self.src_dir,filename)
        debug('reading %s from %s' % (filename,path))
        f = file(path)
        data = f.read()
        f.close()
        return data

    def _get_jar(self):
        if not self._jar:
            path = os.path.join(self.path,self.name + '.jar')
            debug('looking for %s' % path)
            if os.path.exists(path):
                self._jar = JarPlugin(self.name,path)
            else:
                warn("%s doesn't exist" % path)
        return self._jar
    jar = property(_get_jar,None,None,'compiled jar of plugin')


class JarPlugin(Plugin,JarFile):
    """
    Jar'd Plugin.

    This is used to inspect a compiled plugin.
    """
    def __init__(self,name,path):
        """
        name: str
            Plugin's name
        path: str
            Location of jar file.
        """
        JarFile.__init__(self,path)
        Plugin.__init__(self,name,path)
    def read(self,path):
        return JarFile.read(self,path)

    # returns self, since this is the jar
    jar = property(lambda self: self,None,None,'compiled jar of plugin')


# ---------------------------------------------------------
# utility functions

def find_files(directory,pattern):
    """find_files(directory:str,pattern:str) -> [file:str]

    Returns a list of files matching a given pattern.
    """
    names = []
    for (d,dirs,files) in os.walk(directory):
        names.extend([os.path.join(d,f) for f in files])
    return fnmatch.filter(names,pattern)

def find_src_files(directory=None):
    """find_src_files(directory:str) -> [file:str]

    Returns a list of source files to include in
    a plugin.
    """
    if directory is None:
        directory = '.'
    debug('searching for src files in %s' % directory)
    files = []
    for (dirpath,dirs,filenames) in os.walk(directory):
        for d in dirs:
            if d in SRC_SKIP_DIRS:
                dirs.remove(d)
        for f in [os.path.join(dirpath,f) for f in filenames]:
            if filter(None,map(f.endswith, SRC_SKIP_EXTS)):
                continue
            if os.path.basename(f) in SRC_SKIP_FILES:
                continue
            # remove ".\" or "./"
            if f[:1] == '.' and f[1:2] == os.sep:
                f = f[2:]
            # remove relative path portion
            # given by path argument -- mainly
            # for testing
            if f[:len(directory)] == directory:
                f = f[len(directory):]
            # remove '/' or '\'
            if f[:1] == os.sep:
                f = f[1:]
            files.append(f)
    return files

def hexdigest(filename):
    f = open(filename)
    m = md5.new()
    m.update(f.read())
    f.close()
    return m.hexdigest()

# XXX should be able to toggle interactive usage
def pick_one(prompt,choices,default=None):
    """pick_one(prompt:str,[choices],default=None) -> choice
    """
    default_index = None
    for i,choice in enumerate(choices):
        print '%d)' % i, choice
    responses = range(len(choices))
    _prompt = '%s: ' % prompt
    if default:
        try:
            default_index = choices.index(default)
            _prompt = '%s [%d]: ' % (prompt,default_index)
        except ValueError:
            warn('%s not a valid choice for %s' % (default,`choices`))
    while 1:
        try:
            response = raw_input(_prompt)
            index = int(response)
            if index in responses:
                return choices[index]
        except ValueError:
            if response == '' and not default_index is None:
                return choices[default_index]

# ---------------------------------------------------------
# tests
# XXX move tests into separate module(s)

def test():
    import doctest, pjolib
    doctest.testmod(pjolib)

def test_jarplugin_files():
    """
    >>> p = JarPlugin('SendBuffer','test/SendBuffer.jar')
    >>> 'SendBuffer.props' in p
    True
    >>> '<!DOCTYPE ACTIONS SYSTEM "actions.dtd">' in p['actions.xml']
    True
    >>> print '\\n'.join([f.replace(os.sep,'/') for f in p.files])
    META-INF/MANIFEST.MF
    SendBufferPlugin.class
    SendDialog.class
    SendBuffer.html
    SendBuffer.props
    actions.xml
    """

def test_plugin_jar():
    """
    This should return None for plugin.jar:

    >>> p = SourcePlugin('SendBuffer','test/SendBuffer-1.0.3')
    >>> `p.jar`
    'None'

    Get JarPlugin from SourcePlugin

    >>> p = SourcePlugin('JakartaCommons','test/JakartaCommons-0.4.1')
    >>> print p.jar.__class__.__name__
    JarPlugin

    >>> p.jar.jar == p.jar
    True

    >>> p = JarPlugin('JakartaCommons','test/JakartaCommons-0.4.1/JakartaCommons.jar')
    >>> print p.jar.path
    test/JakartaCommons-0.4.1/JakartaCommons.jar
    """

def test_find_src_dir():
    """
    This is an easy one:

    >>> p = SourcePlugin('SendBuffer','test/SendBuffer-1.0.3')
    >>> p.src_dir
    'SendBuffer'

    This one is a pain in the ass:

    >>> p = SourcePlugin('GesturePlugin', 'test/GesturePlugin-1.0')
    >>> p.src_dir
    'Gestures'

    This doesn't have a source dir (no dirs), so it should fail.

    >>> p = SourcePlugin('GesturePlugin', 'test/missing_src_dir')
    Traceback (most recent call last):
    ...
    PluginError: unable to find source directory for GesturePlugin in test/missing_src_dir
    """

def test_srcplugin_files():
    """
    >>> p = SourcePlugin('SendBuffer','test/SendBuffer-1.0.3')
    >>> 'SendBuffer.props' in p
    True
    >>> '<!DOCTYPE ACTIONS SYSTEM "actions.dtd">' in p['actions.xml']
    True
    >>> print '\\n'.join([f.replace(os.sep,'/') for f in p.files])
    actions.xml
    SendBuffer.html
    SendBuffer.props
    SendBufferPlugin.class
    SendDialog.class
    META-INF/MANIFEST.MF
    """

def test_package():
    # Testing finding source files to include
    # in a source package (which includes jars)
    """
    >>> import actions
    >>> p = SourcePlugin('JakartaCommons','test/JakartaCommons-0.4.1')
    >>> a = actions.Package()
    >>> print '\\n'.join([f.replace(os.sep,'/') for f in find_src_files(p.path)])
    JakartaCommons/build.xml
    JakartaCommons/jakartacommons.props
    JakartaCommons/jakartacommons/JakartaCommonsPlugin.java
    >>> print '\\n'.join(p.properties.jars)
    commons-collections.jar
    commons-logging.jar
    bcel.jar
    log4j.jar
    commons-httpclient-2.0-rc2.jar
    >>> a.execute(p)
    Verify plugin has no missing or extra jars ... OK
    Verify plugin version matches matches parent dir ... OK
    Check plugin's properties ... OK
    Verify plugin jar contains docs ... OK
    Verify integrity of archives ... OK
    """

def test_plugin_props():
    """
    Check individual properties.

    >>> p = SourcePlugin('JythonInterpreter','test/JythonInterpreter-0.9.6')
    >>> p.properties.author
    'Carlos Quiroz'
    >>> p.properties.docs
    'index.html'
    >>> p.properties.jars
    ['jython.jar', 'jythonlib.jar']
    >>> p.properties.jdk_version
    '1.4'
    >>> p.properties.jedit_version
    '04.02.11.00'
    >>> p.properties.name
    'Jython Interpreter'
    >>> p.properties.optional_plugins
    [('projectviewer.ProjectPlugin', '2.0.2')]
    >>> p.properties.required_plugins
    [('sidekick.SideKickPlugin', '0.3'), ('errorlist.ErrorListPlugin', '1.3.2')]
    >>> p.properties.activate
    'startup'
    >>> p.properties.version
    '0.9.6'

    This should fail, as the plugin won't be found.

    >>> p = SourcePlugin('JythonInterpreter','test/JythonInterpreter-0.0.0')
    Traceback (most recent call last):
    ...
    PluginError: `test/JythonInterpreter-0.0.0` not found

    This tests a plugin whose name is different from the CVS module name.

    >>> p = SourcePlugin('GesturePlugin', 'test/GesturePlugin-1.0')
    >>> p.name
    'GesturePlugin'
    >>> p.properties.name
    'Gesture Plugin'
    >>> p.properties.author
    'Jeffrey C. Hoyt'
    """

def test_pick_one():
    choices = ['red','green','blue']
    r = pick_one('pick a color',choices)
    print r
    r = pick_one('pick a color',choices,'blue')
    print r
    r = pick_one('pick a color',choices,'yellow')
    print r

def test_jarfile():
    """
    >>> path = 'test/SendBuffer.jar'
    >>> jar = JarFile(path)
    >>> print jar.path == path
    True
    >>> print '\\n'.join(jar.dirs)
    META-INF/
    >>> print '\\n'.join(jar.files)
    META-INF/MANIFEST.MF
    SendBufferPlugin.class
    SendDialog.class
    SendBuffer.html
    SendBuffer.props
    actions.xml
    >>> print jar.files[0]
    META-INF/MANIFEST.MF
    >>> print len(jar.read(jar.files[0]))
    55
    """
    pass


if __name__ == '__main__':
    test()
