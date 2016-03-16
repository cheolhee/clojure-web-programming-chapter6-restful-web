(ns swagger-service.routes.services
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            ; [compojure.api.swagger :refer [swagger-docs]] ; 추가
            [clj-http.client :as client]
            [schema.core :as s]))

(s/defschema Thingie {:id Long
                      :hot Boolean
                      :tag (s/enum :kikka :kukka)
                      :chief [{:name String
                               :type #{{:id String}}}]})


(defn get-links [link-count]
  (client/get
    (str
      "http://thecatapi.com/api/images/get?format=xml&results_per_page="
      link-count)))


(defapi service-routes
 ; (ring.swagger.ui/swagger-ui
 ;   "/swagger-ui")
 ; (swagger-docs
 ;    {:info {:title "Clojure-kr RESTful api"}})
 ; 릴리즈 후 아래 map을 삭제하면 swagger-ui 가 나오지 않게 됩니다.
  {:swagger {:ui "/swagger-ui"
             :spec "/swagger.json"
             :data {:info {:version "1.0.0"
                           :title "Sample API"
                           :description "Sample Services"}}}}
  (context "/api" []
    :tags ["thingie"]

    (GET "/plus" []
      :return       Long
      :query-params [x :- Long, {y :- Long 1}]
      :summary      "x+y with query-parameters. y defaults to 1."
      (ok (+ x y)))

    (POST "/minus" []
      :return      Long
      :body-params [x :- Long, y :- Long]
      :summary     "x-y with body-parameters."
      (ok (- x y)))

    (GET "/times/:x/:y" []
      :return      Long
      :path-params [x :- Long, y :- Long]
      :summary     "x*y with path-parameters"
      (ok (* x y)))

    (POST "/divide" []
      :return      Double
      :form-params [x :- Long, y :- Long]
      :summary     "x/y with form-parameters"
      (ok (/ x y)))

    (GET "/power" []
      :return      Long
      :header-params [x :- Long, y :- Long]
      :summary     "x^y with header-parameters"
      (ok (long (Math/pow x y))))

    (PUT "/echo" []
      :return   [{:hot Boolean}]
      :body     [body [{:hot Boolean}]]
      :summary  "echoes a vector of anonymous hotties"
      (ok body))

    (POST "/echo" []
      :return   (s/maybe Thingie)
      :body     [thingie (s/maybe Thingie)]
      :summary  "echoes a Thingie from json-body"
      (ok thingie)))

  (context "/context" []
    :tags ["context"]
    :summary "summary inherited from context"
    (context "/:kikka" []
      :path-params [kikka :- s/Str]
      :query-params [kukka :- s/Str]
      (GET "/:kakka" []
        :path-params [kakka :- s/Str]
        (ok {:kikka kikka
             :kukka kukka
             :kakka kakka})))))
