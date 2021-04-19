(ns vlagwt.handler
  ^{:author "Thomas Bock >thomas.bock@ptb.de>"
    :doc "Request handler."}
  (:require [vlagwt.config :as c]
            [vlagwt.db :as db]))

(defn req->cus [req] (get-in req [:body :cus]))

(defn req->tdo [req] (get-in req [:body :todo]))

(defn agwt-cus->vl-cus
  [config customer]
  customer)

(defn cal-req->pla
  [config req]
  (let [customer (agwt-cus->vl-cus config (req->cus req))]
    customer))
  
