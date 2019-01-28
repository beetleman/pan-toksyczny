(ns pan-toksyczny.fb.core
  (:require [clojure.core.async :as a]
            [mount.core :as mount]
            [pan-toksyczny.config :refer [env]]
            [pan-toksyczny.fb.interceptors :as interceptors]
            [pan-toksyczny.fb.messages :as messages]
            [pan-toksyczny.http :as http]
            [pan-toksyczny.logic.core :refer [handler]]
            [sieppari.core :as sieppari]))

(defn process-message [message]
  (sieppari/execute [interceptors/error
                     interceptors/conform
                     interceptors/message-flatten
                     interceptors/user
                     interceptors/conversation-context
                     handler]
                    message))

(mount/defstate persistent-menu
  :start (let [action (messages/persistent-menu (:page-access-token env)
                                                [["AQI?" ::aqi]])]
           (-> action
               http/execute
               a/<!!)))
