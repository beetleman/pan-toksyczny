(ns pan-toksyczny.air-quality.aqicn
  (:require [pan-toksyczny.config :refer [env]]
            [pan-toksyczny.http :as http]))

(def url-base "http://api.waqi.info")

(defn search [keyword token]
  {:method  :get
   :url     (str url-base "/search/")
   :options {:query-params {:keyword keyword
                            :token   token}}})


(defn city-feed [uid token]
  {:method  :get
   :url     (str url-base "/feed/@" uid "/")
   :options {:query-params {:token token}}})


(defn coordinates-feed [{long :long lat :lat} token]
  {:method  :get
   :url     (str url-base "/feed/geo:" lat ";" long "/")
   :options {:query-params {:token token}}})


(defn generate-test-data []
  (doseq [long (range 15 25 0.5)
          lat  (range 45 55 0.5)]
    (let [coordinates {:long long, :lat lat}
          token       (:aqicn-token env)
          feed        (-> coordinates
                          (coordinates-feed token)
                          http/execute)
          data        (time (-> feed
                                deref
                                :body))]
      (clojure.pprint/pprint data
                             (clojure.java.io/writer (str "coordinates-feed/"
                                                          "lat:" lat "_"
                                                          "long:" long ".edn"))))))
