(ns backend-tests.server
  (:require [clojure.test :refer [deftest is run-tests testing use-fixtures]]
            [clojure.java.shell :refer [sh]]
            [clojure.edn :refer [read-string]]
            [ring.util.response :refer [response]]
            [traintime.server :refer [start-server stop-server]]))

(deftest connection-test
  (let [curl (-> (sh "curl" "http://localhost:3001")
                 :out
                 read-string
                 response
                 :status)]
    (testing "That the Ring server's endpoints are working as expected"
      (is (= 200 (:status (response {:body "test" :status 200}))))
      (is (= 200 curl)))))

(defn test-server-conn [f]
  (start-server)
  (f)
  (stop-server))

(use-fixtures :once test-server-conn)

(run-tests)

