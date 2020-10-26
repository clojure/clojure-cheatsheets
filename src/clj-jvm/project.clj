(defproject generator "0.1.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.2-alpha2"]
                 ;;[org.clojure/clojure "1.9.0"]
                 ;;[org.codehaus.jsr166-mirror/jsr166y "1.7.0"]
                 [org.clojure/data.json "1.0.0"]
                 [org.clojure/core.async "1.3.610"]
                 [org.clojure/data.priority-map "1.0.0"]
                 [org.clojure/data.avl "0.1.0"]
                 [org.clojure/data.int-map "1.0.0"]
                 [org.clojure/tools.reader "1.3.3"]
                 [org.flatland/ordered "1.5.9"]
                 [org.flatland/useful "0.11.6"]
                 [org.clojure/core.rrb-vector "0.1.2"]
                 [org.clojure/test.check "1.1.0"]]
  :main generator.core)
