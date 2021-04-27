(ns vlagwt.utils
  ^{:author "Thomas Bock <thomas.bock@ptb.de>"
    :doc "Utils."}
  (:require [clojure.string :as string]))

(defn req->inq [req] (get-in req [:body]))

(defn req->req-id [req] (get-in req [:route-params :req-id]))

(defn db-req->value [db-req] (mapv :value db-req))

(defn db-req->key [db-req] (mapv :key db-req))

(defn number-of [v kw] (count (filter kw v)))

;; https://gist.github.com/hozumi/1472865
(defn sha1-str [s]
  (->> (-> "sha1"
           java.security.MessageDigest/getInstance
           (.digest (.getBytes s)))
       (map #(.substring
              (Integer/toString
               (+ (bit-and % 0xff) 0x100) 16) 1))
       (apply str)))


(defn unique-req-id [l] (group-by :key l))
