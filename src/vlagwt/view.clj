(ns vlagwt.view
  (:require
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

(defn not-found
  []
  (hp/html5
   [:h1 "404 Error!"]
   [:b "Page not found!"]
   [:p [:a {:href ".."} "Return to main page"]]))

;;----------------------------------------------------------
;; index page
;;----------------------------------------------------------
(defn index-title
  [conf req]
  [:section {:class "hero is-dark"}
   [:div {:class "hero-body"}
      [:div {:class "container"}
       [:h1 {:class "title"} (:main-title conf)]
       [:h2 {:class "subtitle"} (str "calibration request: " (utils/req->req-id req) )]]]])

(defn index
  [conf req data]
  (hp/html5
   (page-header conf req)
   [:body
    (prn data)
    (index-title conf req)
    (hp/include-js "/js/jquery-3.5.1.min.js")]))
