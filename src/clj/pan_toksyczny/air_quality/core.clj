(ns pan-toksyczny.air-quality.core
  (:require [clojure.core.async :as a]
            [pan-toksyczny.air-quality.aqicn :as aqicn]
            [pan-toksyczny.config :refer [env]]
            [pan-toksyczny.http :as http]))

(defn coordinates-feed [coordinates]
  ;TODO: add error handler
  (a/go
    (-> (aqicn/coordinates-feed (:aqicn-token env) coordinates)
        http/execute
        a/<!
        (get-in [:body :data]))))
