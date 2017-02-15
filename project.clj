(defproject dashboard "0.1.0-SNAPSHOT"
  :description "Shows current and forecasted temperature in some European capital"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [cheshire "5.7.0"]
                 [ring "1.5.1"]
                 [http-kit "2.2.0"]
                 [clj-http "3.4.1"]
                 [metosin/compojure-api "1.1.8"]
                 [ring-cors "0.1.9"]
                 [com.taoensso/timbre "4.7.4"]
                 [jarohen/nomad "0.7.3"]]
  :main ^:skip-aot dashboard.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all :uberjar-name "temperature-dashboard.jar"}
             :dev     {:dependencies [[midje "1.8.3"]]
                       :plugins      [[lein-midje "3.2"]]}})
