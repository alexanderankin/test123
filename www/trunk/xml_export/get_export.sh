#!/bin/bash
# get the XML export from Sourceforge, using wget
#
# Copyright © 2012 - Eric Le Lay <kerik-sf@users.sf.net>
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or any later version.
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
#/

# halt on error + no unitialised variable
set -eu

# usage
usage="
Usage: $0 LOGIN PASSWORD DEST_FILE

    LOGIN:     SourceForge account name (e.g. kerik-sf)

    PASSWORD:  SourceForge password

    DEST_FILE: where to write the XML Export
"

if [ $# != 3 ]; then
        echo "$usage"
        exit -1
fi

login=$1
pw=$2
dest=$3

# use wget -d to get debug output
WGET="wget"

# get the login form by visiting a restricted area
$WGET --cookies=on --keep-session-cookies --save-cookies=cookie.txt  https://sourceforge.net/project/admin/features.php?group_id=588 -O form.html
# grep the form for _visit_cookie which is new each time
secret=`grep _visit_cookie form.html | head -1 | sed 's/.*value="\(.*\)".*/\1/'`

# construct login data and post it
form_data="--post-data=ssl_status=&return_to=%2Fproject%2Fadmin%2Ffeatures.php%3Fgroup_id%3D588&login=Log+in&form_pw=$pw&form_loginname=$login&_visit_cookie=$secret"
$WGET --cookies=on --keep-session-cookies --load-cookies=cookie.txt --save-cookies=cookie.txt --referer=https://sourceforge.net/account/login.php?return_to=%2Fproject%2Fadmin%2Ffeatures.php%3Fgroup_id%3D588 $form_data https://sourceforge.net/account/login.php -O res.html

# get the export.xml
$WGET --progress=dot:mega --referer=https://sourceforge.net/project/admin/features.php?group_id=588 --cookies=on --load-cookies=cookie.txt --keep-session-cookies --save-cookies=cookie.txt https://sourceforge.net/export/xml_export2.php?group_id=588 -O "$dest"

# clean temp files
rm form.html res.html cookie.txt
