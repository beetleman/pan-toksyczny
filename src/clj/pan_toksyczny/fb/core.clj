(ns pan-toksyczny.fb.core
  (:require [pan-toksyczny.fb.messages :as messages]
            [pan-toksyczny.config :refer [env]]
            [pan-toksyczny.http :as http]
            [pan-toksyczny.fb.preprocessing :as preprocessing]
            [pan-toksyczny.air-quality.core :as air-quality]
            [pan-toksyczny.air-quality.interpreter :as interpreter]
            [sieppari.core :as sieppari]
            [mount.core :as mount]))


;TODO: refactor it!!

(def user-intreceptor
  {:enter identity})


(def conversation-context-interceptor
  {:enter identity})


(def user-subscriptions-interceptor
  {:enter identity})


(def message-type-interceptor
  {:enter #(update % :request preprocessing/message-type)})


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


(defn process-message [{entry :entry object :object}]
  (when (= object "page")
    (doseq [events entry]
      (doseq [message (:messaging events)]
        (sieppari/execute [user-intreceptor
                           conversation-context-interceptor
                           user-subscriptions-interceptor
                           message-type-interceptor
                           handler]
                          message)))))

(mount/defstate persistent-menu
  :start (let [action (messages/persistent-menu (:page-access-token env)
                                                ["Check it!"])]
           @(http/execute action)))
