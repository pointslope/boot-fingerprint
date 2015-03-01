(ns pointslope.boot-fingerprint
  {:boot/export-tasks true}
  (:require [boot.core :as boot :refer [deftask temp-dir! with-pre-wrap user-files tmpfile by-ext add-resource commit! get-env]]
            [boot.pod  :as pod :refer [make-pod]]
            [boot.file :as file]
            [boot.util :as util :refer [info]]))

(defn fingerprint-pod
  []
  (make-pod (update-in (get-env) [:dependencies] into '[[enlive "1.1.5"]
                                                        [pandect "0.5.1"]])))

(deftask fingerprint
  []
  (let [output-dir (temp-dir!)
        output-dir-path (.getPath output-dir)]
    (with-pre-wrap fileset
      (info "Fingerprinting files.\n")
      (let [boot-tmp-files (by-ext [".html"] (user-files fileset))
            f-pod (fingerprint-pod)]
        (doseq [boot-tmp-file boot-tmp-files]
          (pod/with-call-in f-pod
            ;(pointslope.boot-fingerprint.fingerprint/test-fingerprint ~msg)
            (pointslope.boot-fingerprint.fingerprint/fingerprint-file ~output-dir-path ~(.getPath (tmpfile boot-tmp-file)))
            ;(pointslope.boot-fingerprint.fingerprint/fingerprint-file ~output-dir-path ~output-dir-path)
            )))
      (-> fileset
          (add-resource output-dir)
          (commit!)))))
