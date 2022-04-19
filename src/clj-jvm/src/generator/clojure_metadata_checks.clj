(ns generator.clojure-metadata-checks)

(set! *warn-on-reflection* true)



(def ^:dynamic *auto-flush* true)

;; The list below was created by starting with the list of all
;; namespaces in a Clojure 1.10.0-RC1 run, with no dependencies other
;; than the spec ones that Clojure needs to run, using these commands:

;; $ clj -Sdeps '{:deps {org.clojure/clojure {:mvn/version "1.10.0-RC1"}}}'
;; Clojure 1.10.0-RC1
;; user=> (pprint (->> (all-ns) (map str) sort))

;; That list was then augmented by grep'ing all .clj files in the
;; Clojure 1.10.0-RC2 source code for 'ns' forms.  Not all namespaces
;; included in Clojure are loaded by default using the commands above.

;; See also +common-namespaces-to-remove-from-shown-symbols+

(def all-clojure-built-in-namespaces
  '[clojure.core
    clojure.core.protocols
    clojure.core.reducers
    clojure.core.server
    clojure.core.specs.alpha
    clojure.data
    clojure.datafy
    clojure.edn
    clojure.inspector
    clojure.instant
    clojure.java.browse
    clojure.java.browse-ui
    clojure.java.io
    clojure.java.javadoc
    clojure.java.shell
    clojure.main
    clojure.math
    ;; clojure.parallel - deprecated, so leave out
    clojure.pprint
    clojure.reflect
    clojure.repl
    clojure.set
    clojure.stacktrace
    clojure.string
    clojure.template
    clojure.test
    clojure.test.junit
    clojure.test.tap
    clojure.uuid
    clojure.walk
    clojure.xml
    clojure.zip

    ;; These are not built into Clojure itself, but _are_ depended on
    ;; by Clojure
    clojure.spec.alpha
    clojure.spec.gen.alpha
    ])


(defn printf-to-writer [w fmt-str & args]
  (binding [*out* w]
    (apply clojure.core/printf fmt-str args)
    (when *auto-flush* (flush))))


(defn iprintf [fmt-str-or-writer & args]
  (if (instance? CharSequence fmt-str-or-writer)
    (apply printf-to-writer *out* fmt-str-or-writer args)
    (apply printf-to-writer fmt-str-or-writer args)))


(defn public-vars-in-ns [ns]
  (vals (ns-publics ns)))

(defn all-loaded-public-vars []
  (mapcat public-vars-in-ns (all-ns)))

(defn try-require-nss [nss]
  (loop [nss nss
         ret []]
    (if-let [s (seq nss)]
      (let [err (try
                  (require (first s))
                  nil
                  (catch Exception e
                    e))]
        (recur (rest s) (conj ret {:ns (first s),
                                   :err err})))
      ret)))

(defn print-results [m]
  (let [wrtr *out*]
    (iprintf wrtr "Clojure version: %s\n" (clojure-version))
    (iprintf wrtr "Requiring namespaces ...\n")
    (let [nss-results (try-require-nss all-clojure-built-in-namespaces)
          _ (doseq [{:keys [ns err]} nss-results]
              (iprintf wrtr "    %s ... %s\n"
                       ns (if err "exception" "ok")))
          nss-successfully-loaded (->> nss-results
                                       (filter #(nil? (:err %)))
                                       (map :ns))
          all-ns-names-sorted (->> (all-ns) (map str) sort)
          public-var-no-docstring (remove #(contains? (meta %) :doc)
                                          (all-loaded-public-vars))
          public-builtin-vars-no-added-meta (remove #(contains? (meta %) :added)
                                                    (mapcat public-vars-in-ns nss-successfully-loaded))]
      (iprintf wrtr "\n\nSorted list of %d namespace names currently existing:\n\n"
               (count all-ns-names-sorted))
      (doseq [s all-ns-names-sorted]
        (iprintf wrtr "%s\n" s))
      (iprintf wrtr "\n\nSorted list of %d public Vars in all currently loaded namespaces that have no doc string:\n\n"
               (count public-var-no-docstring))
      (doseq [var (sort-by str public-var-no-docstring)]
        (iprintf wrtr "%s\n" var))
      (iprintf wrtr "\n\nSorted list of %d public Vars in namespaces built into Clojure that has no :added metadata:\n\n"
               (count public-builtin-vars-no-added-meta))
      (doseq [var (sort-by str public-builtin-vars-no-added-meta)]
        (iprintf wrtr "%s\n" var)))))

(defn -main [& args]
  (print-results {}))

(comment

(clojure-version)
(require '[generator.clojure-metadata-checks :as mc])
(use 'clojure.pprint)
(use 'clojure.repl)
(in-ns 'generator.clojure-metadata-checks)
(print-results {})
(pst)

(map class all-clojure-built-in-namespaces)
(nth all-clojure-built-in-namespaces 1)
(pprint (sort-by str (all-ns)))

(def sym1 'clojure.data)
(require sym1)

(doseq [sym '[clojure.core.reducers]]
  (println "    " sym "...")
  (require sym))

)
