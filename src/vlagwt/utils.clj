(ns vlagwt.utils
  ^{:author "Thomas Bock >thomas.bock@ptb.de>"
    :doc "Utils."}
  (:require [clojure.string :as string]))

(defn req->cus [req] (get-in req [:body :cus]))

(defn req->tdo [req] (get-in req [:body :todo]))

(defn req->pla-id [req] (get-in req [:route-params :pla-id]))
