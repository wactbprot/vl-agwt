(ns vlagwt.handler
  ^{:author "Thomas Bock >thomas.bock@ptb.de>"
    :doc "Request handler."}
  (:require [vlagwt.config :as c]
            [vlagwt.db :as db]))

(defn req->cus [req] (get-in req [:body :cus]))
(defn req->tdo [req] (get-in req [:body :todo]))

(defn cal-req->pla
  [config req]
  (let [customer (req->cus req)]
    customer
    ))
  
