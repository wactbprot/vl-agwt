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

(defn inq->customer [m] (get-in (inq->main m) [:Customer]))
  
(defn inq->customer-name [m] (get-in (inq->customer m) [:Name]))

(defn inq->customer-mail [m] (get-in (inq->customer m) [:Contact :Email]))

(defn inq->customer-sign [m] (get-in (inq->customer m) [:Sign]))

(defn inq->devices [m] (get-in (inq->main m) [:Device]))

(defn inq->comment [m] (not-empty (get-in (inq->main m) [:Comment])))
                         
(defn date-vec->desired-date [v] (:Value (first (filter #(= (keyword (:Type %)) :desired) v))))

(defn inq->doc-id [m]  (str "pla-" (inq->id m)))

;;----------------------------------------------------------
;; checks
;;----------------------------------------------------------
(defn id-ok? [m]
  (when-let [d (inq->id m)]
    (when (re-find #"[0-9a-f]{5,40}" d) true)))

(defn main-parts-ok? [m]
  (and (map? (not-empty (inq->customer m)))
       (vector? (not-empty (inq->device m))))) 

(defn device-amount-ok? [m]
  (when-let [d (inq->devices m)]
    (= (count d) (count (filter (comp integer? :Amount) d)))))
    
(defn date-ok? [m]
  (when-let [d (date-vec->desired-date (not-empty (inq->date m)))]
    (when  (re-find #"^\d{4}-([0]\d|1[0-2])-([0-2]\d|3[01])$" d) true)))

(defn customer-mail-ok? [m]
  (when-let [d (inq->customer-mail m)]
    (when (re-find #"^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$" d) true)))

(defn customer-sign-ok? [m]
  (when-let [d (inq->customer-sign m)]
    (when (re-find #"^[A-Z0-9\_\.]{2,15}$" d) true)))


;;----------------------------------------------------------
;; notification mail
;;----------------------------------------------------------
(defn inq->mail-body
  ([inq]
   (inq->mail-body c/config inq)) 
  ([config m]
   (let [to      (inq->mail-to m)
         cust    (inq->customer-name m)
         comment (inq->comment m)]
     {:from    "vl-proxy@berlin.ptb.de" ;; -> conf
      :to      (:notif-mail config)
      :subject (str "Kalibrieranfrage AGWT " cust)
      :body    (str "Eine Anfrage für eine Kalibrierung des Kunden:\n   "
                    cust "\nhat uns über die AnGeWaNt Schnittstelle erreicht."
                    (when comment
                      (str " Die Anfrage wurde mit folgendem Kommentar versehen:\n   " comment)))})))

(defn send-mail!
  ([body]
   (send-mail! c/config body))
  ([{host :smtp-host-map} body]
   (m/send-message host body)))

(defn mail-ok? [m] (zero? (:code m)))
