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


(defn- get-recipent [message]
  (get-in message [:sender :id]))

(defn- check-aqi [recipent coordinates]
  (http/execute (messages/template (:page-access-token env)
                                   recipent
                                   (-> @(air-quality/coordinates-feed coordinates)
                                       :aqi
                                       interpreter/aqi->text)
                                   [["Check again" ::check-again]])))


(defn- ask-location [recipent]
    (http/execute (messages/text-location (:page-access-token env)
                                          recipent
                                          "Where are you?")))


(defn- -handler-dispatch [{type ::preprocessing/type :as message}]
  (if (= type :postback)
    (get message :payload)
    type))


(defmulti -handler -handler-dispatch)

(defmethod -handler :pan-toksyczny.fb.core/aqi
  [message]
  (ask-location (get-recipent message)))

(defmethod -handler :location
  [{coordinates ::preprocessing/data
    user        ::interceptors/user
    :as         message}]
  (let [coordinates (select-keys message [:long :lat])]
    (db/set-location! (merge user coordinates))
    (check-aqi (get-recipent message)
               coordinates)))

(defmethod -handler ::check-again
  [{user ::interceptors/user
    :as  message}]
  (let [coordinates (select-keys user [:long :lat])
        recipent    (get-recipent message)]
    (if (->> coordinates vals (some nil?))
      (ask-location recipent)
      (check-aqi recipent
                 coordinates))))

(defmethod -handler :default [r] (log/debug "ignored" r))


(defn handler [request]
  (doseq [msg request]
    (-handler msg))
  request)
