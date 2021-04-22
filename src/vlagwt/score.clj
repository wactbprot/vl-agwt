(ns vlagwt.score
  ^{:author "Thomas Bock <thomas.bock@ptb.de>"
    :doc "Utils."}
  (:require [clojure.string :as string]
            [vlagwt.utils :as u]))

(defn number-of-calib [v]
  (:Calibrations  (first (filter  :Calibrations v))))

(defn all-certs-ready? [v] (= (u/number-of v :Certificate) (number-of-calib v)))

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
  (cond
    (:Planning m) 10
    (:Bureaucracy m) 20
    (:Measurement m) 30
    (:Analysis m) 40
    (:Result m) 40
    (:Certificate m) 45
    :else 0))

(defn check-certs [v] (if (all-certs-ready? v) 50 45))

(defn max-score [v]
 
  (let [i (apply max (mapv score v))]
    (if (> 60 i 40) (check-certs v) i)))

(defn result [config req v]
  (assoc (into {} v) :Score (max-score v)))

(defn hash [config req m]
  (assoc m :RequestId (u/sha1-str (u/req->req-id req))))
  
