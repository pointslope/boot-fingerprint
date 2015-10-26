(set-env!
 :source-paths   #{"src"}
 :dependencies '[[org.clojure/clojure "1.6.0" :scope "provided"]
                 [boot/core "2.0.0-rc6"       :scope "provided"]
                 [adzerk/bootlaces "0.1.11"   :scope "test"]
                 [enlive "1.1.5" :exclusions [org.clojure/clojure]]
                 [pandect "0.5.1" :exclusions [org.clojure/clojure]]])

(require
 '[adzerk.bootlaces :refer :all])

(def +version+ "0.1.2-SNAPSHOT")

(bootlaces! +version+)

(task-options!
 pom {:project 'boot-fingerprint
      :version +version+
      :description "Boot task to fingerprint asset references in html files."
      :url "https://github.com/pointslope/boot-fingerprint"
      :scm {:url "https://github.com/pointslope/boot-fingerprint"}
      :license {"EPL" "http://www.eclipse.org/legal/epl-v10.html"}})
