(ns app.core
  (:require [reagent.dom :as r]
            [reagent.core :as rc]
            [promesa.core :as p]
            ["react-dom/client" :as rdom]))

(defn result []
  (fn [rows show]
    (when show [:div
                [:table.table.table-bordered.table-success.table-striped.table-hover
                 [:tbody
                  (for [[header & times] rows]
                    [:tr.m-2.p-2 [:th {:scope "row"} header]
                     (map-indexed
                      (fn [i n] [:td.m-2.p-2 {:key i} (if (some? n) n "----")]) (first times))])]]])))

(defn summary []
  [:div.container-sm.p-2
   [:article "Welcome to Train Time, a little web app written in Clojure(Script).
            This helps you schedule a trip on New Mexico's Rail Runner commuter train,
            and helps me improve my Clojure skills, so thanks for checking it out."]
   #_[:p "Enter your travel info here:"]])

(defn app []
  (let [rows (rc/atom nil)
        show (rc/atom false)
        form (rc/atom "")
        fetch-results (fn [] (p/let [_resp (js/fetch "/api/data" (clj->js {:method "POST"
                                                                           :headers {:Content-Type "application/json"}
                                                                           :body (.stringify js/JSON #js {:data @form})}))
                                     resp (.json _resp)]
                               (swap! show not)
                               (reset! rows (js->clj resp))))]
    (fn [] [:div.container.p-2.bg-primary.bg-gradient.text-white
            [summary]
            [:input.m-2 {:type "text" :placeholder "Origin"}]
            [:input.m-2 {:type "text" :placeholder "Destination"}]
            [:input.m-2 {:type "text"
                         :placeholder "Departure time"
                         :on-change (fn [e] (reset! form (.. e -target -value)))}]
            [:button.m-2 {:on-click #(fetch-results)} "Submit"]
            [result @rows show]
            [:footer "Created by Ben Lieberman with Reagent"]])))

(defonce root (rdom/createRoot (.getElementById js/document "app"))) ;; for update to React 18

(defn ^:app/after-load render
  "Render the toplevel component for this app."
  []
  (.render root (rc/as-element [app])))

(defn init
  "Run application startup logic."
  []
  (render))

