(set-env!
  :source-paths #{"src"}
  :resource-paths #{"resources" "guides"}
  :dependencies '[[hiccup "1.0.5"]
                  [clj-time "0.9.0"]
                  [perun "0.3.0"]
                  [hashobject/boot-s3 "0.1.2-SNAPSHOT"]
                  [deraen/boot-sass "0.2.1"]
                  [pandeiro/boot-http "0.7.3"]
                  [org.martinklepsch/boot-gzip "0.1.1"]])

(require '[io.perun :refer :all]
         '[deraen.boot-sass :refer [sass]]
         '[pandeiro.boot-http :refer [serve]]
         '[hashobject.boot-s3 :refer :all]
         '[org.martinklepsch.boot-gzip :refer [gzip]])

(task-options!
  pom {:project 'perun.io
       :version "0.2.0"}
  s3-sync {
    :bucket "perun.io"
    :access-key (System/getenv "AWS_ACCESS_KEY")
    :secret-key (System/getenv "AWS_SECRET_KEY")
    :source "public"
    :options {"Cache-Control" "max-age=315360000, no-transform, public"}})

(deftask build-dev
  "Build dev version"
  []
  (let [guide? (fn [e] (= "guide" (:type e)))]
    (comp (sass)
          (global-metadata)
          (base)
          (markdown)
          (permalink :permalink-fn (fn [e] (str (:short-filename e) ".html")) :filterer :content)
          (render :renderer 'io.perun.site/guide-page :filterer guide?)
          (collection :renderer 'io.perun.site/render :page "index.html")
          (collection :renderer 'io.perun.site/guides :page "guides.html" :filterer guide?))))

(deftask build
  "Build perun.io."
  []
  (comp (build-dev)
        (gzip :regex [#".html$" #".css$" #".js$"])
        ;(s3-sync)
        ))

(deftask dev
  []
  (comp (watch)
        (build-dev)
        (serve :resource-root "public")))
