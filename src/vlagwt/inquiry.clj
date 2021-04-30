(ns vlagwt.inquiry
  (:require [vlagwt.config :as c]
            [cheshire.core :as che]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [postal.core :as m]
            [clojure.string :as string]
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
;; fixes
;;----------------------------------------------------------
(defn fix-gender [customer]
  (let [p [:Contact :Gender]
        g (keyword (get-in customer p))]
    (assoc-in customer p
              (condp = g
                :m "male"
                :f "female"
                "other"))))

(defn fix-invoice [m] (if-not (:Invoice m) (assoc m :Invoice {}) m))

(defn fix-shipping [m] (if-not (:Shipping m) (assoc m :Shipping  {}) m))

;;----------------------------------------------------------
;; path
;;----------------------------------------------------------
(defn inq->main [m] (:CalibrationRequest m))

(defn inq->customer [m] (-> (get-in (inq->main m) [:Customer])
                            fix-gender
                            fix-invoice
                            fix-shipping))

(defn inq->customer-name [m] (get-in (inq->customer m) [:Name]))

(defn inq->customer-mail [m] (get-in (inq->customer m) [:Contact :Email]))

(defn inq->customer-sign [m] (get-in (inq->customer m) [:Sign]))

(defn inq->req-id [m] (get-in (inq->main m) [:RequestId]))

(defn inq->date [m] (get-in (inq->main m) [:Date]))

(defn inq->device [m] (get-in (inq->main m) [:Device]))

(defn inq->devices [m] (get-in (inq->main m) [:Device]))

(defn inq->amounts [m] (map :Amount (inq->devices m)))

(defn inq->todo-names [m] (mapv :ToDoName (inq->devices m)))

(defn inq->comment [m] (not-empty (get-in (inq->main m) [:Comment])))

(defn date-vec->desired-date [v] (:Value (first (filter #(= (keyword (:Type %)) :desired) v))))

(defn inq->desired-date [m] {:Type "desired" :Value (date-vec->desired-date (inq->date m))})

(defn inq->ref-date [m] {:Type "CustomerRef" :Value (u/today)})

(defn inq->schedule-date [m] {:Type "schedule" :Value (date-vec->desired-date (inq->date m)) :Duration 5})

;;----------------------------------------------------------
;; checks
;;----------------------------------------------------------
(defn id-ok? [m]
  (when-let [d (inq->req-id m)]
    (when (re-find #"[0-9a-f]{5,40}" d) true)))

(defn main-parts-ok? [m]
  (and (map? (not-empty (inq->customer m)))
       (vector? (not-empty (inq->device m))))) 

(defn device-amount-ok? [m] (every? integer? (inq->amounts m)))

(defn date-ok? [m]
  (when-let [d (date-vec->desired-date (not-empty (inq->date m)))]
    (when  (re-find #"^\d{4}-([0]\d|1[0-2])-([0-2]\d|3[01])$" d) true)))

(defn customer-mail-ok? [m]
  (when-let [d (inq->customer-mail m)]
    (when (re-find #"^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$" d) true)))

(defn customer-sign-ok? [m]
  (when-let [d (inq->customer-sign m)]
    (when (re-find #"^[A-Z0-9\_\.]{2,15}$" d) true)))

(defn todo-available? [inq v] (every? (set (u/db-req->key v)) (set (inq->todo-names inq))))

(defn check
  [inq todos]
  (prn inq)
  (cond
    (not (main-parts-ok?    inq))        {:error "Missing, wrong or empty customer and/or device section"}
    (not (id-ok?            inq))        {:error "Malformed or missing RequestId"}
    (not (date-ok?          inq))        {:error "Wrong, malformed or missing Date section"}
    (not (todo-available?   inq todos))  {:error "ToDo is not available"}
    (not (device-amount-ok? inq))        {:error "Wrong or missing Device Amount"}
    (not (customer-mail-ok? inq))        {:error "Malformed or missing customer Email"}
    (not (customer-sign-ok? inq))        {:error "Malformed customer Sign"}
    :success {:ok true}))

;;----------------------------------------------------------
;; notification mail
;;----------------------------------------------------------
(defn inq->mail-body
  ([inq]
   (inq->mail-body c/config inq)) 
  ([config m]
   (let [name    (inq->customer-name m)
         comment (inq->comment m)]
     {:from    "vl-proxy@berlin.ptb.de" ;; -> conf
      :to      (:notif-mail config)
      :subject (str "Kalibrieranfrage AGWT " name)
      :body    (str "Eine Anfrage für eine Kalibrierung des Kunden:\n   "
                    name "\nhat uns über die AnGeWaNt Schnittstelle erreicht."
                    (when comment
                      (str " Die Anfrage wurde mit folgendem Kommentar versehen:\n   " comment)))})))

(defn send-mail!
  ([body]
   (send-mail! c/config body))
  ([{host :smtp-host-map} body]
   (m/send-message host body)))

  (defn mail-ok? [m] (zero? (:code m)))

;;----------------------------------------------------------
;; build planning doc
;;----------------------------------------------------------
(defn inq->pla-doc-id [m]
  (str "pla-"
       (date-vec->desired-date (inq->date m)) "-"
       (string/lower-case (inq->customer-sign m))))

(defn todo-by-name [s v] (:ToDo (:value (first (filter #(= s (:key %)) v)))))

(defn device-todo [t n]
  {:ToDo t
   :Type (:DeviceClass t)
   :Amount n})

(defn inq->devices-with-todo
  ([inq todos]
   (inq->devices-with-todo c/config inq todos))
  ([config inq todos]
   (mapv  #(device-todo (todo-by-name %1 todos) %2)
          (inq->todo-names inq)
          (inq->amounts inq))))

(defn pla-doc
  ([inq todos]
   (pla-doc c/config inq todos))
  ([config inq todos]
   {:_id  (inq->pla-doc-id inq)
    :Planning {:RequestId (inq->req-id inq)
               :Date [(inq->desired-date inq)     
                      (inq->ref-date inq)
                      (inq->schedule-date inq)]
               :Customer (inq->customer inq)
               :Device (inq->devices-with-todo inq todos)
               :Comment (inq->comment inq)
               :PostReminder true
               :PreReminder true}}))
