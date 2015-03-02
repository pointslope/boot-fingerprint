(ns pointslope.boot-fingerprint
  {:boot/export-tasks true}
  (:require [boot.core :as boot :refer [deftask temp-dir! with-pre-wrap user-files tmpfile tmppath empty-dir! by-ext add-resource commit! get-env]]
            [boot.pod  :as pod :refer [make-pod]]
            [boot.file :as file]
            [boot.util :as util :refer [info]]))

(defn fingerprint-pod
  "Creates a pod with the requisite fingerprint dependencies."
  []
  (make-pod (update-in (get-env) [:dependencies] into '[[enlive "1.1.5"]
                                                        [pandect "0.5.1"]])))

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
      (let [boot-tmp-files (by-ext [".html"] (user-files fileset))
            f-pod (fingerprint-pod)]
        (doseq [boot-tmp-file boot-tmp-files]
          (let [absolute-file-path (.getPath (tmpfile boot-tmp-file))
                rel-file-path (tmppath boot-tmp-file)]
            (pod/with-call-in f-pod
              (pointslope.boot-fingerprint.fingerprint/fingerprint-file ~output-dir-path
                                                                        ~absolute-file-path
                                                                        ~rel-file-path)))))
      (-> fileset
          (add-resource output-dir)
          (commit!)))))
