(defproject generator "0.1.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0-RC5"]
                 [org.codehaus.jsr166-mirror/jsr166y "1.7.0"]
                 [org.clojure/data.priority-map "0.0.7"]
                 [org.clojure/data.avl "0.0.12"]
                 [org.clojure/data.int-map "0.2.1"]
                 [org.clojure/tools.reader "0.9.2"]
                 [org.flatland/ordered "1.5.3"]
                 [org.flatland/useful "0.11.3"]
                 [org.clojure/core.rrb-vector "0.0.11"]
                 [org.clojure-grimoire/lib-grimoire "0.9.3"]]
  :main generator.core)
