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

(defn save-pla-doc [req]
  (let [inq  (u/req->inq req)
        id   (i/inq->doc-id inq)
        mail (i/inq->mail-body inq)]
    (cond
      (db/exist? id) {:error "Document alredy exist."}
      (not (i/id-ok? inq)) {:error "Malformed or missing RequestId"}
      (not (i/date-ok? inq)) {:error "Wrong or missing Date section"}
      (not (i/device-ok? inq)) {:error "Wrong or missing Device section"}
      (not (i/mail-to-ok? inq)) {:error "Malformed or missing MailTo"}
      (not (i/mail-ok? (i/send-mail! mail))) {:error "Failed notification Email"}
      #_(not (db/saved? pla-doc))
      :success {:ok true})))
