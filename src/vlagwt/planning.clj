(ns vlagwt.planning
  (:require [vlagwt.config :as c]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [cheshire.core :as che]
            [vlagwt.utils :as u]))

(comment
  (def pla-doc (-> (io/resource "calibration-request.edn")
                   slurp
                   edn/read-string))

  (def conf c/config)
  
  (che/encode pla-doc))

(defn id-ok? [m]
  (if-let [d (get-in m [:CalibrationRequest :RequestId])]
    (if (re-find #"[0-9a-f]{5,40}" d) 
      (assoc m :ok true)
      (assoc m :error "RequestId don't match regex"))
    (assoc m :error "missing RequestId")))

(defn date-ok? [m]
  (if-let [d (get-in m [:CalibrationRequest :Date])]
    (if (vector? d)
      (assoc m :ok true)
      (assoc m :error "date is not a vector"))
    (assoc m :error "missing date")))

(defn device-ok? [m]
  (if-let [d (get-in m [:CalibrationRequest :Device])]
    (if (vector? d)
      (assoc m :ok true)
      (assoc m :error "device is not a vector"))
    (assoc m :error "missing device")))

(defn mail-to-ok? [m]
  (if-let [d (get-in m [:CalibrationRequest :MailTo])]
    (if (re-find #"^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$" d)
      (assoc m :ok true)
      (assoc m :error "device is not a vector"))
    (assoc m :error "missing device")))

(defn check [config pla-doc] (-> pla-doc id-ok? date-ok? device-ok? mail-to-ok?))

(defn cal-req->mail
  [config pla-doc]
  {:host (:smtp-host config)
   :from "vl-agwt@berlin.ptb.de"
   :to [(get-in pla-doc [:CalibrationRequest :MailTo]) ]
   :subject "Kalibrieranfrage AGWT"
   :body "Eine Anfrage für eien Kalibrierung ..."})
