(ns pan-toksyczny.fb.core
  (:require [pan-toksyczny.fb.messages :as messages]
            [pan-toksyczny.config :refer [env]]
            [pan-toksyczny.http :as http]))


(defn process-message [{entry :entry}]
  (doseq [events entry]
    (doseq [message (:messaging events)]
      (when-let [attachments (get-in message [:message :attachments])]
        (println attachments)
        @(http/execute (messages/text (:page-access-token env)
                                      (get-in message [:sender :id])
                                      (-> attachments
                                          first
                                          :payload
                                          :coordinates
                                          str))))
      (when-let [text (get-in message [:message :text])]
        (println message)
        @(http/execute (messages/text-location (:page-access-token env)
                                               (get-in message [:sender :id])
                                               text))))))
