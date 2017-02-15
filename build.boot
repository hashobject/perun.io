(set-env!
  :source-paths #{"src"}
  :resource-paths #{"resources"}
  :dependencies '[[hiccup "1.0.5" :exclusions [org.clojure/clojure]]
                  [perun "0.4.2-SNAPSHOT"]
                  [confetti "0.1.2-SNAPSHOT"]
                  [enlive "1.1.6" :exclusions [org.clojure/clojure]]
                  [hashobject/boot-s3 "0.1.2-SNAPSHOT" :exclusions [org.clojure/clojure]]
                  [deraen/boot-sass "0.2.1"]
                  [org.slf4j/slf4j-nop "1.7.13" :scope "test"]
                  [pandeiro/boot-http "0.7.6" :exclusions [org.clojure/clojure]]
                  ;; tools.nrepl can be removed after https://github.com/pandeiro/boot-http/pull/61
                  ;; is merged
                  [org.clojure/tools.nrepl "0.2.11" :exclusions [org.clojure/clojure]]
                  [org.martinklepsch/boot-gzip "0.1.1"]])

(require '[io.perun :refer :all]
         '[deraen.boot-sass :refer [sass]]
         '[pandeiro.boot-http :refer [serve]]
         '[confetti.boot-confetti :refer [create-site sync-bucket]]
         '[hashobject.boot-s3 :refer :all])

(task-options!
  pom {:project 'perun.io :version "0.2.0"}
  s3-sync {
    :bucket "perun.io"
    :access-key (System/getenv "AWS_ACCESS_KEY")
    :secret-key (System/getenv "AWS_SECRET_KEY")
    :source "public"
    :options {"Cache-Control" "max-age=315360000, no-transform, public"}})

(deftask header-links
  []
  (content-pre-wrap
   {:task-name "header-links"
    :render-form-fn (fn [data] `(io.perun.site/add-header-link-content ~data))
    :paths-fn #(content-paths % {:filterer identity :extensions [".html"]})
    :passthru-fn content-passthru
    :tracer :io.perun/header-links
    :rm-originals true}))

(deftask build
  "Build dev version"
  []
  (let [guide? (fn [e] (= "guide" (:type e)))]
    (comp (sass)
          (global-metadata)
          (markdown :options {:extensions {:smarts true :extanchorlinks true}})
          (header-links)
          (permalink)
          (print-meta)
          (render :renderer 'io.perun.site/guide-page :filterer guide?)
          (collection :renderer 'io.perun.site/render :page "index.html")
          (collection :renderer 'io.perun.site/guides :page "guides/index.html" :filterer guide?))))

(deftask dev
  []
  (comp (watch)
        (build)
        (serve :resource-root "public")))

(deftask deploy []
  (comp (build)
        (target)
        (s3-sync)))
