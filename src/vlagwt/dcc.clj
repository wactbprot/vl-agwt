(ns vlagwt.dcc
  (:require [vlagwt.config :as c]
            [org.httpkit.client :as http]))

(defn res-map->url
  ([m]
   (res-map->url c/config m))
  ([{conn :read-db-conn} {id :id value :value}]
   (str conn "/" id "/" value)))

(defn get-xml [v] (pmap #(:body @(http/get (res-map->url %))) v))
 
