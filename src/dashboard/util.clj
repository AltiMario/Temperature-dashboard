(ns dashboard.util
  (:require [clj-http.client :as cl]
            [cheshire.core :as json]
            [taoensso.timbre :as timbre]
            [nomad :refer [defconfig]]
            [clojure.java.io :as io]))


(defconfig config (io/resource "config.edn"))
(def wug-key (:wug-key (config)))


(defn create-url
  "build the url and use the 'conditions' service by default"
  ([country-code city]
   (create-url country-code city "conditions"))
  ([country-code city service]
   (if (and (not (empty? country-code))
            (not (empty? city))
            (= service "conditions"))
     (do
       (let [base-url "http://api.wunderground.com/api/"]
           (str base-url wug-key "/" service "/q/" country-code "/" city ".json"))))))


(defn- call-api
  "retrieve the data from the wunderground api"
  [url]
  (do
    (json/decode
      (let [data (cl/get url)]
        (if (= 200 (:status data))
          (:body data)
          (timbre/warn url (:status data) (:body data)))) true)))


(defn wug-service-call
  "public call for the wunderground service"
  [place]
  (call-api (create-url (:country-code place) (:city place))))


(defn fake-wug-service-call [place]
  "fake call to simulate an api request (used in dev to save the limited free connections from wunderground)"
  (let [temp-range (range -10 30)]
    (Thread/sleep (+ 500 (rand-int 2500)))                  ;simulate a latency
    (if (and (< (rand-int 100) 95)                          ;5% of possibility of not success
             (not (empty? (:city place)))                   ;in case of empty params
             (not (empty? (:country-code place))))
      {:response            {:version        "0.1",
                             :termsofService "http://www.wunderground.com/weather/api/d/terms.html",
                             :features       {:conditions 1}},
       :current_observation {:windchill_c             "NA",
                             :relative_humidity       "72%",
                             :local_tz_long           (str "Europe/" (:city place)),
                             :dewpoint_string         "48 F (9 C)",
                             :precip_1hr_string       "-9999.00 in (-9999.00 mm)",
                             :forecast_url            "http://www.wunderground.com/global/stations/16242.html",
                             :local_time_rfc822       "Mon, 13 Feb 2017 17:42:08 +0100",
                             :heat_index_c            "NA",
                             :temp_f                  57,
                             :feelslike_f             "57",
                             :pressure_in             "30.27",
                             :precip_today_in         "0.00",
                             :windchill_f             "NA",
                             :wind_degrees            170,
                             :dewpoint_c              9,
                             :dewpoint_f              48,
                             :feelslike_string        "57 F (14 C)",
                             :visibility_km           "N/A",
                             :icon                    "clear",
                             :nowcast                 "",
                             :pressure_trend          "0",
                             :temp_c                  (nth temp-range (rand-int (count temp-range)))
                             :wind_gust_mph           0,
                             :precip_1hr_metric       "--",
                             :solarradiation          "--",
                             :precip_1hr_in           "-9999.00",
                             :heat_index_string       "NA",
                             :pressure_mb             "1025",
                             :station_id              "LIRF",
                             :temperature_string      "57 F (14 C)",
                             :observation_time        "Last Updated on February 13, 5:20 PM CET",
                             :history_url             "http://www.wunderground.com/history/airport/LIRF/2017/2/13/DailyHistory.html",
                             :observation_time_rfc822 "Mon, 13 Feb 2017 17:20:00 +0100",
                             :heat_index_f            "NA",
                             :local_tz_short          "CET",
                             :wind_mph                6,
                             :local_epoch             "1487004128",
                             :UV                      "-1",
                             :estimated               {},
                             :feelslike_c             "14",
                             :weather                 "Clear",
                             :display_location        {:elevation       "3.0",
                                                       :full            (str (:city place) "," (:country-code place)),
                                                       :city            (:city place),
                                                       :longitude       "12.22999954",
                                                       :state           "LZ",
                                                       :magic           "1",
                                                       :state_name      "Italy",
                                                       :zip             "00000",
                                                       :latitude        "41.79999924",
                                                       :country_iso3166 (:country-code place),
                                                       :country         "IY",
                                                       :wmo             "16242"},
                             :image                   {:url   "http://icons.wxug.com/graphics/wu2/logo_130x80.png",
                                                       :title "Weather Underground",
                                                       :link  "http://www.wunderground.com"},
                             :observation_location    {:full            (:city place),
                                                       :city            (:city place),
                                                       :state           "",
                                                       :country         (:country-code place),
                                                       :country_iso3166 (:country-code place),
                                                       :latitude        "41.79999924",
                                                       :longitude       "12.22999954",
                                                       :elevation       "10 ft"},
                             :local_tz_offset         "+0100",
                             :visibility_mi           "N/A",
                             :precip_today_metric     "0.0",
                             :icon_url                "http://icons.wxug.com/i/c/k/nt_clear.gif",
                             :wind_kph                9,
                             :ob_url                  "http://www.wunderground.com/cgi-bin/findweather/getForecast?query=41.79999924,12.22999954",
                             :wind_dir                "South",
                             :wind_gust_kph           0,
                             :precip_today_string     "0.00 in (0.0 mm)",
                             :wind_string             "From the South at 6 MPH",
                             :windchill_string        "NA",
                             :observation_epoch       "1487002800"}}
      {:status 400})))
