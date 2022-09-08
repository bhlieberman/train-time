(ns core
  (:require [clojure.edn :refer [read-string]]
            [tick.core :as t]
            [tick.alpha.interval :as t.i]))

(def time-table (read-string (slurp "times.edn")))

(defn parse-times [val] (if-not (nil? val) (t/time val) nil))

(def time-objs
  (for [times (vals (get-in time-table [:weekday :southbound]))]
    (->> times
         (filter (complement nil?))
         (map parse-times)
         (sort-by <))))

(comment
  time-objs)