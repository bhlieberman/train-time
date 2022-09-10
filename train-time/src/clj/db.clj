(ns db
  (:require [next.jdbc :as jdbc]
            [honey.sql :as sql]
            [honey.sql.helpers :refer [insert-into columns values create-table]]
            [core]
            [env]))

(def db {:host (env/env :host)
         :dbtype "mysql"
         :dbname (env/env :dbname)
         :user (env/env :user)
         :password (env/env :password)})

(def ds (jdbc/get-datasource db))

(defn data-input [begin end orig dest]
  (jdbc/execute! ds (-> (insert-into :travel_times)
                        (columns :begin :end :origin :destination)
                        (values [[begin end orig dest]])
                        (sql/format))))

#_(defn retrieve-travel-windows [])

(comment
  (jdbc/execute! ds ["create table train_times (
                      train_id integer primary key not null,
                      station varchar(50),
                      arr_time time
  )"])
  (data-input "00:00" "23:00" "Las Lomas" "Bernalillo County")
  (jdbc/execute! ds ["select * from train_times"]))
