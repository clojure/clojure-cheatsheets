(require '[generator.core :as g]
         '[clojure.data.json :as json]
         '[clojure.string :as str]
         '[clojure.java.io :as io])

(pprint (ns-publics 'clojure.data.json))
(doc json/read-json)
(doc sorted-set)

(pst)

(def fname1 "clojuredocs-snapshot.edn")
(def d1 (g/read-safely fname1))

(def fname5 "clojuredocs-export.json")
(def d5 (json/read-json (io/reader fname5)))

(:created-at d5)
(def java-date (java.util.Date. (:created-at d5)))
java-date
;; This is the format that :snapshot-time is in clojuredocs-snapshot.edn
;; {:snapshot-time "Fri Aug 29 06:53:55 PDT 2014",
(.getDay java-date)
(.getMonth java-date)
(.getHours java-date)
(.toGMTString java-date)
;; "20 Nov 2018 23:55:16 GMT"
(.toLocaleString java-date)
;; "Nov 20, 2018, 3:55:16 PM"
(.toString java-date)
;; "Tue Nov 20 15:55:16 PST 2018"
(def java-inst (.toInstant java-date))
java-inst
;; #object[java.time.Instant 0x20d059cb "2018-11-20T23:55:16.890Z"]
(def utc-zoneoffset java.time.ZoneOffset/UTC)
utc-zoneoffset

(def java-zoneddatetime (.atZone java-inst utc-zoneoffset))
(.toString java-zoneddatetime)
(def formatter (java.time.format.DateTimeFormatter/ofPattern "EEE MMM dd HH:mm:ss yyyy"))
(.format java-zoneddatetime formatter)

(java.time.ZoneOffset/of "-8")


(let [formatter (java.time.format.DateTimeFormatter/ofPattern
                 "EEE MMM dd HH:mm:ss zzz yyyy")]
  (defn epoch-millis->utc-time-date-string [epoch-millis]
    (let [d (java.util.Date. epoch-millis)
          inst (.toInstant d)
          zoned-time (.atZone inst java.time.ZoneOffset/UTC)]
    (.format zoned-time formatter))))


(def snap-time (epoch-millis->utc-time-date-string (:created-at d5)))
snap-time
(def snap-time2 (g/simplify-time-str snap-time))
snap-time2

(def sym-info (update-in (nth (:vars d5) 3)
                         [:see-alsos] add-see-also-names))

(g/clojuredocs-content-summary snap-time2 sym-info)

(def d5b (g/read-clojuredocs-export-json fname5))



1542758116890
(epoch-millis->utc-time-date-string 1289040035000)

(re-find #"^(\S+ \S+ \d+)\s+.*\s+(\d+)$" s1)

(defn remove-uninteresting-keys [x]
  (dissoc x :added :file :line :column
          :macro :special-form :static :dynamic
          :arglists :forms :deprecated :tag :type :url))

(def d5b (assoc d5 :vars (mapv remove-uninteresting-keys (:vars d5))))
(frequencies (map (fn [x] (apply sorted-set (keys x))) (:vars d5b)))

(type (first (:vars d5)))
(keys (first (:vars d5)))
(keys (dissoc (first (:vars d5)) :href :arglists))
(keys (remove-uninteresting-keys (first (:vars d5))))

(frequencies (map (fn [x] (:type x)) (:vars d5)))
(frequencies (map (fn [x] (type (:column x))) (:vars d5b)))
;; {"var" 146, "function" 1065, "macro" 185}


(keys d5)
(frequencies (map (fn [x] (type (:column x))) (:vars d5b)))

(def fname2 "export.edn")
(def fname3 "export.compact.edn")
(def fname4 "export.compact.min.edn")
(def d2 (g/read-safely fname2))
(def d3 (g/read-safely fname3))
(def d4 (g/read-safely fname4))
(= d3 d4)
;; true

(count d1)
(keys d1)
(def d2 (-> d1 :snapshot-info))
(type d2)
;; map
(count d2)

(frequencies (map type (keys d2)))
;; {java.lang.String 3574}

(frequencies (map type (vals d2)))
;;  {clojure.lang.PersistentArrayMap 3574}

(frequencies (map (fn [m] (-> m keys set)) (vals d2)))
;; {#{:ns :name :comments :see-alsos :examples :id :url} 3574}

(frequencies (map (fn [m] (-> m :ns type)) (vals d2)))
;; {java.lang.String 3574}

(frequencies (map (fn [m] (-> m :name type)) (vals d2)))
;; {java.lang.String 3574}

(frequencies (map (fn [m] (-> m :comments type)) (vals d2)))
;; {clojure.lang.PersistentVector 3444, clojure.lang.PersistentList 130}

(def nec (filter #(not (empty? %)) (map (fn [m] (-> m :comments)) (vals d2))))
(count nec)

(pprint (take 3 nec))
(frequencies (map count nec))
