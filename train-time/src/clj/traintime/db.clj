(ns traintime.db
  (:require [next.jdbc :as jdbc]
            [honey.sql :as sql]
            [honey.sql.helpers :refer [insert-into columns values create-table select]]
            [traintime.core]
            [traintime.env :as env]))

(def db {:host (env/env :host)
         :dbtype "mysql"
         :dbname (env/env :dbname)
         :user (env/env :user)
         :password (env/env :password)})

(def ds (jdbc/get-datasource db))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
#_(def create-time-table
  (jdbc/execute! ds
                 (sql/format {:create-table :arrivals
                              :with-columns
                              [[:train_id :int [:not nil]]
                               [:stations :json]]})))

#_(defn times-to-db [times]
  (let []
    (jdbc/execute! ds (-> (insert-into :arrivals)
                          (columns :train_id :stations)
                          (values [[]])))))

(defn data-input [begin end orig dest]
  (jdbc/execute! ds (-> (insert-into :travel_times)
                        (columns :begin :end :origin :destination)
                        (values [[begin end orig dest]])
                        (sql/format))))

#_(defn retrieve-travel-windows [])

(comment
  (jdbc/execute! ds ["select * from arrivals"])
  )
