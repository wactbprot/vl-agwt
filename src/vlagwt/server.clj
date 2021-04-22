(ns vlagwt.server
  ^{:author "Thomas Bock >thomas.bock@ptb.de>"
    :doc "Server start stop. Routing."}
  (:require [compojure.route :as route]
            [compojure.core :refer :all]
            [compojure.handler :as handler]
            [org.httpkit.server :refer [run-server]]
            [ring.middleware.json :as middleware]
            [ring.util.response :as res]
            [vlagwt.config :as c]
            [vlagwt.db :as db]
            [vlagwt.handler :as h]
            [vlagwt.utils :as u]
            [vlagwt.view :as v])
  (:use   [clojure.repl])
  (:gen-class))

(defonce server (atom nil))

(defroutes app-routes
  (GET "/cal-req/:req-id" [req-id :as req] (->> req-id
                                                (db/cal-req c/config)
                                                (u/db-req->res-vec)
                                                (v/index c/config req)))
  (route/resources "/")
  (route/not-found (res/response {:error "not found"})))

(def app
  (-> (handler/site app-routes)
      (middleware/wrap-json-body {:keywords? true})
      middleware/wrap-json-response))

(defn stop [] (when @server (@server :timeout 100)))

(defn start [] (reset! server (run-server #'app (:server c/config))))
