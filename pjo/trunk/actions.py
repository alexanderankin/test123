"""
PJO Actions

This modules contains package-related actions
like Package, Checkout, etc...

$Id$
"""

import os
import socket
import sys
import tarfile
import urllib
import zipfile

from pjolib import debug,info,warn,error,get_log,hexdigest
import checks

__all__ = ['Action','Checkout','CvsCheckout','SvnCheckout','DownloadJEdit',
           'DownloadPlugin','InstallJEdit','InstallPlugin',
           'Package','PropertiesCheck','Upload',]

DEFAULT_TIMEOUT = 30    # seconds   # XXX put in pjorc
DOWNLOAD_URL = 'http://%(mirror)s.dl.sourceforge.net/sourceforge/%(project)s/%(filename)s'

ERR_CORRUPT_DOWNLOAD = '`%s` is not a valid archive. ' \
                       'This may mean the file wasn\'t found ' \
                       'and a 404 page was downloaded.'

class ActionError(Exception):
    """
    Exception thrown when an action fails
    """
    pass


class Action(object):
    """
    Base class for all actions.
    """

    pre_checks = []
    post_checks = []

    def do_action(self,plugin):
        """do_action(plugin:Plugin)

        Action execution code.  Sub-classes
        should add put their logic in `do_action`.
        """
        raise NotImplementedError

    def execute(self,plugin):
        """execute(plugin:Plugin)

        Call this method to execute the action.
        It runs pre- and post-execution checks
        and executes the action.
        """
        # XXX what level of exception
        #     should stop the process?
        for check in self.pre_checks:
            print check.description,'...',
            check.execute(plugin)
            print 'OK'
        self.do_action(plugin)
        for check in self.post_checks:
            print check.description,'...',
            check.execute(plugin)
            print 'OK'


class Checkout(Action):
    """
    Checks-out the source for a plugin from a sf.net code repo.

    The source is put in::
        name-version/name

    This class is abstract -- you really want either 
    `SvnCheckout` or `CvsCheckout`.
    """

    pre_checks, post_checks = [], []

    def __init__(self,name,version,tag,username):
        """
        name:
            plugin name
        version:
            plugin version
        tag:
            tag name
        username:
            sf.net username
        """
        self.directory = '%s-%s' % (name,version)
        self.name = name
        self.version = version
        self.tag = tag
        self.username = username

    def do_action(self, plugin):
        info('Checking out %s-%s' % (self.name,self.version))
        cwd = os.getcwd()
        try:
            if not os.path.exists(self.directory):
                debug('creating %s' % self.directory)
                os.makedirs(self.directory)
            os.chdir(self.directory)
            cmd = self.CMD % {'username': self.username,
                              'name': self.name,
                              'tag': self.tag,
                              'version': self.version}
            debug('executing %s' % cmd)
            os.system(cmd)
        finally:
            os.chdir(cwd)


class SvnCheckout(Checkout):
    """
    svn-specific Checkout action.
    """
    # template for svn checkout command
    CMD = 'svn co https://jedit.svn.sourceforge.net/svnroot/jedit/plugins/%(name)s/tags/%(tag)s/ %(name)s'



class CvsCheckout(Checkout):
    """
    cvs-specific Checkout action.
    """

    # template for CVS checkout command
    CMD = 'cvs -z3 -d:ext:%(username)s@jedit.cvs.sourceforge.net:/cvsroot/jedit co -r %(tag)s -d %(name)s plugins/%(name)s'


class CheckPackageDownloads(Action):
    """
    Checks whether the files released on sf.net match those
    we have sitting on disk.
    """
    def __init__(self,mirrors,timeout,download_dir):
        self.mirrors = mirrors
        self.timeout = timeout
        self.download_dir = download_dir

    def do_action(self,plugin):
        for p in plugin.packages:
            r = DownloadRelease(p,'jedit-plugins',self.download_dir,self.mirrors,self.timeout)
            r.execute(plugin)
            path_here = os.path.join(plugin.directory,p)
            path_there = os.path.join(self.download_dir,p)
            d_here = hexdigest(path_here)
            d_there = hexdigest(path_there)
            if d_here != d_there:
                print '%s:\n %s\n!=%s:\n %s' % (d_here,path_here,d_there,path_there)
            else:
                print '%s: OK' % p


class DownloadRelease(Action):
    """
    Downloads a release from sourceforge.net
    """
    # XXX add a force param which overwrites already downloaded files
    def __init__(self,filename,project,dest,mirrors,timeout):
        assert len(mirrors) > 0 and len(mirrors[0]) > 1, \
            'mirrors must be a list of strings'
        self.filename = filename
        self.project = project
        self.mirrors = mirrors
        self.dest = dest
        socket.setdefaulttimeout(timeout)

    def do_action(self,plugin):
        dest_path = os.path.join(self.dest,self.filename)

        if os.path.exists(dest_path):
            info('%s already exists in %s' % (self.filename,self.dest,))
            return

        info('downloading %s from %s' % (self.filename,self.project))
        # try mirrors one at a time, break from loop if we get it
        for mirror in self.mirrors:
            debug('using mirror %s' % mirror)
            url = DOWNLOAD_URL % {'mirror': mirror,
                                  'project': self.project,
                                  'filename': self.filename}
            debug('downloading %s from %s to %s' % (self.filename,url,self.dest))

            # download
            try:
                data = urllib.urlopen(url).read()
            except socket.timeout:
                continue

            # write
            f = file(dest_path,'wb')
            f.write(data)
            f.close()

            # check -- in case of 404, etc...
            if dest_path.endswith('.tgz'):
                if not tarfile.is_tarfile(dest_path):
                    os.remove(dest_path)
                    error(ERR_CORRUPT_DOWNLOAD % (self.filename,))
                else:
                    break   # file is OK
            else:   # assume .zip if not .tgz
                try:
                    if not zipfile.ZipFile(dest_path).testzip() is None:
                        os.remove(dest_path)
                        error(ERR_CORRUPT_DOWNLOAD % (self.filename,))
                    else:
                        break   # file OK
                except zipfile.BadZipfile:
                    os.remove(dest_path)
                    error(ERR_CORRUPT_DOWNLOAD % (self.filename,))

        if not os.path.exists(dest_path):
            raise ActionError('failed to download %s from %s' % (self.filename,self.project))


class DownloadJEdit(DownloadRelease):

    project = 'jedit'

    def __init__(self,version,dest,mirrors,timeout=DEFAULT_TIMEOUT):
        filename = 'jedit%(version)sinstall.jar' % {'version': version.replace('.','')}
        DownloadRelease.__init__(self,filename,self.project,dest,mirrors,timeout)


class DownloadPlugin(DownloadRelease):

    project = 'jedit-plugins'

    def __init__(self,name,version,dest,mirrors,timeout=DEFAULT_TIMEOUT):
        # if types is None:
        #     types = ['.zip',]
        # assert types
        # for t in types:
        #     filename = '%s-%s%s' % (name,version,t)
        #     DownloadRelease.__init__(self,filename,self.project,dest,mirrors,timeout)
        filename = '%s-%s.zip' % (name,version)
        DownloadRelease.__init__(self,filename,self.project,dest,mirrors,timeout)


class InstallJEdit(Action):
    CMD = '%(java)s -jar %(jar)s auto %(dest)s'

    def __init__(self,version,download_dir,dest,mirrors,timeout,java='java'):
        self.version = version
        self.download_dir = download_dir
        self.dest = dest
        self.java = java
        self.mirrors = mirrors
        self.timeout = timeout
        self.jar = 'jedit%sinstall.jar' % version.replace('.','')

    def do_action(self,plugin):
        assert plugin is None

        jar = os.path.join(self.download_dir,self.jar)

        if not os.path.exists(jar):
            download = DownloadJEdit(self.version,self.download_dir,
                                     self.mirrors,self.timeout)
            download.execute(plugin)

        dest = os.path.join(self.dest,self.version)
        if not os.path.exists(dest):
            debug('creating %s' % dest)
        cmd = self.CMD % {'java': self.java,
                          'jar': jar,
                          'dest': dest}
        debug('install cmd: %s' % cmd)
        os.system(cmd)


class InstallPlugin(Action):

    def __init__(self,name,version,download_dir,dest,mirrors,timeout):
        self.name = name
        self.version = version
        self.download_dir = download_dir
        self.dest = dest
        self.mirrors = mirrors
        self.timeout = timeout

    # XXX may an AbstractPlugin class, for just keeping
    #     name & version would be handy for actions that
    #     don't actually need a plugin
    def do_action(self,plugin):
        assert plugin is None

        download = DownloadPlugin(self.name,self.version,
                                  self.download_dir,self.mirrors,
                                  self.timeout)
        path = os.path.join(self.download_dir,download.filename)
        if not os.path.exists(path):
            download.execute(plugin)
        assert os.path.exists(path)
        assert zipfile.is_zipfile(path)
        archive = zipfile.ZipFile(path)
        jars = filter(lambda f: f.lower().endswith('.jar'),archive.namelist())
        for jar in jars:
            path = os.path.join(self.dest,jar)
            print 'saving %s to %s' % (jar,path)
            f = file(path,'wb')
            f.write(archive.read(jar))
            f.close()
        archive.close()


class Package(Action):
    """
    Package creates source and binary archives
    for a plugin.

    The following archives are created:

    * <name>-<version>-bin.tgz
    * <name>-<version>-bin.zip
    * <name>-<version>.tgz
    * <name>-<version>.zip
    """

    pre_checks = [checks.JarsCheck(),
                  checks.VersionCheck(),
                  checks.PropertiesCheck(),
                  checks.DocsCheck(),]

    post_checks = [checks.PackagesCheck(),]

    def do_action(self,plugin):
        """action implmenetation"""

        cwd = os.getcwd()
        debug('changing to %s' % plugin.path)
        os.chdir(plugin.path)

        try:
            self.pkg_src_zip(plugin)
            self.pkg_src_tgz(plugin)
            self.pkg_bin_zip(plugin)
            self.pkg_bin_tgz(plugin)
        finally:
            os.chdir(cwd)

    def pkg_bin_zip(self,plugin):
        """pkg_bin_zip(plugin:Plugin) -> filename:str

        Creates a zip archive with only jar files.
        """
        filename = '%(name)s-%(version)s-bin.zip' % {'name': plugin.name,
                                                    'version': plugin.version}
        info('creating %s' % filename)
        z = zipfile.ZipFile(filename,'w')
        for jar in plugin.jars:
            debug('adding %s' % jar)
            z.write(jar,jar,zipfile.ZIP_DEFLATED)
        # add the plugin jar
        plugin_jar = os.path.split(plugin.jar.path)[1]
        debug('adding %s' % plugin_jar)
        z.write(plugin_jar,plugin_jar,zipfile.ZIP_DEFLATED)
        z.close()
        debug('created %s' % filename)
        return filename

    def pkg_src_zip(self,plugin):
        """pkg_src_zip(plugin:Plugin) -> filename:str

        Creates a zip archive containing jar files
        and source files.
        """
        filename = '%(name)s-%(version)s.zip' % {'name': plugin.name,
                                                 'version': plugin.version}
        info('creating %s' % filename)
        z = zipfile.ZipFile(filename, 'w')
        for f in plugin.files:
            path = os.path.join(plugin.src_dir,f)
            debug('adding %s as %s' % (f,path))
            z.write(path,path,zipfile.ZIP_DEFLATED)
        for j in plugin.jars:
            debug('adding %s' % j)
            z.write(j,j,zipfile.ZIP_DEFLATED)
        # add the plugin jar
        plugin_jar = os.path.split(plugin.jar.path)[1]
        debug('adding %s' % plugin_jar)
        z.write(plugin_jar,plugin_jar,zipfile.ZIP_DEFLATED)
        z.close()
        debug('created %s' % filename)
        return filename

    def pkg_bin_tgz(self,plugin):
        """pkg_bin_tgz(plugin:Plugin) -> filename:str

        Creates a tgz archive with only jar files.
        """
        filename = '%(name)s-%(version)s-bin.tgz' % {'name': plugin.name,
                                                     'version': plugin.version}
        info('creating %s' % filename)
        t = tarfile.open(filename,'w:gz')
        for jar in plugin.jars:
            debug('adding %s' % jar)
            t.add(jar,jar,False)
        # add the plugin jar
        plugin_jar = os.path.split(plugin.jar.path)[1]
        debug('adding %s' % plugin_jar)
        t.add(plugin_jar,plugin_jar,False)
        t.close()
        debug('created %s' % filename)
        return filename

    def pkg_src_tgz(self,plugin):
        """pkg_src_tgz(plugin:Plugin) -> filename:str

        Creates a tgz archive containing jar files
        and source files.
        """
        filename = '%(name)s-%(version)s.tgz' % {'name': plugin.name,
                                                 'version': plugin.version}
        info('creating %s' % filename)
        t = tarfile.open(filename,'w:gz')
        for f in plugin.files:
            path = os.path.join(plugin.src_dir,f)
            debug('adding %s as %s' % (f,path))
            t.add(path,path,False)
        for j in plugin.jars:
            debug('adding %s' % j)
            t.add(j,j,False)
        # add the plugin jar
        plugin_jar = os.path.split(plugin.jar.path)[1]
        debug('adding %s' % plugin_jar)
        t.add(plugin_jar,plugin_jar,False)
        t.close()
        return filename


class Patch(Action):

    CMD = 'cvs -Q diff -u'

    def do_action(self,plugin):
        cwd = os.getcwd()
        os.chdir(os.path.join(plugin.directory,plugin.src_dir))
        try:
            i,o,e = os.popen3(self.CMD)
            out = o.read()
            err = e.read()
            status = i.close()
            assert not status, 'failed w/status: %s' % (`status`,)
            assert not err, 'error output: %s' % (`err`,)
            if out:
                os.chdir(cwd)
                filename = os.path.join(plugin.directory,plugin.name + '.diff')
                f = open(filename,'w')
                f.write(out)
                f.close()
        finally:
            os.chdir(cwd)


class Upload(Action):

    SERVER = 'jedit-plugins.sf.net'
    DIR = '/home/groups/j/je/jedit-plugins/batch-%(batch)s/'
    CMD = '%(scp_cmd)s %(filename)s %(username)s@%(server)s:%(directory)s'

    pre_checks = []
    post_checks = [checks.PackagesCheck(),]

    def __init__(self,batch,username):
        self.batch = batch
        self.username = username

    def do_action(self,plugin):
        info('Uploading packages for %s' % plugin)
        if sys.platform == 'win32':
            scp_cmd = 'pscp'    # using putty
        else:
            scp_cmd = 'scp'
        directory = self.DIR % {'batch': self.batch}
        for package in plugin.packages:
            pkg_path = os.path.join(plugin.directory,package)
            info('uploading %s' % pkg_path)
            cmd = self.CMD % {'scp_cmd': scp_cmd,
                              'filename': pkg_path,
                              'username': self.username,
                              'server': self.SERVER,
                              'directory': directory}
            debug(cmd)
            os.system(cmd)


def test():
    import doctest,actions
    doctest.testmod(actions)


def test_download_file():
    """
    >>> dest_dir = 'test/downloads'
    >>> mirrors = ['aleron','unm']
    >>> download = DownloadRelease('jedit42pre11install.jar','jedit',dest_dir,mirrors,20)
    >>> download.execute(None) # no plugin required

    >>> download = DownloadJEdit('4.1',dest_dir,[],20)
    Traceback (most recent call last):
    ...
    AssertionError: mirrors must be a list of strings

    >>> download = DownloadJEdit('4.1',dest_dir,mirrors,20)
    >>> download.execute(None)

    >>> download = DownloadPlugin('ActionHooks','0.5',dest_dir,mirrors,20)
    >>> download.execute(None)
    """


def test_install_jedit():
    """
    >>> dest_dir = 'test/jedit'
    >>> download_dir = 'test/downloads'
    >>> install = InstallJEdit('4.1',download_dir,dest_dir,['aleron',],20,'java')
    >>> install.execute(None)
    """

if __name__ == '__main__':
    test()

# :deepIndent=true:
