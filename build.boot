(set-env!
  :source-paths #{"src"}
  :resource-paths #{"resources"}
  :dependencies '[[hiccup "1.0.5"]
                  [perun "0.4.2-SNAPSHOT"]
                  [confetti "0.1.2-SNAPSHOT"]
                  [hashobject/boot-s3 "0.1.2-SNAPSHOT"]
                  [deraen/boot-sass "0.2.1"]
                  [org.slf4j/slf4j-nop "1.7.13" :scope "test"]
                  [pandeiro/boot-http "0.7.3"]
                  [org.martinklepsch/boot-gzip "0.1.1"]])

(require '[io.perun :refer :all]
         '[deraen.boot-sass :refer [sass]]
         '[pandeiro.boot-http :refer [serve]]
         '[confetti.boot-confetti :refer [create-site sync-bucket]]
         '[hashobject.boot-s3 :refer :all]
         '[org.martinklepsch.boot-gzip :refer [gzip]])

(task-options!
  pom {:project 'perun.io :version "0.2.0"}
  s3-sync {
    :bucket "perun.io"
    :access-key (System/getenv "AWS_ACCESS_KEY")
    :secret-key (System/getenv "AWS_SECRET_KEY")
    :source "public"
    :options {"Cache-Control" "max-age=315360000, no-transform, public"}})

(deftask build
  "Build dev version"
  []
  (let [guide? (fn [e] (= "guide" (:type e)))]
    (comp (sass)
          (global-metadata)
          (markdown :options {:extensions {:smarts true}})
          (permalink)
          (print-meta)
          (render :renderer 'io.perun.site/guide-page :filterer guide?)
          (collection :renderer 'io.perun.site/render :page "index.html")
          (collection :renderer 'io.perun.site/guides :page "guides/index.html" :filterer guide?))))

(deftask dev
  []
  (comp (watch)
        (build)
        (serve :resource-root "public")
        ))


(deftask deploy []
  (comp (build)
        (target)
        (s3-sync)))
