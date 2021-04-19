(ns vlagwt.handler
  ^{:author "Thomas Bock >thomas.bock@ptb.de>"
    :doc "Request handler."}
  (:require [vlagwt.config :as c]
            [vlagwt.db :as db]))

(defn req->pla-id [req] (get-in req [:route-params :pla-id] "*"))

(defn pla->cer
  [config req]
  {:res (req->pla-id req)})
  
