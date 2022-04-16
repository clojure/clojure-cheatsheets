#! /bin/bash

#clojure -X generator.clojure-metadata-checks/print-results

# Some of the new public Vars that have been added in recent releases
# of Clojure, but do not have :added metadata, are reported in this
# Clojure JIRA: https://clojure.atlassian.net/browse/CLJ-2601

set -x
for ver in 1.6.0 1.7.0 1.8.0 1.9.0 1.10.3 1.11.1
do
    clojure -Sdeps "{:deps {org.clojure/clojure {:mvn/version \"${ver}\"}}}" -M --main generator.clojure-metadata-checks > check-${ver}.txt
done
