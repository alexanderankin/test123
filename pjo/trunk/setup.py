# distutils script for pjo
# $Id$

from distutils.core import setup
import glob

# XXX include jv.py
# XXX generate scripts
# XXX url doesn't exist
# XXX need a MANIFEST(.in)?

setup(
    name='pjo',
    description='Scripts for packaging jEdit plugins for Plugin Central',
    url='http://www.rutherfurd.net/pjo/',
    download_url='http://www.rutherfurd.net/pjo/',
    version='0.2.1',
    author='Ollie Rutherfurd',
    author_email='oliver@jedit.org',
    license='Python License',
    py_modules=['pjo',
                'pjolib',
                'actions',
                'checks',
    ],
    data_files=[
        ('./doc',glob.glob('doc/*.*')),
    ]
)

