(ns pan-toksyczny.queues
  (:require [clojure.core.async :as a]
            [clojure.tools.logging :as log]
            [mount.core :as mount]
            [pan-toksyczny.fb.core :as fb]))

(def n-cpu (.availableProcessors (Runtime/getRuntime)))

(defn create-loging-channel [name]
  (let [ch (a/chan 1)]
    (a/go-loop []
      (when-let [data (a/<! ch)]
        (log/debug name data)
        (recur)))
    ch))


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
