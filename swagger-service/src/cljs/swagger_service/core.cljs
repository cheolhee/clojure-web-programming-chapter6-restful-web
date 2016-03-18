(ns swagger-service.core
  (:require [reagent.core :as r :refer [atom]]
            [ajax.core :refer [GET]])
  (:require-macros [secretary.core :refer [defroute]]))

(defn home-page [] (let [links
  (atom
    ["http://25.media.tumblr.com/Jjkybd3nSafemf3rYocB7QcC_500.jpg"
    "http://25.media.tumblr.com/tumblr_ln4zdhp4Uj1qcnzavo1_500.gif"
    "http://24.media.tumblr.com/tumblr_m2kmg2VK2a1qhwmnpo1_1280.jpg"
    "http://24.media.tumblr.com/tumblr_m30w1mNl1w1qgjltdo1_1280.jpg"
    "http://25.media.tumblr.com/tumblr_m3gm5oqm9e1r73wdao1_500.jpg"])]
    (fn []
      [:div
      (for [link @links]
        [:img {:src link}])]
        )))

(defn mount-components []
  (r/render-component [home-page] (.getElementById js/document "app")))


(defn init! []
  (mount-components))
