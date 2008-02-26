"""
Unit tests for jEdit Launcher library functions.

Running these tests requires `ctypes <http://starship.python.net/crew/theller/ctypes/>`__.

Ollie Rutherfurd <oliver@jedit.org>

$Id$
"""

import ctypes
import os
import shutil
import time
import unittest

MAX_PATH = 255

lib = ctypes.cdll.jeditlib

# XXX test -run=script with relative path
# XXX test -run=script with no script
# XXX test -run=script with abs path

class TestLib(unittest.TestCase):

    lib = lib

    def test_escape_filename1(self):
        i,o = 'test.bsh',ctypes.c_char_p('C:\\AUTOEXEC.BAT')
        n = self.lib.escape_filename(i,o)
        self.assertEqual(n,len(o.value))
        self.assertEqual(i,o.value)

    def test_escape_filename2(self):
        i = r'C:\Documents and Settings\orutherfurd\Desktop\roller\WEB-INF\dbscripts\hsql\createdb.sql'
        o = ctypes.c_char_p('\0' * MAX_PATH * 2)
        n = self.lib.escape_filename(i,o)
        self.assertEqual(n,len(o.value))
        self.assertEqual('\\\\'.join(i.split('\\')),o.value)

        # XXX would be nice if same buffer could be passed in
        #n = self.lib.escape_filename(o,o)
        #self.assertEqual('C:\\\\AUTOEXEC.BAT',o.value)

    def test_get_home1(self):
        sz = mksz(MAX_PATH)
        n = self.lib.get_home(sz,MAX_PATH)
        self.assertEqual(n,0)
        self.assertEqual(sz.value,get_home_path())

    def test_get_home2(self):
        sz = mksz(MAX_PATH)
        n = self.lib.get_home(sz,0)
        self.assertEqual(n,len(get_home_path()))    # should return length of home
        self.assertEqual(len(sz.value),0)   # ensure nothing written to dest buffer

        # retry with buffer same size as home
        n = self.lib.get_home(sz,n)
        self.assertEqual(n,0)
        self.assertEqual(sz.value,get_home_path())

    def test_get_server_file1(self):
        sz = mksz(MAX_PATH)
        n = self.lib.get_server_file(sz,None,None)
        self.assertEqual(n,0)
        self.assertEqual(sz.value,get_server_file())

    def test_get_server_file2(self):
        values = [
            (os.path.join(get_home_path(),'.jedit','server'),
                None,
                None),
            (os.path.join(get_home_path(),'.jedit-cvs','server'),
                os.path.join(get_home_path(),'.jedit-cvs'),
                None),
            (os.path.join(get_home_path(),'.jedit-cvs','server-x'),
                os.path.join(get_home_path(),'.jedit-cvs'),
                'server-x'),
        ]
        for expected,settings,filename in values:
            sz = mksz(MAX_PATH)
            self.lib.get_server_file(sz,settings,filename)
            self.assertEqual(sz.value,expected)

# Running these often as not crashes Python -- I don't know why.
'''
class TestExpandGlobs(unittest.TestCase):

    lib = lib
    tmp_dir = 'tmp-%s' % time.time()
    files = [
        'xxx_test.txt',
        'xxx_test.py',
        'xxx_test',
        'xxx_foo.py',
    ]

    def setUp(self):
        print self.tmp_dir
        if not os.path.exists(self.tmp_dir):
            print 'creating', self.tmp_dir
            os.mkdir(self.tmp_dir)
            time.sleep(1)
            print 'created', self.tmp_dir
        else:
            print self.tmp_dir, 'exists'
        for filename in self.files:
            time.sleep(1)
            path = os.path.join(self.tmp_dir,filename)
            print 'writing', path
            f = open(path,'w')
            f.write('tmp file for testing `expand_globs`')
            f.close()

    def tearDown(self):
        time.sleep(1)
        shutil.rmtree(self.tmp_dir)

    def test_expand_globs1(self):
        # shouldn't do anything -- nothing to search for and nowhere to put it
        n = self.lib.expand_globs(None,0,None,0)
        self.assertEqual(n,0)

    """
    def test_expand_globs1(self):
        return
        nglobs,globs = mkargs('*')
        n = self.lib.expand_globs(globs,nglobs,None,0)
        self.assertEqual(n,len(self.files))
        #dest = mkargs(['\0'*255])
    """
'''

# ----------------------------------------------------------------------------
# utility functions

def get_home_path():
    return filter(None,map(lambda p: os.environ.get(p,None),['USERPROFILE','HOME']))[0]

def get_server_file():
    return os.path.join(get_home_path(),'.jedit','server')

def mkargs(*args):
    """
    (*args) -> (int,[char*,])

    Generates a tuple that can be passed to a c function as an argc, argv pair.
    """
    argv = ctypes.ARRAY(ctypes.c_char_p,len(args))()
    for i,e in enumerate(args):
        argv[i] = e
    return (len(argv),argv)

def mksz(size,char='\0'):
    return ctypes.create_string_buffer(char,size=size)


if __name__ == '__main__':
    unittest.main()

