(ns pan-toksyczny.fb
  (:require [pan-toksyczny.http :as http]
            [cheshire.core :refer [generate-string]]
            [pan-toksyczny.config :refer [env]]))


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






#_(let [t (send-text (:page-access-token env)
                   "1937398753004389"
                   "zaabaaa:D")]
  @(http/execute t)
  t)
