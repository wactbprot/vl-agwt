(ns vlagwt.dcc
  (:require [vlagwt.config :as c]
            [org.httpkit.client :as http]))

(defn res-map->url
  ([m]
   (res-map->url c/config m))
  ([{conn :db-conn} m]
   (str conn "/" (:id m) "/" (:value m))))

(defn get-xml [v] (pmap #(:body @(http/get (res-map->url %))) v))
 
