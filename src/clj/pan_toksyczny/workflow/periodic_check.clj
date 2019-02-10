(ns pan-toksyczny.workflow.periodic-check
  (:require [clojure.core.async :as a]
            [pan-toksyczny.air-quality.core :as air-quality]
            [pan-toksyczny.config :refer [max-diff]]
            [pan-toksyczny.db.core :as db]
            [pan-toksyczny.workflow.communication :refer [send-aqi]]))

(defn notify? [{aqi-1 :aqi} {aqi-2 :aqi}]
  (> (Math/abs (- aqi-1 aqi-2)) max-diff))

(defn check-location [{recipent :psid :as location}]
  (a/go
    (let [aqi-data (a/<! (air-quality/coordinates-feed location))]
      (if (notify? location aqi-data)
        (do
          (a/<! (send-aqi recipent aqi-data))
          (db/set-aqi! (assoc aqi-data :psid recipent)))
        {}))))
