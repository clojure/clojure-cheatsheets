(ns generator.core-test
  (:require [clojure.test :refer :all]
            [generator.core :refer :all]))


(deftest test-table-cmds-to-str
  (let [opts {:fmt :html
              :colors :color
              :symbol-name-to-url (hash-from-pairs
                                   (symbol-url-pairs :links-to-grimoire))
              :tooltips :no-tooltips
              :clojuredocs-snapshot {}
              }]
    (testing "String that is not a symbol.  Should pass through unchanged."
      (is (= (table-cmds-to-str opts "(1.6)")
             "(1.6)")))
    
    (testing "Single symbol that is a clojure.core var.  Gets a URL associated with it."
      (is (= (table-cmds-to-str opts 'bit-and)
             "<a href=\"http://grimoire.arrdem.com/1.6.0/clojure.core/bit_DASH_and\">bit-and</a>")))
    
    (testing "Several symbols with :common-prefix"
      (is (= (table-cmds-to-str opts '[:common-prefix bit- and or])
             (str "bit-"
                  "{"
                  "<a href=\"http://grimoire.arrdem.com/1.6.0/clojure.core/bit_DASH_and\">and</a>"
                  ", "
                  "<a href=\"http://grimoire.arrdem.com/1.6.0/clojure.core/bit_DASH_or\">or</a>"
                  "}"))))
    
    (testing "Several symbols with :common-suffix"
      (is (= (table-cmds-to-str
              opts '[:common-suffix -thread-bindings get push])
             (str "{"
                  "<a href=\"http://grimoire.arrdem.com/1.6.0/clojure.core/get_DASH_thread_DASH_bindings\">get</a>"
                  ", "
                  "<a href=\"http://grimoire.arrdem.com/1.6.0/clojure.core/push_DASH_thread_DASH_bindings\">push</a>"
                  "}"
                  "-thread-bindings"))))
    
    (testing "Several symbols with :common-prefix-suffix"
      (is (= (table-cmds-to-str
              opts '[:common-prefix-suffix unchecked- -int add dec])
             (str "unchecked-"
                  "{"
                  "<a href=\"http://grimoire.arrdem.com/1.6.0/clojure.core/unchecked_DASH_add_DASH_int\">add</a>"
                  ", "
                  "<a href=\"http://grimoire.arrdem.com/1.6.0/clojure.core/unchecked_DASH_dec_DASH_int\">dec</a>"
                  "}"
                  "-int"))))
    ))
