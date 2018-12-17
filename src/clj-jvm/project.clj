(defproject generator "0.1.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 ;;[org.clojure/clojure "1.9.0"]
                 ;;[org.codehaus.jsr166-mirror/jsr166y "1.7.0"]
                 [org.clojure/core.async "0.4.474"]
                 [org.clojure/data.priority-map "0.0.10"]
                 [org.clojure/data.avl "0.0.18"]
                 [org.clojure/data.int-map "0.2.4"]
                 [org.clojure/tools.reader "1.3.0"]
                 [org.flatland/ordered "1.5.6"]
                 [org.flatland/useful "0.11.5"]
                 [org.clojure/core.rrb-vector "0.0.13"]
                 [org.clojure-grimoire/lib-grimoire "0.10.10"]
                 [com.cemerick/url "0.1.1"]
                 [org.clojure/test.check "0.10.0-alpha3"]]
  :main generator.core)
