(ns io.perun.site
  (:use [hiccup.core :only (html)]
        [hiccup.page :only (html5 include-css include-js)]))

(defn render [posts]
  (html5 {:lang "en"}
    [:head
      [:meta {:charset "utf-8"}]
      [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge,chrome=1"}]
      [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0, user-scalable=no"}]
      [:meta {:itemprop "author" :name "author" :content "hashobject (team@hashobject.com)"}]]
    [:body
       [:div "Hello world"]]))