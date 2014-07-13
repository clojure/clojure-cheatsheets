(ns generator.core-test
  (:require [clojure.test :refer :all]
            [generator.core :refer :all]))


(deftest test-table-cmds-to-str
  (doseq [expand? [false true]]
    (let [most-opts {:fmt :html
                     :colors :color
                     :symbol-name-to-url (hash-from-pairs
                                          (symbol-url-pairs :links-to-grimoire))
                     :tooltips :no-tooltips
                     :clojuredocs-snapshot {}
                     }
          opts (merge most-opts
                      {:expand-common-prefixes-or-suffixes expand?})]

      (testing (str "expand? " expand? " String that is not a symbol.  Should pass through unchanged.")
        (is (= (table-cmds-to-str opts "(1.6)")
               "(1.6)")))
    
      (testing (str "expand? " expand? " Single symbol that is a clojure.core var.  Gets a URL associated with it.")
        (is (= (table-cmds-to-str opts 'bit-and)
               "<a href=\"http://grimoire.arrdem.com/1.6.0/clojure.core/bit_DASH_and\">bit-and</a>")))
    
      (testing (str "expand? " expand? " Several symbols with :common-prefix")
        (is (= (table-cmds-to-str opts '[:common-prefix bit- and or])
               (if expand?
                 (str "<a href=\"http://grimoire.arrdem.com/1.6.0/clojure.core/bit_DASH_and\">bit-and</a>"
                      " "
                      "<a href=\"http://grimoire.arrdem.com/1.6.0/clojure.core/bit_DASH_or\">bit-or</a>")
                 (str "bit-"
                      "{"
                      "<a href=\"http://grimoire.arrdem.com/1.6.0/clojure.core/bit_DASH_and\">and</a>"
                      ", "
                      "<a href=\"http://grimoire.arrdem.com/1.6.0/clojure.core/bit_DASH_or\">or</a>"
                      "}")))))
      
      (testing (str "expand? " expand? " Several symbols with :common-suffix")
        (is (= (table-cmds-to-str
                opts '[:common-suffix -thread-bindings get push])
               (if expand?
                 (str "<a href=\"http://grimoire.arrdem.com/1.6.0/clojure.core/get_DASH_thread_DASH_bindings\">get-thread-bindings</a>"
                      " "
                      "<a href=\"http://grimoire.arrdem.com/1.6.0/clojure.core/push_DASH_thread_DASH_bindings\">push-thread-bindings</a>")
                 (str "{"
                      "<a href=\"http://grimoire.arrdem.com/1.6.0/clojure.core/get_DASH_thread_DASH_bindings\">get</a>"
                      ", "
                      "<a href=\"http://grimoire.arrdem.com/1.6.0/clojure.core/push_DASH_thread_DASH_bindings\">push</a>"
                      "}"
                      "-thread-bindings")))))
      
      (testing (str "expand? " expand? " Several symbols with :common-prefix-suffix")
        (is (= (table-cmds-to-str
                opts '[:common-prefix-suffix unchecked- -int add dec])
               (if expand?
                 (str "<a href=\"http://grimoire.arrdem.com/1.6.0/clojure.core/unchecked_DASH_add_DASH_int\">unchecked-add-int</a>"
                      " "
                      "<a href=\"http://grimoire.arrdem.com/1.6.0/clojure.core/unchecked_DASH_dec_DASH_int\">unchecked-dec-int</a>")
                 (str "unchecked-"
                      "{"
                      "<a href=\"http://grimoire.arrdem.com/1.6.0/clojure.core/unchecked_DASH_add_DASH_int\">add</a>"
                      ", "
                      "<a href=\"http://grimoire.arrdem.com/1.6.0/clojure.core/unchecked_DASH_dec_DASH_int\">dec</a>"
                      "}"
                      "-int")))))
      )))
