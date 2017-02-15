# Temperature-dashboard ver 0.1

A simple dashboard to display the current and forecasted temperature of some European capital.

* Developed with WebSocket and Multithreading architecture. 


# Rational

The idea is to display the temperatures that change in semi real-time
- but the data (from API) is refreshed after some minutes
- and there is a limited numbers of connection per day

I decided to 
- apply a forecast algorithm (dummy for now, maybe in future I can do something intelligent) to give the sensation of data constantly refreshed
- update the chart every hour, with real values retrieved from the API

My main goal was to use somehow WebSocket and Multithreading.


## Technical Requirements

* Java 7+ / Clojure 1.8 / Leiningen 2.x


## Usage

* Create somewhere your personal configuration file (e.g. secret-wug.edn) adding your private wunderground key (e.g. {:wug-key "xxxxxxx"})


* Edit the file ./resources/config.edn substituting your new secret-wug.edn path
  

* To run the app

    $ java -jar temperature-dashboard.jar
    
* Point your browser to

    http://localhost:8080/index.html
