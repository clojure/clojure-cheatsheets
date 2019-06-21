(ns generator.core
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [clojure.java.javadoc]
            [clojure.java.io :as io]
            [clojure.data.priority-map]
            [clojure.data.avl]
            [clojure.core.async]
            [clojure.core.rrb-vector]
            [clojure.data.int-map]
            [clojure.tools.reader.edn]
            [flatland.ordered.set]
            [flatland.ordered.map]
            [flatland.useful.map]
            [clojure data pprint repl set string xml zip]
            [clojure.spec.alpha]
            [clojure.spec.test.alpha :as stest]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test.check.generators :as tcgen]
            [clojure.core.reducers]
            [cemerick.url :as c.u]
            [grimoire.util :as g.u]

            clojure.core.protocols
            clojure.core.server
            clojure.core.specs.alpha
            clojure.edn
            clojure.inspector
            clojure.instant
            clojure.java.browse-ui
            clojure.main
            clojure.spec.gen.alpha
            clojure.stacktrace
            clojure.template
            clojure.test.junit
            clojure.test.tap
            clojure.test
            clojure.uuid
            ))

;; Andy Fingerhut
;; andy_fingerhut@alum.wustl.edu
;; Feb 21, 2011


;; Documentation describing the structure of the value of
;; cheatsheat-structure:

;; At the top level, the structure must be:
;;
;; [:title "title string"
;;  :page <page-desc>
;;  :page <page-desc>
;;  ... as many pages as you want here ...
;; ]

;; A <page-desc> looks like:
;;
;; [:column <box-desc1> <box-desc2> ... :column <box-desc7> <box-desc8> ...]
;;
;; There must be exactly two :column keywords, one at the beginning,
;; and the rest must be <box-descriptions>.

;; A <box-desc> looks like:
;;
;; [:box "box color"
;;  :section "section name"
;;  :subsection "subsection name"
;;  :table <table-desc>
;;  ... ]
;;
;; It must begin with :box "box color", but after that there can be as
;; many of the following things as you wish.  They can be placed in
;; any order, i.e. there is no requirement that you have a :section
;; before a :subsection, or have either one at all.  The only
;; difference between :section and :subsection is the size of the
;; heading created (e.g. <h2> vs. <h3> in HTML).
;;
;; :section "section name"
;; :subsection "subsection name"
;; :table <table-desc>
;; :cmds-one-line [vector of cmds]

;; A <table-desc> is a vector of <row-desc>s, where each <row-desc>
;; looks like:
;;
;; ["string" :cmds '[<cmd1> <cmd2> <cmd3> ...]]
;;
;; or:
;;
;; ["string" :str <str-spec>]
;;
;; Each <cmd> or <str-spec> (and "string" at the beginning) can be any
;; one of:
;;
;; (a) a symbol, in which case it should have a link created to the
;; documentation URL.
;;
;; (b) a vector of the form [:common-prefix prefix-symbol- suffix1
;; suffix2 ...].  This will be shown in a form that looks similar to
;; "prefix-symbol-{suffix1, suffix2, ...}", where suffix1 will have a
;; link to the documentation for the symbol named
;; prefix-symbol-suffix1 (if there is any), and similarly for the
;; other suffixes.  Note: This cannot be used in the "string" position
;; in the examples above, only in place of one or more of the commands
;; after :cmds, or in place of <str-spec>.  This is useful for
;; economizing on space for names like unchecked-add, unchecked-dec,
;; etc.
;;
;; (c) a vector of the form [:common-suffix -suffix prefix1 prefix2
;; ...].  This is similar to :common-prefix above, except that it will
;; be shown similar to "{prefix1, prefix2, ...}-suffix", and the
;; symbols whose documentation will be linked to will be
;; prefix1-suffix, prefix2-suffix, etc.
;;
;; (d) a vector of the form [:common-prefix-suffix prefix- -suffix
;; middle1 middle2 ...].  Similar to :common-prefix and :common-suffix
;; above, except that it will be shown similar to "prefix-{middle1,
;; middle2, ...}-suffix", and the symbols whose documentation will be
;; linked to will be prefix-middle1-suffix, prefix-middle2-suffix,
;; etc.
;;
;; (e) a string, in which case it should simply be copied to the
;; output as is.
;;
;; (f) a 'conditional string', which is a Clojure map of the form
;; {:html "html-string", :latex "latex-string}.  Only "html-string"
;; will be output if HTML output is being gnerated, and only
;; "latex-string" will be output if LaTeX output is being generated.

;; Note: The last <box-desc> in the last page can optionally be
;; replaced with a <footer-desc>, but only one, and only in that
;; place.  TBD: This has not yet been implemented.


(def cheatsheet-structure
     [:title {:latex "Clojure Cheat Sheet (Clojure 1.7 - 1.10, sheet v49)"
              :html "Clojure Cheat Sheet (Clojure 1.7 - 1.10, sheet v49)"}
      :page [:column
             [:box "green"
              :section "Documentation"
              :table [["clojure.repl/"
                       :cmds '[clojure.repl/doc clojure.repl/find-doc
                               clojure.repl/apropos clojure.repl/dir
                               clojure.repl/source
                               clojure.repl/pst clojure.java.javadoc/javadoc
                               "(foo.bar/ is namespace for later syms)"]]]
              ]
             [:box "blue"
              :section "Primitives"
              :subsection "Numbers"
              :table [["Literals" :cmds '[{:latex "\\href{https://docs.oracle.com/javase/8/docs/api/java/lang/Long.html}{Long}:"
                                           :html "<a href=\"https://docs.oracle.com/javase/8/docs/api/java/lang/Long.html\">Long</a>:"}
                                          "7,"
                                          "hex" "0xff,"
                                          "oct" "017,"
                                          "base 2" "2r1011,"
                                          "base 36" "36rCRAZY"
                                          "BigInt:"
                                          "7N"
                                          "Ratio:"
                                          "-22/7"
                                          {:latex "\\href{https://docs.oracle.com/javase/8/docs/api/java/lang/Double.html}{Double}:"
                                           :html "<a href=\"https://docs.oracle.com/javase/8/docs/api/java/lang/Double.html\">Double</a>:"}
                                          "2.78"
                                          "-1.2e-5"
                                          {:latex "\\href{https://docs.oracle.com/javase/8/docs/api/java/math/BigDecimal.html}{BigDecimal}:"
                                           :html "<a href=\"https://docs.oracle.com/javase/8/docs/api/java/math/BigDecimal.html\">BigDecimal</a>:"}
                                          "4.2M"
                                          ]]
                      ["Arithmetic" :cmds '[+ - * / quot rem mod inc dec
                                            max min +' -' *' inc' dec']]
                      ["Compare" :cmds '[== < > <= >= compare]]
                      ["Bitwise" :cmds '[[:common-prefix bit- and or xor not
                                          flip set shift-right shift-left
                                          and-not clear test]
                                         unsigned-bit-shift-right
                                         "(see"
                                          {:latex "\\href{https://docs.oracle.com/javase/8/docs/api/java/math/BigInteger.html}{BigInteger}"
                                           :html "<a href=\"https://docs.oracle.com/javase/8/docs/api/java/math/BigInteger.html\">BigInteger</a>"}
                                          " for integers larger than Long)"
                                         ]]
                      ["Cast" :cmds '[byte short int long float double
                                      bigdec bigint num rationalize biginteger]]
                      ["Test" :cmds '[zero? pos? neg? even? odd? number?
                                      rational? integer? ratio? decimal?
                                      float?
                                      "(1.9)" double? int? nat-int?
                                      neg-int? pos-int?]]
                      ["Random" :cmds '[rand rand-int]]
                      ["BigDecimal" :cmds '[with-precision]]
                      ;; TBD: Why do these not exist in Clojure?
                      ;; There are -int versions, but not long
                      ;; versions.  unchecked-divide
                      ;; unchecked-remainder.  Filed ticket CLJ-1545
                      ;; to add them.
                      ["Unchecked" :cmds '[*unchecked-math*
                                           [:common-prefix
                                            unchecked-
                                            add dec inc multiply negate
                                            subtract]]]]
              :subsection "Strings"
              :table [["Create" :cmds '[str format
                                        {:latex "\\href{https://clojure.org/reference/reader\\#\\_literals}{\"a string\"} \\",
                                         :html "<a href=\"https://clojure.org/reference/reader#_literals\">\"a string\"</a>"}
                                        {:latex "\"escapes \\textbackslash b\\textbackslash f\\textbackslash n\\textbackslash t\\textbackslash r\\textbackslash \" octal \\textbackslash 377 hex \\textbackslash ucafe\"",
                                         :html "\"escapes \\b\\f\\n\\t\\r\\\" octal \\377 hex \\ucafe\"",}
                                        {:latex "\\textmd{\\textsf{See also section IO/to string}}",
                                         :html "See also section IO/to string"}]]
                      ["Use" :cmds '[count get subs compare
                                     {:latex "\\textmd{\\textsf{(clojure.string/)}}",
                                      :html "(clojure.string/)"}
                                     clojure.string/join clojure.string/escape
                                     clojure.string/split clojure.string/split-lines
                                     clojure.string/replace
                                     clojure.string/replace-first
                                     clojure.string/reverse
                                     "(1.8)" 
                                     clojure.string/index-of
                                     clojure.string/last-index-of
                                     ]]
                      [
;;                       "Regex"
                       {:latex "\\href{http://www.regular-expressions.info}{Regex}"
                        :html "<a href=\"http://www.regular-expressions.info\">Regex</a>"}
                               :cmds '[
;;                                       {:latex "\\#\"pattern\"",
;;                                        :html "#\"<var>pattern</var>\""}
                                       {:latex "\\href{https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html}{\\#\"pattern\"}",
                                        :html "<a href=\"https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html\">#\"<var>pattern</var>\"</a>"}
                                       re-find re-seq re-matches
                                       re-pattern re-matcher re-groups
                                       {:latex "\\textmd{\\textsf{(clojure.string/)}}",
                                        :html "(clojure.string/)"}
                                       clojure.string/replace
                                       clojure.string/replace-first
                                       clojure.string/re-quote-replacement
                                       {:latex "Note: \\textbackslash{} in \\cmd{\\#\"\"} is not escape char. \\cmd{(re-pattern \"{\\textbackslash}{\\textbackslash}s*{\\textbackslash}{\\textbackslash}d+\")} can be written \\cmd{\\#\"{\\textbackslash}s*{\\textbackslash}d+\"}",
                                        :html "Note: \\ in <code>#\"\"</code> is not escape char. <code>(re-pattern \"\\\\s*\\\\d+\")</code> can be written <code>#\"\\s*\\d+\"</code>"}]]
                      ["Letters" :cmds '[{:latex "\\textmd{\\textsf{(clojure.string/)}}",
                                          :html "(clojure.string/)"}
                                         clojure.string/capitalize
                                         clojure.string/lower-case
                                         clojure.string/upper-case]]
                      ["Trim" :cmds '[{:latex "\\textmd{\\textsf{(clojure.string/)}}",
                                       :html "(clojure.string/)"}
                                      clojure.string/trim clojure.string/trim-newline
                                      clojure.string/triml clojure.string/trimr]]
                      ["Test" :cmds '[string?
                                      {:latex "\\textmd{\\textsf{(clojure.string/)}}",
                                       :html "(clojure.string/)"}
                                      clojure.string/blank?
                                      "(1.8)"
                                      clojure.string/starts-with?
                                      clojure.string/ends-with?
                                      clojure.string/includes?
                                      ]]
                      ]
              :subsection "Other"
              :table [["Characters" :cmds '[char char? char-name-string
                                            char-escape-string
                                            {:latex "\\href{https://clojure.org/reference/reader\\#\\_literals}{literals}:",
                                             :html "<a href=\"https://clojure.org/reference/reader#_literals\">literals</a>:"}
                                            {:latex "\\textbackslash a",
                                             :html "\\a",}
                                            {:latex "\\textbackslash newline",
                                             :html "\\newline",}
                                            "(more at link)"]]
                      ["Keywords" :cmds '[keyword keyword? find-keyword
                                          {:latex "\\href{https://clojure.org/reference/reader\\#\\_literals}{literals}:",
                                           :html "<a href=\"https://clojure.org/reference/reader#_literals\">literals</a>:"}
                                          ":kw" ":my.name.space/kw" "::in-cur-namespace" "::namespace-alias/kw"]]
                      ["Symbols" :cmds '[symbol symbol? gensym
                                         {:latex "\\href{https://clojure.org/reference/reader\\#\\_literals}{literals}:",
                                          :html "<a href=\"https://clojure.org/reference/reader#_literals\">literals</a>:"}
                                         "my-sym" "my.ns/foo"]]
                      ["Misc" :cmds '[{:latex "\\href{https://clojure.org/reference/reader\\#\\_literals}{literals}:",
                                       :html "<a href=\"https://clojure.org/reference/reader#_literals\">literals</a>:"}
                                      "true" "false" "nil"]]
                      ]
              ]
             [:box "yellow"
              :section "Collections"
              :subsection "Collections"
              :table [["Generic ops" :cmds '[count empty not-empty into conj
                                             {:latex "\\textmd{\\textsf{(clojure.walk/)}}",
                                              :html "(clojure.walk/)"}
                                             clojure.walk/walk
                                             clojure.walk/prewalk
                                             clojure.walk/prewalk-demo
                                             clojure.walk/prewalk-replace
                                             clojure.walk/postwalk
                                             clojure.walk/postwalk-demo
                                             clojure.walk/postwalk-replace
                                             "(1.9)" bounded-count
                                             ]]
                      ["Content tests" :cmds '[distinct? empty?
                                               every? not-every? some not-any?]]
                      ["Capabilities" :cmds '[sequential? associative? sorted?
                                              counted? reversible?]]
                      ["Type tests" :cmds '[coll? list? vector? set? map?
                                            seq? record?
                                            "(1.8)" map-entry?]]]
              :subsection {:latex "Lists (conj, pop, \\& peek at beginning)"
                           :html "Lists (conj, pop, &amp; peek at beginning)"}
              :table [["Create" :cmds '["()" list list*]]
                      ["Examine" :cmds '[first nth peek
                                         {:latex "\\href{https://docs.oracle.com/javase/8/docs/api/java/util/List.html\\#indexOf-java.lang.Object-}{.indexOf}"
                                          :html "<a href=\"https://docs.oracle.com/javase/8/docs/api/java/util/List.html#indexOf-java.lang.Object-\">.indexOf</a>"}
                                         {:latex "\\href{https://docs.oracle.com/javase/8/docs/api/java/util/List.html\\#lastIndexOf-java.lang.Object-}{.lastIndexOf}"
                                          :html "<a href=\"https://docs.oracle.com/javase/8/docs/api/java/util/List.html#lastIndexOf-java.lang.Object-\">.lastIndexOf</a>"}
                                         ]]
                      [{:html "'Change'", :latex "`Change'"}
                       :cmds '[cons conj rest pop]]
                      ]
              :subsection {:latex "Vectors (conj, pop, \\& peek at end)"
                           :html "Vectors (conj, pop, &amp; peek at end)"}
              :table [["Create" :cmds '["[]" vector vec vector-of
                                        mapv filterv
                                        {:latex "\\textmd{\\textsf{(clojure.core.rrb-vector/)}}",
                                         :html "(clojure.core.rrb-vector/)"}
                                        clojure.core.rrb-vector/vector
                                        clojure.core.rrb-vector/vec
                                        clojure.core.rrb-vector/vector-of]]
                      ["Examine" :cmds '[{:latex "\\cmd{(my-vec idx)} $\\to$ \\cmd{(}",
                                          :html "<code>(my-vec idx)</code> &rarr; <code>("}
                                         nth
                                         {:latex " \\cmd{my-vec idx)}",
                                          :html " my-vec idx)</code>"}
                                         get peek
                                         {:latex "\\href{https://docs.oracle.com/javase/8/docs/api/java/util/List.html\\#indexOf-java.lang.Object-}{.indexOf}"
                                          :html "<a href=\"https://docs.oracle.com/javase/8/docs/api/java/util/List.html#indexOf-java.lang.Object-\">.indexOf</a>"}
                                         {:latex "\\href{https://docs.oracle.com/javase/8/docs/api/java/util/List.html\\#lastIndexOf-java.lang.Object-}{.lastIndexOf}"
                                          :html "<a href=\"https://docs.oracle.com/javase/8/docs/api/java/util/List.html#lastIndexOf-java.lang.Object-\">.lastIndexOf</a>"}
                                         ]]
                      [{:html "'Change'", :latex "`Change'"}
                       :cmds '[assoc assoc-in pop subvec replace conj rseq
                               update update-in]]
                      ["Ops" :cmds '[reduce-kv]]]
              :subsection "Sets"
              :table [["Create unsorted"
                       :cmds '[{:latex "\\#\\{\\}", :html "#{}"}
                               set hash-set]]
                      ["Create sorted"
                       :cmds '[sorted-set sorted-set-by
                               {:latex "\\textmd{\\textsf{(clojure.data.avl/)}}",
                                :html "(clojure.data.avl/)"}
                               clojure.data.avl/sorted-set
                               clojure.data.avl/sorted-set-by
                               {:latex "\\textmd{\\textsf{(flatland.ordered.set/)}}",
                                :html "(flatland.ordered.set/)"}
                               flatland.ordered.set/ordered-set
                               {:latex "\\textmd{\\textsf{(clojure.data.int-map/)}}",
                                :html "(clojure.data.int-map/)"}
                               clojure.data.int-map/int-set
                               clojure.data.int-map/dense-int-set]]
                      ["Examine" :cmds '[{:latex "\\cmd{(my-set item)} $\\to$ \\cmd{(}",
                                          :html "<code>(my-set item)</code> &rarr; <code>("}
                                         get
                                         {:latex " \\cmd{my-set item)}",
                                          :html " my-set item)</code>"}
                                         contains?]]
                      [{:html "'Change'", :latex "`Change'"}
                       :cmds '[conj disj]]
                      ["Set ops"
                       :cmds '[{:latex "\\textmd{\\textsf{(clojure.set/)}}",
                                :html "(clojure.set/)"}
                               clojure.set/union clojure.set/difference
                               clojure.set/intersection clojure.set/select
                               {:latex "\\textmd{\\textsf{See also section Relations}}",
                                :html "See also section Relations"}
                               ]]
                      ["Test"
                       :cmds '[{:latex "\\textmd{\\textsf{(clojure.set/)}}",
                                :html "(clojure.set/)"}
                               clojure.set/subset? clojure.set/superset?]]
                      ["Sorted sets" :cmds '[rseq subseq rsubseq]]
                      ]
              :subsection "Maps"
              :table [["Create unsorted"
                       :cmds '[{:latex "\\{\\}", :html "{}"}
                               hash-map array-map zipmap
                               bean frequencies group-by
                               {:latex "\\textmd{\\textsf{(clojure.set/)}}",
                                :html "(clojure.set/)"}
                               clojure.set/index]]
                      ["Create sorted"
                       :cmds '[sorted-map sorted-map-by
                               {:latex "\\textmd{\\textsf{(clojure.data.avl/)}}",
                                :html "(clojure.data.avl/)"}
                               clojure.data.avl/sorted-map
                               clojure.data.avl/sorted-map-by
                               {:latex "\\textmd{\\textsf{(flatland.ordered.map/)}}",
                                :html "(flatland.ordered.map/)"}
                               flatland.ordered.map/ordered-map
                               {:latex "\\textmd{\\textsf{(clojure.data.priority-map/)}}",
                                :html "(clojure.data.priority-map/)"}
                               clojure.data.priority-map/priority-map
                               {:latex "\\textmd{\\textsf{(flatland.useful.map/)}}",
                                :html "(flatland.useful.map/)"}
                               flatland.useful.map/ordering-map
                               {:latex "\\textmd{\\textsf{(clojure.data.int-map/)}}",
                                :html "(clojure.data.int-map/)"}
                               clojure.data.int-map/int-map
                               ]]
                      ["Examine"
                       :cmds '[
                               {:latex "\\cmd{(my-map k)} $\\to$ \\cmd{(}",
                                :html "<code>(my-map k)</code> &rarr; <code>("}
                               get
                               {:latex " \\cmd{my-map k)}",
                                :html " my-map k)</code>"}
                               "also"
                               {:latex "\\cmd{(:key my-map)} $\\to$ \\cmd{(}",
                                :html "<code>(:key my-map)</code> &rarr; <code>("}
                               get
                               {:latex " \\cmd{my-map :key)}",
                                :html " my-map :key)</code>"}
                               get-in contains? find keys vals
                               ]]
                      [{:html "'Change'", :latex "`Change'"}
                       :cmds '[assoc assoc-in dissoc merge
                               merge-with select-keys update update-in
                               {:latex "\\textmd{\\textsf{(clojure.set/)}}",
                                :html "(clojure.set/)"}
                               clojure.set/rename-keys
                               clojure.set/map-invert
                               {:latex "\\textmd{\\textsf{GitHub:}}",
                                :html "GitHub:"}
                               {:latex "\\href{https://github.com/weavejester/medley}{Medley}"
                                :html "<a href=\"https://github.com/weavejester/medley\">Medley</a>"}
                               ]]
                      ["Ops" :cmds '[reduce-kv]]
                      ["Entry" :cmds '[key val]]
                      ["Sorted maps" :cmds '[rseq subseq rsubseq]]]
              :subsection {:latex "Queues (conj at end, peek \\& pop from beginning)"
                           :html "Queues (conj at end, peek &amp; pop from beginning)"}
              :table [["Create"
                       :cmds '[ "clojure.lang.PersistentQueue/EMPTY"
                                "(no literal syntax or constructor fn)" ]]
                       ["Examine" :cmds '[ peek ]]
                       ["'Change'" :cmds '[ conj pop ]]]
              ]
             :column
             [:box "yellow"
              :subsection "Relations (set of maps, each with same keys, aka rels)"
              :table [["Rel algebra"
                       :cmds '[
                               {:latex "\\textmd{\\textsf{(clojure.set/)}}",
                                :html "(clojure.set/)"}
                               clojure.set/join clojure.set/select
                               clojure.set/project clojure.set/union
                               clojure.set/difference clojure.set/intersection

                               clojure.set/index
                               clojure.set/rename
                               ]]]
              :subsection {:latex "Transients (\\href{https://clojure.org/reference/transients}{clojure.org/reference/transients})"
                           :html "Transients (<a href=\"https://clojure.org/reference/transients\">clojure.org/reference/transients</a>)"}
              :table [["Create" :cmds '[transient persistent!]]
                      ["Change" :cmds '[conj! pop! assoc! dissoc! disj!
                                        {:latex "\\textmd{\\textsf{Note: always use return value for later changes, never original!}}",
                                         :html "Note: always use return value for later changes, never original!"}]]]
              :subsection "Misc"
              :table [["Compare" :cmds '[= identical? not= not compare
                                         clojure.data/diff]]
                      ["Test" :cmds '[true? false? instance? nil?
                                      some?]]]
              ]
             [:box "orange"
              :section "Sequences"
              :subsection "Creating a Lazy Seq"
              :table [["From collection" :cmds '[seq vals keys rseq
                                                 subseq rsubseq sequence]]
                      ["From producer fn" :cmds '[lazy-seq repeatedly iterate]]
                      ["From constant" :cmds '[repeat range]]
                      ["From other" :cmds '[file-seq line-seq resultset-seq
                                            re-seq tree-seq xml-seq
                                            iterator-seq enumeration-seq]]
                      ["From seq" :cmds '[keep keep-indexed]]]
              :subsection "Seq in, Seq out"
              :table [["Get shorter" :cmds '[distinct filter remove
                                             take-nth for
                                             dedupe random-sample]]
                      ["Get longer" :cmds '[cons conj concat lazy-cat mapcat
                                            cycle interleave interpose]]
                      ["Tail-items" :cmds '[rest nthrest next fnext nnext
                                            drop drop-while take-last for]]
                      ["Head-items" :cmds '[take take-while butlast
                                            drop-last for]]
                      [{:html "'Change'", :latex "`Change'"}
                       :cmds '[conj concat distinct flatten group-by
                               partition partition-all partition-by
                               split-at split-with filter remove
                               replace shuffle]]
                      ["Rearrange" :cmds '[reverse sort sort-by compare]]
                      ["Process items" :cmds '[map pmap map-indexed
                                              mapcat for replace seque]]]
              :subsection "Using a Seq"
              :table [["Extract item" :cmds '[first second last rest next
                                              ffirst nfirst fnext
                                              nnext nth nthnext rand-nth
                                              when-first max-key min-key]]
                      ["Construct coll" :cmds '[zipmap into reduce reductions
                                                set vec into-array to-array-2d
                                                mapv filterv]]
                      ["Pass to fn" :cmds '[apply]]
                      ["Search" :cmds '[some filter]]
                      ["Force evaluation" :cmds '[doseq dorun doall run!]]
                      ["Check for forced" :cmds '[realized?]]]
              ]
             [:box "blue"
              :section {:latex "Transducers (\\href{https://clojure.org/reference/transducers}{clojure.org/reference/transducers})"
                        :html "Transducers (<a href=\"https://clojure.org/reference/transducers\">clojure.org/reference/transducers</a>)"}
              :table [["Off the shelf"
                       :cmds '[map mapcat filter remove take
                               take-while take-nth drop drop-while
                               replace partition-by partition-all
                               keep keep-indexed map-indexed distinct
                               interpose cat dedupe random-sample
                               "(1.9)" halt-when]]
                      ["Create your own"
                       :cmds '[completing ensure-reduced unreduced
                               {:latex "\\textmd{\\textsf{See also section Concurrency/Volatiles}}",
                                :html "See also section Concurrency/Volatiles"}]]
                      ["Use" :cmds '[into sequence transduce eduction]]
                      ["Early termination" :cmds '[reduced reduced? deref]]]
              ]
             [:box "green"
              :section {:latex "Spec (\\href{https://clojure.org/about/spec}{rationale}, \\href{https://clojure.org/guides/spec}{guide})"
                        :html "Spec (<a href=\"https://clojure.org/about/spec\">rationale</a>, <a href=\"https://clojure.org/guides/spec\">guide</a>)"}
              :table [
                      ["Operations"
                       :cmds '[clojure.spec.alpha/valid?
                               clojure.spec.alpha/conform
                               clojure.spec.alpha/unform
                               clojure.spec.alpha/explain
                               clojure.spec.alpha/explain-data
                               clojure.spec.alpha/explain-str
                               clojure.spec.alpha/explain-out
                               clojure.spec.alpha/form
                               clojure.spec.alpha/describe
                               clojure.spec.alpha/assert
                               clojure.spec.alpha/check-asserts
                               clojure.spec.alpha/check-asserts?
                               ]]
                      ["Generator ops"
                       :cmds '[
                               clojure.spec.alpha/gen
                               clojure.spec.alpha/exercise
                               clojure.spec.alpha/exercise-fn
                               ]]
                      [{:latex "Defn. \\& registry"
                        :html "Defn. &amp; registry"}
                       :cmds '[
                               clojure.spec.alpha/def
                               clojure.spec.alpha/fdef
                               clojure.spec.alpha/registry
                               clojure.spec.alpha/get-spec
                               clojure.spec.alpha/spec?
                               clojure.spec.alpha/spec
                               clojure.spec.alpha/with-gen
                               ]]
                      ["Logical"
                       :cmds '[
                               clojure.spec.alpha/and
                               clojure.spec.alpha/or
                               ]]
                      ["Collection"
                       :cmds '[
                               clojure.spec.alpha/coll-of
                               clojure.spec.alpha/map-of
                               clojure.spec.alpha/every
                               clojure.spec.alpha/every-kv
                               clojure.spec.alpha/keys
                               clojure.spec.alpha/merge
                               ]]
                      ["Regex"
                       :cmds '[
                               clojure.spec.alpha/cat
                               clojure.spec.alpha/alt
                               clojure.spec.alpha/*
                               clojure.spec.alpha/+
                               clojure.spec.alpha/?
                               clojure.spec.alpha/&
                               clojure.spec.alpha/keys*
                               ]]
                      ["Range"
                       :cmds '[
                               clojure.spec.alpha/int-in
                               clojure.spec.alpha/inst-in
                               clojure.spec.alpha/double-in
                               clojure.spec.alpha/int-in-range?
                               clojure.spec.alpha/inst-in-range?
                               ]]
                      ["Other"
                       :cmds '[
                               clojure.spec.alpha/nilable
                               clojure.spec.alpha/multi-spec
                               clojure.spec.alpha/fspec
                               clojure.spec.alpha/conformer
                               ]]
                      ["Custom explain"
                       :cmds '[
                               clojure.spec.alpha/explain-printer
                               clojure.spec.alpha/*explain-out*
                               ]]
                      ]
              :subsection "Predicates with test.check generators"
              :table [
                      ["Numbers"
                       :cmds '[number? rational? integer? ratio? decimal?
                               float? zero?
                               "(1.9)" double? int? nat-int? neg-int? pos-int?]]
                      [{:latex "\\begin{tabular}[t]{@{}l@{}} Symbols, \\\\ keywords \\end{tabular}"
                        :html "Symbols, keywords"}
                       :cmds '[keyword? symbol? "(1.9)" ident? qualified-ident?
                               qualified-keyword? qualified-symbol?
                               simple-ident? simple-keyword? simple-symbol?]]
                      [{:latex "\\begin{tabular}[t]{@{}l@{}} Other \\\\ scalars \\end{tabular}"
                        :html "Other scalars"}
                       :cmds '[string? true? false? nil? some?
                               "(1.9)" boolean? bytes? inst? uri? uuid?]]
                      ["Collections"
                       :cmds '[list? map? set? vector? associative? coll?
                               sequential? seq? empty?
                               "(1.9)" indexed? seqable?]]
                      ["Other" :cmds '["(1.9)" any?]]]
              ]
             [:box "magenta"
              :section "IO"
              :table [
                      [{:latex "\\begin{tabular}[t]{@{}l@{}} to/from \\\\ ... \\end{tabular}"
                        :html "to/from ..."}
                        :cmds '[spit slurp
                                {:latex "\\textmd{\\textsf{(to writer/from reader, Socket, string with file name, URI, etc.)}}",
                                 :html "(to writer/from reader, Socket, string with file name, URI, etc.)"}
                                ]]
                      ["to *out*" :cmds '[pr prn print printf println
                                          newline
                                           {:latex "\\textmd{\\textsf{(clojure.pprint/)}}",
                                            :html "(clojure.pprint/)"}
                                          clojure.pprint/print-table]]
                      ["to writer" :cmds '[{:latex "\\textmd{\\textsf{(clojure.pprint/)}}",
                                            :html "(clojure.pprint/)"}
                                           clojure.pprint/pprint
                                           clojure.pprint/cl-format
                                           {:latex "\\textmd{\\textsf{also:}}",
                                            :html "also:"}
                                           "(binding [*out* writer] ...)"]]
                      ["to string" :cmds '[format with-out-str pr-str
                                           prn-str print-str println-str]]
                      ["from *in*" :cmds '[read-line
                                           {:latex "\\textmd{\\textsf{(clojure.edn/)}}",
                                            :html "(clojure.edn/)"}
                                           clojure.edn/read
                                           {:latex "\\textmd{\\textsf{(clojure.tools.reader.edn/)}}",
                                            :html "(clojure.tools.reader.edn/)"}
                                           clojure.tools.reader.edn/read
                                           ]]
                      ["from reader" :cmds '[line-seq
                                             {:latex "\\textmd{\\textsf{(clojure.edn/)}}",
                                              :html "(clojure.edn/)"}
                                             clojure.edn/read
                                             {:latex "\\textmd{\\textsf{(clojure.tools.reader.edn/)}}",
                                              :html "(clojure.tools.reader.edn/)"}
                                             clojure.tools.reader.edn/read
                                             {:latex "\\textmd{\\textsf{also:}}",
                                              :html "also:"}
                                             "(binding [*in* reader] ...)"
                                             {:latex "\\href{https://docs.oracle.com/javase/8/docs/api/java/io/Reader.html}{java.io.Reader}"
                                              :html "<a href=\"https://docs.oracle.com/javase/8/docs/api/java/io/Reader.html\">java.io.Reader</a>"}
                                             ]]
                      ["from string" :cmds '[
                                             with-in-str
                                             {:latex "\\textmd{\\textsf{(clojure.edn/)}}",
                                              :html "(clojure.edn/)"}
                                             clojure.edn/read-string
                                             {:latex "\\textmd{\\textsf{(clojure.tools.reader.edn/)}}",
                                              :html "(clojure.tools.reader.edn/)"}
                                             clojure.tools.reader.edn/read-string
                                             ]]
                      ["Open" :cmds '[with-open
;                                      {:latex "\\textmd{\\textsf{string:}}",
;                                       :html "string:"}
;                                      with-out-str with-in-str
                                      {:latex "\\textmd{\\textsf{(clojure.java.io/)}}",
                                       :html "(clojure.java.io/)"}
                                      {:latex "\\textmd{\\textsf{text:}}",
                                       :html "text:"}
                                      clojure.java.io/reader clojure.java.io/writer
                                      {:latex "\\textmd{\\textsf{binary:}}",
                                       :html "binary:"}
                                      clojure.java.io/input-stream clojure.java.io/output-stream
                                      ]]
                      ["Binary" :cmds '["(.write ostream byte-arr)"
                                        "(.read istream byte-arr)"
;                                        "(javadoc java.io.OutputStream)"
                                        {:latex "\\href{https://docs.oracle.com/javase/8/docs/api/java/io/OutputStream.html}{java.io.OutputStream}"
                                         :html "<a href=\"https://docs.oracle.com/javase/8/docs/api/java/io/OutputStream.html\">java.io.OutputStream</a>"}
;                                        "java.io.InputStream"
                                        {:latex "\\href{https://docs.oracle.com/javase/8/docs/api/java/io/InputStream.html}{java.io.InputStream}"
                                         :html "<a href=\"https://docs.oracle.com/javase/8/docs/api/java/io/InputStream.html\">java.io.InputStream</a>"}
                                        {:latex "\\textmd{\\textsf{GitHub:}}",
                                         :html "GitHub:"}
                                        {:latex "\\href{https://github.com/ztellman/gloss}{gloss}"
                                         :html "<a href=\"https://github.com/ztellman/gloss\">gloss</a>"}
                                        {:latex "\\href{https://github.com/rosejn/byte-spec}{byte-spec}"
                                         :html "<a href=\"https://github.com/rosejn/byte-spec\">byte-spec</a>"}
                                        ]]
                      ["Misc" :cmds '[flush "(.close s)" file-seq
                                      *in* *out* *err*
                                      {:latex "\\textmd{\\textsf{(clojure.java.io/)}}",
                                       :html "(clojure.java.io/)"}
                                      clojure.java.io/file
                                      clojure.java.io/copy
                                      clojure.java.io/delete-file
                                      clojure.java.io/resource
                                      clojure.java.io/as-file
                                      clojure.java.io/as-url
                                      clojure.java.io/as-relative-path
                                      {:latex "\\textmd{\\textsf{GitHub:}}",
                                       :html "GitHub:"}
                                      {:latex "\\href{https://github.com/funcool/fs}{fs}"
                                       :html "<a href=\"https://github.com/funcool/fs\">fs</a>"}
                                      ]]
                      ["Data readers" :cmds '[*data-readers*
                                              default-data-readers
                                              *default-data-reader-fn*]]
                      ["tap" :cmds '["(1.10)" tap> add-tap remove-tap]]
                      ]
              ]
             ]
      :page [:column
             [:box "blue"
              :section "Functions"
              :table [["Create" :cmds '[fn defn defn- definline identity
                                        constantly memfn comp complement
                                        partial juxt memoize fnil every-pred
                                        some-fn]]
                      ["Call" :cmds '[apply -> ->> trampoline
                                      as-> cond-> cond->> some-> some->>]]
                      ["Test" :cmds '[fn? ifn?]]]
              ]
             [:box "orange"
              :section {:latex "Abstractions (\\href{https://github.com/cemerick/clojure-type-selection-flowchart}{Clojure type selection flowchart})"
                        :html "Abstractions (<a href=\"https://github.com/cemerick/clojure-type-selection-flowchart\">Clojure type selection flowchart</a>)"}
              :subsection {:latex "Protocols (\\href{https://clojure.org/reference/protocols}{clojure.org/reference/protocols})"
                           :html "Protocols (<a href=\"https://clojure.org/reference/protocols\">clojure.org/reference/protocols</a>)"}
              :table [
                      ["Define" :cmds '[
                                        {:latex "\\cmd{(}"
                                         :html "<code>(</code>"}
                                        defprotocol
                                        {:latex "\\cmd{Slicey (slice [at]))}"
                                         :html "<code>Slicey (slice [at]))</code>"}
                                        ]]
                      ["Extend" :cmds '[
                                        {:latex "\\cmd{(}"
                                         :html "<code>(</code>"}
                                        extend-type
                                        {:latex "\\cmd{String Slicey (slice [at] ...))}"
                                         :html "<code>String Slicey (slice [at] ...))</code>"}
                                        ]]
                      ["Extend null" :cmds '[
                                             {:latex "\\cmd{(}"
                                              :html "<code>(</code>"}
                                             extend-type
                                             {:latex "\\cmd{nil Slicey (slice [\\_] nil))}"
                                              :html "<code>nil Slicey (slice [_] nil))</code>"}
                                             ]]
                      ["Reify" :cmds '[
                                       {:latex "\\cmd{(}"
                                        :html "<code>(</code>"}
                                       reify
                                       {:latex "\\cmd{Slicey (slice [at] ...))}"
                                        :html "<code>Slicey (slice [at] ...))</code>"}
                                       ]]
                      ["Test" :cmds '[satisfies? extends?]]
                      ["Other" :cmds '[extend extend-protocol extenders]]
                      ]
              :subsection {:latex "Records (\\href{https://clojure.org/reference/datatypes}{clojure.org/reference/datatypes})"
                           :html "Records (<a href=\"https://clojure.org/reference/datatypes\">clojure.org/reference/datatypes</a>)"}
              :table [
                      ["Define" :cmds '[
                                        {:latex "\\cmd{(}"
                                         :html "<code>(</code>"}
                                        defrecord
                                        {:latex "\\cmd{Pair [h t])}"
                                         :html "<code>Pair [h t])</code>"}
                                        ]]
                      ["Access" :cmds '[
                                        {:latex "\\cmd{(:h (Pair. 1 2))} $\\to$ \\cmd{1}"
                                         :html "<code>(:h (Pair. 1 2))</code> &rarr; <code>1</code>"}
                                        ]]
                      ["Create" :cmds '[
                                        Pair. ->Pair map->Pair
                                        ]]
                      ["Test" :cmds '[record?]]
                      ]
              :subsection {:latex "Types (\\href{https://clojure.org/reference/datatypes}{clojure.org/reference/datatypes})"
                           :html "Types (<a href=\"https://clojure.org/reference/datatypes\">clojure.org/reference/datatypes</a>)"}
              :table [
                      ["Define" :cmds '[
                                        {:latex "\\cmd{(}"
                                         :html "<code>(</code>"}
                                        deftype
                                        {:latex "\\cmd{Pair [h t])}"
                                         :html "<code>Pair [h t])</code>"}
                                        ]]
                      ["Access" :cmds '[
                                        {:latex "\\cmd{(.h (Pair. 1 2))} $\\to$ \\cmd{1}"
                                         :html "<code>(.h (Pair. 1 2))</code> &rarr; <code>1</code>"}
                                        ]]
                      ["Create" :cmds '[Pair. ->Pair]]
                      ["With methods" :cmds '[
                                              {:latex "\\begin{tabular}[c]{@{}l@{}} \\cmd{(}"
                                               :html "<code>(</code>"}
                                              deftype
                                              {:latex "\\cmd{Pair [h t]} \\\\ \\ \\ \\cmd{Object} \\\\ \\ \\ \\cmd{(toString [this] (str \"<\" h \",\" t \">\")))}\\end{tabular}"
                                               :html "<code>Pair [h t]<br>&nbsp;&nbsp;Object<br>&nbsp;&nbsp;(toString [this] (str \"&lt;\" h \",\" t \"&gt;\")))</code>"}
                                              ]]
                      ]
              :subsection {:latex "Multimethods (\\href{https://clojure.org/reference/multimethods}{clojure.org/reference/multimethods})"
                           :html "Multimethods (<a href=\"https://clojure.org/reference/multimethods\">clojure.org/reference/multimethods</a>)"}
              :table [
                      ["Define" :cmds '[
                                        {:latex "\\cmd{(}"
                                         :html "<code>(</code>"}
                                        defmulti
                                        {:latex "\\cmd{my-mm dispatch-fn)}"
                                         :html "<code>my-mm dispatch-fn)</code>"}
                                        ]]
                      ["Method define" :cmds '[
                                               {:latex "\\cmd{(}"
                                                :html "<code>(</code>"}
                                               defmethod
                                               {:latex "\\cmd{my-mm :dispatch-value [args] ...)}"
                                                :html "<code>my-mm :dispatch-value [args] ...)</code>"}
                                               ]]
;                      ["Create" :cmds '[defmulti defmethod]]
                      ["Dispatch" :cmds '[get-method methods]]
                      ["Remove" :cmds '[remove-method remove-all-methods]]
                      ["Prefer" :cmds '[prefer-method prefers]]
                      ["Relation" :cmds '[derive underive isa? parents ancestors
                                          descendants make-hierarchy]]]
              ]
             [:box "magenta"
              :section {:latex "Datafy (\\href{https://corfield.org/blog/2018/12/03/datafy-nav}{article})"
                        :html "Datafy (<a href=\"https://corfield.org/blog/2018/12/03/datafy-nav\">article</a>)"}
              :table [["Datafy" :cmds '[{:latex "\\textmd{\\textsf{(clojure.datafy/)}}",
                                         :html "(clojure.datafy/)"}
                                        clojure.datafy/datafy
                                        clojure.datafy/nav]]]
              ]
             [:box "green"
              :section "Macros"
              :table [["Create" :cmds '[defmacro definline]]
                      ["Debug" :cmds '[macroexpand-1 macroexpand
                                       {:latex "\\textmd{\\textsf{(clojure.walk/)}}",
                                        :html "(clojure.walk/)"}
                                       clojure.walk/macroexpand-all]]
                      ["Branch" :cmds '[and or when when-not when-let
                                        when-first if-not if-let cond condp
                                        case when-some if-some]]
                      ["Loop" :cmds '[for doseq dotimes while]]
                      ["Arrange" :cmds '[.. doto -> ->>
                                         as-> cond-> cond->> some-> some->>]]
                      ["Scope" :cmds '[binding locking time
                                       [:common-prefix with-
                                       in-str local-vars open out-str
                                       precision redefs redefs-fn]]]
                      ["Lazy" :cmds '[lazy-cat lazy-seq delay]]
                      ["Doc." :cmds '[assert comment clojure.repl/doc]]]
              ]
             [:box "yellow"
              :section {:latex "Special Characters (\\href{https://clojure.org/reference/reader\\#macrochars}{clojure.org/reference/reader}, \\href{https://clojure.org/guides/weird_characters}{guide})"
                        :html "Special Characters (<a href=\"https://clojure.org/reference/reader#macrochars\">clojure.org/reference/reader</a>, <a href=\"https://clojure.org/guides/weird_characters\">guide</a>)"}
              :table [
                      [{:latex "\\cmd{,}",
                        :html "<code>,</code>"}
                       :str "Comma reads as white space.  Often used between map key/value pairs for readability."]
                      [{:latex "\\cmd{'}",
                        :html "<code>'</code>"}
                       :cmds '[{:latex "\\href{https://clojure.org/reference/reader\\#\\_quote}{quote}: 'form $\\to$ (",
                                :html "<a href=\"https://clojure.org/reference/reader#_quote\">quote</a>: <code>'<var>form</var></code> &rarr; <code>(</code>"}
                               quote
                               {:latex "form)",
                                :html "<code><var>form</var>)</code>"}]]
                      [{:latex "\\cmd{/}",
                        :html "<code>/</code>"}
                       :str "Namespace separator (see Primitives/Other section)"]
                      [{:latex "\\cmd{\\textbackslash}",
                        :html "<code>\\</code>"}
                       :str "Character literal (see Primitives/Other section)"]
                      [{:latex "\\cmd{:}",
                        :html "<code>:</code>"}
                       :str "Keyword (see Primitives/Other section)"]
                      [{:latex "\\cmd{;}",
                        :html "<code>;</code>"}
                       :str "Single line comment"]
                      [{:latex "\\cmd{\\^{}}",
                        :html "<code>^</code>"}
                       :str "Metadata (see Metadata section)"]
                      [{:latex "\\cmd{*foo*}",
                        :html "<code>*foo*</code>"}
                       :cmds '[{:latex "'earmuffs' - convention to indicate \\href{https://clojure.org/reference/vars}{dynamic vars}, compiler warns if not dynamic",
                                :html "'earmuffs' - convention to indicate <a href=\"https://clojure.org/reference/vars\">dynamic vars</a>, compiler warns if not dynamic"}]]
                      [{:latex "\\cmd{@}",
                        :html "<code>@</code>"}
                       :cmds '[{:latex "Deref: @form $\\to$ (",
                                :html "Deref: <code>@<var>form</var></code> &rarr; <code>(</code>"}
                               deref
                               {:latex "form)",
                                :html "<code><var>form</var>)</code>"}]]
                      [{:latex "\\cmd{`}",
                        :html "<code>`</code>"}
                       :cmds '[{:latex "\\href{https://clojure.org/reference/reader\\#syntax-quote}{Syntax-quote}"
                                :html "<a href=\"https://clojure.org/reference/reader#syntax-quote\">Syntax-quote</a>"}]]
                      [{:latex "\\cmd{foo\\#}"
                        :html "<code>foo#</code>"}
                       :cmds '[{:latex "\\href{https://clojure.org/reference/reader\\#syntax-quote}{'auto-gensym'}, consistently replaced with same auto-generated symbol everywhere inside same \\cmd{`( ... )}"
                                :html "<a href=\"https://clojure.org/reference/reader#syntax-quote\">'auto-gensym'</a>, consistently replaced with same auto-generated symbol everywhere inside same <code>`( ... )</code>"}]]
                      [{:latex "\\cmd{\\textasciitilde}",
                        :html "<code>~</code>"}
                       :cmds '[{:latex "\\href{https://clojure.org/reference/reader\\#syntax-quote}{Unquote}"
                                :html "<a href=\"https://clojure.org/reference/reader#syntax-quote\">Unquote</a>"}]]
                      [{:latex "\\cmd{\\textasciitilde@}",
                        :html "<code>~@</code>"}
                       :cmds '[{:latex "\\href{https://clojure.org/reference/reader\\#syntax-quote}{Unquote-splicing}"
                                :html "<a href=\"https://clojure.org/reference/reader#syntax-quote\">Unquote-splicing</a>"}]]
                      [{:latex "\\cmd{->}",
                        :html "<code>-></code>"}
                       :cmds '[ "'thread first' macro" -> ]]
                      [{:latex "\\cmd{->{>}}",
                        :html "<code>->></code>"}
                       :cmds '[ "'thread last' macro" ->> ]]
                      [{:latex "\\cmd{>!! <!! >! <!}",
                        :html "<code>&gt;!! &lt;!! &gt;! &lt;!</code>"}
                       :cmds '[{:latex "\\href{https://clojure.org/guides/weird\\_characters\\#\\_\\_code\\_code\\_code\\_code\\_code\\_code\\_and\\_code\\_code\\_core\\_async\\_channel\\_macros}{core.async channel macros}"
                                :html "<a href=\"https://clojure.org/guides/weird_characters#__code_code_code_code_code_code_and_code_code_core_async_channel_macros\">core.async channel macros</a>"}
                               clojure.core.async/>!! clojure.core.async/<!!
                               clojure.core.async/>!  clojure.core.async/<! ]]
                      [{:latex "\\cmd{(}",
                        :html "<code>(</code>"}
                       :str "List literal (see Collections/Lists section)"]
                      [{:latex "\\cmd{[}",
                        :html "<code>[</code>"}
                       :str "Vector literal (see Collections/Vectors section)"]
                      [{:latex "\\cmd{\\{}",
                        :html "<code>{</code>"}
                       :str "Map literal (see Collections/Maps section)"]
                      [{:latex "\\cmd{\\#'}",
                        :html "<code>#'</code>"}
                       :cmds '[{:latex "Var-quote \\#'x $\\to$ (",
                                :html "Var-quote: <code>#'<var>x</var></code> &rarr; <code>(</code>"}
                               var
                               {:latex "x)",
                                :html "<code><var>x</var>)</code>"}]]
                      [{:latex "\\cmd{\\#\"}",
                        :html "<code>#\"</code>"}
                       :str {:latex "\\cmd{\\#\"}\\textit{p}\\cmd{\"} reads as regex pattern \\textit{p} (see Strings/Regex section)",
                             :html "<code>#\"<var>p</var>\"</code> reads as regex pattern <var>p</var> (see Strings/Regex section)"}]
                      [{:latex "\\cmd{\\#\\{}",
                        :html "<code>#{</code>"}
                       :str "Set literal (see Collections/Sets section)"]
                      [{:latex "\\cmd{\\#(}",
                        :html "<code>#(</code>"}
                       :cmds [
                              {:latex "\\href{https://clojure.org/reference/reader\\#\\_dispatch}{Anonymous function literal}:"
                               :html "<a href=\"https://clojure.org/reference/reader#_dispatch\">Anonymous function literal</a>:"}
                              {:latex "\\#(...) $\\to$ (fn [args] (...))",
                               :html "<code>#(...)</code> &rarr; <code>(fn [args] (...))</code>"}]]
                      [{:latex "\\cmd{\\%}",
                        :html "<code>%</code>"}
                       :cmds '[{:latex "\\href{https://clojure.org/reference/reader\\#\\_dispatch}{Anonymous function argument}: \\cmd{\\%N} is value of anonymous function arg \\cmd{N}.  \\cmd{\\%} short for \\cmd{\\%{1}}.  \\cmd{\\%\\&} for rest args."
                                :html "<a href=\"https://clojure.org/reference/reader#_dispatch\">Anonymous function argument</a>: <code>%N</code> is value of anonymous function arg <code>N</code>.  <code>%</code> short for <code>%1</code>.  <code>%&</code> for rest args."}
                               ]]
                      [{:latex "\\cmd{\\#?}",
                        :html "<code>#?</code>"}
                       :cmds [{:latex "\\href{https://clojure.org/reference/reader\\#\\_reader\\_conditionals}{Reader conditional}:"
                               :html "<a href=\"https://clojure.org/reference/reader#_reader_conditionals\">Reader conditional</a>:"}
                              {:latex "\\#?(:clj x :cljs y) reads as x on JVM, y in ClojureScript, nothing elsewhere.  Other keys: :cljr :default",
                               :html "<code>#?(:clj x :cljs y)</code> reads as <code>x</code> on JVM, <code>y</code> in ClojureScript, nothing elsewhere.  Other keys: <code>:cljr :default</code>"}
                              ]]
                      [{:latex "\\cmd{\\#?@}",
                        :html "<code>#?@</code>"}
                       :cmds [{:latex "\\href{https://clojure.org/reference/reader\\#\\_reader\\_conditionals}{Splicing reader conditional}:"
                               :html "<a href=\"https://clojure.org/reference/reader#_reader_conditionals\">Splicing reader conditional</a>:"}
                              {:latex "[1 \\#?@(:clj [x y] :cljs [w z]) 3] reads as [1 x y 3] on JVM, [1 w z 3] in ClojureScript, [1 3] elsewhere.",
                               :html "<code>[1 #?@(:clj [x y] :cljs [w z]) 3]</code> reads as <code>[1 x y 3]</code> on JVM, <code>[1 w z 3]</code> in ClojureScript, <code>[1 3]</code> elsewhere."}
                              ]]
                      [{:latex "\\cmd{\\#foo}",
                        :html "<code>#foo</code>"}
                       :cmds [{:latex "\\href{https://clojure.org/reference/reader\\#tagged\\_literals}{tagged literal} e.g. \\cmd{\\#inst} \\cmd{\\#uuid}"
                               :html "<a href=\"https://clojure.org/reference/reader#tagged_literals\">tagged literal</a> e.g. <code>#inst</code> <code>#uuid</code>"}]]
                      [{:latex "\\cmd{\\#:}",
                        :html "<code>#:</code>"}
                       :cmds [{:latex "\\href{https://clojure.org/reference/reader\\#map\\_namespace\\_syntax}{map namespace syntax} e.g. \\cmd{\\#:foo\\{:a 1 :b 2\\}} is equal to \\{:foo/a 1 :foo/b 2\\}"
                               :html "<a href=\"https://clojure.org/reference/reader#map_namespace_syntax\">map namespace syntax</a> e.g. <code>#:foo{:a 1}</code> is equal to <code>{:foo/a 1}</code>"}]]
                      [{:latex "\\cmd{\\#\\#}",
                        :html "<code>##</code>"}
                       :cmds '["(1.9) symbolic values:"
                               {:latex "\\cmd{\\#\\#Inf \\#\\#-Inf \\#\\#NaN}",
                                :html "<code>##Inf ##-Inf ##NaN<code>"}]]
                      [{:latex "\\cmd{\\$}",
                        :html "<code>$</code>"}
                       :cmds '[{:latex "\\cmd{JavaContainerClass\\$InnerClass}",
                                :html "<code>JavaContainerClass$InnerClass</code>"} ]]
                      [{:latex "\\cmd{foo?}",
                        :html "<code>foo?</code>"}
                       :cmds '["conventional ending for a predicate, e.g.:"
                               zero? vector? instance? "(unenforced)"]]
                      [{:latex "\\cmd{foo!}",
                        :html "<code>foo!</code>"}
                       :cmds '["conventional ending for an unsafe operation, e.g.:"
                               set! swap! alter-meta! "(unenforced)"]]
                      [{:latex "\\cmd{\\_}",
                        :html "<code>_</code>"}
                       :cmds '["conventional name for an unused value (unenforced)"]]
                      [{:latex "\\cmd{\\#\\_}",
                        :html "<code>#_</code>"}
                       :str "Ignore next form"]
                      ]
              ]
             [:box "red"
              :section {:latex "Metadata (\\href{https://clojure.org/reference/reader\\#\\_metadata}{clojure.org/reference/reader}, \\href{https://clojure.org/reference/special\\_forms}{special\\_forms})"
                        :html "Metadata (<a href=\"https://clojure.org/reference/reader#_metadata\">clojure.org/reference/reader</a>, <a href=\"https://clojure.org/reference/special_forms\">special_forms</a>)"}
              :table [
                      ["General" :cmds [{:latex "\\cmd{\\^{}\\{:key1 val1 :key2 val2 ...\\}}"
                                         :html "<code>^{:key1 val1 :key2 val2 ...}</code>"}
                                        ]]
                      ["Abbrevs" :cmds [{:latex "\\cmd{\\^{}Type} $\\to$ \\cmd{\\^{}\\{:tag Type\\}},
\\cmd{\\^{}:key} $\\to$ \\cmd{\\^{}\\{:key true\\}}"
                                         :html
                                         "<code>^Type</code> &rarr; <code>^{:tag Type}</code><br>
<code>^:key</code> &rarr; <code>^{:key true}</code>"}
                                        ]]
                      ["Common" :cmds [{:latex (str
                                                "\\cmd{\\^{}:dynamic} "
                                                "\\cmd{\\^{}:private} "
                                                "\\cmd{\\^{}:doc} "
                                                "\\cmd{\\^{}:const}"
                                                )
                                        :html (str
                                               "<code>"
                                               "^:dynamic "
                                               "^:private "
                                               "^:doc "
                                               "^:const"
                                               "</code>")}
                                        ]]
                      ["Examples" :cmds '[
                                         {:latex "\\cmd{(defn \\^{}:private \\^{}String my-fn ...)}"
                                          :html "<code>(defn ^:private ^String my-fn ...)</code>"}
                                         {:latex " \\ \\ \\ " ; fragile hack to get 2nd example to start on next line
                                          :html " <br>"}
                                         {:latex "\\cmd{(def \\^{}:dynamic *dyn-var* val)}"
                                          :html "<code>(def ^:dynamic *dyn-var* val)</code>"}
                                         ]]
;;                      ["Others" :cmds [
;;                                       {:latex (str
;;                                                "\\cmd{:added}"
;;                                                " \\cmd{:author}"
;;                                                " \\cmd{:doc} "
;;                                                " \\cmd{:arglists} "
;;                                                " \\cmd{:inline}"
;;                                                " \\cmd{:inline-arities}"
;;                                                " \\cmd{:macro}"
;; ;                                                " (examples in Clojure source)"
;; ;                                                " (see Clojure source for examples.  Can use arbitrary keys for your own purposes.)"
;;                                                )
;;                                        :html (str
;;                                               "<code>"
;;                                               ":added"
;;                                               " :author"
;;                                               " :arglists "
;;                                               " :doc "
;;                                               " :inline"
;;                                               " :inline-arities"
;;                                               " :macro"
;;                                               "</code>"
;; ;                                               " (examples in Clojure source)"
;; ;                                               " (see Clojure source for examples.  Can use arbitrary keys for your own purposes.)"
;;                                               )}
;;                                       ]]
                      ["On Vars" :cmds '[meta with-meta vary-meta
                                         alter-meta! reset-meta!
                                         clojure.repl/doc
                                         clojure.repl/find-doc test]]
                      ]
              ]
             :column
             [:box "red"
              :section {:latex "Special Forms (\\href{https://clojure.org/reference/special\\_forms}{clojure.org/reference/special\\_forms})"
                        :html "Special Forms (<a href=\"https://clojure.org/reference/special_forms\">clojure.org/reference/special_forms</a>)"}
              :cmds-one-line '[def if do let letfn quote var fn loop
                               recur set! throw try monitor-enter monitor-exit]
              :table [[{:latex "\\begin{tabular}[t]{@{}l@{}} Binding Forms / \\\\ Destructuring \\end{tabular}"
                        :html "Binding Forms / Destructuring"}
                       :cmds '[
                               {:latex "(\\href{https://clojure.org/reference/special\\_forms\\#binding-forms}{examples})"
                                :html "(<a href=\"https://clojure.org/reference/special_forms#binding-forms\">examples</a>)"}
                               let fn defn defmacro
                               loop for doseq if-let when-let
                               if-some when-some]]
                      ]
              ]
             [:box "blue2"
              :section {:latex "Vars and global environment (\\href{https://clojure.org/reference/vars}{clojure.org/reference/vars})"
                        :html "Vars and global environment (<a href=\"https://clojure.org/reference/vars\">clojure.org/reference/vars</a>)"}
              :table [["Def variants" :cmds '[def defn defn- definline defmacro
                                              defmethod defmulti defonce
                                              defrecord]]
                      ["Interned vars" :cmds '[declare intern binding
                                               find-var var]]
                      ["Var objects" :cmds '[with-local-vars var-get var-set
                                             alter-var-root var? bound?
                                             thread-bound?]]
                      ["Var validators" :cmds '[set-validator! get-validator]]
                      ;; Now covered in Metadata section
;;                      ["Var metadata" :cmds '[meta clojure.repl/doc
;;                                              clojure.repl/find-doc test]]
                      ]
              ]
             [:box "yellow"
              :section "Namespace"
              :table [["Current" :cmds '[*ns*]]
                      ["Create/Switch" :cmds '[{:latex "(\\href{https://blog.8thlight.com/colin-jones/2010/12/05/clojure-libs-and-namespaces-require-use-import-and-ns.html}{tutorial})"
                                                :html "(<a href=\"https://blog.8thlight.com/colin-jones/2010/12/05/clojure-libs-and-namespaces-require-use-import-and-ns.html\">tutorial</a>)"}
                                               ns in-ns create-ns]]
                      ["Add" :cmds '[alias def import intern refer]]
                      ["Find" :cmds '[all-ns find-ns]]
;;                      ["Examine" :cmds '[ns-name ns-aliases ns-map
;;                                         ns-interns ns-publics ns-refers
;;                                         ns-imports]]
                      ["Examine" :cmds '[[:common-prefix ns-
                                          name aliases map interns publics
                                          refers imports]]]
                      ["From symbol" :cmds '[resolve ns-resolve namespace
                                             the-ns "(1.10)" requiring-resolve]]
                      ["Remove" :cmds '[ns-unalias ns-unmap remove-ns]]]
              ]
             [:box "green"
              :section "Loading"
              :table [["Load libs" :cmds '[{:latex "(\\href{https://blog.8thlight.com/colin-jones/2010/12/05/clojure-libs-and-namespaces-require-use-import-and-ns.html}{tutorial})"
                                            :html "(<a href=\"https://blog.8thlight.com/colin-jones/2010/12/05/clojure-libs-and-namespaces-require-use-import-and-ns.html\">tutorial</a>)"}
                                           require use import refer]]
                      ["List loaded" :cmds '[loaded-libs]]
                      ["Load misc" :cmds '[load load-file load-reader
                                           load-string]]]
              ]
             [:box "magenta"
              :section "Concurrency"
              :table [["Atoms" :cmds '[atom swap! reset! compare-and-set!
                                       "(1.9)" swap-vals! reset-vals!]]
                      ["Futures" :cmds '[future
                                         [:common-prefix future-
                                          call done? cancel cancelled?]
                                         future?]]
                      ["Threads" :cmds '[bound-fn bound-fn*
                                         [:common-suffix -thread-bindings
                                          get push pop]
                                         thread-bound?]]
                      ["Volatiles" :cmds '[volatile! vreset! vswap! volatile?]]
                      ["Misc" :cmds '[locking pcalls pvalues pmap seque
                                      promise deliver]]]
              :subsection {:latex "Refs and Transactions (\\href{https://clojure.org/reference/refs}{clojure.org/reference/refs})"
                           :html "Refs and Transactions (<a href=\"https://clojure.org/reference/refs\">clojure.org/reference/refs</a>)"}
              :table [["Create" :cmds '[ref]]
                      ["Examine"
                       :cmds '[deref "@"
                               {:latex "\\textmd{\\textsf{(@form $\\to$ (deref form))}}",
                                :html "(<code>@<var>form</var></code> &rarr; <code>(deref <var>form</var>)</code>)"}]]
                      ["Transaction" :cmds '[sync dosync io!]]
                      ["In transaction" :cmds '[ensure ref-set alter commute]]
                      ["Validators" :cmds '[set-validator! get-validator]]
                      ["History" :cmds '[ref-history-count
                                         [:common-prefix-suffix
                                          ref- -history min max]]]]
              :subsection {:latex "Agents and Asynchronous Actions (\\href{https://clojure.org/reference/agents}{clojure.org/reference/agents})"
                           :html "Agents and Asynchronous Actions (<a href=\"https://clojure.org/reference/agents\">clojure.org/reference/agents</a>)"}
              :table [["Create" :cmds '[agent]]
                      ["Examine" :cmds '[agent-error]]
                      ["Change state" :cmds '[send send-off restart-agent
                                              send-via set-agent-send-executor!
                                              set-agent-send-off-executor!]]
                      ["Block waiting" :cmds '[await await-for]]
                      ["Ref validators" :cmds '[set-validator! get-validator]]
                      ["Watchers" :cmds '[add-watch remove-watch]]
                      ["Thread handling" :cmds '[shutdown-agents]]
                      ["Error" :cmds '[error-handler set-error-handler!
                                       error-mode set-error-mode!]]
                      ["Misc" :cmds '[*agent* release-pending-sends]]]
              ]
             [:box "orange"
              :section {:latex "Java Interoperation (\\href{https://clojure.org/reference/java\\_interop}{clojure.org/reference/java\\_interop})"
                        :html "Java Interoperation (<a href=\"https://clojure.org/reference/java_interop\">clojure.org/reference/java_interop</a>)"}
              :table [["General" :cmds '[.. doto "Classname/" "Classname."
                                         new bean comparator enumeration-seq
                                         import iterator-seq memfn set! class
                                         class? bases supers type
                                         gen-class gen-interface definterface]]
                      ["Cast" :cmds '[boolean byte short char int long
                                      float double bigdec bigint num cast
                                      biginteger]]
                      ["Exceptions" :cmds '[throw try catch finally
                                            clojure.repl/pst ex-info ex-data
                                            Throwable->map
                                            "(1.9)" StackTraceElement->vec
                                            "(1.10)" ex-cause ex-message
                                            {:latex "\\textmd{\\textsf{(clojure.main/)}}",
                                             :html "(clojure.main/)"}
                                            clojure.main/ex-triage
                                            clojure.main/ex-str
                                            clojure.main/err->msg
                                            clojure.main/report-error]]]
              :subsection "Arrays"
              :table [["Create" :cmds '[make-array
                                        [:common-suffix -array object
                                         boolean byte short char int long
                                         float double]
                                        aclone to-array to-array-2d into-array]]
                      ["Use" :cmds '[aget aset
                                     [:common-prefix aset- boolean byte short
                                      char int long float double]
                                     alength amap areduce]]
                      ;; TBD: This would be a good place to give an
                      ;; example like ^"[Ljava.lang.BigInteger", yes?
                      ;; Also the cast ^objects?  Is there a doc page
                      ;; for that?
                      ["Cast" :cmds '[booleans bytes shorts chars
                                      ints longs floats doubles]]
                      ]
              :subsection {:latex "Proxy (\\href{https://github.com/cemerick/clojure-type-selection-flowchart}{Clojure type selection flowchart})"
                           :html "Proxy (<a href=\"https://github.com/cemerick/clojure-type-selection-flowchart\">Clojure type selection flowchart</a>)"}
              :table [["Create" :cmds '[proxy get-proxy-class
                                        [:common-suffix -proxy
                                         construct init]]]
                      ["Misc" :cmds '[proxy-mappings proxy-super update-proxy]]]
              ]
             [:box "blue"
              :subsection "Zippers (clojure.zip/)"
              :table [["Create" :cmds '[clojure.zip/zipper
                                        clojure.zip/seq-zip
                                        clojure.zip/vector-zip
                                        clojure.zip/xml-zip]]
                      ["Get loc" :cmds '[clojure.zip/up
                                         clojure.zip/down clojure.zip/left
                                         clojure.zip/right
                                         clojure.zip/leftmost
                                         clojure.zip/rightmost]]
                      ["Get seq" :cmds '[clojure.zip/lefts clojure.zip/rights
                                         clojure.zip/path clojure.zip/children]]
                      [{:html "'Change'", :latex "`Change'"}
                       :cmds '[clojure.zip/make-node clojure.zip/replace
                               clojure.zip/edit clojure.zip/insert-child
                               clojure.zip/insert-left clojure.zip/insert-right
                               clojure.zip/append-child clojure.zip/remove]]
                      ["Move" :cmds '[clojure.zip/next clojure.zip/prev]]
                      ["Misc" :cmds '[clojure.zip/root clojure.zip/node
                                      clojure.zip/branch? clojure.zip/end?]]]
              ]
             [:box "green2"
              :section "Other"
              :table [["XML" :cmds '[clojure.xml/parse xml-seq]]
                      ["REPL" :cmds '[*1 *2 *3 *e *print-dup* *print-length*
                                      *print-level* *print-meta*
                                      *print-readably*]]
                      ["Code" :cmds '[*compile-files* *compile-path* *file*
                                      *warn-on-reflection* compile
                                      loaded-libs test]]
                      ["Misc" :cmds '[eval force hash name *clojure-version*
                                      clojure-version *command-line-args*]]
                      [{:latex "\\begin{tabular}[t]{@{}l@{}} Browser \\\\ / Shell \\end{tabular}"
                        :html "Browser / Shell"}
                       :cmds '[{:latex "\\textmd{\\textsf{(clojure.java.browse/)}}",
                                :html "(clojure.java.browse/)"}
                               clojure.java.browse/browse-url
                               {:latex "\\textmd{\\textsf{(clojure.java.shell/)}}",
                                :html "(clojure.java.shell/)"}
                               clojure.java.shell/sh
                               clojure.java.shell/with-sh-dir
                               clojure.java.shell/with-sh-env]]]
              ]
;             [:footer
;               tbd
;
;              ]
             ]
      ])



(def ^:dynamic *auto-flush* true)


(defn printf-to-writer [w fmt-str & args]
  (binding [*out* w]
    (apply clojure.core/printf fmt-str args)
    (when *auto-flush* (flush))))


(defn iprintf [fmt-str-or-writer & args]
  (if (instance? CharSequence fmt-str-or-writer)
    (apply printf-to-writer *out* fmt-str-or-writer args)
    (apply printf-to-writer fmt-str-or-writer args)))


(defn die [fmt-str & args]
  (apply iprintf *err* fmt-str args)
  (System/exit 1))


(defn read-safely [x & opts]
  (with-open [r (java.io.PushbackReader. (apply io/reader x opts))]
    (clojure.edn/read r)))


(defn clojuredocs-url-fixup [s]
  (let [s (str/replace s "?" "_q")
        s (str/replace s "/" "_fs")
        s (str/replace s "." "_dot")]
    (c.u/url-encode s)))

(defn grimoire-url-fixup [s]
  (-> s g.u/munge (str "/")))

(def grimoire-base-url
  (str "https://conj.io/store/v0/org.clojure/clojure/latest/clj/"))

(defn sym-to-pair [prefix sym link-dest base-url]
  [(str prefix sym)
   (str base-url
        (case link-dest
              (:nolinks :links-to-clojure) sym
              :links-to-clojuredocs (clojuredocs-url-fixup (str sym))
              :links-to-grimoire    (grimoire-url-fixup (str sym))))])

(defn sym-to-url-list [link-target-site info]
  (let [{:keys [namespace-str symbol-list clojure-base-url
                clojuredocs-base-url grimoire-base-url]} info
         namespace-str (if (= "" namespace-str) "" (str namespace-str "/"))]
    (map #(sym-to-pair
           namespace-str % link-target-site
           (case link-target-site
                 :links-to-clojure clojure-base-url
                 :links-to-clojuredocs clojuredocs-base-url
                 :links-to-grimoire grimoire-base-url))
         symbol-list)))


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

(defn ns-info-common-case [ns-sym]
  (let [s (str ns-sym)
        clojure-url (str "https://clojure.github.com/clojure/"
                         s "-api.html#" s "/")
        ;; There is no documentation for clojure.core.reducers
        ;; namespace on ClojureDocs.org as of Oct 2018, so just use
        ;; the same clojure-url as above for now.
        clojuredocs-url
        (if (= ns-sym 'clojure.core.reducers)
          clojure-url
          (str "https://clojuredocs.org/clojure_core/" s "/"))]
    {:namespace-str (if (= ns-sym 'clojure.core) "" s)
     :symbol-list (keys (ns-publics ns-sym))
     :clojure-base-url clojure-url
     :clojuredocs-base-url clojuredocs-url
     :grimoire-base-url (str grimoire-base-url s "/")}))

(defn symbol-url-pairs-for-whole-namespaces [link-target-site]
  (apply concat
    (map
     #(sym-to-url-list link-target-site %)
     (concat
      [{:namespace-str "",
        :symbol-list '(def if do let quote var fn loop recur throw try
                        monitor-enter monitor-exit),
        :clojure-base-url "https://clojure.org/reference/special_forms#",
        :clojuredocs-base-url "https://clojuredocs.org/clojure_core/clojure.core/"
        :grimoire-base-url    (str grimoire-base-url "clojure.core/")}]

      (map ns-info-common-case all-clojure-built-in-namespaces)))))


(defn symbol-url-pairs-specified-by-hand [link-target-site]
  (concat
   ;; Manually specify links for a few symbols in the cheatsheet.
   [["new" (case link-target-site
                 :links-to-clojure
                 "https://clojure.org/reference/java_interop#new"
                 :links-to-clojuredocs
                 "https://clojuredocs.org/clojure_core/clojure.core/new"
                 :links-to-grimoire
                 (str grimoire-base-url "clojure.core/new")
                 )]
    ["set!" (case link-target-site
                  :links-to-clojure
                  "https://clojure.org/reference/java_interop#Java%20Interop-The%20Dot%20special%20form-%28set!%20%28.%20Classname-symbol%20staticFieldName-symbol%29%20expr%29"
                  :links-to-clojuredocs
                  "https://clojuredocs.org/clojure_core/clojure.core/set!"
                  :links-to-grimoire
                  (str grimoire-base-url "clojure.core/set!")
                  )]
    ["catch" (case link-target-site
                   :links-to-clojure
                   "https://clojure.org/reference/special_forms#try"
                   :links-to-clojuredocs
                   "https://clojuredocs.org/clojure_core/clojure.core/catch"
                   :links-to-grimoire
                   (str grimoire-base-url "clojure.core/catch")
                   )]
    ["finally" (case link-target-site
                     :links-to-clojure
                     "https://clojure.org/reference/special_forms#try"
                     :links-to-clojuredocs
                     "https://clojuredocs.org/clojure_core/clojure.core/finally"
                     :links-to-grimoire
                     (str grimoire-base-url "clojure.core/finally")
                     )]]
   (case link-target-site
         :links-to-clojure
         [["Classname." "https://clojure.org/reference/java_interop#Java%20Interop-The%20Dot%20special%20form-%28new%20Classname%20args*%29"]
          ["Classname/" "https://clojure.org/reference/java_interop#Java%20Interop-%28Classname/staticMethod%20args*%29"]]
         :links-to-clojuredocs
         ;; I don't have a good idea where on clojuredocs.org these
         ;; should link to, if anywhere.
         []
         :links-to-grimoire
         [])

   ;; ClojureDocs.org does not have new vars from Clojure 1.10 yet as
   ;; of 2019-Jun-20.  Until it does, point at the clojure.org
   ;; official API docs.
   (map (fn make-symbol-url-pair-to-clojure-org-api
          [[namespace-str symbol-str]]
          [(str (if (= "clojure.core" namespace-str) "" (str namespace-str "/"))
                symbol-str)
           (format "https://clojure.github.io/clojure/%s-api.html#%s/%s"
                   namespace-str namespace-str symbol-str)])
        [
         [ "clojure.core" "tap>" ]
         [ "clojure.core" "add-tap" ]
         [ "clojure.core" "remove-tap" ]
         [ "clojure.core" "requiring-resolve" ]
         [ "clojure.datafy" "datafy" ]
         [ "clojure.datafy" "nav" ]
         [ "clojure.core" "ex-cause" ]
         [ "clojure.core" "ex-message" ]
         [ "clojure.main" "ex-triage" ]
         [ "clojure.main" "ex-str" ]
         [ "clojure.main" "err->msg" ]
         [ "clojure.main" "report-error" ]
         ])

   ;; These symbols do not have API docs anywhere that I can find,
   ;; yet.  Point at the github page for tools.reader for now.
   (map (fn [sym-str]
          [sym-str "https://github.com/clojure/tools.reader" ])
        [ "clojure.tools.reader.edn/read"
          "clojure.tools.reader.edn/read-string" ])

   [[ "clojure.data.priority-map/priority-map"
      "https://github.com/clojure/data.priority-map" ]]

   (map (fn [sym-str]
          [sym-str "https://github.com/clojure/data.avl" ])
        [ "clojure.data.avl/sorted-set"
          "clojure.data.avl/sorted-set-by"
          "clojure.data.avl/sorted-map"
          "clojure.data.avl/sorted-map-by" ])

   (map (fn [sym-str]
          [sym-str "https://github.com/clojure/core.async" ])
        [ "clojure.core.async/>!!"
          "clojure.core.async/<!!"
          "clojure.core.async/>!"
          "clojure.core.async/<!" ])

   (map (fn [sym-str]
          [sym-str "https://github.com/clojure/core.rrb-vector" ])
        [ "clojure.core.rrb-vector/vector"
          "clojure.core.rrb-vector/vec"
          "clojure.core.rrb-vector/vector-of" ])

   (map (fn [sym-str]
          [sym-str "https://github.com/clojure/data.int-map" ])
        [ "clojure.data.int-map/int-set"
          "clojure.data.int-map/dense-int-set"
          "clojure.data.int-map/int-map" ])

   (map (fn [sym-str]
          [sym-str "https://github.com/amalloy/ordered" ])
        [ "flatland.ordered.set/ordered-set"
          "flatland.ordered.map/ordered-map" ])

   [[ "flatland.useful.map/ordering-map"
      "https://github.com/amalloy/useful/blob/master/src/flatland/useful/map.clj#L243-L245" ]]
   ))


(defn symbol-url-pairs [link-target-site]
  (if (= link-target-site :nolinks)
    []
    (concat
     (symbol-url-pairs-for-whole-namespaces link-target-site)
     (symbol-url-pairs-specified-by-hand link-target-site))))


;; Use the following usepackage line if you want text with clickable
;; links in the PDF file to look no different from normal text:

;; \usepackage[colorlinks=false,breaklinks=true,pdfborder={0 0 0},dvipdfm]{hyperref}

;; The following line causes blue boxes to appear around words that
;; have links in the PDF file.  This can be good for debugging, but
;; might not be what you want long term.

;; \\usepackage[dvipdfm]{hyperref}


(def latex-header-except-documentclass
     "
% Authors: Steve Tayon, Andy Fingerhut
% Comments, errors, suggestions: Create an issue at
% https://github.com/jafingerhut/clojure-cheatsheets

% Most of the content is based on the clojure wiki, api and source code by Rich Hickey on https://clojure.org/.

% License
% Eclipse Public License v1.0
% https://opensource.org/licenses/eclipse-1.0.php

% Packages
\\usepackage[utf8]{inputenc}
\\usepackage[T1]{fontenc}
\\usepackage{textcomp}
\\usepackage[english]{babel}
\\usepackage{tabularx}
\\usepackage[colorlinks=false,breaklinks=true,pdfborder={0 0 0},dvipdfm]{hyperref}
\\usepackage{lmodern}
\\renewcommand*\\familydefault{\\sfdefault}


\\usepackage[table]{xcolor}

% Set column space
\\setlength{\\columnsep}{0.25em}

% Define colours
\\definecolorset{hsb}{}{}{red,0,.4,0.95;orange,.1,.4,0.95;green,.25,.4,0.95;yellow,.15,.4,0.95}

\\definecolorset{hsb}{}{}{blue,.55,.4,0.95;purple,.7,.4,0.95;pink,.8,.4,0.95;blue2,.58,.4,0.95}

\\definecolorset{hsb}{}{}
{magenta,.9,.4,0.95;green2,.29,.4,0.95}

\\definecolor{grey}{hsb}{0.25,0,0.85}

\\definecolor{white}{hsb}{0,0,1}

% Redefine sections
\\makeatletter
\\renewcommand{\\section}{\\@startsection{section}{1}{0mm}
	{-1.7ex}{0.7ex}{\\normalfont\\large\\bfseries}}
\\renewcommand{\\subsection}{\\@startsection{subsection}{2}{0mm}
	{-1.7ex}{0.5ex}{\\normalfont\\normalsize\\bfseries}}
\\makeatother

% No section numbers
\\setcounter{secnumdepth}{0}

% No indentation
\\setlength{\\parindent}{0em}

% No header and footer
\\pagestyle{empty}


% A few shortcuts
\\newcommand{\\cmd}[1] {\\texttt{\\textbf{{#1}}}}
\\newcommand{\\cmdline}[1] {
	\\begin{tabularx}{\\hsize}{X}
			\\texttt{\\textbf{{#1}}}
	\\end{tabularx}
}

\\newcommand{\\colouredbox}[2] {
	\\colorbox{#1!40}{
		\\begin{minipage}{0.95\\linewidth}
			{
			\\rowcolors[]{1}{#1!20}{#1!10}
			#2
			}
		\\end{minipage}
	}
}

\\begin{document}

")

(def latex-header-after-title "")

(def latex-footer
     "
\\end{document}
")


(def latex-a4-header-before-title
     (str "\\documentclass[footinclude=false,twocolumn,DIV40,fontsize=6.1pt]{scrreprt}\n"
          latex-header-except-documentclass))

;; US letter is a little shorter, so formatting gets completely messed
;; up unless we use a slightly smaller font size.
(def latex-usletter-header-before-title
     (str "\\documentclass[footinclude=false,twocolumn,DIV40,fontsize=5.9pt,letterpaper]{scrreprt}\n"
          latex-header-except-documentclass))



(def html-header-before-title "<!doctype html>
<html lang=\"en\">
<head>
  <meta charset=\"utf-8\">
")

(defn inline-css [& {:keys [js?]}]
  (let [css (slurp (io/resource "inline.css"))]
    (if js?
      (str/replace css  #"\n" "\\\\n")
      css)))

(def html-header-after-title (format "
  <link rel=\"stylesheet\" href=\"cheatsheet_files/style.css\" type=\"text/css\" />
  <style type=\"text/css\">
  %s
  </style>
  <link href=\"cheatsheet_files/tipTip.css\" rel=\"stylesheet\">
  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">
  <script src=\"cheatsheet_files/jquery.js\"></script>
  <script src=\"cheatsheet_files/jquery.tipTip.js\"></script>
  <script>
  $(function(){
      $(\".tooltip\").tipTip();
  });

  escape_re=function(s) {
    return s.replace(/[-\\/\\\\^$*+?.()|[\\]{}]/g, '\\\\$&');
  };
  $(function(){
    var $links = $('a');
    $(window).on('keyup', function(e) {
      if (e.keyCode == 27) {
        $('#search').focus();
      }
    });
    $('#search').keydown(function(e) {
      var val = $(this).val();
      if (!val && e.key && (e.key == \"'\" || e.key == \"/\")) {
        this.blur();
      }
    });
    $('#search').keyup(function() {
       var val = $(this).val(),
       strs = $.trim(val).split(/\\s+/);
       strs = $.map(strs,escape_re);
       regstr = '^.*' + strs.join('.*') + '.*$',
       reg = RegExp(regstr, 'i');
       console.log(val, reg);

       var matched = $links.filter(function() {
          var text = $(this).text().replace(/\\s+/g, ' ');
          if ($.trim(val)) {return reg.test(text)};
       });

       if (matched.length > 0) {
        $('table').hide();
        $('table').prev('h3').hide();
        $('.section').hide();
        $('a').removeClass('highlight');
        $('#search').removeClass('highlight');

        matched.closest('table').prev('h3').show();
        matched.closest('table').show();
        matched.closest('.section').show();
        matched.addClass('highlight');
       }
       else {
        $('table').show();
        $('table').prev('h3').show();
        $('.section').show();
        $('a').removeClass('highlight');
        if ($.trim(val) == '') {
         $('#search').removeClass('highlight');
       }
       else {
         $('#search').addClass('highlight');
       };
      };
     });
     $('#search').keyup();
  })
  </script>
</head>

<body id=\"cheatsheet\">
  <nav class=\"search\"><input type='text' id='search' placeholder='Type to search...' autofocus='autofocus'></nav>
  <div class=\"wiki wikiPage\" id=\"content_view\">
" (inline-css)))


(def html-footer "  </div>
</body>
</html>
")


(def embeddable-html-fragment-header-before-title "")
(def embeddable-html-fragment-header-after-title (format "
<script language=\"JavaScript\" type=\"text/javascript\">
//<![CDATA[
document.write('<style type=\"text/css\">%s<\\/style>')
//]]>
</script>
" (inline-css :js? true)))
(def embeddable-html-fragment-footer "")


(defmacro verify [cond]
  `(when (not ~cond)
     (iprintf "%s\n" (str "verify of this condition failed: " '~cond))
     (throw (Exception.))))


(defn wrap-line
  "Given a string 'line' that is assumed not to contain line separators,
  but may contain spaces and tabs, return a sequence of strings where
  each is at most width characters long, and all 'words' (consecutive
  sequences of non-whitespace characters) are kept together in the
  same line.  The only exception to the maximum width are if a single
  word is longer than width, in which case it is kept together on one
  line.  Whitespace in the original string is kept except it is
  removed from the end and where lines are broken.  As a special case,
  any whitespace before the first word is preserved.  The second and
  all later lines will always begin with a non-whitespace character."
  [line width]
  (let [space-plus-words (map first (re-seq #"(\s*\S+)|(\s+)"
                                            (str/trimr line)))]
    (loop [finished-lines []
           partial-line []
           len 0
           remaining-words (seq space-plus-words)]
      (if-let [word (first remaining-words)]
        (if (zero? len)
          ;; Special case for first word of first line.  Keep it as
          ;; is, including any leading whitespace it may have.
          (recur finished-lines [ word ] (count word) (rest remaining-words))
          (let [word-len (count word)
                len-if-append (+ len word-len)]
            (if (<= len-if-append width)
              (recur finished-lines (conj partial-line word) len-if-append
                     (rest remaining-words))
              ;; else we're done with current partial-line and need to
              ;; start a new one.  Trim leading whitespace from word,
              ;; which will be the first word of the next line.
              (let [trimmed-word (str/triml word)]
                (recur (conj finished-lines (apply str partial-line))
                       [ trimmed-word ]
                       (count trimmed-word)
                       (rest remaining-words))))))
        (if (zero? len)
          [ "" ]
          (conj finished-lines (apply str partial-line)))))))


(defn output-title [fmt t]
  (let [t (if (map? t)
            (get t (:fmt fmt))
            t)]
    (iprintf "%s" (case (:fmt fmt)
                    :latex (format "{\\Large{\\textbf{%s}}}\n\n" t)
                    :html (format "  <title>%s</title>\n" t)
                    :verify-only ""))))


(defn htmlize-str [s]
  (str/escape s {\" "&quot;"
                 \& "&amp;"
                 \< "&lt;"
                 \> "&gt;"}))


;; Handle a thing that could be a string, symbol, or a 'conditional
;; string'

(defn cond-str [fmt cstr & htmlize]
  (cond (string? cstr) cstr
        (symbol? cstr) (if (= (:fmt (first htmlize)) :html)
                         (htmlize-str (str cstr))
                         (str cstr))
        (map? cstr) (do
                      (verify (contains? cstr (:fmt fmt)))
                      (cstr (:fmt fmt)))
        :else (do
                (iprintf "%s\n" (str "cond-str: cstr=" cstr " is not a string, symbol, or map"))
                (verify (or (string? cstr) (symbol? cstr) (map? cstr))))))


(def symbols-looked-up (atom #{}))


(defn url-for-cmd-doc [opts cmd-str]
  (when (:warn-about-unknown-symbols opts)
    (swap! symbols-looked-up conj cmd-str))
  (if-let [url-str (get (:symbol-name-to-url opts) cmd-str)]
    url-str
    (do
      (when (:warn-about-unknown-symbols opts)
        (iprintf *err* "No URL known for symbol with name: '%s'\n" cmd-str))
      nil)))


(defn escape-latex-hyperref-url [url]
  (-> url
      (str/replace "#" "\\#")
      (str/replace "%" "\\%")
      (str/replace "<" "\\%3C")
      (str/replace "=" "\\%3D")
      (str/replace ">" "\\%3E")
      (str/replace "&" "\\&")))


(defn escape-latex-hyperref-target [target]
  (-> target
      ;; -> doesn't seem to have a problem in LaTeX, but ->> looks
      ;; like - followed by a special symbol that is two >'s
      ;; combined, not two separate characters.
      (str/replace "->>" "-{>}{>}")
      (str/replace "&" "\\&")))


;; Only remove the namespaces that are very commonly used in the
;; cheatsheet.  For the ones that only have one or a few symbol there,
;; it seems best to leave the namespace in there explicitly.

(def +common-namespaces-to-remove-from-shown-symbols+
  ["clojure.core.async/"
   "clojure.core.rrb-vector/"
   "clojure.data.avl/"
   "clojure.data.int-map/"
   "clojure.data.priority-map/"
   "clojure.datafy/"
   "clojure.edn/"
   "clojure.java.browse/"
   "clojure.java.io/"
   "clojure.java.javadoc/"
   "clojure.java.shell/"
   "clojure.main/"
   "clojure.pprint/"
   "clojure.repl/"
   "clojure.set/"
   "clojure.spec.alpha/"
   "clojure.string/"
   "clojure.tools.reader.edn/"
   "clojure.walk/"
   "clojure.zip/"
   "flatland.ordered.map/"
   "flatland.ordered.set/"
   "flatland.useful.map/"
   ])

(defn remove-common-ns-prefix [s]
  (if-let [pre (first (filter #(str/starts-with? s %)
                              +common-namespaces-to-remove-from-shown-symbols+))]
    (subs s (count pre))
    s))


(defn cleanup-doc-str-tooltip
  "Get rid of the first line of the doc string, which is always a line
of dashes, and keep at most the first 25 lines of the doc string, to
keep the tooltip from being too large.  Also replace double quote
characters (\") with &quot;"
  [s]
  (let [max-line-width 80
        lines (-> s (str/split-lines) (rest))
        lines (mapcat #(wrap-line % max-line-width) lines)
        max-to-keep 25
        combined-lines
        (if (> (count lines) max-to-keep)
          (str (str/trim-newline (str/join "\n" (take max-to-keep lines)))
               "\n\n[ documentation truncated.  Click link for the rest. ]")
          (str/trim-newline (str/join "\n" lines)))]
    (htmlize-str combined-lines)))


(defn doc-for-symbol-str [s]
  (let [sym (symbol s)]
    (if-let [special-sym ('{& fn catch try finally try} sym)]
      (with-out-str
        (#'clojure.repl/print-doc (#'clojure.repl/special-doc special-sym)))
      (if (#'clojure.repl/special-doc-map sym)
        (with-out-str
          (#'clojure.repl/print-doc (#'clojure.repl/special-doc sym)))
        (if-let [v (try
                     (resolve sym)
                     (catch Exception e nil))]
          (with-out-str (#'clojure.repl/print-doc (meta v))))))))


(defn clojuredocs-content-summary [snap-time sym-info]
  (let [num-examples (count (:examples sym-info))
        ;; remove lines containing nothing but <pre> or </pre> before
        ;; counting them, since clojuredocs.org doesn't show those as
        ;; separate lines.
        total-example-lines (count
                             (remove #(re-find #"(?i)^\s*<\s*/?\s*pre\s*>\s*$" %)
                                     (mapcat str/split-lines
                                             (map :body (:examples sym-info)))))
        num-see-alsos (count (:see-alsos sym-info))
        num-comments (count (:comments sym-info))
        see-also-style :list-see-alsos]
    (str (case num-examples
           0 "0 examples"
           1 (format "1 example with %d lines"
                     total-example-lines)
           (format "%d examples totaling %d lines"
                   num-examples total-example-lines))
         (if (and (= see-also-style :number-of-see-alsos)
                  (> num-see-alsos 0))
           (format ", %d see also%s" num-see-alsos
                   (if (== num-see-alsos 1) "" "s"))
           "")
         (if (zero? num-comments)
           ""
           (format ", %d comment%s" num-comments
                   (if (== num-comments 1) "" "s")))
         " on " snap-time
         (if (and (= see-also-style :list-see-alsos)
                  (> num-see-alsos 0))
           (str/join "\n"
                     (wrap-line (str "\nSee also: "
                                     (str/join ", " (map :name
                                                         (:see-alsos sym-info))))
                                72))
           ""))))


(defn table-one-cmd-to-str [fmt cmd prefix suffix]
  (let [cmd-str (cond-str fmt cmd)
        whole-cmd (str prefix cmd-str suffix)
        url-str (url-for-cmd-doc fmt whole-cmd)
        ;; cmd-str-to-show has < converted to HTML &lt; among other
        ;; things, if (:fmt fmt) is :html
        cmd-str-to-show (remove-common-ns-prefix (cond-str fmt cmd fmt))
;;        _ (iprintf *err* "andy-debug: cmd='%s' prefix='%s' suffix='%s' (class whole-cmd)='%s' whole-cmd='%s'\n"
;;                   cmd prefix suffix (class whole-cmd) whole-cmd)
        orig-doc-str (doc-for-symbol-str whole-cmd)
        cleaned-doc-str (if orig-doc-str
                          (cleanup-doc-str-tooltip orig-doc-str))
        clojuredocs-snapshot (:clojuredocs-snapshot fmt)
        cleaned-doc-str (if cleaned-doc-str
                          (do
;;                            (iprintf *err* "whole-cmd='%s' sym-info='%s'\n"
;;                                     whole-cmd
;;                                     (get-in clojuredocs-snapshot
;;                                             [:snapshot-info whole-cmd]))
                            (if-let [sym-info
                                     (or (get-in clojuredocs-snapshot
                                                 [:snapshot-info whole-cmd])
                                         (get-in clojuredocs-snapshot
                                                 [:snapshot-info
                                                  (str "clojure.core/"
                                                       whole-cmd)]))]
                              (str cleaned-doc-str "\n\n"
                                   (clojuredocs-content-summary
                                    (get clojuredocs-snapshot :snapshot-time)
                                    sym-info))
                              cleaned-doc-str)))]
    (if url-str
      (case (:fmt fmt)
        :latex (str "\\href{" (escape-latex-hyperref-url url-str)
                    "}{" (escape-latex-hyperref-target cmd-str-to-show) "}")
        :html (str "<a href=\"" url-str "\""
                   (if cleaned-doc-str
                     (case (:tooltips fmt)
                       :no-tooltips ""
                       :tiptip (str " class=\"tooltip\" title=\"<pre>"
                                    cleaned-doc-str "</pre>\"")
                       :use-title-attribute (str " title=\""
                                                 cleaned-doc-str "\""))
                     ;; else no tooltip available to show
                     "")
                   ">" cmd-str-to-show "</a>")
        :verify-only "")
      cmd-str-to-show)))


;; When expand? is true, we expand prefixes and suffixes.
;; Disadvantage: longer output, which is especially bad for the PDF
;; cheatsheet.  Advantage: can search for the complete names of the
;; vars.

;; When expand? is false, don't expand prefixes or suffixes -- leave
;; them 'compressed'.

(defn table-cmds-to-str [fmt cmds]
  (if (vector? cmds)
    (let [expand? (:expand-common-prefixes-or-suffixes fmt)
          [keyw & cmds] cmds
          [pre suff cmds] (case keyw
                            :common-prefix [(first cmds) nil (rest cmds)]
                            :common-suffix [nil (first cmds) (rest cmds)]
                            :common-prefix-suffix [(first cmds) (second cmds) (nnext cmds)])
          [before between after] (if expand?
                                   ["" " " ""]
                                   (case (:fmt fmt)
                                     :latex ["\\{" ", " "\\}"]
                                     :html  [  "{" ", "   "}"]
                                     :verify-only ["" "" ""]))
          pre-str (if pre (cond-str fmt pre) "")
          suff-str (if suff (cond-str fmt suff) "")
          str-list (if expand?
                     (map #(table-one-cmd-to-str fmt (str pre-str
                                                          (cond-str fmt %)
                                                          suff-str)
                                                 "" "")
                          cmds)
                     (map #(table-one-cmd-to-str fmt % pre-str suff-str)
                          cmds))
          most-str (str before
                        (str/join between str-list)
                        after)
          ;; pre-to-show has < converted to HTML &lt; etc., if fmt is
          ;; :html
          pre-to-show (if (and pre (not expand?))
                        (cond-str fmt pre fmt)
                        "")
          suff-to-show (if (and suff (not expand?))
                         (cond-str fmt suff fmt)
                         "")]
      (str pre-to-show most-str suff-to-show))
    ;; handle the one thing, with no prefix or suffix
    (table-one-cmd-to-str fmt cmds "" "")))


(defn output-table-cmd-list [fmt k cmds]
  (if (= k :str)
    (iprintf "%s" (cond-str fmt cmds))
    (do
      (iprintf "%s" (case (:fmt fmt)
                      :latex
                      (case k
                        :cmds "\\cmd{"
                        :cmds-one-line "\\cmdline{")
                      :html "<code>"
                      :verify-only ""))
      (iprintf "%s" (str/join " " (map #(table-cmds-to-str fmt %) cmds)))
      (iprintf "%s" (case (:fmt fmt)
                      :latex "}"
                      :html "</code>"
                      :verify-only "")))))


(defn output-table-row [fmt row row-num nrows]
  (verify (not= nil (#{:cmds :str} (second row))))

  (let [[row-title k cmd-desc] row]
    (iprintf "%s" (case (:fmt fmt)
                    :latex (str (cond-str fmt row-title fmt) " & ")
                    :html (format "              <tr class=\"%s\">
                <td>%s</td>
                <td>"
                                  (if (even? row-num) "even" "odd")
                                  (cond-str fmt row-title fmt))
                    :verify-only ""))
    (output-table-cmd-list fmt k cmd-desc)
    (iprintf "%s" (case (:fmt fmt)
                    :latex (if (= row-num nrows) "\n" " \\\\\n")
                    :html "</td>
              </tr>\n"
                    :verify-only ""))))


(defn output-table [fmt tbl]
  (iprintf "%s" (case (:fmt fmt)
                  :latex "\\begin{tabularx}{\\hsize}{lX}\n"
                  :html "          <table>
            <tbody>
"
                  :verify-only ""))
  (let [nrows (count tbl)]
    (doseq [[row row-num] (map (fn [& args] (vec args))
                               tbl (iterate inc 1))]
      (output-table-row fmt row row-num nrows)))
  (iprintf "%s" (case (:fmt fmt)
                  :latex "\\end{tabularx}\n"
                  :html "            </tbody>
          </table>
"
                  :verify-only "")))


(defn output-cmds-one-line [fmt tbl]
  (iprintf "%s" (case (:fmt fmt)
                  :latex ""
                  :html "          <div class=\"single_row\">
            "
                  :verify-only ""))
  (output-table-cmd-list fmt :cmds-one-line tbl)
  (iprintf "%s" (case (:fmt fmt)
                  :latex "\n"
                  :html "
          </div>\n"
                  :verify-only "")))


(defn output-box [fmt box]
  (verify (even? (count box)))
  (verify (= :box (first box)))
  (let [box-color (if (:colors fmt)
                    (case (:colors fmt)
                          :color (second box)
                          :grey "grey"
                          :bw "white")
                    nil)
        key-val-pairs (partition 2 (nnext box))]
    (iprintf "%s" (case (:fmt fmt)
                    :latex (format "\\colouredbox{%s}{\n" box-color)
                    :html (format "        <div class=\"section%s\">\n" (if box-color (str " " box-color) ""))
                    :verify-only ""))
    (doseq [[k v] key-val-pairs]
      (case k
            :section
            (case (:fmt fmt)
                  :latex (iprintf "\\section{%s}\n" (cond-str fmt v))
                  :html (iprintf "          <h2>%s</h2>\n" (cond-str fmt v)))
            :subsection
            (case (:fmt fmt)
                  :latex (iprintf "\\subsection{%s}\n" (cond-str fmt v))
                  :html (iprintf "          <h3>%s</h3>\n" (cond-str fmt v)))
            :table
            (output-table fmt v)
            :cmds-one-line
            (output-cmds-one-line fmt v)))
    (iprintf "%s" (case (:fmt fmt)
                    :latex "}\n\n"
                    :html "        </div><!-- /section -->\n"
                    :verify-only ""))))


(defn output-col [fmt col]
  (iprintf "%s" (case (:fmt fmt)
                  :latex ""
                  :html "      <div class=\"column\">\n"
                  :verify-only ""))
  (doseq [box col]
    (output-box fmt box))
  (iprintf "%s" (case (:fmt fmt)
                  ;;:latex "\\columnbreak\n\n"
                  :latex "\n\n"
                  :html "      </div><!-- /column -->\n"
                  :verify-only "")))


(defn output-page [fmt pg]
  (verify (= (first pg) :column))
  (verify (== 2 (count (filter #(= % :column) pg))))
  (iprintf "%s" (case (:fmt fmt)
                  :latex ""
                  :html "    <div class=\"page\">\n"
                  :verify-only ""))
  (let [tmp (rest pg)
        [col1 col2] (split-with #(not= % :column) tmp)
        col2 (rest col2)]
    (output-col fmt col1)
    (output-col fmt col2))
  (iprintf "%s" (case (:fmt fmt)
                  :latex ""
                  :html "    </div><!-- /page -->\n"
                  :verify-only "")))


(defn output-cheatsheet [fmt cs]
  (verify (even? (count cs)))
  (iprintf "%s" (case (:fmt fmt)
                  :latex (case (:paper fmt)
                           :a4 latex-a4-header-before-title
                           :usletter latex-usletter-header-before-title)
                  :html html-header-before-title
                  :embeddable-html embeddable-html-fragment-header-before-title
                  :verify-only ""))
  (let [[k title & pages] cs
        [show-title fmt-passed-down]
        (if (= (:fmt fmt) :embeddable-html)
          [false (assoc fmt :fmt :html)]
          [true fmt])]
    (verify (= k :title))
    (when show-title
      (output-title fmt-passed-down title))
    (iprintf "%s" (case (:fmt fmt)
                    :latex latex-header-after-title
                    :html html-header-after-title
                    :embeddable-html embeddable-html-fragment-header-after-title
                    :verify-only ""))
    (doseq [[k pg] (partition 2 pages)]
      (verify (= k :page))
      (output-page fmt-passed-down pg)))
  (iprintf "%s" (case (:fmt fmt)
                  :latex latex-footer
                  :html html-footer
                  :embeddable-html embeddable-html-fragment-footer
                  :verify-only "")))


(defn simplify-snapshot-time [clojuredocs-snapshot]
  (if-let [snap-time (:snapshot-time clojuredocs-snapshot)]
    (merge clojuredocs-snapshot
           {:snapshot-time (if-let [[_ day-month-date year]
                                    (re-find #"^(\S+ \S+ \d+)\s+.*\s+(\d+)$"
                                             snap-time)]
                             (str day-month-date " " year)
                             snap-time)})
    clojuredocs-snapshot))


;; Supported command line args:

;; links-to-clojure (default if nothing specified on command line):
;; Generate HTML and LaTeX files with links from the symbols to
;; clojure.org and clojure.github.com URLs where they are documented.

;; links-to-clojuredocs: Generate HTML and LaTeX files with links from
;; the symbols to clojuredocs.org URLs where they are documented.

;; links-to-grimoire: Generate HTML and LaTeX files with links from
;; the symbols to conj.io URLs where they are documented.

;; nolinks: Do not include any links in the output files.  Except for
;; that and the likely difference in appearance in HTML of anchor text
;; from text with no links, there should be no difference in the
;; appearance of the output files compared to the choices above.

(defn parse-args [args]
  (let [supported-link-targets #{"nolinks" "links-to-clojure" "links-to-clojuredocs" "links-to-grimoire"}
        supported-tooltips #{"no-tooltips" "use-title-attribute" "tiptip"}
        link-target-site (if (< (count args) 1)
                           :links-to-clojure
                           (let [arg (nth args 0)]
                             (if (supported-link-targets arg)
                               (keyword arg)
                               (die "Unrecognized argument: %s\nSupported args are: %s\n"
                                    arg
                                    (str/join " " (seq supported-link-targets))))))
        tooltips (if (< (count args) 2)
                   :no-tooltips
                   (let [arg (nth args 1)]
                     (if (supported-tooltips arg)
                       (keyword arg)
                       (die "Unrecognized argument: %s\nSupported args are: %s\n"
                            arg
                            (str/join " " (seq supported-tooltips))))))
        clojuredocs-snapshot-filename (if (< (count args) 3)
                                        nil
                                        (nth args 2))]
    {:link-target-site link-target-site
     :tooltips tooltips
     :clojuredocs-snapshot-filename clojuredocs-snapshot-filename}))


(defn read-clojuredocs-snapshot [fname]
  (let [clojuredocs-snapshot (if fname
                               (simplify-snapshot-time
                                (read-safely fname))
                               {})]
    (when (not= clojuredocs-snapshot {})
      (iprintf *err* "Read info for %d symbols from file '%s' with time %s\n"
               (count (get clojuredocs-snapshot :snapshot-info))
               fname
               (:snapshot-time clojuredocs-snapshot))
      (iprintf *err* "No clojuredocs snapshot file specified.\n"))
    clojuredocs-snapshot))


(defn print-warnings [wrtr symbol-name-to-url symbols-looked-up]
  ;; Print out a list of all symbols in our symbol-name-to-url
  ;; table that we never looked up.
  (let [never-used (set/difference (set (keys symbol-name-to-url))
                                   symbols-looked-up)
        all-ns-names-sorted (->> (all-ns) (map str) sort)]
    (iprintf wrtr "\n\n%d symbols successfully looked up.\n\n"
             (count symbols-looked-up))
    (iprintf wrtr "\n\nSorted list of %d symbols in lookup table that were never used:\n\n"
             (count never-used))
    (iprintf wrtr "%s\n" (str/join "\n" (sort (seq never-used))))
    (iprintf wrtr "\n\nSorted list of links to documentation for symbols that were never used:\n\n\n")
    (iprintf wrtr "%s\n" (str/join "<br>"
                                    (map #(format "<a href=\"%s\">%s</a>\n"
                                                  (symbol-name-to-url %) %)
                                         (sort (seq never-used)))))
    (iprintf wrtr "\n\nSorted list of %d namespace names currently existing:\n\n"
             (count all-ns-names-sorted))
    (doseq [s all-ns-names-sorted]
      (iprintf wrtr "%s\n" s))))


(defn -main [& args]
  (let [opts (parse-args args)
        clojuredocs-snapshot (read-clojuredocs-snapshot
                              (:clojuredocs-snapshot-filename opts))
        symbol-name-to-url (into {} (symbol-url-pairs (:link-target-site opts)))
        opts (merge opts
                    {:clojuredocs-snapshot clojuredocs-snapshot
                     :symbol-name-to-url symbol-name-to-url
                     :expand-common-prefixes-or-suffixes true})]
    (binding [*out* (io/writer "cheatsheet-full.html")
              *err* (io/writer "warnings.log")]
      (output-cheatsheet (merge opts {:fmt :html, :colors :color,
                                      :warn-about-unknown-symbols true})
                         cheatsheet-structure)
      (print-warnings *err* symbol-name-to-url @symbols-looked-up)
      (.close *out*)
      (.close *err*))
    (doseq [x [{:filename "cheatsheet-embeddable.html",
                 :format {:fmt :embeddable-html}}
               {:filename "cheatsheet-a4-color.tex",
                :format {:fmt :latex, :paper :a4, :colors :color}}
               {:filename "cheatsheet-a4-grey.tex",
                :format {:fmt :latex, :paper :a4, :colors :grey}}
               {:filename "cheatsheet-a4-bw.tex",
                :format {:fmt :latex, :paper :a4, :colors :bw}}
               {:filename "cheatsheet-usletter-color.tex",
                :format {:fmt :latex, :paper :usletter, :colors :color}}
               {:filename "cheatsheet-usletter-grey.tex",
                :format {:fmt :latex, :paper :usletter, :colors :grey}}
               {:filename "cheatsheet-usletter-bw.tex",
                :format {:fmt :latex, :paper :usletter, :colors :bw}}]]
      (binding [*out* (io/writer (:filename x))]
        (output-cheatsheet (merge opts (:format x))
                           cheatsheet-structure)
        (.close *out*)))))
