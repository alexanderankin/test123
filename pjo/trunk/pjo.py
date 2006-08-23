#!/usr/bin/env python
"""

$Id$
"""

import cmd
import glob
import optparse
import os
import re
import sys
import traceback

import actions
import pjolib


# ---------------------------------------------------------
# constants

CHECKSUMS = 'PACKAGE-CHECKSUMS'
PACKAGE_EXTS = ('.tgz','.zip','-bin.tgz','-bin.zip')
BATCH_RE = re.compile(r'\d{4}-\d{2}-\d{2}')
DOWNLOAD_URL = 'http://%(mirror)s.dl.sourceforge.net/sourceforge/%(project)s/'
DEFAULT_MIRRORS = 'aleron unc unm'
JEDIT_RE = re.compile(r'\d\.\d(?:\.\d)?(?:pre\d+)?')
DEFAULT_TIMEOUT = 20

# ---------------------------------------------------------
# exceptions

class PackagingError(Exception):
    """Exception raised when there's a problem packaging a plugin.
    """
    pass


# ---------------------------------------------------------
# utility functions

def dir2plugin(directory):
    """
    <path>/ActionHooks-0.5 -> ('ActionHooks','0.5')
    """
    if directory[-1:] == os.sep:
        directory = directory[:-1]
    try:
        return os.path.split(directory)[-1].split('-')
    except ValueError:
        raise PackagingError('directory should be `name-version` (example: ActionHooks-0.4.1)')

def glob_dirs(dirs):
    """
    Wrapper around glob.glob which returns directory
    patterns even if they don't exist.  This way
    one can pass a directory to be created, and it won't
    be ignored if it doesn't exist.
    """
    _dirs = []
    if type(dirs) in (type(''),type(u'')):
        dirs = [dirs]
    for d in dirs:
        d = os.path.expanduser(d)
        globbed = glob.glob(d)
        if not globbed:
            globbed.append(d)
        _dirs.extend(globbed)
    return _dirs

def path2plugin(path):
    name = get_name(path)
    return pjolib.SourcePlugin(name,path)

def get_name(path):
    return os.path.split(os.path.abspath(path))[1].split('-')[0]

# ---------------------------------------------------------
# Shell class: shell-like interface

class Shell(cmd.Cmd):
    """
    A shell-like interface for packaging jEdit plugins.
    """

    prompt = 'pjo$ '
    PROMPT = 'pjo [%(BATCH)s]$ '

    def __init__(self, batch, username, install_dir,
                 settings_dir, download_dir, timeout,
                 pjorc, debug):
        cmd.Cmd.__init__(self)
        self.env = {'PROMPT': self.PROMPT, 
                    'BATCH': '',
                    'MIRRORS': DEFAULT_MIRRORS,
                    'TIMEOUT': DEFAULT_TIMEOUT}
        self.init(pjorc) # run rc scripts
        self.env['DEBUG']=debug
        if batch:
            self.env['BATCH']=batch
        else:
            d = os.path.split(os.getcwd())[-1]
            if BATCH_RE.match(d):
                self.env['BATCH']=d
            else:
                self.env['BATCH'] = ''
        # download_dir defaults to parent directory of 
        # batch, or current directory if there is no batch
        # XXX ensure batch directory exists
        #     if defaulting to parent dir
        if not download_dir and not self.env.get('DOWNLOAD',''):
            if self.env['BATCH']:
                download_dir = '..'
            else:
                download_dir = '.'
        elif download_dir:
            self.env['DOWNLOAD']=download_dir
        try:
            if not username:
                for var in ('USER','USERNAME'):
                    if os.environ.has_key(var):
                        self.env['USER'] = os.environ[var]
                        break
                else:
                    raise KeyError, 'USER not found'
            else:
                self.env['USER'] = username
        except:
            try:
                # get username w/for win32
                if sys.platform == 'win32':
                    import win32api
                    self.env['USER'] = win32api.GetUserName()
                else:
                    print "WARNING: couldn't determine USER"
            except ImportError:
                print "WARNING: couldn't determine USER"
        if install_dir:
            self.env['INSTALL']=os.path.expanduser(install_dir)
        if settings_dir:
            self.env['SETTINGS']=os.path.expanduser(settings_dir)
        self.update_prompt()

    def init(self,pjorc):
        for path in ('~/.pjorc','./.pjorc'):
            path = os.path.expanduser(path)
            if os.path.exists(path):
                self.run_rc(path)
        if pjorc:
            self.run_rc(pjorc)

    def run_rc(self,path):
        try:
            f = file(path)
            lines = f.readlines()
            f.close()
        except IOError,e:
            print 'ERROR: %s' % e
            return
        for line in lines:
            line = line[:-1].strip()
            if line != '' and not line[0] in ('#',';'):
                self.onecmd(line)

    def update_prompt(self):
        self.prompt = self.env['PROMPT'] % self.env

    def help_env(self):
        print 'list variables'
    def do_env(self,arg):
        for k,v in self.env.items():
            print '%s=%s' % (k,v)

    def help_exit(self):
        print 'exit pjo shell'
    def do_exit(self,arg):
        sys.exit(0)

    def help_export(self):
        print 'export sets a variable'
        print 'Usage: export name=value'

    def do_export(self,arg):
        try:
            n,v = arg.split('=',1)
            n = n.upper()
            try:
                if n == 'TIMEOUT':
                    v = int(v)
                elif n == 'DEBUG':
                    v = bool(v)
                else:
                    v = os.path.expanduser(v)
                self.env[n]=v
                self.update_prompt()
            except ValueError,e:
                print 'ERROR: %s' % e
        except Exception:
            self.help_export()

    help_EOF = help_exit
    do_EOF = do_exit

    def _checkout(self,arg,klass,help_fn):
        """
        arg: name-version tag
        klass: checkout action class (SvnCheckout, CvsCheckout)
        help_fn: help function (svn or cvs specific)
        """
        if not arg:
            help_fn()
            return
        try:
            directory,tag = arg.split()
            name,version = dir2plugin(directory)
            co = klass(name,version,tag,self.env['USER'])
            co.execute(None)    # no plugin, yet
            assert not pjolib.SourcePlugin(name,directory) is None
        except (ValueError,PackagingError),e:
            self.help_checkout()
            if self.env['DEBUG']:
                traceback.print_exc()
        except Exception, e:
            print 'ERROR: %s' % e
            if self.env['DEBUG']:
                traceback.print_exc()

    def help_checkout(self):
        print 'get source, by tag, for a plugin from CVS (for preparing release)'
        print 'Usage: checkout directory tag'

    def do_checkout(self,arg):
        self._checkout(arg,actions.CvsCheckout,self.help_checkout)
    do_co = do_checkout     # alias for brevity
    do_cvsco = do_checkout
    help_cvsco = help_checkout

    def help_svnco(self) :
        print 'get source, by tag, for a plugin from SVN (for preparing release)'
        print 'Usage: svnco directory tag'
    def do_svnco(self,arg) :
        self._checkout(arg,actions.SvnCheckout,self.help_svnco)

    def help_download(self):
        print 'downloads a version of a plugin or jEdit.'
        print 'Usage: download (name-version|version)+'
        print 'examples: download ActionHooks-0.5 4.2pre5'
    def do_download(self,arg):
        # XXX handle missing vars, bad vars
        mirrors = self.env['MIRRORS'].split()
        timeout = int(self.env['TIMEOUT'])
        dest = self.env['DOWNLOAD']
        for a in arg.split():
            try:
                if '-' in a:
                    name,version = dir2plugin(a)
                    download = actions.DownloadPlugin(name,version,dest,mirrors,timeout)
                    download.execute(None)
                elif JEDIT_RE.match(a):
                    download = actions.DownloadJEdit(a,dest,mirrors,timeout)
                    download.execute(None)
                else:
                    print "ERROR: don't recognize `%s`" % a
            except actions.ActionError,e:
                print 'ERROR: %s' % e
                if self.env['DEBUG']:
                    traceback.print_exc()
    do_dl = do_download     # alias for brevity

    
    
    def help_install(self):
        # XXX doesn't yet work w/plugins
        print 'installs a plugin, or jedit'
        print 'Usage: install (name-version|version)+'
        print 'examples: install ActionHooks-0.5 4.2pre13'
    def do_install(self,arg):
        # XXX handle missing vars, bad vars
        mirrors = self.env['MIRRORS'].split()
        timeout = int(self.env['TIMEOUT'])
        download_dir = self.env['DOWNLOAD']
        install_dir = self.env['INSTALL']
        settings_dir = self.env['SETTINGS']
        args = arg.split()
        if not args:
            self.help_install()
            return
        args.reverse()

        while args:
            plugins = []
            version = None
            while 1:
                a = args.pop()
                if '-' in a:
                    plugins.append(a)
                elif JEDIT_RE.match(a):
                    version = a
                    break
                else:
                    print 'ERROR: expected jEdit version after plugins'
                    return
            if plugins and not version:
                print 'ERROR: expected jEdit version after plugins'
                return
            plugin_dir = os.path.join(settings_dir, '.%s' % version, 'jars')
            if not plugins and version:
                try:
                    install = actions.InstallJEdit(a,download_dir,install_dir,mirrors,timeout)
                    install.execute(None)
                except actions.ActionError,e:
                    print 'ERROR: %s' % e
                    if self.env['DEBUG']:
                        traceback.print_exc()
            while plugins:
                p = plugins.pop()
                #print 'installing %s into %s' % (p,version)
                try:
                    name,version = p.split('-')
                    install = actions.InstallPlugin(name,version,
                                                download_dir,
                                                plugin_dir,
                                                mirrors,
                                                timeout)
                    install.execute(None)
                except actions.ActionError,e:
                    print 'ERROR: %s' % e
                    if self.env['DEBUG']:
                        traceback.print_exc()
    do_dl = do_download     # alias for brevity
    do_in = do_install  # alias for brevity

    def help_package(self):
        print 'package one or more plugins'
        print 'Usage: pkg directory [directory]+'
    def do_package(self,arg):
        if not arg:
            self.help_package()
            return
        dirs = glob_dirs([arg])
        if not dirs:
            self.help_package()
        for directory in dirs:
            name = get_name(directory)
            plugin = pjolib.SourcePlugin(name,directory)
            a = actions.Package()
            try:
                a.execute(plugin)
            except Exception,e:
                print 'ERROR: %s' % e
                if self.env['DEBUG']:
                    traceback.print_exc()
    do_pkg = do_package     # alias for brevity

    def help_patch(self):
        print 'generate a patch for one or more plugins'
        print 'Usage: patch directory [directory]+'
    def do_patch(self,arg):
        if not arg:
            self.help_patch()
            return
        dirs = glob_dirs([arg])
        if not dirs:
            self.help_patch()
        for directory in dirs:
            name = get_name(directory)
            plugin = pjolib.SourcePlugin(name,directory)
            a = actions.Patch()
            try:
                a.execute(plugin)
            except Exception,e:
                print 'Error: %s' % e
                if self.env['DEBUG']:
                    traceback.print_exc()

    def help_props(self):
        print 'displays plugin properties for one or more plugins'
        print 'Usage: props directory [directory]+'
    def do_props(self,arg):
        if not arg:
            self.help_props()
            return
        dirs = glob_dirs([arg])
        if not dirs:
            self.help_package()
        for d in dirs:
            try:
                # XXX would be better to only display plugin properties
                p = pjolib.SourcePlugin(get_name(d),d)
                props = p.properties
                for n,v in props.items():
                    print '%s=%s' % (n,v)
            except Exception,e:
                print 'ERROR: %s' % e
                if self.env['DEBUG']:
                    traceback.print_exc()

    def help_upload(self):
        print 'uploads one or more plugin\'s packages to shell server'
        print 'Usage: upload directory [directory]+'
    def do_upload(self,arg):
        if not arg:
            self.help_upload()
            return
        dirs = glob_dirs([arg])
        if not dirs:
            self.help_upload()
        for directory in dirs:
            try:
                u = actions.Upload(self.env['BATCH'],self.env['USER'])
                name = get_name(directory)
                plugin = pjolib.SourcePlugin(name,directory)
                u.execute(plugin)
            except Exception,e:
                print 'ERROR: %s' % e
                if self.env['DEBUG']:
                    traceback.print_exc()
    do_up = do_upload    # alias for brevity

    def help_verify(self):
        print 'checks one or more plugin\'s package releases for integrity'
    def do_verify(self,arg):
        if not arg:
            self.help_verify()
            return
        dirs = glob_dirs(arg.split())
        if not dirs:
            self.help_verify()
        mirrors = self.env['MIRRORS'].split()
        timeout = int(self.env['TIMEOUT'])
        download_dir = self.env['DOWNLOAD']
        args = arg.split()
        for directory in dirs:
            try:
                name = get_name(directory)
                p = pjolib.SourcePlugin(name,directory)
                v = actions.CheckPackageDownloads(mirrors,timeout,download_dir)
                v.execute(p)
            except Exception,e:
                print 'ERROR: %s' % e
                if self.env['DEBUG']:
                    traceback.print_exc()

    def emptyline(self):
        pass

# ---------------------------------------------------------
# main

def main(args=None):

    if args is None:
        args = sys.argv

    parser = optparse.OptionParser(usage='%prog [options] [directory]+')
    parser.add_option('-b','--batch',default=None,
                      metavar='<date>',
                      help='Batch date (YYYY-MM-DD)')
    parser.add_option('-c','--command',action='append',
                      default=[],dest='commands',
                      help='command to execute')
    parser.add_option('-i','--install-dir',default=None,
                      metavar='<dir>',
                      help='directory for jEdit installations')
    parser.add_option('-d','--download-dir',default=None,
                      metavar='<dir>',
                      help='directory for plugin and jEdit downloads')
    parser.add_option('-r','--pjorc',default=None,
                      metavar='<path>',
                      help='pjorc file to run after defaults')
    parser.add_option('-s','--settings-dir',default=None,
                      metavar='<dir>',
                      help='directory containing .jedit directories')
    parser.add_option('-t','--timeout',default=20,type='int',
                      metavar='<num>',
                      help='socket timeout, in seconds')
    parser.add_option('-u','--username',default=None,
                      help='sf.net username')
    parser.add_option('--debug',action='store_true',
                      default=False,
                      help='enable tracebacks in errors')

    options,args = parser.parse_args(args)

    if options.batch:
        if not BATCH_RE.match(options.batch):
            parser.print_usage()
            sys.exit(2)

    shell = Shell(batch=options.batch,
                  username=options.username,
                  install_dir=options.install_dir,
                  settings_dir=options.settings_dir,
                  download_dir=options.download_dir,
                  timeout=options.timeout,
                  pjorc=options.pjorc,
                  debug=True)
    for c in options.commands:
        cmd = c + ' ' + ' '.join(args[1:])
        shell.onecmd(cmd)

    if not options.commands:
        shell.cmdloop()


if __name__ == '__main__':
    if ''.join(sys.argv[1:]).lower() in ('test','-t','--test',):
        import doctest, pjo
        doctest.testmod(pjo)
    else:
        main()

# :wrap=none:noTabs=true:lineSeparator=\n:tabSize=4:indentSize=4:deepIndent=true:
