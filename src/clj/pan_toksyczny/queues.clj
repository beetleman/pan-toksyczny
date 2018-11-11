(ns pan-toksyczny.queues
  (:require [immutant.messaging :refer [queue listen stop publish]]
            [mount.core :as mount]
            [pan-toksyczny.fb.core :as fb]))


(mount/defstate fb-messages
  :start (queue "fb-messages" :durable? true))

(mount/defstate fb-listener
  :start (listen fb-messages fb/process-message)
  :stop (stop fb-listener))

(defn fb-publish [x]
  (publish fb-messages x))
