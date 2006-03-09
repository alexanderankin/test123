"""
PJO Checks

This modules contains checks that can be
on plugins.

$Id$
"""

import fnmatch
import os
import tarfile
import zipfile

# XXX what about allowing one to interactively abort on errors?
#     need an easy way to control whether or not processing should
#     continue when a check fails -- could be type of check.

# XXX add logging?

class CheckFailed(Exception):
    """Exception thrown when a check fails."""
    pass
class CheckWarning(Exception):
    """Exception thrown as a warning message."""
    pass

class Check(object):
    """
    Abstract base class for plugin checks.

    Sub-classes must implement `do_check`.
    """

    def do_check(self,plugin):
        """
        Sub-classes must implement.
        """
        raise NotImplementedError

    def execute(self,plugin):
        self.do_check(plugin)


class DocsCheck(Check):
    description = 'Verify plugin jar contains docs'
    def do_check(self,plugin):
        docs = plugin.jar.properties.docs
        if not docs:
            # XXX test for this in PropertiesCheck
            return
        if not docs in plugin.jar:
            raise CheckFailed('%s not found in %s (%s)' % (docs,plugin.jar,plugin.jar.path))

class JarsCheck(Check):
    description = 'Verify plugin has no missing or extra jars'
    def do_check(self,plugin):
        n,v = get_nv_from_path(plugin)
        d = plugin.directory
        # ensure we have all required jars
        for jar in plugin.properties.jars:
            if not os.path.exists(os.path.join(d,jar)):
                raise CheckFailed('%s not found for %s' % (jar,plugin))
            # check jar integrity
            try:
                if not zipfile.ZipFile(os.path.join(d,jar)).testzip() is None:
                    raise CheckFailed('%s is corrupt for %s' % (jar,plugin))
            except zipfile.BadZipfile:
                raise CheckFailed('%s is corrupt for %s' % (jar,plugin))
        # ensure we have no extra jars
        for jar in fnmatch.filter(os.listdir(d),'*.jar'):
            if not jar in plugin.properties.jars:
                # make sure we're not bitching about the plugin itself
                if jar == (n+'.jar'):
                    continue
                raise CheckFailed('%s not plugin and not in jars for %s' % (jar,plugin))
        # ensure we have plugin jar
        plugin_jar = os.path.join(d,n+'.jar')
        if not os.path.exists(plugin_jar):
            raise CheckFailed('%s not found for %s' % (os.path.split(plugin_jar)[1],plugin))
        # check jar integrity
        try:
            if not zipfile.ZipFile(plugin_jar).testzip() is None:
                raise CheckFailed('%s is corrupt for %s' % (n+'.jar',plugin))
        except zipfile.BadZipfile:
            raise CheckFailed('%s is corrupt for %s' % (n+'.jar',plugin))

# XXX test
class NameCheck(Check):
    # XXX this may or may not be usefull
    description = 'Verify plugin\'s name is consistent'
    def do_check(self,plugin):
        n,v = get_nv_from_path(plugin)
        d = plugin.directory
        jar = os.path.join(d,'%s.jar')
        if not os.path.exists(jar):
            raise CheckFailed('%s not found for %s (%s)' % (jar,plugin,plugin.path))

class PackagesCheck(Check):
    description = 'Verify integrity of archives'
    def do_check(self,plugin):
        for package in plugin.packages:
            path = os.path.join(plugin.directory,package)
            if not os.path.exists(path):
                raise CheckFailed('%s not found for %s' % (package,plugin))
            if package.endswith('.zip'):
                try:
                    if not zipfile.ZipFile(path).testzip() is None:
                        raise CheckFailed('%s is corrupt for %s' % (package,plugin))
                except zipfile.BadZipfile:
                    raise CheckFailed('%s is corrupt for %s' % (package,plugin))
            elif package.endswith('.tgz'):
                if not tarfile.is_tarfile(path):
                    raise CheckFailed('%s is corrupt for %s' % (package,plugin))
            else:
                raise CheckFailed('%s not a recognized archive for %s' % (package,plugin))

class PropertiesCheck(Check):

    description = 'Check plugin\'s properties'

    # XXX figure out how to log
    # XXX might be nice to be able to specify threshold
    # XXX store all this in a config file?
    def do_check(self,plugin):
        #if not plugin.properties.jdk_version:
        #    print 'WARNING: %s has no JDK dependency' % (plugin,)
        #if not plugin.properties.jedit_version:
        #    print 'ERROR: %s no jEdit dependency' % (plugin,)
        if not plugin.properties.name:
            raise CheckFailed('%s has no name property' % (plugin,))
        if not plugin.properties.version:
            raise CheckFailed('%s has to version property' % (plugin,))
        #if not plugin.properties.docs:
        #    print 'WARNING: %s has not `doc` property' % (plugin,)
    # XXX - check for expected properties
    # name,version,jdk_version (optional)?,jedit_version,
    # activate (warning), docs (warning), others?
    pass

class VersionCheck(Check):
    description = 'Verify plugin version matches matches parent dir'
    def do_check(self,plugin):
        version = plugin.version
        if not version:
            raise CheckFailed('%s (%s) has no version' % (plugin,plugin.path))
        n,v = get_nv_from_path(plugin)
        if not v == version:
            raise CheckFailed('%s != %s, for %s (%s)' % (version,v,plugin,plugin.path))

def get_nv_from_path(plugin):
    """get_nv_from_path(path:str) -> (name:str,version:str)

    Splits directory in the path into 2 parts on '-'."""

    path = plugin.directory
    # split directory into name,version on '-'
    directory = os.path.split(path)[1]
    if not '-' in directory:
        raise CheckFailed('`-` not in parent directory of %s (%s)' % (plugin,directory))
    return tuple(directory.split('-'))

def get_plugin_dir(plugin):
    """get_plugin_dir(plugin) -> path:str

    Returns the directory containing the plugin -- 
    either packaged jar, or parent of directory 
    containing source code."""

    path = os.path.abspath(plugin.path)
    if not os.path.isdir(path):
        path = os.path.split(path)[0]
    return path

def test_get_nv_from_path():
    """
    >>> from pjolib import JarPlugin, SourcePlugin

    >>> p = JarPlugin('JythonInterpreter','test/JythonInterpreter-0.9.6/JythonInterpreter.jar')
    >>> print get_nv_from_path(p)
    ('JythonInterpreter', '0.9.6')

    >>> p = SourcePlugin('JythonInterpreter','test/JythonInterpreter-0.9.6')
    >>> print get_nv_from_path(p)
    ('JythonInterpreter', '0.9.6')
    """

# XXX move to pjolib tests, since this is done by Plugin
def test_get_plugin_dir():
    """
    >>> from pjolib import JarPlugin, SourcePlugin
    
    >>> p = JarPlugin('JythonInterpreter','test/JythonInterpreter-0.9.6/JythonInterpreter.jar')
    >>> print os.path.split(p.directory)[1]
    JythonInterpreter-0.9.6

    >>> p = SourcePlugin('JythonInterpreter','test/JythonInterpreter-0.9.6')
    >>> print os.path.split(p.directory)[1]
    JythonInterpreter-0.9.6
    """

def test_docs_check():
    """
    >>> from pjolib import JarPlugin, SourcePlugin

    This should pass.

    >>> p = JarPlugin('JythonInterpreter','test/JythonInterpreter-0.9.6/JythonInterpreter.jar')
    >>> c = DocsCheck()
    >>> c.execute(p)

    This should fail.

    >>> p = JarPlugin('SendBuffer','test/nodocs/SendBuffer.jar')
    >>> c = DocsCheck()
    >>> c.execute(p)
    Traceback (most recent call last):
    ...
    CheckFailed: SendBuffer.html not found in SendBuffer-1.0.3 (test/nodocs/SendBuffer.jar)

    Test finding the JarPlugin using a SourceJar.

    >>> p = SourcePlugin('JythonInterpreter','test/JythonInterpreter-0.9.6')
    >>> c = DocsCheck()
    >>> c.execute(p)

    JakartaCommons doesn't have any docs, but this should still pass.

    >>> p = SourcePlugin('JakartaCommons','test/JakartaCommons-0.4.1')
    >>> c = DocsCheck()
    >>> c.execute(p)
    """

def test_jars_check():
    """
    >>> from pjolib import JarPlugin, SourcePlugin

    This should pass.

    >>> p = JarPlugin('JythonInterpreter','test/JythonInterpreter-0.9.6/JythonInterpreter.jar')
    >>> c = JarsCheck()
    >>> c.execute(p)

    This should pass.

    >>> p = SourcePlugin('JythonInterpreter','test/JythonInterpreter-0.9.6')
    >>> c = JarsCheck()
    >>> c.execute(p)

    This should fail because log4j.jar is missing.

    >>> p = SourcePlugin('JakartaCommons','test/missing_lib_jar/JakartaCommons-0.4.1')
    >>> c = JarsCheck()
    >>> c.execute(p)
    Traceback (most recent call last):
    ...
    CheckFailed: log4j.jar not found for JakartaCommons-0.4.1

    This should fail because JakartaCommons.jar is missing.

    >>> p = SourcePlugin('JakartaCommons','test/missing_plugin_jar/JakartaCommons-0.4.1')
    >>> c = JarsCheck()
    >>> c.execute(p)
    Traceback (most recent call last):
    ...
    CheckFailed: JakartaCommons.jar not found for JakartaCommons-0.4.1

    This should fail becuase log4j.jar is corrupt.

    >>> p = SourcePlugin('JakartaCommons','test/corrupt_lib_jar/JakartaCommons-0.4.1')
    >>> c = JarsCheck()
    >>> c.execute(p)
    Traceback (most recent call last):
    ...
    CheckFailed: log4j.jar is corrupt for JakartaCommons-0.4.1

    >>> p = SourcePlugin('SendBuffer','test/corrupt_plugin_jar/SendBuffer-1.0.3')
    >>> c = JarsCheck()
    >>> c.execute(p)
    Traceback (most recent call last):
    ...
    CheckFailed: SendBuffer.jar is corrupt for SendBuffer-1.0.3
    """

def test_version_check():
    """
    >>> from pjolib import JarPlugin, SourcePlugin

    This should fails because there is no '-' in the
    parent directory.

    >>> p = JarPlugin('SendBuffer','test/SendBuffer.jar')
    >>> c = VersionCheck()
    >>> c.execute(p)
    Traceback (most recent call last):
    ...
    CheckFailed: `-` not in parent directory of SendBuffer-1.0.3 (test)
    
    This should pass.

    >>> p = SourcePlugin('SendBuffer','test/SendBuffer-1.0.3')
    >>> c = VersionCheck()
    >>> c.execute(p)

    This should pass.

    >>> p = JarPlugin('JythonInterpreter','test/JythonInterpreter-0.9.6/JythonInterpreter.jar')
    >>> c = VersionCheck()
    >>> c.execute(p)
    """

def test():
    import doctest, checks
    doctest.testmod(checks)

if __name__ == '__main__':
    test()

# :deepIndent=true:
