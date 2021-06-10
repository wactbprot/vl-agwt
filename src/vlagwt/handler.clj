(ns vlagwt.handler
  ^{:author "Thomas Bock <thomas.bock@ptb.de>"
    :doc "Request handler."}
  (:require [vlagwt.db :as db] 
            [vlagwt.config :as c]
            [vlagwt.inquiry :as i]
            [vlagwt.dcc :as dcc]
            [ring.util.response :as res]
            [vlagwt.score :as s]
            [vlagwt.utils :as u]))

(defn not-found [] (res/response {:ok false :error "not found"}))

(defn raw-cal-req [req]
  (->> (u/req->req-id req)
       (db/cal-req) 
       (u/db-req->value)
       (s/result req)
       (s/id req)))

(defn cal-req [req] (res/response (raw-cal-req req)))

(defn all-req [req] (u/unique-req-id (db/cal-req :all)))

(defn dcc [req] (res/response (dcc/get-xml (db/dcc (u/req->req-id req)))))

(defn todo [req] (res/response (u/db-req->key (db/todo :all))))

(defn planning [req]
  (let [v (u/db-req->value (db/planning (u/req->req-id req)))]
    (if (empty? v)
      (res/status (res/response {:error "Not found" :ok false}) 404)
      (res/response (first v)))))

(defn pla-doc [req]
  (let [inq    (u/req->inq req)
        todos  (db/todo :all)
        result (i/check inq todos)]
    (if (contains? result :ok)
      (res/response (i/pla-doc inq todos))
      (res/status (res/response result) 400)))) 

(defn save-pla-doc [req]
  (let [inq    (u/req->inq req)
        todos  (db/todo :all)
        result (i/check inq todos)]
    (if (contains? result :ok)
      (if (i/mail-ok? (i/send-mail! (i/inq->mail-body inq)))
        (if (db/saved? (db/put-doc (i/pla-doc inq todos)))
          (res/response {:ok true :error nil})
          (res/status (res/response {:ok false :error "Failed database put"}) 500))
        (res/status (res/response {:ok false :error "Failed notification Email"}) 500))
      (res/status (res/response (assoc result :ok false)) 400))))
