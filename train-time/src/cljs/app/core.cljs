(ns app.core
  (:require [reagent.dom :as r]
            ["react-dom/client" :as rdom]
            [promesa.core :as p]))

(defn app []
  [:div
   [:p "Enter your travel info here:"]
   [:input {:type "text" :placeholder "Origin"}]
   [:input {:type "text" :placeholder "Destination"}]
   [:input {:type "text" :placeholder "Departure time"}]])

(defn ^:app/after-load render
  "Render the toplevel component for this app."
  []
  (r/render [app] (.getElementById js/document "app")))

(defn init
  "Run application startup logic."
  []
  (render))