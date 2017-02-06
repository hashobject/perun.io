(set-env!
  :source-paths #{"src"}
  :resource-paths #{"resources"}
  :dependencies '[[hiccup "1.0.5"]
                  [perun "0.4.2-SNAPSHOT"]
                  [confetti "0.1.2-SNAPSHOT"]
                  [deraen/boot-sass "0.2.1"]
                  [org.slf4j/slf4j-nop "1.7.13" :scope "test"]
                  [pandeiro/boot-http "0.7.3"]
                  [org.martinklepsch/boot-gzip "0.1.1"]])

(require '[io.perun :refer :all]
         '[deraen.boot-sass :refer [sass]]
         '[pandeiro.boot-http :refer [serve]]
         '[confetti.boot-confetti :refer [create-site sync-bucket]]
         '[org.martinklepsch.boot-gzip :refer [gzip]])

(task-options!
  pom {:project 'perun.io :version "0.2.0"})

(deftask build
  "Build dev version"
  []
  (let [guide? (fn [e] (= "guide" (:type e)))]
    (comp (sass)
          (global-metadata)
          (markdown :options {:extensions {:smarts true}})
          (slug
            :slug-fn (fn [_ m]
              (:short-filename m)
              )
            )
          (permalink)
          (print-meta)
          (render :renderer 'io.perun.site/guide-page :filterer guide?)
          (collection :renderer 'io.perun.site/render :page "index.html")
          (collection :renderer 'io.perun.site/guides :page "guides/index.html" :filterer guide?))))

(deftask dev
  []
  (comp (watch)
        (build)
        (target)
        (serve :resource-root "public")
        ))

;(def c (-> "perun-martinklepsch-org.confetti.edn" slurp read-string))

; (deftask deploy []
;   (comp (build)
;         (sift :include #{#"^public/.+"})
;         (sift :move {#"^public/" ""})
;         (sync-bucket :access-key (:access-key c)
;                      :secret-key (:secret-key c)
;                      :bucket (:bucket-name c)
;                      :cloudfront-id (:cloudfront-id c))))
