(ns vlagwt.view
  (:require [vlagwt.config :as c]
            [hiccup.form :as hf]
            [hiccup.page :as hp]
            [vlagwt.utils :as utils]))

(defn page-header
  [conf req]
  [:head
   [:meta {:http-equiv "Cache-Control" :content="no-cache, no-store, must-revalidate"}]
   [:meta {:http-equiv "Pragma" :content "no-cache"}]
   [:meta {:http-equiv "Expires" :content "0"}]
   [:title (:page-title conf)]
   (hp/include-css "/css/bulma.css")
   (hp/include-css "/css/all.css")])

(defn not-found []
  (hp/html5
   [:h1 "404 Error!"]
   [:b "Page not found!"]
   [:p [:a {:href ".."} "Return to main page"]]))


(defn sub-id-view [v] (into [:ul] (map (fn [m] [:li (:id m)])  v)))
 
(defn val-view [[k v]] [:li [:a {:href (str "/status/ui/" k)} k] (sub-id-view v)])

(defn score-view [m] (into [:ul] (map (fn [[k v]]
                                        (condp = k
                                          :Score [:li [:b "Status: " v]]
                                          [:li (str k ": " v)])) m)))

;;----------------------------------------------------------
;; index page
;;----------------------------------------------------------
(defn index-title
  [conf req]
  (let [id (utils/req->req-id req)]
    [:section {:class "hero is-dark"}
     [:div {:class "hero-body"}
      [:div {:class "container"}
       [:h1 {:class "title"} (:main-title conf)]
       [:h2 {:class "subtitle"} (if id
                                  (str "calibration request: " id )
                                  "available calibration request" )]]]]))
  
(defn index
  ([req data]
   (index c/config req data))
  ([conf req data]
   (hp/html5
    (page-header conf req)
    [:body
     (index-title conf req)
     [:section {:class "section"}
      [:div {:class "container content"}
       [:div {:class "box"}
     (if (utils/req->req-id req)
       (score-view data)
       (into [:ul] (map val-view data)))
     (hp/include-js "/js/jquery-3.5.1.min.js")]]]])))
