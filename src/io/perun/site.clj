(ns io.perun.site
  (:require [net.cgrand.enlive-html :as enlive]
            [clojure.java.io :as io])
  (:use [hiccup.core :only (html)]
        [hiccup.page :only (html5 include-css include-js)]))

(defn nav []
  (let [nav-item (fn [uri label]
                   [:li.dib.mr2 [:a.pv2.ph3.fw6.link.near-black.focus-black {:href uri} label]])]
    [:nav {:role "navigation"}
     [:ul.list.pl0.ma0
      (nav-item "/guides/" "Guides")
      (nav-item "https://github.com/hashobject/perun/wiki" "Community")
      (nav-item "https://github.com/hashobject/perun" "GitHub")]]))

(defn icon [id]
  [:svg {:viewBox "0 0 64 64" :width "48px" :height "48px"
         :style "stroke: #000; color: #000; stroke-width: 2px"}
   [:use {:xmlns:xlink "http://www.w3.org/1999/xlink"
          :xlink:href (str "/perun-icons.svg#nc-icon-" (name id))}]])

(defn triangle' [size color]
  [:svg {:width "100px" :height "100px" :viewBox "0 0 100 100" :version "1.1"
         :xmlns "http://www.w3.org/2000/svg" :xmlns:xlink "http://www.w3.org/1999/xlink"
         :style (str "transform: scale(" size ")")}
   [:polygon {:points  "50 0 100 100 0 100"
              :opacity "0.6"
              :fill    color}]])

(defn perun-logo [size]
  [:svg {:viewBox "0 0 100 100" :version "1.1"
         :width (str size "px") :height (str size "px")
         :xmlns "http://www.w3.org/2000/svg" :xmlns:xlink "http://www.w3.org/1999/xlink"
         ;; :style (str "transform: scale(" size ")")
         }
   [:polygon#triangle-1
    {:points "49.4096257 0 98.8192514 98.3899994 0 98.3899994",
     :fill "#DCF9BB",:fill-opacity "0.840000033"}]
   [:polygon#triangle-2
    {:points "62.9854112 2.18787658 99.9963667 75.8882507 25.9744556 75.8882507",
     :fill "#EF81E7",:fill-opacity "0.59692029"}]
   [:polygon#triangle-3
    {:points "56.2399558 45.7867705 78.4465291 90.006995 34.0333825 90.006995",
     :fill "#465292",:fill-opacity "0.377292799"}]])

(defn triangle [size color]
  #_[:img {:src "/perun-triangles.svg"}]
  [:svg {:viewBox "0 0 100 100" :width (str size "px") :height (str size "px")
         ;; :style (str "transform: scale(" size ")")
         :stroke-width "40px" :fill color}
   [:use {:xmlns:xlink "http://www.w3.org/1999/xlink"
          ;; :mask "url(/perun-triangles.svg#mask)"
          :xlink:href  "/perun-triangles.svg#page-1"}]
     ])

(defn feature [{:keys [icon-id body title]}]
  [:div.pv5
   (icon icon-id)
   [:h4.mb4.ttu.tracked title]
   body])

(def slogan "Programmable static site generator built with Clojure and Boot")

(defn footer []
  [:footer.bg-near-white.pv5.ph3.pa6-ns.mt5.dt-l.w-100.lh-copy
   [:div.dtc-l [:b "Perun"] " is a project by "
    [:span.dim "Anton Podviaznikov"] ", "
    [:span.dim "Martin Klepsch"] ", "
    [:span.dim "Juho Teperi"] ", "
    [:span.dim "Brent Hagany"] " & "
    [:span.dim "other contributors."]]
   [:div.dtc-l.tr-l.mt2.mt0-l "Built with Perun and Boot, obviously."]])

(defn base [& contents]
  (html5 {:lang "en"}
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge,chrome=1"}]
     [:title "perun: composable static site generator build with Clojure and Boot"]
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0, user-scalable=no"}]
     [:meta {:itemprop "author" :name "author" :content "Anton Podviaznikov, Martin Klepsch, Juho Teperi, Brent Hagany & other contributors"}]
     [:link {:rel "icon" :type "image/png" :href "/images/48.png" :sizes "48x48"}]
     [:link {:rel "icon" :type "image/png" :href "/images/128.png" :sizes "128x128"}]
     [:link {:rel "icon" :type "image/png" :href "/images/256.png" :sizes "256x256"}]
     [:link {:href "https://fonts.googleapis.com/css?family=Fira+Sans:400,700" :rel "stylesheet" :type "text/css"}]
     (include-css "/site.main.css")]
    [:body
     contents
     (footer)]))

(defn render [{global-meta :meta posts :entries}]
  (base
   [:div
    [:div
     [:div.mw8.center.pv7.cf
      [:div.fl
       [:div.ph3
        [:h1.f1.ma0 "Perun" [:span.f5.fw5.ml3.dn slogan]]
        [:p.mv3 slogan]]
       (nav)]
      [:div.f7.pr6.pt4
       (perun-logo 300)]]

     [:div.mw8.center.ph3
      (feature {:icon-id :puzzle-10
                :title "A level playing ground"
                :body [:div.mw6.lh-copy
                       [:p "Perun isn't a program on it's own. It's a collection of Boot tasks that help with the creation of static sites. This means everything is customizable and optional if you don't like it."]
                       [:p "Extending Perun is a matter of writing simple Boot tasks, often not longer than a handful lines of easy-to-understand Clojure."]]})

      (feature {:icon-id :planet
                :title "All of Boot's greatness"
                :body [:div.mw6.lh-copy
                       [:p "Being merely a set of Boot tasks Perun integrates nicely with the entire Boot ecosystem. Syncing to S3? Compiling Sass or Less? All just a dependency away."]
                       [:p "Oh, and did we mention live reloading?"]]})

      (feature {:icon-id :books
                :title "Everything to get you started"
                :body [:div.mw6.lh-copy
                       [:p "Being as loosely coupled as Perun is it can be hard to get started and understand how all these things fit together. This website provides comprehensive guides to get you started as well as properly documented example projects for you to start with."]]})

      (feature {:icon-id :chat-round-content
                :title "A welcoming community"
                :body [:div.mw6.lh-copy
                       [:p "As things are, at some point you'll get stuck. In this case you can reach out to fellow users on the Clojurians Slack or just open an issue. We're happy to help."]]})]]


    ;; [:section.max-width-4.mx-auto.py4
    ;;  [:h2#plugins "Plugins"]
    ;;  [:p "Perun comes with a set of bundled plugins but what important is that you can also
    ;;          use Boot plugins and easily create your own."]
    ;;  [:p "Here is the list of the current plugins:"]
    ;;  [:ul.list-reset.flex.flex-wrap
    ;;   (->> (:plugins global-meta)
    ;;        (map (fn [plugin]
    ;;               [:li.col.col-4.p2.border--silver.border
    ;;                [:span (:name plugin)]
    ;;                [:p (:description plugin)]])))]
    ;;  [:div.py4.border--silver.border-top (:content (first posts))]]

    ]))

(defn with-top-nav [& contents]
  (base
   [:div.mw7.center.lh-copy.relative
    [:div.absolute.right-0 {:style "top:-130px"} (perun-logo 300)]
    [:div.relative
     {:style "z-index: 1"}
     [:div.ph3.pt4
      [:a.f2.mr3.no-underline.fw6.black {:href "/"} "Perun"]
      [:div.dib-ns slogan]]
     [:div.mt2 (nav)]]
    [:div.ph3.mt5 contents]]))

(defn edit-link [post]
  (let [branch "master"]
    (str "https://github.com/hashobject/perun.io/edit/" branch "/resources/" (:original-path post))))

(defn guide-page [{global-meta :meta post :entry}]
  (with-top-nav
    [:div.guide
     [:h1.dib.ma0.pr2 (:title post)]
     [:a.no-underline {:href (edit-link post)} "edit"]]
    [:div.md (:content post)]))

(defn guides [{global-meta :meta guides :entries}]
  (with-top-nav
    (for [g (sort-by :index guides)]
      (if (:complete g)
        [:a.dt.pv3.mv3.no-underline
         {:href (:permalink g)}
         [:div.dtc.pv1 (icon (:icon g))]
         [:div.dtc.pl4.black.v-top
          [:h2.ma0 (:title g)]
          [:p (:description g)]]]
        [:div.dt.pv3.mv3
         {:style "opacity:0.5"}
         [:div.dtc.pv1 (icon (:icon g))]
         [:div.dtc.pl4.v-top
          [:div
           [:h2.dib.mv0.mr3 (:title g)]
           [:span "TBD"]]
          [:p (:description g)]]]))))

;; This is the free octicon from Github
(def link-icon (enlive/html-resource
                (java.io.StringReader.
                 (str "<svg height=\"16\" version=\"1.1\" viewBox=\"0 0 16 16\" width=\"16\"><path fill-rule=\"evenodd\" d=\"M4 9h1v1H4c-1.5 0-3-1.69-3-3.5S2.55 3 4 3h4c1.45 0 3 1.69 3 3.5 0 1.41-.91 2.72-2 3.25V8.59c.58-.45 1-1.27 1-2.09C10 5.22 8.98 4 8 4H4c-.98 0-2 1.22-2 2.5S3 9 4 9zm9-3h-1v1h1c1 0 2 1.22 2 2.5S13.98 12 13 12H9c-.98 0-2-1.22-2-2.5 0-.83.42-1.64 1-2.09V6.25c-1.09.53-2 1.84-2 3.25C6 11.31 7.55 13 9 13h4c1.45 0 3-1.69 3-3.5S14.5 6 13 6z\"></path></svg>"))))

(defn add-header-link-content
  [{:keys [entry]}]
  (let [xformed (-> entry
                    :full-path
                    io/file
                    slurp
                    java.io.StringReader.
                    enlive/html-resource
                    (enlive/transform
                     #{[:h2 :a] [:h3 :a] [:h4 :a] [:h5 :a] [:h6 :a]}
                     (enlive/content link-icon))
                    enlive/emit*)]
    (assoc entry :rendered (apply str xformed))))
