(ns pan-toksyczny.async
  (:require [clojure.core.async :as a]
            [clojure.tools.logging :as log]))

(def n-cpu (.availableProcessors (Runtime/getRuntime)))


(defn create-loging-channel [name]
  (let [ch (a/chan 1)]
    (a/go-loop []
      (when-let [data (a/<! ch)]
        (if (instance? Throwable data)
          (log/error data name)
          (log/debug name data))
        (recur)))
    ch))
