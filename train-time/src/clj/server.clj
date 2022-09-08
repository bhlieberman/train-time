(ns server
  (:require [ring.adapter.jetty :as jetty]
            [reitit.ring :as ring]
            [ring.util.response :as r]
            [clojure.java.io :as io]))

(defn index []
  (slurp (io/resource "public/index.html")))

(def app
  (ring/ring-handler
   (ring/router
    ["/" {:handler (fn [_req] {:body (index) :status 200})}])))

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
  (index)
  (start-server)
  (stop-server))