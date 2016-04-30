(ns pointslope.boot-fingerprint
  {:boot/export-tasks true}
  (:require [boot.core :as boot :refer [deftask tmp-dir! with-pre-wrap output-files empty-dir! by-ext add-resource commit!]]
            [boot.pod  :as pod :refer [make-pod]]
            [boot.file :as file]
            [boot.util :as util :refer [info]]
            [pointslope.boot-fingerprint.fingerprint :as boot-fingerprint]))

(deftask fingerprint
  "Add cache buster fingerprints to all html files. Resources to be
   fingerprinted should be wrapped in enlive's replace-var syntax.

   <link rel=\"stylesheet\" src=\"${css/main.css}\" />

   Fingerprinting can be skipped with the -s true (command line) or
   :skip true (code) option. When skipping, vars are replaced with
   their values. The above example would become:

   <link rel=\"stylesheet\" src=\"css/main.css\" />
"
  [s skip     bool "Skips file fingerprinting and
                    replaces all vars with their value."]
  (let [output-dir (tmp-dir!)]
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
