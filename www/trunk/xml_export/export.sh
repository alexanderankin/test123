#!/bin/bash
# Export jEdit trackers to plain HTML and put it on jedit.org
#
# Copyright © 2014 - Eric Le Lay <kerik-sf@users.sf.net>
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
set -eu
set -x verbose

# ins and outs
mydirabs=`realpath $0`
mydirabs=`dirname $mydirabs`
mydir=`dirname $0`
export_n=export_`date "+%y-%m-%d"`
export_zip="$mydirabs/export/${export_n}.zip"
extract_dir=/tmp/$export_n
res_dir=$mydir/res

# private configuration
source $mydir/project_export.conf

# directory setup
if [ ! -d "$mydir/export" ]; then
  mkdir "$mydir/export"
fi
if [ ! -d "$res_dir" ]; then
  mkdir "$res_dir"
fi

# get jedit-backup-xxx.zip
if [ "${SKIPD:-}" == "" ]; then
 ssh-add $SSH_KEY
 $mydir/project_export.sh -v -c $mydir/project_export.conf -o $export_zip
fi

if [ "${SKIPT:-}" == "" ]; then
 # extract zip
 mkdir -p $extract_dir
 (cd $extract_dir && unzip $export_zip)
 extracted=`ls -d $extract_dir/jedit-backup-*`

 # create html
 $mydir/transform.sh "$extracted" "$res_dir"
fi

if [ "${SKIPUP:-}" == "" ]; then
# tar and upload 
$mydir/rsync.sh "$res_dir"

fi
