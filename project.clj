(defproject pan-toksyczny "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :local-repo "./.m2"
  :repositories {"sonatype-oss-public" "https://oss.sonatype.org/content/groups/public/"}
  :dependencies [[clojure.java-time "0.3.2"]
                 [com.fasterxml.jackson.core/jackson-core "2.9.7"]
                 [com.fasterxml.jackson.datatype/jackson-datatype-jdk8 "2.9.7"]
                 [com.h2database/h2 "1.4.197"]
                 [conman "0.8.3"]
                 [cprop "0.1.13"]
                 [luminus-immutant "0.2.4" :exclusions [org.immutant/web]]
                 [luminus-migrations "0.6.1"]
                 [luminus-transit "0.1.1"]
                 [luminus/ring-ttl-session "0.3.2"]
                 [markdown-clj "1.0.5"]
                 [metosin/muuntaja "0.6.1"]
                 [metosin/reitit "0.2.7"]
                 [metosin/ring-http-response "0.9.1"]
                 [mount "0.1.14"]
                 [nrepl "0.4.5"]
                 [org.clojure/clojure "1.9.0"]
                 [org.clojure/tools.cli "0.4.1"]
                 [org.clojure/tools.logging "0.4.1"]
                 [ring/ring-core "1.7.1"]
                 [ring/ring-defaults "0.3.2"]
                 [selmer "1.12.3"]
                 [clj-http "3.9.1"]

                 [funcool/promesa "1.9.0"]
                 [funcool/cats "2.3.1"]

                 [org.immutant/web "2.1.10"]
                 [org.immutant/messaging "2.1.10"]
                 [org.immutant/scheduling "2.1.10"]]

  :min-lein-version "2.0.0"

  :source-paths ["src/clj"]
  :test-paths ["test/clj"]
  :resource-paths ["resources"]
  :target-path "target/%s/"
  :main ^:skip-aot pan-toksyczny.core

  :plugins [[lein-immutant "2.1.0"]
            [lein-ancient "0.6.15"]
            [lein-kibit "0.1.6"]]

  :profiles
  {:uberjar {:omit-source true
             :aot :all
             :uberjar-name "pan-toksyczny.jar"
             :source-paths ["env/prod/clj"]
             :resource-paths ["env/prod/resources"]}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   :project/dev  {:jvm-opts ["-Dconf=dev-config.edn"]
                  :dependencies [[expound "0.7.1"]
                                 [pjstadig/humane-test-output "0.9.0"]
                                 [prone "1.6.1"]
                                 [ring/ring-devel "1.7.1"]
                                 [ring/ring-mock "0.3.2"]

                                 [nrepl "0.4.5"] ; emacs/cider
                                 ]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.23.0"]

                                 [refactor-nrepl "2.4.0"] ; emacs/cider
                                 [cider/cider-nrepl "0.19.0-SNAPSHOT"] ; emacs/cider
                                 ]

                  :source-paths ["env/dev/clj"]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test {:jvm-opts ["-Dconf=test-config.edn"]
                  :resource-paths ["env/test/resources"]}
   :profiles/dev {}
   :profiles/test {}})
