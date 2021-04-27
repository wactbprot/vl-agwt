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
    (couch/get-document (:db-conn c/config) id)
    (catch Exception e {:error (.getMessage e) :doc-id id})))

;;------------------------------
;; put doc
;;------------------------------
(defn put-doc
  "Puts a document to the long term memory."
  [doc]
  (try
    (couch/put-document (:db-conn c/config) doc)
    (catch Exception e {:error (.getMessage e) :doc-id (:_id doc)})))

;;------------------------------
;; utils
;;------------------------------
(defn rev-refresh
  "Refreshs the revision `_rev` of the document if it exist."
  [doc]
  (if-let [db-doc (get-doc (:_id doc))]
    (assoc doc :_rev (:_rev db-doc))
    doc))

(defn cal-req
  "Returns all docs belonging to a calibration request."
  ([id]
   (cal-req c/config id))
  ([{conn :db-conn d :db-req-design v :db-req-view} id]
   (if (= id :all)
     (couch/get-view conn d v)
     (couch/get-view conn d v {:key id}))))

(defn dcc
  "Returns all dcc belonging to a calibration request."
  ([id]
   (dcc c/config id))
  ([{conn :db-conn d :db-dcc-design v :db-dcc-view} id]
   (couch/get-view conn d v {:key id})))


(defn todo
  "Returns all dcc belonging to a calibration request."
  ([id]
   (todo c/config id))
  ([{conn :db-conn d :db-todo-design v :db-todo-view} id]
   (prn d)
   (if (= id :all)
     (couch/get-view conn d v)
     (couch/get-view conn d v {:key id}))))

(defn exist? [id] (map? (get-doc id)))
