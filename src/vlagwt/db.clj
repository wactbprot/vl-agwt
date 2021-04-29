(ns vlagwt.db
  (:require [vlagwt.config :as c]
            [com.ashafa.clutch :as couch]
            [clojure.string :as string]))

;;------------------------------
;; utils
;;------------------------------
(defn cal-req
  "Returns all docs belonging to a calibration request."
  ([id]
   (cal-req c/config id))
  ([{conn :read-db-conn d :db-req-design v :db-req-view} id]
   (if (= id :all)
     (couch/get-view conn d v)
     (couch/get-view conn d v {:key id}))))

(defn dcc
  "Returns all dcc belonging to a calibration request."
  ([id]
   (dcc c/config id))
  ([{conn :read-db-conn d :db-dcc-design v :db-dcc-view} id]
   (couch/get-view conn d v {:key id})))


(defn todo
  "Returns all dcc belonging to a calibration request."
  ([id]
   (todo c/config id))
  ([{conn :read-db-conn d :db-todo-design v :db-todo-view} id]
   (if (= id :all)
     (couch/get-view conn d v)
     (couch/get-view conn d v {:key id}))))

(defn saved? [m] m)

;;------------------------------
;; get doc
;;------------------------------
(defn get-doc
  "Gets a document from the db."
  ([id]
   (get-doc id c/config :read))
  ([id config op]
   (try
     (couch/get-document (condp = op
                           :read (:read-db-conn config)
                           :write (:write-db-conn config))
                         id)
     (catch Exception e {:error (.getMessage e) :doc-id id}))))

(defn exist? [id] (map? (get-doc id)))

;;------------------------------
;; refresh _rev means overwrite
;;------------------------------
(defn rev-refresh
  "Refreshs the revision `_rev` of the document if it exist."
  [doc]
  (if-let [db-doc (get-doc (:_id doc) c/config :write)]
    (assoc doc :_rev (:_rev db-doc))
    doc))
  
;;------------------------------
;; put doc
;;------------------------------
(defn put-doc
  "Puts a document to the long term memory."
  [doc]
  (try
    (couch/put-document (:write-db-conn c/config) (rev-refresh doc))
    (catch Exception e {:error (.getMessage e) :doc-id (:_id doc)})))

