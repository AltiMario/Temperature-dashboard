# Temperature-dashboard ver 0.1

A simple dashboard to display the current and forecasted temperature of some European capital.
Developed with WebSocket and multithreading design. 


## Technical Requirements

* Java 7+ / Clojure 1.8 / Leiningen 2.x


## Usage

* Create somewhere your personal configuration file (e.g. secret-wug.edn) adding your private wunderground key (e.g. {:wug-key "xxxxxxx"})


* Edit the file ./resources/config.edn substituting your new secret-wug.edn path
  

* To run the app

    $ java -jar temperature-dashboard.jar
    
* Point your browser to

    http://localhost:8080/index.html
