(ns pan-toksyczny.fb.messages
  (:require [pan-toksyczny.http :as http]
            [cheshire.core :refer [generate-string]]))


(def base-url "https://graph.facebook.com/v2.6")
(def messages-url (str base-url "/me/messages"))

(defn send-msg [access_token body]
  {:method  :post
   :url     messages-url
   :options {:query-params {:access_token access_token}
             :content-type :json
             :body         (generate-string body)}})


(defn send-text [access_token recipient text]
  (send-msg access_token {:recipient      {:id recipient}
                          :message        {:text text}}))
