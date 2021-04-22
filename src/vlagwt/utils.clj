(ns vlagwt.utils
  ^{:author "Thomas Bock >thomas.bock@ptb.de>"
    :doc "Utils."}
  (:require [clojure.string :as string]))

(defn req->req-id [req] (get-in req [:route-params :req-id]))

(defn db-req->res-vec [db-req] (mapv :value db-req))

(defn score
  "
  NEU(10, Neu),
  EINGEREICHT(20, Eingereicht),
  MESSUNG(30, In Messung),
  ANALYSE(40, In Analyse),
  ZERTIFIKAT_ERTEILT(50, Zertifikat verfügbar),
  KALIBRIERUNG_FEHLER(51, Kalibrierung fehlerhaft),
  ZERTIFIKAT_ARCHIVIERT(60, Zertifikat archiviert);"
  [m]
  (assoc m :Score 
         (cond
           (:Planning m) 10
           (:Bureaucracy m) 20
           (:Measurement m) 30
           (:Analysis m) 35
           (:Result m) 40
           (:Certificate m) 50
           :else 0)))
  
(defn res-vec->score-vec [v] (mapv score v))
  
