(ns vlagwt.server
  ^{:author "Thomas Bock <thomas.bock@ptb.de>"
    :doc "Server start & stop. Routing."}
  (:require [compojure.route :as route]
            [compojure.core :refer :all]
            [compojure.handler :as handler]
            [org.httpkit.server :refer [run-server]]
            [ring.middleware.json :as middleware]
            [ring.util.response :as res]
            [vlagwt.config :as c]
            [vlagwt.handler :as h]
            [vlagwt.view :as v])
  (:use [clojure.repl])
  (:gen-class))

(defonce server (atom nil))

(defroutes app-routes
  (GET "/status/ui/:req-id"  [req-id :as req] (v/index    req (h/raw-cal-req  req)))
  (GET "/status/ui"          [:as req]        (v/index    req (h/all-req  req)))
  
  (GET "/status/raw/:req-id" [req-id :as req] (h/cal-req  req))
  (GET "/dcc/:req-id"        [req-id :as req] (h/dcc      req))
  (GET "/planning/:req-id"   [req-id :as req] (h/planning req))
  (GET "/todo"               [:as req]        (h/todo     req))
  (POST "/convert"           [:as req]        (h/pla-doc  req))
  (POST "/request"           [:as req]        (h/save-pla-doc req))

  (route/resources "/")
  (route/not-found  (h/not-found)))

(def app
  (-> (handler/site app-routes)
      (middleware/wrap-json-body {:keywords? true})
       middleware/wrap-json-response))

(defn stop [] (when @server (@server :timeout 100)))

(defn start [] (reset! server (run-server #'app (:server c/config))))

(defn -main [& args] (start))
