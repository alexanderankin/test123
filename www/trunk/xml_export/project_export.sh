#!/bin/bash
# this is https://forge-allura.apache.org/p/allura/git/ci/master/tree/scripts/project_export
# at commit f6b77b in full below.
# Only modification is s/python/python2/g
#
#       Licensed to the Apache Software Foundation (ASF) under one
#       or more contributor license agreements.  See the NOTICE file
#       distributed with this work for additional information
#       regarding copyright ownership.  The ASF licenses this file
#       to you under the Apache License, Version 2.0 (the
#       "License"); you may not use this file except in compliance
#       with the License.  You may obtain a copy of the License at
#
#         http://www.apache.org/licenses/LICENSE-2.0
#
#       Unless required by applicable law or agreed to in writing,
#       software distributed under the License is distributed on an
#       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
#       KIND, either express or implied.  See the License for the
#       specific language governing permissions and limitations
#       under the License.

ACCESS_TOKEN=''
USER_NAME=''
PROJECT=''
TOOLS=''
HOST='sourceforge.net'
OUTPUT=.
VERBOSE=false

MAX_MINUTES=10
HEADERS=/tmp/project_export_response_headers
BODY=/tmp/project_export_response

function error() {
    echo "$1"
    exit 1
}

while getopts "ht:u:o:H:c:v" opt; do
    case $opt in
        h)
            echo "Usage: $0 [OPTION]... PROJECT TOOL,TOOL,TOOL,..."
            echo "Schedule an export of PROJECT and retrieve the file when ready"
            echo
            echo "  -c CONFIG_FILE    Load config (optionally including PROJECT and TOOLs) from file"
            echo "  -H HOST           Host to use (default sourceforge.net)"
            echo "  -o OUTPUT         Directory and/or filename for downloaded export file"
            echo "  -t ACCESS_TOKEN   Access (bearer) token for the API"
            echo "  -u USER_NAME      Username associated with access token (to download file)"
            echo "  -v                Verbose mode"
            echo "  -h                Show this help message"
            echo
            echo "The TOOLS should be one or more tool mount-point names, separated by commas"
            exit
            ;;
        t)
            C_ACCESS_TOKEN="$OPTARG"
            ;;
        u)
            C_USER_NAME="$OPTARG"
            ;;
        H)
            C_HOST="$OPTARG"
            ;;
        o)
            C_OUTPUT="$OPTARG"
            ;;
        v)
            C_VERBOSE=true
            ;;
        c)
            source "$OPTARG"
            ;;
        *)
            error "Invalid option: -$opt"
    esac
done
# command-line overrides config
[ -n "$C_ACCESS_TOKEN" ] && ACCESS_TOKEN="$C_ACCESS_TOKEN"
[ -n "$C_USER_NAME" ] && USER_NAME="$C_USER_NAME"
[ -n "$C_HOST" ] && HOST="$C_HOST"
[ -n "$C_OUTPUT" ] && OUTPUT="$C_OUTPUT"
[ -n "$C_VERBOSE" ] && VERBOSE="$C_VERBOSE"
shift $(($OPTIND - 1))
if [[ $# -ge 1 ]]; then
    PROJECT="$1"
    shift
fi
[ $# -ge 1 ] && TOOLS="$@"
# fall back to prompt
[ -z "$ACCESS_TOKEN" ] && read -p "Access (bearer) token: " ACCESS_TOKEN
[ -z "$USER_NAME" ] && read -p "Username: " USER_NAME
[ -z "$PROJECT" ] && read -p "Project: " PROJECT
[ -z "$TOOLS" ] && read -p "Tools: " TOOLS

curl_s='-s'
$VERBOSE && curl_s=''

URL="https://$HOST/rest/p/$PROJECT/admin/export"

function json() {
    python2 -c "import sys, json;  print json.load(sys.stdin)['$1']"
}

$VERBOSE && echo "Posting to $URL"
curl $curl_s -D $HEADERS -o $BODY --data "access_token=$ACCESS_TOKEN&tools=$TOOLS" "$URL" || error "Running curl failed"
head -n1 $HEADERS | grep 400 && error "Invalid or missing tool"
head -n1 $HEADERS | grep 503 && error "Export already in progress"
head -n1 $HEADERS | grep -v 200 && error "Error: $(head -n1 $HEADERS)"

filename=`cat $BODY | json filename`

minutes=0
while true; do
    $VERBOSE && echo "Checking $URL_status"
    curl $curl_s -D $HEADERS -o $BODY "${URL}_status?access_token=$ACCESS_TOKEN" || error "Running curl failed"
    head -n1 $HEADERS | grep -v 200 && error "Error: $(head -n1 $HEADERS)"
    status=`cat $BODY | json status`
    $VERBOSE && echo "Status: $status"
    [ "$status" == "ready" ] && break
    sleep 60
    let minutes+=1
    [ "$minutes" -ge "$MAX_MINUTES" ] && error "Timeout waiting for export"
done

CMD="scp $USER_NAME@web.$HOST:/home/project-exports/$PROJECT/$filename $OUTPUT"
$VERBOSE && echo "Running $CMD"
$CMD
