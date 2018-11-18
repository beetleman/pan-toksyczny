(ns pan-toksyczny.air-quality.core
  (:require [pan-toksyczny.config :refer [env]]
            [pan-toksyczny.air-quality.aqicn :as aqicn]
            [pan-toksyczny.http :as http]))


(defn coordinates-feed [coordinates]
  (->> coordinates
       (aqicn/coordinates-feed (:aqicn-token env))
       http/execute))


(defn generate-test-data []
  (doseq [long (range 15 25 0.5)
          lat  (range 45 55 0.5)]
    (let [coordinates {:long long, :lat lat}
          feed        @(coordinates-feed coordinates)
          data        (:body feed)]
      (clojure.pprint/pprint data
                             (clojure.java.io/writer (str "coordinates-feed/"
                                                          "lat:" lat "_"
                                                          "long:" long ".edn"))))))
