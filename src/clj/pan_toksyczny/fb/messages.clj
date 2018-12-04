(ns pan-toksyczny.fb.messages
  (:require [pan-toksyczny.http :as http]
            [cheshire.core :refer [generate-string]]))


(def base-url "https://graph.facebook.com/v2.6")
(def messages-url (str base-url "/me/messages"))
(def messenger-profile-url (str base-url "/me/messenger_profile"))

(defn- create-call
  ([access-token body]
   (create-call access-token body messages-url))
  ([access-token body url]
   {:method  :post
    :url     url
    :options {:query-params {:access_token access-token}
              :content-type :json
              :body         (generate-string body)}}))


(defn- message-body [recipient message]
  {:recipient {:id recipient}
   :message   message})

(defn- text-body [recipient text]
  (message-body recipient {:text text}))

(defn text [access-token recipient text]
  (create-call access-token
               (text-body recipient text)))

(defn text-location [access-token recipient text]
  (create-call access-token
               (assoc-in (text-body recipient text)
                         [:message :quick_replies]
                         [{:content_type "location"}])))


(defn- persistent-menu-item-nody [title]
  {:title title,
   :type "postback",
   :payload title})

(defn- persistent-menu-body [items composer-input-disabled]
  {:get_started {:payload "get_started"}
   :persistent_menu
   [{:locale                  "default",
     :composer_input_disabled composer-input-disabled,
     :call_to_actions
     (mapv persistent-menu-item-nody items)}]})


(defn persistent-menu
  ([access-token items]
   (persistent-menu access-token items true))
  ([access-token items composer-input-disabled]
   (create-call access-token
                (persistent-menu-body items composer-input-disabled)
                messenger-profile-url)))
