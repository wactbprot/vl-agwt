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
  (def conf c/config)
  (che/encode inq))

(defn inq->id [m] (get-in m [:CalibrationRequest :RequestId]))
(defn inq->date [m] (get-in m [:CalibrationRequest :Date]))
(defn inq->device [m] (get-in m [:CalibrationRequest :Device]))
(defn inq->mail-to [m] (get-in m [:CalibrationRequest :MailTo]))

(defn id-ok? [m]
  (when-let [d (inq->id m)]
    (when (re-find #"[0-9a-f]{5,40}" d) true)))

(defn date-ok? [m]
  (when-let [d (inq->date m)]
    (when (vector? d) true)))

(defn device-ok? [m]
  (when-let [d (inq->device m)]
    (when (vector? d) true)))

(defn mail-to-ok? [m]
  (when-let [d (inq->mail-to m)]
    (when (re-find #"^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$" d) true)))

(defn inq->doc-id [m]  (str "inq-" (inq->id m)))

(defn inq->mail-body
  ([inq]
   (inq->mail-body c/config inq)) 
  ([config m]
   {:from "vl-agwt@berlin.ptb.de"
    :to [(inq->mail-to m)]
    :subject "Kalibrieranfrage AGWT"
    :body "Eine Anfrage für eien Kalibrierung ..."}))

(defn send-mail!
  ([body]
   (send-mail! c/config body))
  ([{host :smtp-host-map} body]
   (m/send-message host body)))

(defn mail-ok? [m] (zero? (:code m)))