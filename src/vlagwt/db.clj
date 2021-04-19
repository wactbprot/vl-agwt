(ns vlagwt.db
  (:require [vlagwt.config :as c]
            [com.ashafa.clutch :as couch]
            [clojure.string :as string]))

;;------------------------------
;; get doc
;;------------------------------
(defn get-doc
  "Gets a document from the db."
  [id]
  (try
    (couch/get-document (:ltmem-conn c/config) id)
    (catch Exception e {:error (.getMessage e) :doc-id id})))

;;------------------------------
;; put doc
;;------------------------------
(defn put-doc
  "Puts a document to the long term memory."
  [doc]
  (try
    (couch/put-document (:ltmem-conn c/config) doc)
    (catch Exception e {:error (.getMessage e) :doc-id (:_id doc)})))

;;------------------------------
;; utils
;;------------------------------
(defn exist?
  "Returns `true` if a document with the `id` exists.

  TODO: HEAD request not entire doc

  Example:
  ```clojure
  (exist? \"foo-bar\")
  ;; =>
  ;; false
  ```"
  [id]
  (map? (get-doc id)))

(defn rev-refresh
  "Refreshs the revision `_rev` of the document if it exist."
  [doc]
  (if-let [db-doc (get-doc (:_id doc))]
    (assoc doc :_rev (:_rev db-doc))
    doc))
