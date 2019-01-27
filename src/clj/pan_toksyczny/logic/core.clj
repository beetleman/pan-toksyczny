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


(defn- check-aqi [recipent coordinates user]
  (let [aqi-data (air-quality/coordinates-feed coordinates)]
    (db/set-aqi! (merge user @aqi-data))
    (db/set-location! (merge user coordinates))
    @(http/execute (messages/template-button (:page-access-token env)
                                             recipent
                                             (-> @aqi-data
                                                 :aqi
                                                 interpreter/aqi->text)
                                             [["Details" ::details]
                                              ["Check again" ::check-again]]))))

(defn- details-aqi [recipent aqi-data user]
  @(http/execute (messages/template-button (:page-access-token env)
                                           recipent
                                           (interpreter/aqi-data->text aqi-data)
                                           [["Check again" ::check-again]])))


(defn- ask-location [recipent user]
    (http/execute (messages/text-location (:page-access-token env)
                                          recipent
                                          "Where are you?")))


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

(defmethod -handler ::check-again
  [{user ::interceptors/user
    :as  message}]
  (let [coordinates (select-keys user [:long :lat])
        recipent    (get-recipent message)]
    (if (->> coordinates vals (some nil?))
      (ask-location recipent user)
      (check-aqi recipent
                 coordinates
                 user))))

(defmethod -handler ::details
  [{user ::interceptors/user
    :as  message}]
  (let [aqi-data (db/get-aqi user)
        recipent (get-recipent message)]
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
