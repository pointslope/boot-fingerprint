(ns pointslope.boot-fingerprint.fingerprint
  (:require [boot.util :as util :refer [info]]
            [net.cgrand.enlive-html :as html :refer [template html-resource any-node replace-vars]]
            [clojure.java.io :as io]
            [pandect.algo.sha1 :refer [sha1-file]]))

(defn fingerprint-asset [asset]
  (let [path (subs (str asset) 1)
        file (str "target/" path)
        sha1 (sha1-file file)]
    (info (format "Fingerprinting file '%s'.\n" path))
    (str path "?v=" sha1)))

(defn fingerprint-file [output-dir file-path]
  (let [file (io/file file-path)
        template-fn (template
                     (html-resource file)
                     []
                     [any-node] (replace-vars fingerprint-asset))]
    (spit (io/file output-dir (.getName file))
          (reduce str (template-fn)))))
