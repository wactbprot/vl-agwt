(ns vlagwt.handler
  ^{:author "Thomas Bock <thomas.bock@ptb.de>"
    :doc "Request handler."}
  (:require [vlagwt.db :as db]
            [vlagwt.config :as c]
            [vlagwt.inquiry :as i]
            [vlagwt.score :as s]
            [vlagwt.utils :as u]))

(defn cal-req [req]
  (->> (u/req->req-id req)
       (db/cal-req)
       (u/db-req->res-vec)
       (s/result req)
       (s/id req)))

(defn save-request-doc [req]
  (let [inq  (u/req->inq req)
        id   (i/inq->doc-id inq)
        mail (i/inq->mail-body inq)]
    (and
      (not (db/exist? id))
      (i/data-ok? inq)
      (i/mail-ok? (i/send-mail! mail)))))
