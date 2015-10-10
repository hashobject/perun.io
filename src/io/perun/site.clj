(ns io.perun.site
  (:use [hiccup.core :only (html)]
        [hiccup.page :only (html5 include-css include-js)]))

(defn render [global-meta posts]
  (html5 {:lang "en"}
    [:head
      [:meta {:charset "utf-8"}]
      [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge,chrome=1"}]
      [:title "Perun: composable static site generator build with Clojure and Boot"]
      [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0, user-scalable=no"}]
      [:meta {:itemprop "author" :name "author" :content "hashobject (team@hashobject.com)"}]
      [:link {:rel "stylesheet" :href "/index.css"}]
      [:link {:href "https://fonts.googleapis.com/css?family=Bevan" :rel "stylesheet" :type "text/css"}]
      [:link {:href "https://cdnjs.cloudflare.com/ajax/libs/github-fork-ribbon-css/0.1.1/gh-fork-ribbon.min.css" :rel "stylesheet" :type "text/css"}]]
    [:body
      [:div.hero
        [:h1 "Perun"]
        [:p "Composable static site generator build with Clojure and Boot"]]
      [:div.github-fork-ribbon-wrapper.right
        [:div.github-fork-ribbon
          [:a {:href "https://github.com/hashobject/perun"} "Star on GitHub"]]]
      [:section.docs-section
        [:h2 "Plugins"]
        [:p "Perun comes with a set of bundled plugins but what important is that you can also
             use Boot plugins and easily create your own."]
        [:p "Here is the list of the current plugins:"]
        [:ul.plugins
          (map (fn [plugin]
            [:li.plugin
              [:p.plugin-title (:name plugin)]
              [:p.plugin-description (:description plugin)]
            ]
            ) (:plugins global-meta))
        ]
      ]
      ]))
