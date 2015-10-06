#######################################################################
#
# FormScanner - Free OMR Software
# 
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or 
# (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful, 
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with this program. If not, see http://www.gnu.org/licenses
#
####################################################################### 

#!/bin/sh

# Constants
JAVA_MAJOR=1
JAVA_MINOR=7
APP_JAR="formscanner-main-1.1.1-SNAPSHOT.jar"
APP_NAME="FormScanner"
VM_ARGS=""

# Set the working directory
DIR=$(cd "$(dirname "$0")"; pwd)

# Error message for NO JAVA dialog
ERROR_TITLE="Cannot launch $APP_NAME"
ERROR_MSG="$APP_NAME requires Java version $JAVA_MAJOR.$JAVA_MINOR or later to run."
DOWNLOAD_URL="http://www.oracle.com/technetwork/java/javase/downloads/jre7-downloads-1880261.html"

# Is Java installed?
if type -p java; then
    _java="java"
elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]]; then
    _java="$JAVA_HOME/bin/java"
else
	echo "$ERROR_TITLE"
	echo "$ERROR_MSG"
	exit 1
fi

# Java version check
if [[ "$_java" ]]; then
    version=$("$_java" -version 2>&1 | awk -F '"' '/version/ {print $2}')
    if [[ "$version" < "$JAVA_MAJOR.$JAVA_MINOR" ]]; then
    	echo "$ERROR_TITLE"
    	echo "$ERROR_MSG"
    	exit 1
    fi
fi

# Run the application
exec $_java $VM_ARGS -cp ".;$DIR;$DIR/lib;" -jar "$DIR/lib/$APP_JAR"