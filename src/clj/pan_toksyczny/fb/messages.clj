(ns pan-toksyczny.fb.messages
  (:require [pan-toksyczny.http :as http]
            [cheshire.core :refer [generate-string]]))


(def base-url "https://graph.facebook.com/v2.6")
(def messages-url (str base-url "/me/messages"))

(defn- create-call [access_token body]
  {:method  :post
   :url     messages-url
   :options {:query-params {:access_token access_token}
             :content-type :json
             :body         (generate-string body)}})



(defn- message-body [recipient message]
  {:recipient {:id recipient}
   :message   message})

(defn- text-body [recipient text]
  (message-body recipient {:text text}))

(defn text [access_token recipient text]
  (create-call access_token
               (text-body recipient text)))

(defn text-location [access_token recipient text]
  (create-call access_token
               (assoc-in (text-body recipient text)
                         [:message :quick_replies]
                         [{:content_type "location"}])))
