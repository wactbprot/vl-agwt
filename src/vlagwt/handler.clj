(ns vlagwt.handler
  ^{:author "Thomas Bock >thomas.bock@ptb.de>"
    :doc "Request handler."}
  (:require [vlagwt.db :as db]
            [vlagwt.score :as s]
            [vlagwt.utils :as u]))
  

(defn cal-req
  [config req]
  (->> (u/req->req-id req)
       (db/cal-req c/config)
       (u/db-req->res-vec)
       (s/result config req)
       (s/hash config req)))
