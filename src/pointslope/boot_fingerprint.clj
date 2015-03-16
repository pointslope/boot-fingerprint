(ns pointslope.boot-fingerprint
  {:boot/export-tasks true}
  (:require [boot.core :as boot :refer [deftask temp-dir! with-pre-wrap output-files tmpfile tmppath empty-dir! by-ext add-resource commit! get-env]]
            [boot.pod  :as pod :refer [make-pod]]
            [boot.file :as file]
            [boot.util :as util :refer [info]]
            [pointslope.boot-fingerprint.fingerprint :as boot-fingerprint]))

(deftask fingerprint
  "Add cache buster fingerprints to all html files. Resources to be
   fingerprinted should be wrapped in enlive's replace-var syntax.

   <link rel=\"stylesheet\" src=\"${css/main.css}\" />
"
  [s skip bool "Skips file fingerprinting and replaces all vars with their value."]
  (let [output-dir (temp-dir!)]
    (empty-dir! output-dir)
    (with-pre-wrap fileset
      (let [html-files (by-ext [".html"] (output-files fileset))]
        (info "Fingerprinting files.\n")
        (doseq [html-file html-files]
          (boot-fingerprint/fingerprint-file output-dir
                                             html-file
                                             (output-files fileset)
                                             skip)))
      (-> fileset
         (add-resource output-dir)
         (commit!)))))
