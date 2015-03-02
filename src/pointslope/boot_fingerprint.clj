(ns pointslope.boot-fingerprint
  {:boot/export-tasks true}
  (:require [boot.core :as boot :refer [deftask temp-dir! with-pre-wrap output-files tmpfile tmppath empty-dir! by-ext add-resource commit! get-env]]
            [boot.pod  :as pod :refer [make-pod]]
            [boot.file :as file]
            [boot.util :as util :refer [info]]))

(defn fingerprint-pod
  "Creates a pod with the requisite fingerprint dependencies."
  []
  (make-pod (update-in (get-env) [:dependencies] into '[[enlive "1.1.5"]
                                                        [pandect "0.5.1"]])))

(defn- create-css-js-map
  "Creates a map of css/js file paths. The map has the following structure:
   {:rel-path abosulute-path}"
  [fileset]
  (into {} (map #(hash-map (keyword (tmppath %)) (.getPath (tmpfile %)))
                (by-ext [".css" ".js"] (output-files fileset)))))

(deftask fingerprint
  "Add cache buster fingerprints to all html files. Resources to be
   fingerprinted should be wrapped in enlive's replace-var syntax.

   <link rel=\"stylesheet\" src=\"${css/main.css}\" />
"
  []
  (let [output-dir (temp-dir!)
        output-dir-path (.getPath output-dir)]
    (empty-dir! output-dir)
    (with-pre-wrap fileset
      (info "Fingerprinting files.\n")
      (let [html-files (by-ext [".html"] (output-files fileset))
            css-js-files (create-css-js-map fileset)
            f-pod (fingerprint-pod)]
        (doseq [html-file html-files]
          (let [absolute-file-path (.getPath (tmpfile html-file))
                rel-file-path (tmppath html-file)]
            (pod/with-call-in f-pod
              (pointslope.boot-fingerprint.fingerprint/fingerprint-file ~output-dir-path
                                                                        ~absolute-file-path
                                                                        ~rel-file-path
                                                                        ~css-js-files)))))
      (-> fileset
          (add-resource output-dir)
          (commit!)))))
