(ns dashboard.core-test
  (:require [dashboard.core :as co]
            [dashboard.util :as ut])
  (:use midje.sweet))

(facts "about connection and url generation"
       (fact "should create a valid url for wunderground for just the 'conditions' service"
             (ut/create-url "IT" "Rome") => "http://api.wunderground.com/api/8c5dcc62d26e347c/conditions/q/IT/Rome.json"
             (ut/create-url "IT" "Rome" "conditions") => "http://api.wunderground.com/api/8c5dcc62d26e347c/conditions/q/IT/Rome.json"
             (ut/create-url "IT" "Rome" "somevalue") => nil
             (ut/create-url "IT" "") => nil
             (ut/create-url "IT" nil) => nil
             (ut/create-url "" "Rome") => nil
             (ut/create-url nil "Rome") => nil
             (ut/create-url nil nil) => nil)

       (fact "should connect to wunderground and retrieve data"
             (:city (:display_location (:current_observation (ut/wug-service-call {:country-code "NO" :city "Oslo"})))) => "Oslo"
             (:city (:display_location (:current_observation (ut/wug-service-call {:country-code "XYZ" :city "somevalue"})))) = nil
             )

       (fact "should retrieve data from the fake api (just for dev)"
             (:city (:display_location (:current_observation (ut/fake-wug-service-call {:country-code "NO" :city "Oslo"})))) => "Oslo"
             (:city (:display_location (:current_observation (ut/fake-wug-service-call {:country-code "XYZ" :city "somevalue"})))) = "somevalue"
             (:city (:display_location (:current_observation (ut/fake-wug-service-call {:country-code "XYZ"})))) = nil))


(facts "about `forecast-it`"
       (fact "should forecast a new value"
             (co/forecast-it 6.3) => number?
             (co/forecast-it "b") => nil))
