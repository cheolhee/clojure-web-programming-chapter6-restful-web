(ns swagger-service.core
  (:require [reagent.core :as r :refer [atom]]
            [ajax.core :refer [GET]])
  (:require-macros [secretary.core :refer [defroute]])
  )


; cat-link API호출.
; 이미지를 링크를 6개씩 나눠서 links(ATOM)에 저장한다.
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

; 다음 페이지 번호
(defn forward [i pages]
  (if (< i (dec pages)) (inc i) i))
; 이전 페이지 번호
(defn back [i]
  (if (pos? i) (dec i) i))

;  page == i 이면  class="active"
(defn nav-link [page i]
  [:li.page-item>a.page-link.btn.btn-primary ; 그냥 li 만 하면 버튼으로 표시되지 않는다.
  {:on-click #(reset! page i) :class (when (= i @page) "active")}
    [:span i]])

; 페이지 버튼 div 생성
(defn pager [pages page] ; 전체페이지 현재페이지(atom)
  [:div.row
   [:div.col-sm-12
    [:div.text-xs-center
     [:ul.pagination.pagination-lg
      (concat
        [[:li.page-item>a.page-link.btn  ; 그냥 li 만 하면 버튼으로 표시되지 않는다.
           {:on-click #(swap! page back pages)
             :class (when (= @page 0) "disabled")}
             [:span "<<"]]]
        (map (partial nav-link page) (range pages))
        [[:li.page-item>a.page-link.btn
           {:on-click #(swap! page forward pages)
             :class (when (= @page (dec pages)) "disabled")}
             [:span ">>"]]]
        )]]]])

(defn home-page []
  (let [page (atom 0) links (atom nil)]
    (fetch-links! links 50)
    (fn []
     (if (not-empty @links)
      [:div
         [pager (count @links) page]
         [images (@links @page)]]
      [:div "Standby for cats!"
        ])
    )))

(defn mount-components []
  (r/render-component [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-components))
