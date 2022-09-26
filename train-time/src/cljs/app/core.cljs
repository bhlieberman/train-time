(ns app.core
  (:require [reagent.dom :as r]
            [reagent.core :as rc]
            [promesa.core :as p]))

(defn result []
  (fn [rows show]
    (when show [:div
                [:table
                 (into [:tbody] (for [_ rows]
                                  [:tr (map (fn [n] [:td {:key n} n]) (range 10))]))]])))

(defn app []
  (let [rows (rc/atom nil)
        show (rc/atom true)
        fetch-results (fn [] (swap! show not)
                        (if (false? @show) (reset! rows nil) (reset! rows (repeat 9 [])))) #_(fn [] (reset! rows (repeat 9 []))
                                                                                               (swap! show not) #_(p/let [_resp (js/fetch "/api")
                                                                                                                          resp (.json _resp)
                                                                                                                          data (js->clj resp)]
                                                                                                                    (reset! rows data)
                                                                                                                    (swap! show not)))]
    (fn [] [:div.container
            [:p "Enter your travel info here:"]
            [:input {:type "text" :placeholder "Origin"}]
            [:input {:type "text" :placeholder "Destination"}]
            [:input {:type "text" :placeholder "Departure time"}]
            [:button {:on-click #(fetch-results)} "Submit"]
            [result @rows show]])))

(defn ^:app/after-load render
  "Render the toplevel component for this app."
  []
  (r/render [app] (.getElementById js/document "app")))

(defn init
  "Run application startup logic."
  []
  (render))