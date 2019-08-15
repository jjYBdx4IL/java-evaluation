#!/bin/bash

# to run a particular class on a remote machine where you cannot build the entire
# package or do not have the disk space for the maven repository,
# build the package with -Pcopydeps, copy the package to the remote machine,
# and then start this script with the class name as argument, plus additional
# main parameters if needed.

set -Eex
set -o pipefail

exec java -Xmx1g -Dbasedir=$HOME -cp target/java-evaluation-1.0-SNAPSHOT-tests.jar:target/java-evaluation-1.0-SNAPSHOT.jar "$@"
