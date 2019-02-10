(ns pan-toksyczny.schedulers.core
  (:require [clojure.core.async :as a]
            [clojure.tools.logging :as log]))


(defn secs->msecs [n]
  (* n 1000))

(defn mins->msecs [n]
  (* (secs->msecs n) 60))

(defn hours->msecs [n]
  (* (mins->msecs n) 60))


(defn start [f {:keys [hours mins secs] :or {hours 0
                                             mins 0
                                             secs 0}}]
  (let [poison-ch (a/chan 1)
        msecs     (+ (hours->msecs hours)
                     (mins->msecs mins)
                     (secs->msecs secs))]
    (a/go-loop []
      (a/alt!
        poison-ch ([_])
        (a/timeout msecs) ([_]
                           (try
                             (f)
                             (catch Exception e
                               (log/error e "scheduled task fail!")))
                           (recur))))
    {:poison-ch poison-ch}))

(defn stop [scheduler]
  (a/put! (:poison-ch scheduler) :stop!))
