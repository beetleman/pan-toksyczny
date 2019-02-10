(ns pan-toksyczny.queues
  (:require [clojure.core.async :as a]
            [mount.core :as mount]
            [pan-toksyczny.async :refer [create-loging-channel n-cpu]]
            [pan-toksyczny.workflow.periodic-check :as periodic-check]
            [pan-toksyczny.fb.core :as fb]
            [pan-toksyczny.config :refer [check-limit]]))


(mount/defstate fb-messages
  :start (a/chan 1)
  :stop (a/close! fb-messages))

(def fb-listener-xf (map fb/process-message))

(mount/defstate fb-listener
  :start (a/pipeline (* 2 n-cpu)
                     (create-loging-channel "fb-messages")
                     fb-listener-xf
                     fb-messages)
  :stop (a/close! fb-listener))

(defn fb-publish [x]
  (a/put! fb-messages x))



(mount/defstate periodic-check-messages
  :start (a/chan check-limit)
  :stop (a/close! periodic-check-messages))

(mount/defstate periodic-check-listener
  :start (a/pipeline-async (* 2 n-cpu)
                           (create-loging-channel "periodic-check-messages")
                           (fn [location result]
                             (a/go
                               (a/>! result
                                     (a/<! (periodic-check/check-location location)))
                               (a/close! result)))
                           periodic-check-messages)
  :stop (a/close! periodic-check-listener))

(defn periodic-check-publish [location]
  (a/put! periodic-check-messages location))
