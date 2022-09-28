(ns app.core
  (:require [reagent.dom :as r]
            [reagent.core :as rc]
            [promesa.core :as p]
            #_["react-dom/client" :as rdom]))

(defn result []
  (fn [rows show]
    (when show [:div
                [:table.table.table-bordered.table-success.table-striped.table-hover
                 [:tbody
                  (for [row rows]
                    [:tr.m-2.p-2 (map-indexed
                                  (fn [i n] [:td.m-2.p-2 {:key i} (if (some? n) n "----")]) row)])]]])))

(defn summary []
  [:div.container-sm.p-2 
   [:article "Welcome to Train Time, a little web app written in Clojure(Script).
            This helps you schedule a trip on New Mexico's Rail Runner commuter train,
            and helps me improve my Clojure skills, so thanks for checking it out."]
   #_[:p "Enter your travel info here:"]])

(defn app []
  (let [rows (rc/atom nil)
        show (rc/atom false)
        fetch-results (fn [] (p/let [_resp (js/fetch "/api/data")
                                     resp (.json _resp)]
                               (swap! show not)
                               (reset! rows resp)))]
    (fn [] [:div.container.p-2.bg-primary.bg-gradient.text-white
            [summary]
            [:input.m-2 {:type "text" :placeholder "Origin"}]
            [:input.m-2 {:type "text" :placeholder "Destination"}]
            [:input.m-2 {:type "text" :placeholder "Departure time"}]
            [:button.m-2 {:on-click #(fetch-results)} "Submit"]
            [result @rows show]
            [:footer "Created by Ben Lieberman with Reagent"]])))

#_(defonce root (rdom/createRoot (.getElementById js/document "app"))) ;; for update to React 18

(defn ^:app/after-load render
  "Render the toplevel component for this app."
  []
  (r/render [app] (.getElementById js/document "app")))

(defn init
  "Run application startup logic."
  []
  (render))