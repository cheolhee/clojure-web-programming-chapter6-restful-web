(ns swagger-service.core
  (:require [reagent.core :as r :refer [atom]]
            [ajax.core :refer [GET]])
  (:require-macros [secretary.core :refer [defroute]]))


(defn fetch-links! [links link-count]
  (GET "/api/cat-links"
    {:params {:link-count link-count}
    :handler #(reset! links (vec (partition-all 6 %)))
    })
  )

(defn images [links]
[:div.text-center
  (for [row (partition-all 3 links)]
    ^{:key row}
    [:div.row
      (for [link row]
        ^{:key link}
        [:div.col-sm-4 [:img {:width 400 :src link}]]
        )
      ]
    )
  ]
  )

(defn forward [i pages]
  (if (< i (dec pages)) (inc i) i))

(defn back [i]
  (if (pos? i) (dec i) i))

(defn nav-link [page i]
  [:li {:on-click #(reset! page i)
    :class (when (= i @page) "active")}
    [:span i]
    ]
  )
(defn pager [pages page]
  [:div.row.text-center
    [:div.col-sm-12
     [:ul.pagination.pagination-lg
      (concat
        [[:li
           {:on-click #(swap! page back pages)
             :class (when (= @page 0) "disabled")}
             [:span "<<"]
             ]
           ]

           (map (partial nav-link page) (range pages))
           [[:li
            {:on-click #(swap! page forward pages)
              :class (when (= @page (dec pages)) "disabled")
            }
            [:span ">>"]
           ]]
        )
      ]
     ]
    ]
  )

(defn home-page []
  (let [page (atom 0) links (atom nil)]
 (fetch-links! links 50)

 (fn []
 [:div
  [:hr]
   (if (not-empty @links)
   [:div [pager (count @links) page]
    [images (@links @page)]]
    [:div "Standby for cats!"])
  [:hr]
  ]
  )
    ))


(defn mount-components []
  (r/render-component [home-page] (.getElementById js/document "app")))


(defn init! []
  (mount-components))
