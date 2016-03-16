(ns swagger-service.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [swagger-service.layout :refer [error-page]]
            [swagger-service.routes.home :refer [home-routes]]
            [swagger-service.routes.services :refer [service-routes]]
            [compojure.route :as route]
            [swagger-service.middleware :as middleware]))

(def app-routes
  (routes
    #'service-routes
    (wrap-routes #'home-routes middleware/wrap-csrf)
    (route/not-found
      (:body
        (error-page {:status 404
                     :title "page not found"})))))

(def app (middleware/wrap-base #'app-routes))
