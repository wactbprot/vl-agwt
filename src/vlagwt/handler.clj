(ns vlagwt.handler
  ^{:author "Thomas Bock <thomas.bock@ptb.de>"
    :doc "Request handler."}
  (:require [vlagwt.db :as db]
            [vlagwt.config :as c]
            [postal.core :as m]
            [vlagwt.planning :as p]
            [vlagwt.score :as s]
            [vlagwt.utils :as u]))

(defn cal-req
  [config req]
  (->> (u/req->req-id req)
       (db/cal-req c/config)
       (u/db-req->res-vec)
       (s/result c/config req)
       (s/hash c/config req)))

(defn pla-doc
  [config req]
  (let[cal-req (u/req->cal-req req)]
    (if-let [err-msg (:error (p/check c/config ))]
      {:error err-msg}
      (let [report (m/send-message (:smtp-host-map c/config)
                                   (p/cal-req->mail c/config cal-req))]
        (if (zero? (:code report))
          {:ok true :next "convert and save pla doc"}
          report)))))
