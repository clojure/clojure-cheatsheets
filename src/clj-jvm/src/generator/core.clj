(ns generator.core
  (:require [clojure.string :as str])
  (:require [clojure.set :as set])
  (:require [clojure.java.javadoc])
  (:require [clojure.java.io :as io])
  (:require [clojure.tools.reader.edn])
  (:require [clojure data pprint repl set string xml zip])
  (:require [clojure.core.reducers])
  (:require [grimoire.util :as g.u])
  (:require [generator.data :refer :all]))

;; Andy Fingerhut
;; andy_fingerhut@alum.wustl.edu
;; Feb 21, 2011

;; TBD: Fix the broken link for ->> in the generated PDF files.

;; TBD: For commands specified via strings rather than symbols, "@",
;; make a way to specify the desired target URL right in the
;; structure.  Maybe Clojure metadata would be a good way to do this,
;; since I expect it to be fairly uncommon?

;; TBD: Make an option to use a local file:///... root directory for
;; the URLs in links, in case someone wants to have a local copy on a
;; machine that is not always connected to the Internet, similar to
;; how the CLHS Hyperspec can be locally used?


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
;; You can optionally replace :cmds with :cmds-with-frenchspacing
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
    (binding [*read-eval* false]
      ;; TBD: Change read to clojure.tools.reader.edn after it is
      ;; updated to work with java.io.PushbackReader instances.
      (read r))))

(defn clojuredocs-url-fixup [s]
  (let [s (str/replace s "?" "_q")
        s (str/replace s "/" "_")
        s (str/replace s "." "_dot")]
    s))

(defn grimoire-url-fixup [s]
  (-> s g.u/munge (str "/")))

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

(def grimoire-base-url
  (str "http://conj.io/store/org.clojure/clojure/latest/"))

(defn symbol-url-pairs-for-whole-namespaces [link-target-site]
  (apply concat
         (map
          #(sym-to-url-list link-target-site %)
          [{:namespace-str "",
            :symbol-list '(def if do let quote var fn loop recur throw try
                            monitor-enter monitor-exit),
            :clojure-base-url "http://clojure.org/special_forms#",
            :clojuredocs-base-url "http://clojuredocs.org/clojure_core/clojure.core/"
            :grimoire-base-url    (str grimoire-base-url "clojure.core/")}
           {:namespace-str "",
            :symbol-list (keys (ns-publics 'clojure.core)),
            :clojure-base-url "http://clojure.github.com/clojure/clojure.core-api.html#clojure.core/",
            :clojuredocs-base-url "http://clojuredocs.org/clojure_core/clojure.core/"
            :grimoire-base-url (str grimoire-base-url "clojure.core/")}
           {:namespace-str "clojure.data"
            :symbol-list (keys (ns-publics 'clojure.data)),
            :clojure-base-url "http://clojure.github.com/clojure/clojure.data-api.html#clojure.data/",
            :clojuredocs-base-url "http://clojuredocs.org/clojure_core/clojure.data/"
            :grimoire-base-url (str grimoire-base-url "clojure.data/")}
           {:namespace-str "clojure.java.io"
            :symbol-list (keys (ns-publics 'clojure.java.io)),
            :clojure-base-url "http://clojure.github.com/clojure/clojure.java.io-api.html#clojure.java.io/",
            :clojuredocs-base-url "http://clojuredocs.org/clojure_core/clojure.java.io/"
            :grimoire-base-url (str grimoire-base-url "clojure.java.io/")}
           {:namespace-str "clojure.java.browse"
            :symbol-list (keys (ns-publics 'clojure.java.browse)),
            :clojure-base-url "http://clojure.github.com/clojure/clojure.java.browse-api.html#clojure.java.browse/",
            :clojuredocs-base-url "http://clojuredocs.org/clojure_core/clojure.java.browse/"
            :grimoire-base-url (str grimoire-base-url "clojure.java.browse/")}
           {:namespace-str "clojure.java.javadoc"
            :symbol-list (keys (ns-publics 'clojure.java.javadoc)),
            :clojure-base-url "http://clojure.github.com/clojure/clojure.java.javadoc-api.html#clojure.java.javadoc/",
            :clojuredocs-base-url "http://clojuredocs.org/clojure_core/clojure.java.javadoc/"
            :grimoire-base-url (str grimoire-base-url "clojure.java.javadoc/")}
           {:namespace-str "clojure.java.shell"
            :symbol-list (keys (ns-publics 'clojure.java.shell)),
            :clojure-base-url "http://clojure.github.com/clojure/clojure.java.shell-api.html#clojure.java.shell/",
            :clojuredocs-base-url "http://clojuredocs.org/clojure_core/clojure.java.shell/"
            :grimoire-base-url (str grimoire-base-url "clojure.java.shell/")}
           {:namespace-str "clojure.pprint"
            :symbol-list (keys (ns-publics 'clojure.pprint)),
            :clojure-base-url "http://clojure.github.com/clojure/clojure.pprint-api.html#clojure.pprint/",
            :clojuredocs-base-url "http://clojuredocs.org/clojure_core/clojure.pprint/"
            :grimoire-base-url (str grimoire-base-url "clojure.pprint/")}
           {:namespace-str "clojure.repl"
            :symbol-list (keys (ns-publics 'clojure.repl)),
            :clojure-base-url "http://clojure.github.com/clojure/clojure.repl-api.html#clojure.repl/",
            :clojuredocs-base-url "http://clojuredocs.org/clojure_core/clojure.repl/"
            :grimoire-base-url (str grimoire-base-url "clojure.repl/")}
           {:namespace-str "clojure.set"
            :symbol-list (keys (ns-publics 'clojure.set)),
            :clojure-base-url "http://clojure.github.com/clojure/clojure.set-api.html#clojure.set/",
            :clojuredocs-base-url "http://clojuredocs.org/clojure_core/clojure.set/"
            :grimoire-base-url (str grimoire-base-url "clojure.set/")}
           {:namespace-str "clojure.string"
            :symbol-list (keys (ns-publics 'clojure.string)),
            :clojure-base-url "http://clojure.github.com/clojure/clojure.string-api.html#clojure.string/",
            :clojuredocs-base-url "http://clojuredocs.org/clojure_core/clojure.string/"
            :grimoire-base-url (str grimoire-base-url "clojure.string/")}
           {:namespace-str "clojure.walk"
            :symbol-list (keys (ns-publics 'clojure.walk)),
            :clojure-base-url "http://clojure.github.com/clojure/clojure.walk-api.html#clojure.walk/",
            :clojuredocs-base-url "http://clojuredocs.org/clojure_core/clojure.walk/"
            :grimoire-base-url (str grimoire-base-url "clojure.walk/")}
           {:namespace-str "clojure.xml"
            :symbol-list (keys (ns-publics 'clojure.xml)),
            :clojure-base-url "http://clojure.github.com/clojure/clojure.xml-api.html#clojure.xml/",
            :clojuredocs-base-url "http://clojuredocs.org/clojure_core/clojure.xml/"
            :grimoire-base-url (str grimoire-base-url "clojure.xml/")}
           {:namespace-str "clojure.zip"
            :symbol-list (keys (ns-publics 'clojure.zip)),
            :clojure-base-url "http://clojure.github.com/clojure/clojure.zip-api.html#clojure.zip/",
            :clojuredocs-base-url "http://clojuredocs.org/clojure_core/clojure.zip/"
            :grimoire-base-url (str grimoire-base-url "clojure.zip/")}
           {:namespace-str "clojure.core.reducers"
            :symbol-list (keys (ns-publics 'clojure.core.reducers)),
            :clojure-base-url "http://clojure.github.com/clojure/clojure.core-api.html#clojure.core.reducers/",
            ;; There is no documentation for clojure.core.reducers
            ;; namespace on ClojureDocs.org as of March 2013, so just use
            ;; the same URL as above for now.
            :clojuredocs-base-url "http://clojure.github.com/clojure/clojure.core-api.html#clojure.core.reducers/"
            :grimoire-base-url (str grimoire-base-url "clojure.core.reducers/")}
           ])))

(defn symbol-url-pairs-specified-by-hand [link-target-site]
  (concat
   ;; Manually specify links for a few symbols in the cheatsheet.
   [["new" (case link-target-site
             :links-to-clojure
             "http://clojure.org/java_interop#new"
             :links-to-clojuredocs
             "http://clojuredocs.org/clojure_core/clojure.core/new"
             :links-to-grimoire
             ;; Grimoire doesn't have anything for some of these
             ;; symbols yet.
                                        ;(str grimoire-base-url "clojure.core/new")
             "http://clojure.org/java_interop#new"
             )]
    ["set!" (case link-target-site
              :links-to-clojure
              "http://clojure.org/java_interop#Java%20Interop-The%20Dot%20special%20form-%28set!%20%28.%20Classname-symbol%20staticFieldName-symbol%29%20expr%29"
              :links-to-clojuredocs
              "http://clojuredocs.org/clojure_core/clojure.core/set!"
              :links-to-grimoire
              (str grimoire-base-url "clojure.core/set!")
                                        ;"http://clojure.org/java_interop#Java%20Interop-The%20Dot%20special%20form-%28set!%20%28.%20Classname-symbol%20staticFieldName-symbol%29%20expr%29"
              )]
    ["catch" (case link-target-site
               :links-to-clojure
               "http://clojure.org/special_forms#try"
               :links-to-clojuredocs
               "http://clojuredocs.org/clojure_core/clojure.core/catch"
               :links-to-grimoire
                                        ;(str grimoire-base-url "clojure.core/catch")
               "http://clojure.org/special_forms#try"
               )]
    ["finally" (case link-target-site
                 :links-to-clojure
                 "http://clojure.org/special_forms#try"
                 :links-to-clojuredocs
                 "http://clojuredocs.org/clojure_core/clojure.core/finally"
                 :links-to-grimoire
                                        ;(str grimoire-base-url "clojure.core/finally")
                 "http://clojure.org/special_forms#try"
                 )]]
   (case link-target-site
     :links-to-clojure
     [["Classname." "http://clojure.org/java_interop#Java%20Interop-The%20Dot%20special%20form-%28new%20Classname%20args*%29"]
      ["Classname/" "http://clojure.org/java_interop#Java%20Interop-%28Classname/staticMethod%20args*%29"]]
     :links-to-clojuredocs
     ;; I don't have a good idea where on clojuredocs.org these
     ;; should link to, if anywhere.
     []
     :links-to-grimoire
     [])

   ;; These symbols do not have API docs anywhere that I can find,
   ;; yet.  Point at the github page for tools.reader for now.
   (map (fn [sym-str]
          [sym-str "http://github.com/clojure/tools.reader" ])
        [ "clojure.tools.reader.edn/read"
          "clojure.tools.reader.edn/read-string" ])
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
  % Comments, errors, suggestions: andy.fingerhut(at)gmail.com

  % Most of the content is based on the clojure wiki, api and source code by Rich Hickey on http://clojure.org/.

  % License
  % Eclipse Public License v1.0
  % http://opensource.org/licenses/eclipse-1.0.php

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
  \\newcommand{\\cmd}[1] {\\frenchspacing\\texttt{\\textbf{{#1}}}}
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
  (str "\\documentclass[footinclude=false,twocolumn,DIV40,fontsize=7.5pt]{scrreprt}\n"
       latex-header-except-documentclass))

;; US letter is a little shorter, so formatting gets completely messed
;; up unless we use a slightly smaller font size.
(def latex-usletter-header-before-title
  (str "\\documentclass[footinclude=false,twocolumn,DIV40,fontsize=7.2pt,letterpaper]{scrreprt}\n"
       latex-header-except-documentclass))

(def html-header-before-title "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"
  \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">

  <html xmlns=\"http://www.w3.org/1999/xhtml\">
  <head>
  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=us-ascii\" />
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

(defn htmlize-str [str]
  (let [str (str/replace str "<" "&lt;")
        str (str/replace str ">" "&gt;")]
    str))

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
     (str/replace ">" "\\%3E")))

(defn escape-latex-hyperref-target [target]
  (-> target
     ;; -> doesn't seem to have a problem in LaTeX, but ->> looks
     ;; -> like - followed by a special symbol that is two >'s
     ;; -> combined, not two separate characters.
     (str/replace "->>" "-{>}{>}")))

(defn has-prefix? [s pre]
  (and (>= (count s) (count pre))
     (= (subs s 0 (count pre)) pre)))

;; Only remove the namespaces that are very commonly used in the
;; cheatsheet.  For the ones that only have one or a few symbol there,
;; it seems best to leave the namespace in there explicitly.

(def +common-namespaces-to-remove-from-shown-symbols+
  ["clojure.java.browse/"
   "clojure.java.io/"
   "clojure.java.javadoc/"
   "clojure.java.shell/"
   "clojure.pprint/"
   "clojure.repl/"
   "clojure.set/"
   "clojure.string/"
   "clojure.tools.reader.edn/"
   "clojure.walk/"
   "clojure.zip/"
   ])

(defn remove-common-ns-prefix [s]
  (if-let [pre (->> +common-namespaces-to-remove-from-shown-symbols+
                  (filter #(has-prefix? s %))
                  first)]
    (subs s (count pre))
    s))

(defn cleanup-doc-str-tooltip
  "Get rid of the first line of the doc string, which is always a line
  of dashes, and keep at most the first 25 lines of the doc string, to
  keep the tooltip from being too large.  Also replace double quote
  characters (\") with &quot;"
  [s]
  (let [max-line-width 80
        lines          (-> s (str/split-lines) (rest))
        lines          (mapcat #(wrap-line % max-line-width) lines)
        max-to-keep    25
        combined-lines (if (> (count lines) max-to-keep)
                         (str (str/trim-newline (str/join "\n" (take max-to-keep lines)))
                              "\n\n[ documentation truncated.  Click link for the rest. ]")
                         (str/trim-newline (str/join "\n" lines)))]
    (str/escape combined-lines {\" "&quot;"})))

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
  (let [num-examples        (count (:examples sym-info))
        ;; remove lines containing nothing but <pre> or </pre> before
        ;; counting them, since clojuredocs.org doesn't show those as
        ;; separate lines.
        total-example-lines (->> sym-info
                               :examples
                               (map :body)
                               (mapcat str/split-lines)
                               (remove #(re-find #"(?i)^\s*<\s*/?\s*pre\s*>\s*$" %))
                               count)
        num-see-alsos       (count (:see-alsos sym-info))
        num-comments        (count (:comments sym-info))
        see-also-style      :list-see-alsos]
    (str (case num-examples
           0 "0 examples"
           1 (format "1 example with %d lines"
                     total-example-lines)
           (format "%d examples totaling %d lines"
                   num-examples total-example-lines))

         (when(and (= see-also-style :number-of-see-alsos)
                 (> num-see-alsos 0))
           (format ", %d see also%s" num-see-alsos
                   (if (== num-see-alsos 1) "" "s")))

         (when-not (zero? num-comments)
           (format ", %d comment%s" num-comments
                   (if (== num-comments 1) "" "s")))
         
         " on " snap-time

         (when (and (= see-also-style :list-see-alsos)
                  (> num-see-alsos 0))
           (as-> sym-info v
             (:see-also v)
             (map :name v)
             (str/join ", " v)
             (str "\nSee also: " v)
             (wrap-line v 72)
             (str/join "\n" v))))))

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
                        :cmds-with-frenchspacing "\\cmd{\\frenchspacing "
                        :cmds-one-line "\\cmdline{")
                      :html "<code>"
                      :verify-only ""))
      (iprintf "%s" (str/join " " (map #(table-cmds-to-str fmt %) cmds)))
      (iprintf "%s" (case (:fmt fmt)
                      :latex "}"
                      :html "</code>"
                      :verify-only "")))))

(defn output-table-row [fmt row row-num nrows]
  (verify (not= nil (#{:cmds :cmds-with-frenchspacing :str} (second row))))

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

(defn write-pre-title [fmt]
  (case (:fmt fmt)
    :latex
    ,,(case (:paper fmt)
        :a4
        ,,latex-a4-header-before-title

        :usletter
        ,,latex-usletter-header-before-title)

    :html
    ,,html-header-before-title

    :embeddable-html
    ,,embeddable-html-fragment-header-before-title

    :verify-only
    ,,""))

(defn write-post-title [fmt]
  (case (:fmt fmt)
    :latex
    ,,latex-header-after-title

    :html
    ,,html-header-after-title

    :embeddable-html
    ,,embeddable-html-fragment-header-after-title

    :verify-only
    ,,""))

(defn write-footer [fmt]
  (case (:fmt fmt)
    :latex
    ,,latex-footer

    :html
    ,,html-footer

    :embeddable-html
    ,,embeddable-html-fragment-footer

    :verify-only
    ,,""))

(defn output-cheatsheet [fmt cs]
  (let [[k title & pages]            cs
        [show-title fmt-passed-down] (if (= (:fmt fmt) :embeddable-html)
                                       [false (assoc fmt :fmt :html)]
                                       [true fmt])]
    (verify (= k :title))
    (verify (even? (count cs)))

    (iprintf "%s" (write-pre-title fmt))

    (when show-title
      (output-title fmt-passed-down title))

    (iprintf "%s" (write-post-title fmt))

    (doseq [[k pg] (partition 2 pages)]
      (verify (= k :page))
      (output-page fmt-passed-down pg)))

  (iprintf "%s" (write-footer fmt)))

(defn hash-from-pairs [pairs]
  (zipmap (map first pairs) (map second pairs)))

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

(defn -main [& args]
  (let [supported-link-targets #{"nolinks" "links-to-clojure" "links-to-clojuredocs" "links-to-grimoire"}

        supported-tooltips     #{"no-tooltips" "use-title-attribute" "tiptip"}

        link-target-site       (if (< (count args) 1)
                                 :links-to-clojure
                                 (let [arg (nth args 0)]
                                   (if (supported-link-targets arg)
                                     (keyword arg)
                                     (die "Unrecognized argument: %s\nSupported args are: %s\n"
                                          arg
                                          (str/join " " (seq supported-link-targets))))))

        tooltips               (if (< (count args) 2)
                                 :no-tooltips
                                 (let [arg (nth args 1)]
                                   (if (supported-tooltips arg)
                                     (keyword arg)
                                     (die "Unrecognized argument: %s\nSupported args are: %s\n"
                                          arg
                                          (str/join " " (seq supported-tooltips))))))

        clojuredocs-snapshot   (if (< (count args) 3)
                                 {}
                                 (let [snapshot-fname (nth args 2)]
                                   (simplify-snapshot-time
                                    (read-safely snapshot-fname))))

        _                      (if (not= clojuredocs-snapshot {})
                                 (iprintf *err* "Read info for %d symbols from file '%s' with time %s\n"
                                          (count (get clojuredocs-snapshot :snapshot-info))
                                          (nth args 2)
                                          (:snapshot-time clojuredocs-snapshot))
                                 (iprintf *err* "No clojuredocs snapshot file specified.\n"))

        symbol-name-to-url     (hash-from-pairs (symbol-url-pairs link-target-site))

        opts                   {:symbol-name-to-url symbol-name-to-url
                                :tooltips tooltips
                                :clojuredocs-snapshot clojuredocs-snapshot
                                :expand-common-prefixes-or-suffixes true}]

    (binding [*out* (io/writer "cheatsheet-full.html")
              *err* (io/writer "warnings.log")]
      (output-cheatsheet (merge opts {:fmt :html, :colors :color,
                                      :warn-about-unknown-symbols true})
                         cheatsheet-structure)
      ;; Print out a list of all symbols in our symbol-name-to-url
      ;; table that we never looked up.
      (let [never-used (set/difference
                        (set (keys symbol-name-to-url))
                        @symbols-looked-up)]
        (iprintf *err* "\n\n%d symbols successfully looked up.\n\n"
                 (count @symbols-looked-up))
        (iprintf *err* "\n\nSorted list of %d symbols in lookup table that were never used:\n\n"
                 (count never-used))
        (iprintf *err* "%s\n" (str/join "\n" (sort (seq never-used))))
        (iprintf *err* "\n\nSorted list of links to documentation for symbols that were never used:\n\n\n")
        (iprintf *err* "%s\n" (str/join "<br>"
                                        (map #(format "<a href=\"%s\">%s</a>\n"
                                                      (symbol-name-to-url %) %)
                                             (sort (seq never-used))))))
      (.close *out*)
      (.close *err*))
    
    (doseq [x [{:filename  "cheatsheet-embeddable.html",
                :structure cheatsheet-structure,
                :format    {:fmt :embeddable-html}}
               {:filename  "cheatsheet-a4-color.tex",
                :structure cheatsheet-structure,
                :format    {:fmt :latex, :paper :a4, :colors :color}}
               {:filename  "cheatsheet-a4-grey.tex",
                :structure cheatsheet-structure,
                :format    {:fmt :latex, :paper :a4, :colors :grey}}
               {:filename  "cheatsheet-a4-bw.tex",
                :structure cheatsheet-structure,
                :format    {:fmt :latex, :paper :a4, :colors :bw}}
               {:filename  "cheatsheet-usletter-color.tex",
                :structure cheatsheet-structure,
                :format    {:fmt :latex, :paper :usletter, :colors :color}}
               {:filename  "cheatsheet-usletter-grey.tex",
                :structure cheatsheet-structure,
                :format    {:fmt :latex, :paper :usletter, :colors :grey}}
               {:filename  "cheatsheet-usletter-bw.tex",
                :structure cheatsheet-structure,
                :format    {:fmt :latex, :paper :usletter, :colors :bw}}]]
      (binding [*out* (io/writer (:filename x))]
        (output-cheatsheet (merge opts (:format x))
                           (:structure x))
        (.close *out*)))))
