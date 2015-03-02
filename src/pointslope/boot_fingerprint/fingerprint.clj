(ns pointslope.boot-fingerprint.fingerprint
  (:require [boot.util :as util :refer [info]]
            [net.cgrand.enlive-html :as html :refer [template html-resource any-node replace-vars]]
            [clojure.java.io :as io]
            [pandect.algo.sha1 :refer [sha1-file]]))

(defn fingerprint-asset [asset input-dir]
  (let [path (subs (str asset) 1)
        sha1 (sha1-file (str input-dir "/" path))
        fingerprint (str path "?v=" sha1)]
    (info (format "Adding fingerprint '%s'.\n" fingerprint))
    fingerprint))

(defn fingerprint-file [output-dir file-path rel-path]
  (let [root-input-dir (first (clojure.string/split file-path (re-pattern rel-path)))
        input-file (io/file file-path)
        output-file (io/file output-dir rel-path)
        template-fn (template
                     (html-resource input-file)
                     []
                     [any-node] (replace-vars #(fingerprint-asset % root-input-dir)))]
    (info (format "Fingerprinting file %s.\n" rel-path))
    (.mkdirs (.getParentFile output-file))
    (spit output-file (reduce str (template-fn)))))
