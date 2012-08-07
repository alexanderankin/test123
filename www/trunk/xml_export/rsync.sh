#!/bin/bash
rm res/Export.tgz
tar czf res/Export.tgz --exclude=Export.tgz res
cd res
rsync -avP -e ssh --delete . kerik-sf@web.sourceforge.net:/home/project-web/jedit/htdocs/trackers
