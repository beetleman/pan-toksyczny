(ns pan-toksyczny.workflow.core
  (:require [clojure.tools.logging :as log]
            [pan-toksyczny.db.core :as db]
            [pan-toksyczny.fb.interceptors :as interceptors]
            [pan-toksyczny.fb.preprocessing :as preprocessing]
            [pan-toksyczny.workflow.communication :refer [ask-location
                                                          check-aqi
                                                          details-aqi]]))


(defn- get-recipent [message]
  (get-in message [:sender :id]))


(defn- -handler-dispatch [{type ::preprocessing/type :as message}]
  (if (= type :postback)
    (get message :payload)
    type))


(defmulti -handler -handler-dispatch)

(defmethod -handler :pan-toksyczny.fb.core/aqi
  [{user ::interceptors/user
    :as  message}]
  (ask-location (get-recipent message)
                user))

(defmethod -handler :location
  [{coordinates ::preprocessing/data
    user        ::interceptors/user
    :as         message}]
  (let [coordinates (select-keys message [:long :lat])]
    (check-aqi (get-recipent message)
               coordinates
               user)))

(defmethod -handler :check-again
  [{user ::interceptors/user
    :as  message}]
  (let [coordinates (select-keys user [:long :lat])
        recipent    (get-recipent message)]
    (println {:coordinates coordinates})
    (if (->> coordinates vals (some nil?))
      (ask-location recipent user)
      (check-aqi recipent
                 coordinates
                 user))))

(defmethod -handler :details
  [{user ::interceptors/user
    :as  message}]
  (let [aqi-data (db/get-aqi user)
        recipent (get-recipent message)]
    (println {:aqi-data aqi-data})
    (if (nil? aqi-data)
      (ask-location recipent user)
      (details-aqi recipent
                   aqi-data
                   user))))

(defmethod -handler :default [r] (log/debug "ignored" r))


(defn handler [request]
  (doseq [msg request]
    (-handler msg))
  request)
