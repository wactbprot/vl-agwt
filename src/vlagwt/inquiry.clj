(ns vlagwt.inquiry
  (:require [vlagwt.config :as c]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [postal.core :as m]
            [cheshire.core :as che]
            [vlagwt.utils :as u]))

(comment
  (def inq (-> (io/resource "calibration-request.edn")
                   slurp
                   edn/read-string))
  
  (def inq (che/decode
            (-> (io/resource "calibration-request.json")
                   slurp) true))


  (def conf c/config)
  (che/encode inq))

;;----------------------------------------------------------
;; pathes
;;----------------------------------------------------------
(defn inq->main [m] (:CalibrationRequest m))

(defn inq->id [m] (get-in (inq->main m) [ :RequestId]))

(defn inq->date [m] (get-in (inq->main m) [:Date]))

(defn inq->device [m] (get-in (inq->main m) [:Device]))

(defn inq->mail-to [m] (get-in (inq->main m) [:MailTo]))

(defn inq->customer-name [m] (get-in (inq->main m) [:Customer :Name]))

(defn inq->customer-mail [m] (get-in (inq->main m) [:Customer :Contact :Email]))

(defn inq->devices [m] (get-in (inq->main m) [:Device]))

;;----------------------------------------------------------
;; checks
;;----------------------------------------------------------
(defn id-ok? [m]
  (when-let [d (inq->id m)]
    (when (re-find #"[0-9a-f]{5,40}" d) true)))

(defn device-amount-ok? [m]
  (when-let [d (inq->devices m)]
    (= (count d) (count (filter (comp integer? :Amount) d)))))
    
(defn date-ok? [m]
  (when-let [d (inq->date m)]
    (when (vector? d) true)))

(defn device-ok? [m]
  (when-let [d (inq->device m)]
    (when (vector? d) true)))

(defn customer-mail-ok? [m]
  (when-let [d (inq->customer-mail m)]
    (when (re-find #"^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$" d) true)))

(defn inq->doc-id [m]  (str "inq-" (inq->id m)))

;;----------------------------------------------------------
;; notification mail
;;----------------------------------------------------------
(defn inq->mail-body
  ([inq]
   (inq->mail-body c/config inq)) 
  ([config m]
   (let [to   (inq->mail-to m)
         cust (inq->customer-name m)]
   {:from    "vl-agwt@berlin.ptb.de" ;; -> conf
    :to      (:notif-mail config)
    :subject (str "Kalibrieranfrage AGWT " cust)
    :body    (str "Eine Anfrage für eine Kalibrierung von "
                  cust)})))

(defn send-mail!
  ([body]
   (send-mail! c/config body))
  ([{host :smtp-host-map} body]
   (m/send-message host body)))

(defn mail-ok? [m] (zero? (:code m)))
