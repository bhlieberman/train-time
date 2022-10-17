(ns traintime.server
  #_{:clj-kondo/ignore [:unused-namespace]}
  (:require [ring.adapter.jetty :as jetty]
            [reitit.ring :as ring]
            [ring.util.response :as r]
            [clojure.java.io :as io]
            [muuntaja.core :as m]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [traintime.core :refer [times]]))

(defn index []
  (slurp (io/resource "public/index.html")))

(defn post-handler [_req]
  {:body times :status 200})

(def app
  (ring/ring-handler
   (ring/router
    ["/"
     ["api/"
      ["data" {:post post-handler}]]
     ["assets/*" (ring/create-resource-handler {:root "public/assets"})]
     ["" {:handler (fn [_req] {:body (index) :status 200})}]]
    {:data {:muuntaja m/instance
            :middleware [muuntaja/format-middleware]}})))

(defonce server (atom nil))

(defn start-server []
  (swap! server
         assoc
         :jetty
         (jetty/run-jetty #'app
                          {:port 3001
                           :join? false})))

(defn stop-server []
  (when-some [s @server]
    (.stop (:jetty s))
    (reset! server nil)))

(comment
  (stop-server)
  (start-server)
  )