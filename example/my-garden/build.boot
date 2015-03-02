(set-env!
 :source-paths   #{"src/clj" "src/cljs"}
 :resource-paths #{"html"}
 :dependencies '[[org.clojure/clojurescript "0.0-2814"]
                 [org.clojure/clojure   "1.6.0"      :scope "provided"]
                 [adzerk/boot-cljs      "0.0-2814-1" :scope "test"]
                 [adzerk/boot-reload    "0.2.4"      :scope "test"]
                 [pandeiro/boot-http    "0.3.0"      :scope "test"]
                 [boot-garden           "1.2.5-2"    :scope "test"]
                 [boot-fingerprint      "0.1.0"      :scope "test"]])

(require
 '[adzerk.boot-cljs            :refer [cljs]]
 '[adzerk.boot-reload          :refer [reload]]
 '[pandeiro.http               :refer [serve]]
 '[boot-garden.core            :refer [garden]]
 '[pointslope.boot-fingerprint :refer [fingerprint]])

(deftask start
  []
  (comp (serve :dir "target")
        (watch)
        (garden :output-to "css/main.css" :styles-var 'mygarden.styles/base)
        (fingerprint)
        (speak)
        (reload)
        (cljs :source-map true
              :optimizations :none)))

(deftask dev
  []
  (comp (garden :output-to "css/main.css" :styles-var 'mygarden.styles/base)
        (fingerprint)))
