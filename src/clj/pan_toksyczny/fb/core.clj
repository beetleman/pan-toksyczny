(ns pan-toksyczny.fb.core
  (:require [pan-toksyczny.fb.messages :refer [send-text]]
            [pan-toksyczny.config :refer [env]]
            [pan-toksyczny.http :as http]))


(defn process-message [{entry :entry}]
  (doseq [events entry]
    (doseq [message  (:messaging events)]
      (when-let [text (get-in message [:message :text])]
        @(http/execute (send-text (:page-access-token env)
                                  (get-in message [:sender :id])
                                  text))))))
