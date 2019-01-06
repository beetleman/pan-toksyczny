(ns pan-toksyczny.logic.core
  (:require [clojure.tools.logging :as log]
            [pan-toksyczny.air-quality.core :as air-quality]
            [pan-toksyczny.air-quality.interpreter :as interpreter]
            [pan-toksyczny.config :refer [env]]
            [pan-toksyczny.db.core :as db]
            [pan-toksyczny.fb.messages :as messages]
            [pan-toksyczny.fb.preprocessing :as preprocessing]
            [pan-toksyczny.fb.interceptors :as interceptors]
            [pan-toksyczny.http :as http]))

(defmulti -handler ::preprocessing/type)

(defmethod -handler :postback
  [message]
  (http/execute (messages/text-location (:page-access-token env)
                                        (get-in message [:sender :id])
                                        "Where are you?")))

(defmethod -handler :location
  [{coordinates ::preprocessing/data
    user        ::interceptors/user
    :as         message}]
  (let [coordinates (select-keys message [:long :lat])]
    (db/set-location! (merge user coordinates))
    (http/execute (messages/text (:page-access-token env)
                                 (get-in message [:sender :id])
                                 (-> @(air-quality/coordinates-feed coordinates)
                                     :aqi
                                     interpreter/aqi->text)))))

(defmethod -handler :default [r] (log/debug r))

(defn handler [request]
  (doseq [msg request]
    (-handler msg)))
