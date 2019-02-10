(ns pan-toksyczny.workflow.communication
  (:require [clojure.core.async :as a]
            [pan-toksyczny.air-quality.core :as air-quality]
            [pan-toksyczny.air-quality.interpreter :as interpreter]
            [pan-toksyczny.config :refer [env]]
            [pan-toksyczny.db.core :as db]
            [pan-toksyczny.fb.messages :as messages]
            [pan-toksyczny.http :as http]))


(defn send-aqi [recipent aqi-data]
  (http/execute (messages/template-button (:page-access-token env)
                                              recipent
                                              (-> aqi-data
                                                  :aqi
                                                  interpreter/aqi->text)
                                              [["Details" :details]
                                               ["Check again" :check-again]])))


(defn check-aqi [recipent coordinates user]
  (a/go
    (let [aqi-data (-> coordinates
                       air-quality/coordinates-feed
                       a/<!)]
      (db/set-aqi! (merge user aqi-data))
      (db/set-location! (merge user coordinates))
      (send-aqi recipent aqi-data))))


(defn details-aqi [recipent aqi-data user]
  (http/execute (messages/template-button (:page-access-token env)
                                          recipent
                                          (interpreter/aqi-data->text aqi-data)
                                          [["Check again" :check-again]])))


(defn ask-location [recipent user]
    (http/execute (messages/text-location (:page-access-token env)
                                          recipent
                                          "Where are you?")))
