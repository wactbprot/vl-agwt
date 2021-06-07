(ns vlagwt.config
  (:require [clojure.edn     :as edn]
            [clojure.java.io :as io]
            [clojure.string  :as string]))

(defn get-config
  "Reads a `edn` configuration in file `f`."
  ([]
   (get-config (io/resource "config.edn")))
  ([f]
   (-> f slurp edn/read-string)))

(defn db-conn
  ([c]
   (db-conn c :read))
  ([c op]
  (let [lt-srv (System/getenv "CMP_LT_SRV")
        usr    (System/getenv "CAL_USR")
        pwd    (System/getenv "CAL_PWD")]
    (str (:db-prot c) "://"
         (when (and usr pwd) (str usr ":" pwd "@"))
         (or lt-srv (:db-srv c)) ":"
         (:db-port c) "/"
         (condp = op
           :read (:read-db c)
           :write (:write-db c))))))

(def config
    (let [c (get-config)]
      (assoc c
             :read-db-conn (db-conn c :read)
             :write-db-conn (db-conn c :write)
             :smtp-host-map {:host (:smtp-host c)})))
