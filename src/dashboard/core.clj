(ns dashboard.core
  (:use [compojure.core :only (defroutes GET)]
        ring.util.response
        ring.middleware.cors
        org.httpkit.server)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.util.response :refer [redirect]]
            [ring.middleware.reload :as reload]
            [cheshire.core :refer :all]
            [dashboard.util :as ut]
            [taoensso.timbre :as timbre])
  (:gen-class))


(def clients (atom {}))
(def city-temperature (atom {}))
(def country-city '({:country-code "IT" :city "Rome"}
                     {:country-code "ES" :city "Madrid"}
                     {:country-code "GB" :city "London"}
                     {:country-code "FR" :city "Paris"}
                     {:country-code "NO" :city "Oslo"}
                     {:country-code "GR" :city "Athens"}))


(defn ws
  "websocket connection/disconnection with all clients"
  [req]
  (with-channel req con
                (swap! clients assoc con true)
                (timbre/info con " connected")
                (on-close con (fn [status]
                                (swap! clients dissoc con)
                                (timbre/info con " disconnected : " status)))))


(defn send-to-dashboard!
  "A thread sends to the clients the values city & temperature, every 5 secs"
  []
  (future (loop []
            (doseq [[k v] @city-temperature]
              (doseq [client @clients]
                (send! (key client)
                       (generate-string
                         {:place       k
                          :temperature v})
                       false))
              (Thread/sleep 5000))
            (recur)))
  nil)


(defn observation->map-entry
  "Take the relevant into (city and temperature) from the hash-map of wunderground"
  [data]
  (let [city (:city (:display_location (:current_observation data)))
        temp (:temp_c (:current_observation data))]
    [city temp]))


(defn retrieve-api-data!
  "A thread takes the info from wunderground, parallelizing the api calls (with child threads).
  Will sleep for some minutes to avoid unnecessary calls"
  []
  (future (loop []
            (->> country-city
                 (pmap (comp observation->map-entry ut/wug-service-call)) ;parallel map
                 (into {} (remove (fn [[k v]] (nil? v))))   ;remove the possibles nil values
                 (reset! city-temperature))
            (Thread/sleep 3600000)                           ;wait for 60 mins
            (recur)))
  nil)


(defn forecast-it
  "very dummy/fake forecasting algorithm"
  [v]
  (let [excursion (range -2 2)
        val (nth excursion (rand-int (count excursion)))]
    (Thread/sleep 2500)                                     ;time consuming simulation
    (try
      (+ v val)
      (catch Exception e))))


(defn apply-forecasting!
  "A thread to forecast the temperatures"
  []
  (future (loop []
            (doseq [[k v] @city-temperature]
              (swap! city-temperature assoc k (forecast-it v)))
            (recur)))
  nil)


(defroutes routes
           (GET "/temperature" [] ws)
           (GET "/" [] (redirect "/index.html"))
           (route/resources "/"))


(def application (-> (handler/site routes)
                     reload/wrap-reload
                     (wrap-cors
                       :access-control-allow-origin #".+")))


(defn -main [& args]
  (run-server application {:port (:port (get-in (ut/config) [:server])) :join? false})
  (retrieve-api-data!)
  (send-to-dashboard!)
  (apply-forecasting!)
  )

(comment
  (-main))
