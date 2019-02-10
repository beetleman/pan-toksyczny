(ns pan-toksyczny.schedulers
  (:require [pan-toksyczny.queues :as queues]
            [pan-toksyczny.schedulers.core :as schedulers]
            [pan-toksyczny.db.core :as db]
            [mount.core :as mount]
            [pan-toksyczny.config :refer [check-limit]]))


(defn- -periodic-check []
  (doseq [location (db/get-locations-to-check {:limit check-limit})]
    (queues/periodic-check-publish location)))


(mount/defstate periodic-check
  :start (schedulers/start
          -periodic-check
          {:mins 30})
  :stop (schedulers/stop periodic-check))
