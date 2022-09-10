(ns server
  #_{:clj-kondo/ignore [:unused-namespace]}
  (:require [ring.adapter.jetty :as jetty]
            [reitit.ring :as ring]
            [compojure.core :as comp]
            [compojure.route :as route]
            [ring.util.response :as r]
            [clojure.java.io :as io]
            [clojure.java.javadoc :refer [javadoc]]
            [clojure.repl :refer [doc]]))

(defn index []
  (slurp (io/resource "public/index.html")))

(comp/defroutes routes
  (comp/GET "/" [] {:status 200
                    :body (index)}))

(def app (fn [req] (routes req)))

(defonce server (atom nil))

(defn start-server []
  (swap! server
         assoc
         :jetty
         (jetty/run-jetty (fn [req] (app req))
                          {:port 3001
                           :join? false})))

(defn stop-server []
  (when-some [s @server]
    (.stop (:jetty s))
    (reset! server nil)))

(comment 
  (start-server)
  (stop-server)
  )