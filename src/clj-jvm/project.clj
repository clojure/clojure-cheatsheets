(defproject generator "0.1.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/data.json "2.4.0"]
                 [org.clojure/core.async "1.5.648"]
                 [org.clojure/data.priority-map "1.1.0"]
                 [org.clojure/data.avl "0.1.0"]
                 [org.clojure/data.int-map "1.0.0"]
                 [org.clojure/tools.reader "1.3.6"]
                 [org.flatland/ordered "1.15.10"]
                 [org.flatland/useful "0.11.6"]
                 [org.clojure/core.rrb-vector "0.1.2"]
                 [org.clojure/test.check "1.1.0"]]
  :main generator.generator)
