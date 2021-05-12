(ns vlagwt.handler
  ^{:author "Thomas Bock <thomas.bock@ptb.de>"
    :doc "Request handler."}
  (:require [vlagwt.db :as db] 
            [vlagwt.config :as c]
            [vlagwt.inquiry :as i]
            [vlagwt.dcc :as dcc]
            [vlagwt.score :as s]
            [vlagwt.utils :as u]))

(defn cal-req [req]
  (->> (u/req->req-id req)
       (db/cal-req) 
       (u/db-req->value)
       (s/result req)
       (s/id req)))

(defn all-req [req] (u/unique-req-id (db/cal-req :all)))

(defn dcc [req] (dcc/get-xml (db/dcc (u/req->req-id req))))

(defn todo [req] (u/db-req->key (db/todo :all)))

(defn pla-doc [req]
  (let [inq    (u/req->inq req)
        todos  (db/todo :all)
        result (i/check inq todos)]
    (if (:ok result)
      (i/pla-doc inq todos)
      result))) 

(defn save-pla-doc [req]
  (let [inq    (u/req->inq req)
        todos  (db/todo :all)
        result (i/check inq todos)]
    (if (:ok result)
      (if (i/mail-ok? (i/send-mail! (i/inq->mail-body inq)))
        (db/saved? (db/put-doc (i/pla-doc inq todos)))
        {:error "Failed notification Email"})
      result)))
