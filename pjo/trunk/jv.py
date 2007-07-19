#!/usr/bin/python

"""
A script for running different versions
of jEdit with different settings directories.

$Id$
"""

import optparse
import os
import sys
import urllib

JEDIT_HOME = {
    'darwin': '/Users/orutherfurd/Code/jEdit',
    'win32': 'C:/jEdit',
    'linux2' : '/home/ezust/workspace/jedit/pjo'
}

DOWNLOAD_URL = 'http://easynews.dl.sourceforge.net/sourceforge/jedit/'

JAVA = 'java'
if os.environ.get('JAVA_HOME',None):
    JAVA = os.path.join(os.environ.get('JAVA_HOME'),'bin','java')

class JarNotFound(Exception):
    pass

def install(version,options):
    print 'installing', version, '...',
    path = os.path.join(options.root, version)
    if os.path.exists(path):
        print '%s already exists'
        return

    # XXX use a download directory
    # XXX determine whether or not cmd-line 
    # install works based on the version number
    filename = 'jedit%(version)sinstall.jar' % {'version': version.replace('.','')}
    url = DOWNLOAD_URL + filename;
    print 'downloading ', url, '...',
    jar = urllib.urlopen(url).read()
    print 'saving ', filename, '...',
    f = open(filename,'wb')
    f.write(jar)
    f.close()
    print 'installing', '...',
    cmd = '%s -jar %s auto %s' % (JAVA,filename,path)
    print 'cmd=%s' % cmd
    os.system(cmd)
    print 'OK'
    return os.path.join(path,'jedit.jar')

def get_settings_dir(version,options):
    if sys.platform == 'win32':
        settings = 'c:/jEdit/.%(version)s' % locals()
    else:
        settings = os.path.join(options.root,'.%(version)s' % locals())
    return settings

def error(msg):
    sys.stderr.write("ERROR: " + msg)
    sys.exit(1)

def run(version,options):
    import shutil
    settings = get_settings_dir(version,options)
    if options.clean and os.path.exists(settings):
        print 'removing %s' % settings
        shutil.rmtree(settings)
    jar = None
    java = JAVA
    try:
        jar = find_jar(version,options)
    except JarNotFound,e:
        jar = install(version,options)
    cmd = '%(java)s -jar %(jar)s -settings=%(settings)s -noserver' % locals()
    print cmd
    os.system(cmd)

def find_jar(version,options):
    jar = os.path.join(options.root,version,'jedit.jar')
    if not os.path.exists(jar):
        raise JarNotFound(jar)
    return jar

def main():
    parser = optparse.OptionParser(usage='%prog version')
    parser.add_option('-c','--clean',action='store_true',
                      help='remove existing settings directory')
    parser.add_option('-r','--root',metavar='<PATH>',
                      default=JEDIT_HOME[sys.platform],
                      help='root of all jEdit versions')
    parser.add_option('-d','--downloads',metavar='<PATH>',default='.',
                      help='where to save installer downloads')
    options,args = parser.parse_args()

    if len(args) != 1:
        parser.print_help()
        sys.exit(2)
    version = args[0]

    run(version,options)

if __name__ == '__main__':
    main()

