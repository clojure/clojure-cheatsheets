#! /bin/bash

#clojure -X generator.clojure-metadata-checks/print-results

set -x
for ver in 1.6.0 1.7.0 1.8.0 1.9.0 1.10.0 1.10.2-rc2
do
    clojure -Sdeps "{:deps {org.clojure/clojure {:mvn/version \"${ver}\"}}}" -M --main generator.clojure-metadata-checks > check-${ver}.txt
done
