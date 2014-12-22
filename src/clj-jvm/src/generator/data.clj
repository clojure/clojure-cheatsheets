(ns generator.data)

(def clojure-repl
  [:box "green"
   :section "Documentation"
   :table [["clojure.repl/"
            :cmds '[clojure.repl/doc clojure.repl/find-doc
                    clojure.repl/apropos clojure.repl/source
                    clojure.repl/pst clojure.java.javadoc/javadoc
                    "(foo.bar/ is namespace for later syms)"]]]])

(def clojure-primitives
  [:box "blue"
   :section "Primitives"
   :subsection "Numbers"
   :table [["Literals" :cmds '[{:latex "\\href{http://docs.oracle.com/javase/7/docs/api/java/lang/Long.html}{Long}:"
                                :html "<a href=\"http://docs.oracle.com/javase/7/docs/api/java/lang/Long.html\">Long</a>:"}
                               "7,"
                               "hex" "0xff,"
                               "oct" "017,"
                               "base 2" "2r1011,"
                               "base 36" "36rCRAZY"
                               "BigInt:"
                               "7N"
                               "Ratio:"
                               "-22/7"
                               {:latex "\\href{http://docs.oracle.com/javase/7/docs/api/java/lang/Double.html}{Double}:"
                                :html "<a href=\"http://docs.oracle.com/javase/7/docs/api/java/lang/Double.html\">Double</a>:"}
                               "2.78"
                               "-1.2e-5"
                               {:latex "\\href{http://docs.oracle.com/javase/7/docs/api/java/math/BigDecimal.html}{BigDecimal}:"
                                :html "<a href=\"http://docs.oracle.com/javase/7/docs/api/java/math/BigDecimal.html\">BigDecimal</a>:"}
                               "4.2M"
                               ]]
           ["Arithmetic" :cmds '[+ - * / quot rem mod inc dec
                                 max min +' -' *' inc' dec']]
           ["Compare" :cmds '[= == not= < > <= >= compare]]
           ["Bitwise" :cmds '[[:common-prefix bit- and or xor not
                               flip set shift-right shift-left
                               and-not clear test]
                              "(1.6)" unsigned-bit-shift-right
                              "(see"
                              {:latex "\\href{http://docs.oracle.com/javase/7/docs/api/java/math/BigInteger.html}{BigInteger}"
                               :html "<a href=\"http://docs.oracle.com/javase/7/docs/api/java/math/BigInteger.html\">BigInteger</a>"}
                              " for integers larger than Long)"
                              ]]
           ["Cast" :cmds '[byte short int long float double
                           bigdec bigint num rationalize biginteger]]
           ["Test" :cmds-with-frenchspacing '[zero? pos? neg?
                                              even? odd?
                                              number? rational?
                                              integer? ratio?
                                              decimal? float?]]
           ["Random" :cmds '[rand rand-int]]
           ["BigDecimal" :cmds '[with-precision]]
           ;; TBD: Why do these not exist in Clojure?
           ;; There are -int versions, but not long
           ;; versions.  unchecked-divide
           ;; unchecked-remainder
           ["Unchecked" :cmds '[*unchecked-math*
                                [:common-prefix
                                 unchecked-
                                 add dec inc multiply negate
                                 subtract]]]]
   :subsection "Strings"
   :table [["Create" :cmds '[str format
                             {:latex "\\textmd{\\textsf{See also IO/to string}}",
                              :html "See also IO/to string"}]]
           ["Use" :cmds '[count get subs compare
                          {:latex "\\textmd{\\textsf{(clojure.string/)}}",
                           :html "(clojure.string/)"}
                          clojure.string/join clojure.string/escape
                          clojure.string/split clojure.string/split-lines
                          clojure.string/replace
                          clojure.string/replace-first
                          clojure.string/reverse
                          "(1.5)" clojure.string/re-quote-replacement
                          {:latex "(\\href{http://docs.oracle.com/javase/7/docs/api/java/lang/String.html}{String})"
                           :html "(<a href=\"http://docs.oracle.com/javase/7/docs/api/java/lang/String.html\">java.lang.String</a>)"}
                          {:latex "\\href{http://docs.oracle.com/javase/7/docs/api/java/lang/String.html\\#indexOf\\%28java.lang.String\\%29}{.indexOf}"
                           :html "<a href=\"http://docs.oracle.com/javase/7/docs/api/java/lang/String.html#indexOf%28java.lang.String%29\">.indexOf</a>"}
                          {:latex "\\href{http://docs.oracle.com/javase/7/docs/api/java/lang/String.html\\#lastIndexOf\\%28java.lang.String\\%29}{.lastIndexOf}"
                           :html "<a href=\"http://docs.oracle.com/javase/7/docs/api/java/lang/String.html#lastIndexOf%28java.lang.String%29\">.lastIndexOf</a>"}
                          ]]
           [
            ;;                       "Regex"
            {:latex "\\href{http://www.regular-expressions.info}{Regex}"
             :html "<a href=\"http://www.regular-expressions.info\">Regex</a>"}
            :cmds '[
                    ;;                                       {:latex "\\#\"pattern\"",
                    ;;                                        :html "#\"<var>pattern</var>\""}
                    {:latex "\\href{http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html}{\\#\"pattern\"}",
                     :html "<a href=\"http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html\">#\"<var>pattern</var>\"</a>"}
                    re-find re-seq re-matches
                    re-pattern re-matcher re-groups
                    {:latex "\\textmd{\\textsf{(clojure.string/)}}",
                     :html "(clojure.string/)"}
                    clojure.string/replace
                    clojure.string/replace-first
                    "(1.5)"
                    clojure.string/re-quote-replacement]]
           ["Letters" :cmds '[{:latex "\\textmd{\\textsf{(clojure.string/)}}",
                               :html "(clojure.string/)"}
                              clojure.string/capitalize
                              clojure.string/lower-case
                              clojure.string/upper-case]]
           ["Trim" :cmds '[{:latex "\\textmd{\\textsf{(clojure.string/)}}",
                            :html "(clojure.string/)"}
                           clojure.string/trim clojure.string/trim-newline
                           clojure.string/triml clojure.string/trimr]]
           ["Test" :cmds '[char char? string?
                           {:latex "\\textmd{\\textsf{(clojure.string/)}}",
                            :html "(clojure.string/)"}
                           clojure.string/blank?

                           {:latex "(\\href{http://docs.oracle.com/javase/7/docs/api/java/lang/String.html}{String})"
                            :html "(<a href=\"http://docs.oracle.com/javase/7/docs/api/java/lang/String.html\">java.lang.String</a>)"}
                           {:latex "\\href{http://docs.oracle.com/javase/7/docs/api/java/lang/String.html\\#startsWith\\%28java.lang.String\\%29}{.startsWith}"
                            :html "<a href=\"http://docs.oracle.com/javase/7/docs/api/java/lang/String.html#startsWith%28java.lang.String%29\">.startsWith</a>"}
                           {:latex "\\href{http://docs.oracle.com/javase/7/docs/api/java/lang/String.html\\#endsWith\\%28java.lang.String\\%29}{.endsWith}"
                            :html "<a href=\"http://docs.oracle.com/javase/7/docs/api/java/lang/String.html#endsWith%28java.lang.String%29\">.endsWith</a>"}
                           {:latex "\\href{http://docs.oracle.com/javase/7/docs/api/java/lang/String.html\\#contains\\%28java.lang.CharSequence\\%29}{.contains}"
                            :html "<a href=\"http://docs.oracle.com/javase/7/docs/api/java/lang/String.html#contains%28java.lang.CharSequence%29\">.contains</a>"}
                           ]]
           ]
   :subsection "Other"
   :table [["Characters" :cmds '[char char-name-string
                                 char-escape-string]]
           ["Keywords" :cmds '[keyword keyword? find-keyword]]
           ["Symbols" :cmds '[symbol symbol? gensym]]]])

(def clojure-collections
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
                                  ]]
           ["Content tests" :cmds '[distinct? empty?
                                    every? not-every? some not-any?]]
           ["Capabilities" :cmds '[sequential? associative? sorted?
                                   counted? reversible?]]
           ["Type tests" :cmds '[coll? list? vector? set? map?
                                 seq? "(1.6)" record?]]]
   :subsection "Lists"
   :table [["Create" :cmds '["'()" list list*]]
           ["Examine" :cmds-with-frenchspacing '[first nth peek
                                                 {:latex "\\href{http://docs.oracle.com/javase/7/docs/api/java/util/Vector.html\\#indexOf\\%28java.lang.Object\\%29}{.indexOf}"
                                                  :html "<a href=\"http://docs.oracle.com/javase/7/docs/api/java/util/Vector.html#indexOf%28java.lang.Object%29\">.indexOf</a>"}
                                                 {:latex "\\href{http://docs.oracle.com/javase/7/docs/api/java/util/Vector.html\\#lastIndexOf\\%28java.lang.Object\\%29}{.lastIndexOf}"
                                                  :html "<a href=\"http://docs.oracle.com/javase/7/docs/api/java/util/Vector.html#lastIndexOf%28java.lang.Object%29\">.lastIndexOf</a>"}
                                                 ]]
           [{:html "'Change'", :latex "`Change'"}
            :cmds '[cons conj rest pop]]
           ]
   :subsection "Vectors"
   :table [["Create" :cmds '["[]" vector vec vector-of]]
           ["Examine" :cmds '[{:latex "\\cmd{(my-vec idx)} $\\to$ \\cmd{(}",
                               :html "<code>(my-vec idx)</code> &rarr; <code>("}
                              nth
                              {:latex " \\cmd{my-vec idx)}",
                               :html " my-vec idx)</code>"}
                              get peek
                              {:latex "\\href{http://docs.oracle.com/javase/7/docs/api/java/util/Vector.html\\#indexOf\\%28java.lang.Object\\%29}{.indexOf}"
                               :html "<a href=\"http://docs.oracle.com/javase/7/docs/api/java/util/Vector.html#indexOf%28java.lang.Object%29\">.indexOf</a>"}
                              {:latex "\\href{http://docs.oracle.com/javase/7/docs/api/java/util/Vector.html\\#lastIndexOf\\%28java.lang.Object\\%29}{.lastIndexOf}"
                               :html "<a href=\"http://docs.oracle.com/javase/7/docs/api/java/util/Vector.html#lastIndexOf%28java.lang.Object%29\">.lastIndexOf</a>"}
                              ]]
           [{:html "'Change'", :latex "`Change'"}
            :cmds '[assoc pop subvec replace conj rseq]]
           ["Ops" :cmds '["(1.4)" mapv filterv reduce-kv]]]
   :subsection "Sets"
   :table [["Create" :cmds '[{:latex "\\#\\{\\}", :html "#{}"}
                             set hash-set sorted-set sorted-set-by]]
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
                    {:latex "\\textmd{\\textsf{See also Relations}}",
                     :html "See also Relations"}
                    ]]
           ["Test"
            :cmds '[{:latex "\\textmd{\\textsf{(clojure.set/)}}",
                     :html "(clojure.set/)"}
                    clojure.set/subset? clojure.set/superset?]]
           ]
   :subsection "Maps"
   :table [["Create"
            :cmds '[{:latex "\\{\\}", :html "{}"}
                    hash-map array-map zipmap
                    sorted-map sorted-map-by bean
                    frequencies group-by
                    {:latex "\\textmd{\\textsf{(clojure.set/)}}",
                     :html "(clojure.set/)"}
                    clojure.set/index
                    ]]
           ["Examine"
            :cmds '[
                    {:latex "\\cmd{(:key my-map)} $\\to$ \\cmd{(}",
                     :html "<code>(:key my-map)</code> &rarr; <code>("}
                    get
                    {:latex " \\cmd{my-map :key)}",
                     :html " my-map :key)</code>"}
                    get-in contains? find keys vals
                    ]]
           [{:html "'Change'", :latex "`Change'"}
            :cmds '[assoc assoc-in dissoc merge
                    merge-with select-keys update-in
                    {:latex "\\textmd{\\textsf{(clojure.set/)}}",
                     :html "(clojure.set/)"}
                    clojure.set/rename-keys
                    clojure.set/map-invert
                    {:latex "\\textmd{\\textsf{GitHub:}}",
                     :html "GitHub:"}
                    {:latex "\\href{http://github.com/weavejester/medley}{Medley}"
                     :html "<a href=\"http://github.com/weavejester/medley\">Medley</a>"}
                    ]]
           ["Entry" :cmds '[key val]]
           ["Sorted maps" :cmds '[rseq subseq rsubseq]]]])

(def clojure-relations
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
   :subsection {:latex "Transients (\\href{http://clojure.org/transients}{clojure.org/transients})"
                :html "Transients (<a href=\"http://clojure.org/transients\">clojure.org/transients</a>)"}
   :table [["Create" :cmds '[transient persistent!]]
           ["Change" :cmds-with-frenchspacing
            '[conj! pop! assoc! dissoc! disj!
              {:latex "\\textmd{\\textsf{Note: always use return value for later changes, never original!}}",
               :html "Note: always use return value for later changes, never original!"}]]]
   :subsection "Misc"
   :table [["Compare" :cmds '[= == identical? not= not compare
                              clojure.data/diff]]
           ["Test" :cmds '[true? false? instance? nil?
                           "(1.6)" some?]]]])

(def clojure-sequences
  [:box "orange"
   :section "Sequences"
   :subsection "Creating a Lazy Seq"
   :table [["From collection" :cmds '[seq vals keys rseq
                                      subseq rsubseq]]
           ["From producer fn" :cmds '[lazy-seq repeatedly iterate]]
           ["From constant" :cmds '[repeat range]]
           ["From other" :cmds '[file-seq line-seq resultset-seq
                                 re-seq tree-seq xml-seq
                                 iterator-seq enumeration-seq]]
           ["From seq" :cmds '[keep keep-indexed]]]
   :subsection "Seq in, Seq out"
   :table [["Get shorter" :cmds '[distinct filter remove
                                  take-nth for]]
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
                                     set vec into-array to-array-2d]]
           ["Pass to fn" :cmds '[apply]]
           ["Search" :cmds '[some filter]]
           ["Force evaluation" :cmds '[doseq dorun doall]]
           ["Check for forced" :cmds '[realized?]]]])

(def clojure-zippers
  [:box "green"
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
                           clojure.zip/branch? clojure.zip/end?]]]])

(def clojure-io
  [:box "magenta"
   :section "IO"
   :table [
                                        ;                      ["to/from ..."
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
                                {:latex "\\textmd{\\textsf{(clojure.tools.reader.edn/)}}",
                                 :html "(clojure.tools.reader.edn/)"}
                                clojure.tools.reader.edn/read
                                ]]
           ["from reader" :cmds '[line-seq
                                  {:latex "\\textmd{\\textsf{(clojure.tools.reader.edn/)}}",
                                   :html "(clojure.tools.reader.edn/)"}
                                  clojure.tools.reader.edn/read
                                  {:latex "\\textmd{\\textsf{also:}}",
                                   :html "also:"}
                                  "(binding [*in* reader] ...)"
                                  {:latex "\\href{http://docs.oracle.com/javase/7/docs/api/java/io/Reader.html}{java.io.Reader}"
                                   :html "<a href=\"http://docs.oracle.com/javase/7/docs/api/java/io/Reader.html\">java.io.Reader</a>"}
                                  ]]
           ["from string" :cmds '[
                                  with-in-str
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
                             {:latex "\\href{http://docs.oracle.com/javase/7/docs/api/java/io/OutputStream.html}{java.io.OutputStream}"
                              :html "<a href=\"http://docs.oracle.com/javase/7/docs/api/java/io/OutputStream.html\">java.io.OutputStream</a>"}
                                        ;                                        "java.io.InputStream"
                             {:latex "\\href{http://docs.oracle.com/javase/7/docs/api/java/io/InputStream.html}{java.io.InputStream}"
                              :html "<a href=\"http://docs.oracle.com/javase/7/docs/api/java/io/InputStream.html\">java.io.InputStream</a>"}
                             {:latex "\\textmd{\\textsf{GitHub:}}",
                              :html "GitHub:"}
                             {:latex "\\href{http://github.com/ztellman/gloss}{gloss}"
                              :html "<a href=\"http://github.com/ztellman/gloss\">gloss</a>"}
                             {:latex "\\href{http://github.com/rosejn/byte-spec}{byte-spec}"
                              :html "<a href=\"http://github.com/rosejn/byte-spec\">byte-spec</a>"}
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
                           {:latex "\\href{http://github.com/Raynes/fs}{fs}"
                            :html "<a href=\"http://github.com/Raynes/fs\">fs</a>"}
                           ]]
           ["Data readers" :cmds '["(1.4)" *data-readers*
                                   default-data-readers
                                   "(1.5)" *default-data-reader-fn*]]]])

(def clojure-functions
  [:box "blue"
   :section "Functions"
   :table [["Create" :cmds '[fn defn defn- definline identity
                             constantly memfn comp complement
                             partial juxt memoize fnil every-pred
                             some-fn]]
           ["Call" :cmds '[apply -> ->> trampoline
                           "(1.5)" as-> cond-> cond->>
                           some-> some->>]]
           ["Test" :cmds '[fn? ifn?]]]])

(def clojure-abstractions
  [:box "orange"
   :section {:latex "Abstractions (\\href{https://github.com/cemerick/clojure-type-selection-flowchart}{Clojure type selection flowchart})"
             :html "Abstractions (<a href=\"https://github.com/cemerick/clojure-type-selection-flowchart\">Clojure type selection flowchart</a>)"}
   :subsection {:latex "Protocols (\\href{http://clojure.org/protocols}{clojure.org/protocols})"
                :html "Protocols (<a href=\"http://clojure.org/protocols\">clojure.org/protocols</a>)"}
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
   :subsection {:latex "Records (\\href{http://clojure.org/datatypes}{clojure.org/datatypes})"
                :html "Records (<a href=\"http://clojure.org/datatypes\">clojure.org/datatypes</a>)"}
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
   :subsection {:latex "Types (\\href{http://clojure.org/datatypes}{clojure.org/datatypes})"
                :html "Types (<a href=\"http://clojure.org/datatypes\">clojure.org/datatypes</a>)"}
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
                                    :html "<code>Pair [h t]<br>&nbsp;&nbsp;Object<br>&nbsp;&nbsp;(toString [this] (str \"<\" h \",\" t \">\")))</code>"}
                                   ]]
           ]
   :subsection {:latex "Multimethods (\\href{http://clojure.org/multimethods}{clojure.org/multimethods})"
                :html "Multimethods (<a href=\"http://clojure.org/multimethods\">clojure.org/multimethods</a>)"}
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
           ["Relation" :cmds '[derive isa? parents ancestors
                               descendants make-hierarchy]]]])

(def clojure-macros
  [:box "green"
   :section "Macros"
   :table [["Create" :cmds '[defmacro definline]]
           ["Debug" :cmds '[macroexpand-1 macroexpand
                            {:latex "\\textmd{\\textsf{(clojure.walk/)}}",
                             :html "(clojure.walk/)"}
                            clojure.walk/macroexpand-all]]
           ["Branch" :cmds '[and or when when-not when-let
                             when-first if-not if-let cond condp
                             case "(1.6)" when-some if-some]]
           ["Loop" :cmds '[for doseq dotimes while]]
           ["Arrange" :cmds '[.. doto -> ->>
                              "(1.5)" as-> cond-> cond->>
                              some-> some->>]]
           ["Scope" :cmds '[binding locking time
                            [:common-prefix with-
                             in-str local-vars open out-str
                             precision redefs redefs-fn]]]
           ["Lazy" :cmds '[lazy-cat lazy-seq delay]]
           ["Doc." :cmds '[assert comment clojure.repl/doc]]]])

(def clojure-reader-macros
  [:box "yellow"
   :section "Reader Macros"
   ;; TBD: Should probably think of a good place for all of
   ;; the reader macro descriptions to link to.  A few
   ;; suggestions are given in comments below.
   :table [[{:latex "\\cmd{'}",
             :html "<code>'</code>"}
            ;; TBD: This should point to same URL that 'quote' does
            :str {:latex "Quote 'form $\\to$ (quote form)",
                  :html "Quote: <code>'<var>form</var></code> &rarr; <code>(quote <var>form</var>)</code>"}]
           [{:latex "\\cmd{\\textbackslash}",
             :html "<code>\\</code>"}
            :str "Character literal"]
           [{:latex "\\cmd{;}",
             :html "<code>;</code>"}
            :str "Single line comment"]
           [{:latex "\\cmd{\\^{}}",
             :html "<code>^</code>"}
            :str "Metadata (see Metadata section)"]
           [{:latex "\\cmd{@}",
             :html "<code>@</code>"}
            ;; TBD: This should point to same URL that 'deref' does
            :str {:latex "Deref @form $\\to$ (deref form)",
                  :html "Deref: <code>@<var>form</var></code> &rarr; <code>(deref <var>form</var>)</code>"}]
           [{:latex "\\cmd{`}",
             :html "<code>`</code>"}
            :str "Syntax-quote"]
           [{:latex "\\cmd{\\textasciitilde}",
             :html "<code>~</code>"}
            :str "Unquote"]
           [{:latex "\\cmd{\\textasciitilde@}",
             :html "<code>~@</code>"}
            :str "Unquote-splicing"]
           [{:latex "\\cmd{\\#\"}\\textit{p}\\cmd{\"}",
             :html "<code>#\"<var>p</var>\"</code>"}
            :str {:latex "Regex Pattern \\textit{p}",
                  :html "Regex Pattern <var>p</var>"}]
           [{:latex "\\cmd{\\#$'$}",
             :html "<code>#'</code>"}
            ;; TBD: This should point to same URL that 'var' does
            :str {:latex "Var quote \\#$'$x $\\to$ (var x)",
                  :html "Var quote: <code>#'<var>x</var></code> &rarr; <code>(var <var>x</var>)</code>"}]
           [{:latex "\\cmd{\\#()}",
             :html "<code>#()</code>"}
            ;; TBD: This should point to same URL that 'fn' does
            :str {:latex "\\#(...) $\\to$ (fn [args] (...))",
                  :html "<code>#(...)</code> &rarr; <code>(fn [args] (...))</code>"}]
           [{:latex "\\cmd{\\#\\_}",
             :html "<code>#_</code>"}
            :str "Ignore next form"]]])

(def clojure-metadata
  [:box "red"
   :section {:latex "Metadata (\\href{http://clojure.org/special\\_forms}{clojure.org/special\\_forms})"
             :html "Metadata (<a href=\"http://clojure.org/special_forms\">clojure.org/special_forms</a>)"}
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
           ]])

(def clojure-special-forms
  [:box "red"
   :section {:latex "Special Forms (\\href{http://clojure.org/special\\_forms}{clojure.org/special\\_forms})"
             :html "Special Forms (<a href=\"http://clojure.org/special_forms\">clojure.org/special_forms</a>)"}
   :cmds-one-line '[def if do let letfn quote var fn loop
                    recur throw try monitor-enter monitor-exit]
   :table [[{:latex "\\begin{tabular}[t]{@{}l@{}} Binding Forms / \\\\ Destructuring \\end{tabular}"
             :html "Binding Forms / Destructuring"}
            :cmds '[
                    {:latex "(\\href{http://clojure.org/special\\_forms\\#binding-forms}{examples})"
                     :html "(<a href=\"http://clojure.org/special_forms#binding-forms\">examples</a>)"}
                    let fn defn defmacro
                    loop for doseq if-let when-let
                    "(1.6)" if-some when-some]]]])

(def clojure-vars
  [:box "blue2"
   :section {:latex "Vars and global environment (\\href{http://clojure.org/vars}{clojure.org/vars})"
             :html "Vars and global environment (<a href=\"http://clojure.org/vars\">clojure.org/vars</a>)"}
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
           ]])

(def clojure-namespaces
  [:box "yellow"
   :section "Namespace"
   :table [["Current" :cmds '[*ns*]]
           ["Create/Switch" :cmds '[{:latex "(\\href{http://blog.8thlight.com/colin-jones/2010/12/05/clojure-libs-and-namespaces-require-use-import-and-ns.html}{tutorial})"
                                     :html "(<a href=\"http://blog.8thlight.com/colin-jones/2010/12/05/clojure-libs-and-namespaces-require-use-import-and-ns.html\">tutorial</a>)"}
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
                                  the-ns]]
           ["Remove" :cmds '[ns-unalias ns-unmap remove-ns]]]])

(def clojure-loading
  [:box "green"
   :section "Loading"
   :table [["Load libs" :cmds '[{:latex "(\\href{http://blog.8thlight.com/colin-jones/2010/12/05/clojure-libs-and-namespaces-require-use-import-and-ns.html}{tutorial})"
                                 :html "(<a href=\"http://blog.8thlight.com/colin-jones/2010/12/05/clojure-libs-and-namespaces-require-use-import-and-ns.html\">tutorial</a>)"}
                                require use import refer]]
           ["List loaded" :cmds '[loaded-libs]]
           ["Load misc" :cmds '[load load-file load-reader
                                load-string]]]])

(def clojure-concurrency
  [:box "magenta"
   :section "Concurrency"
   :table [["Atoms" :cmds '[atom swap! reset! compare-and-set!]]
           ["Futures" :cmds '[future
                              [:common-prefix future-
                               call done? cancel cancelled?]
                              future?]]
           ["Threads" :cmds '[bound-fn bound-fn*
                              [:common-suffix -thread-bindings
                               get push pop]
                              thread-bound?]]
           ["Misc" :cmds '[locking pcalls pvalues pmap seque
                           promise deliver]]]
   :subsection {:latex "Refs and Transactions (\\href{http://clojure.org/refs}{clojure.org/refs})"
                :html "Refs and Transactions (<a href=\"http://clojure.org/refs\">clojure.org/refs</a>)"}
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
   :subsection {:latex "Agents and Asynchronous Actions (\\href{http://clojure.org/agents}{clojure.org/agents})"
                :html "Agents and Asynchronous Actions (<a href=\"http://clojure.org/agents\">clojure.org/agents</a>)"}
   :table [["Create" :cmds '[agent]]
           ["Examine" :cmds '[agent-error]]
           ["Change state" :cmds '[send send-off restart-agent
                                   "(1.5)"
                                   send-via set-agent-send-executor!
                                   set-agent-send-off-executor!]]
           ["Block waiting" :cmds '[await await-for]]
           ["Ref validators" :cmds '[set-validator! get-validator]]
           ["Watchers" :cmds '[add-watch remove-watch]]
           ["Thread handling" :cmds '[shutdown-agents]]
           ["Error" :cmds '[error-handler set-error-handler!
                            error-mode set-error-mode!]]
           ["Misc" :cmds '[*agent* release-pending-sends]]]])

(def clojure-interop
  [:box "orange"
   :section {:latex "Java Interoperation (\\href{http://clojure.org/java\\_interop}{clojure.org/java\\_interop})"
             :html "Java Interoperation (<a href=\"http://clojure.org/java_interop\">clojure.org/java_interop</a>)"}
   :table [["General" :cmds '[.. doto "Classname/" "Classname."
                              new bean comparator enumeration-seq
                              import iterator-seq memfn set! class
                              class? bases supers type]]
           ["Cast" :cmds '[boolean byte short char int long
                           float double bigdec bigint num cast
                           biginteger]]
           ["Exceptions" :cmds '[throw try catch finally
                                 clojure.repl/pst
                                 "(1.4)" ex-info ex-data]]]
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
           ["Misc" :cmds '[proxy-mappings proxy-super update-proxy]]]])

(def clojure-other
  [:box "green2"
   :section "Other"
   :table [["XML" :cmds '[clojure.xml/parse xml-seq]]
           ["REPL" :cmds '[*1 *2 *3 *e *print-dup* *print-length*
                           *print-level* *print-meta*
                           *print-readably*]]
           ["Code" :cmds '[*compile-files* *compile-path* *file*
                           *warn-on-reflection* compile gen-class
                           gen-interface loaded-libs test]]
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
                    clojure.java.shell/with-sh-env]]]])

(def cheatsheet-structure
  [:title {:latex "Clojure Cheat Sheet (Clojure 1.3 - 1.6, sheet v17)"
           :html "Clojure Cheat Sheet (Clojure 1.3 - 1.6, sheet v17)"}
   :page [:column
          clojure-repl
          clojure-primitives
          clojure-collections
          :column
          clojure-relations
          clojure-sequences
          clojure-zippers
          clojure-io
          ]
   :page [:column
          clojure-functions
          clojure-abstractions
          clojure-macros
          clojure-reader-macros
          clojure-metadata
          :column
          clojure-special-forms
          clojure-vars
          clojure-namespaces
          clojure-loading
          clojure-concurrency
          clojure-interop
          clojure-other
          ]
   ])

