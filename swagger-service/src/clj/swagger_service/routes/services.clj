(ns swagger-service.routes.services
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            ; [compojure.api.swagger :refer [swagger-docs]] ; 추가
            [clj-http.client :as client]
            [clojure.java.io :as io]
            [clojure.xml :as xml]
            [schema.core :as s]))

(s/defschema Thingie {:id Long
                      :hot Boolean
                      :tag (s/enum :kikka :kukka)
                      :chief [{:name String
                               :type #{{:id String}}}]})

(defn get-first-child [tag xml-node]
(->> xml-node :content (filter #(= (:tag %) tag)) first))
(defn parse-link [link]
(->> link (get-first-child :url) :content first))
(defn parse-links [links] (->> links
       (get-first-child :data)
       (get-first-child :images)
       :content
       (map parse-link)))

(defn parse-xml [xml]
 (-> xml .getBytes io/input-stream xml/parse))

(defn get-links [link-count]
(-> "http://thecatapi.com/api/images/get?format=xml&results_per_page="
      (str link-count)
      client/get
      :body
      parse-xml
      parse-links))

(defapi service-routes
 ; 릴리즈 후 아래 map을 삭제하면 swagger-ui 가 나오지 않게 됩니다.
  {:swagger {:ui "/swagger-ui"
             :spec "/swagger.json"
             :data {:info {:version "1.0.0"
                           :title "Sample API"
                           :description "Sample Services"}}}}
  (context "/api" []
    :tags ["the Cat API"]
    (GET "/catlinks" []
    :query-params [link-count :- Long]
    ;  :query-params [link-count :- s/Num] ; 에러 "(not (instance? java.lang.Number \"2\"))"
    ;  :coercion #(-> % :params :link-count Integer/parseInt) ; 에러..
    ;  :body [body [s/Str]]
    :body [body [s/Str]]
    :return [s/Str]
    :summary "returns a collection of image links"
    (ok (get-links link-count)))
    ))
