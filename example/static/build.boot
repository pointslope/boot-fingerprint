(set-env!
 :resource-paths #{"html"}
 :dependencies '[[boot/core             "2.0.0-rc6"  :scope "provided"]
                 [pandeiro/boot-http    "0.3.0"      :scope "test"    ]
                 [boot-fingerprint      "0.1.0"                       ]
                 [org.clojure/clojure   "1.6.0"      :scope "provided"]])

(require
 '[pointslope.boot-fingerprint :refer [fingerprint]]
 '[pandeiro.http         :refer [serve]])

(deftask run
  []
  (comp (serve :dir "target")
        (watch)
        (speak)
        (fingerprint)))
