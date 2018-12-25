(ns pan-toksyczny.fb.core
  (:require [mount.core :as mount]
            [pan-toksyczny.air-quality.core :as air-quality]
            [pan-toksyczny.air-quality.interpreter :as interpreter]
            [pan-toksyczny.config :refer [env]]
            [pan-toksyczny.fb.interceptors :as interceptors]
            [pan-toksyczny.fb.messages :as messages]
            [pan-toksyczny.fb.preprocessing :as preprocessing]
            [pan-toksyczny.http :as http]
            [sieppari.core :as sieppari]))

(defmulti -handler ::preprocessing/type)

(defmethod -handler ::preprocessing/text
  [{text ::preprocessing/data :as message}]
  (http/execute (messages/text-location (:page-access-token env)
                                        (get-in message [:sender :id])
                                        text)))

(defmethod -handler ::preprocessing/coordinates
  [{coordinates ::preprocessing/data :as message}]
  (http/execute (messages/text (:page-access-token env)
                               (get-in message [:sender :id])
                               (-> @(air-quality/coordinates-feed coordinates)
                                   :aqi
                                   interpreter/aqi->text))))

(defmethod -handler :default [r] (println r (= ::preprocessing/text (::preprocessing/type r))))

(defn handler [request]
  (-handler request))


(defn process-message [message]
  (sieppari/execute [interceptors/error
                     interceptors/conform
                     interceptors/user
                     interceptors/conversation-context
                     interceptors/message-type
                     handler]
                    message))

(mount/defstate persistent-menu
  :start (let [action (messages/persistent-menu (:page-access-token env)
                                                [["AQI?" ::aqi]])]
           @(http/execute action)))
