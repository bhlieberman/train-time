(ns traintime.core
  (:require [clojure.edn :refer [read-string]]
            [tick.core :as t]
            [tick.alpha.interval :as t.i]
            [clojure.data.json :as json]))

(def time-table (read-string (slurp "times.edn")))

(def weekdays (let [days ["MONDAY" "TUESDAY" "WEDNESDAY" "THURSDAY" "FRIDAY"]]
                (map t/day-of-week days)))

(def weekend (let [days ["SATURDAY" "SUNDAY"]]
               (map t/day-of-week days)))

(defn parse-times [val] (if (some? val) (t/time val) nil))

(defn time-seqs [day travel-dir]
  (let [day (keyword day)
        dir (keyword travel-dir)]
    (vals (get-in time-table [day dir]))))

(defn time-objs [day travel-dir]
  (for [times (time-seqs day travel-dir)]
    (->> times
         (filter some?)
         (map parse-times)
         (sort-by <))))

(defn times-to-go [day travel-dir]
  (let [day (cond (some #{(t/day-of-week day)} weekdays) "weekday"
                  (some #{(t/day-of-week day)} weekend) "weekend")]
    (time-objs day travel-dir)))

(def time-durations
  (for [station (time-objs "weekday" "southbound")
        :let [begin (first station)
              end (last station)]]
    (t.i/new-interval begin end)))

(def times (-> time-table
               (get-in [:weekday :southbound])
               vec
               json/write-str))

(defn departure-time [time-str]
  (every?
   #(t.i/contains? % (t/time time-str)) time-durations))

(comment
  (t.i/contains? (first time-durations) (t/time "13:00"))
  (departure-time "13:00")
  (:tick/beginning (first time-durations))
  #(t/< (t/time %) (t/time "13:00"))
  ;;;;;;;;;;;;;;;;;; 
  (->> (get-in
        time-table [:weekday :southbound :kewa])
       (filter some?)
       (map (fn [t] (when (t/< (t/time t) (t/time "13:00")) t))))
  (let [[header & times] (-> (get-in time-table [:weekday :southbound])
            vec
            first)]
    times))
