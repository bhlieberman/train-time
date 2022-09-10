(ns core
  (:require [clojure.edn :refer [read-string]]
            [tick.core :as t]
            [tick.alpha.interval :as t.i]))

(def time-table (read-string (slurp "times.edn")))

(def weekdays (let [days ["MONDAY" "TUESDAY" "WEDNESDAY" "THURSDAY" "FRIDAY"]]
                (map t/day-of-week days)))

(def weekend (let [days ["SATURDAY" "SUNDAY"]]
               (map t/day-of-week days)))

(defn parse-times [val] (if-not (nil? val) (t/time val) nil))

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
  (for [station time-objs
        :let [begin (first station)
              end (last station)]]
    (t.i/new-interval begin end)))

(def times (vals (get-in time-table [:weekday :southbound])))

(comment
  (times-to-go "monday" "southbound")
  ;;;;;;;;;;
  (->> (-> "times.edn"
           slurp
           read-string
           (get-in [:weekday :southbound])
           vals
           flatten)
       (partition 15)) 
  ;;;;;;;;;;
  weekdays
  weekend
  time-objs
  )