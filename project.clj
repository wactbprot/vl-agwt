(defproject vlagwt "0.1.0"
  :description "A agwt to vl interface."
  :url "https://gitlab1.ptb.de/bock04/vlagwt"
  :license {:name "BSD 2-Clause"
            :url "https://gitlab1.ptb.de/bock04/vlagwt"}
  :dependencies [[org.clojure/clojure                  "1.10.3"]
                 [compojure                            "1.6.1"]
                 [http-kit                             "2.5.0"]
                 [cheshire                             "5.10.0"]
                 [ring/ring-defaults                   "0.3.2"]
                 [ring/ring-core                       "1.7.1"]
                 [ring/ring-devel                      "1.7.1"]
                 [ring/ring-json                       "0.5.0"]
                 [com.ashafa/clutch                    "0.4.0"]
]
  :resource-paths ["resources"]
  :java-source-paths []
  :plugins [[lein-codox  "0.10.7"]
            [lein-cloverage  "1.1.2"]
            [lein-kibit "0.1.8"]]
  :repl-options {:init-ns vlagwt.server}
  :main vlagwt.server
  :aot [vlagwt.server]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
