(ns pointslope.boot-fingerprint.fingerprint
  (:require [boot.util :as util :refer [info]]
            [boot.core :as boot :refer [tmpfile tmppath by-re]]
            [net.cgrand.enlive-html :as html :refer [template html-resource any-node replace-vars]]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [pandect.algo.sha1 :refer [sha1-file]]))

(def prefix-regex #"^[\./]+")

(defn- asset->relpath
  "Returns the asset name with as a relative path, with prefixes stripped."
  [asset]
  (string/replace asset prefix-regex ""))

(defn- asset->asset-prefix
  "Returns the absolute path pefix for an an asset name."
  [asset]
  (re-find prefix-regex asset))

(defn find-asset-file
  "Looks up the asset by relative path in the files seq."
  [asset files]
  (let [path (asset->relpath asset)
        pattern (re-pattern (str "^" path))
        matches (by-re [pattern] files)]
    (first matches)))

(defn fingerprint-asset
  "Returns a fingerprint based on the sha1 of the asset file, 'asset'."
  [asset-file asset-prefix]
  (let [sha1 (sha1-file (tmpfile asset-file))
        fingerprint (str asset-prefix (tmppath asset-file) "?v=" sha1)]
    (info (format "Adding fingerprint '%s'.\n" fingerprint))
    fingerprint))

(defn find-and-fingerprint-asset
  [asset files]
  (let [asset-prefix (asset->asset-prefix asset)
        asset-file (find-asset-file asset files)]
    (fingerprint-asset asset-file asset-prefix)))

(defn fingerprint-file
  "Adds a fingerprint query parameter to all asset vars in the file 
  and creates the output file in the output directory, 'output-dir'.
  Nested output directories are created if necessary."
  [output-dir file files skip]
  (let [input-file (tmpfile file)
        output-file (io/file output-dir (tmppath file))
        template-fn (template
                     (html-resource input-file)
                     []
                     [any-node] (replace-vars
                                 (fn [asset-name]
                                   (let [asset-file (subs (str asset-name) 1)]
                                     (if skip
                                       (asset->relpath asset-file)
                                       (find-and-fingerprint-asset asset-file files))))))]
    (info (format "Fingerprinting file %s.\n" (tmppath file)))
    (.mkdirs (.getParentFile output-file))
    (spit output-file (reduce str (template-fn)))))
