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


(defn- postback-button-body [item]
  (let [[title payload] (if (coll? item)
                          item
                          [item item])]
    {:title title,
     :type "postback",
     :payload (pr-str payload)}))


(defn- text-body [recipient text]
  (message-body recipient {:text text}))

(defn text [access-token recipient text]
  (create-call access-token
               (text-body recipient text)))


(defn- template-generic-element-body [{:keys [title subtitle items]}]
  {:title title
   :subtitle subtitle
   :buttons (mapv postback-button-body items)})

(defn- template-generic-body [recipient elements]
  (message-body recipient
                {:attachment
                 {:type "template",
                  :payload
                  {:template_type "generic",
                   :elements (mapv template-generic-element-body elements)}}}))

(defn template-generic [access-token recipient elements]
  (create-call access-token
               (template-generic-body recipient elements)))


(defn- template-button-body [recipient text items]
  (message-body recipient
                {:attachment
                 {:type "template"
                  :payload
                  {:template_type "button"
                   :text text
                   :buttons (mapv postback-button-body items)}}}))

(defn template-button [access-token recipient text items]
  (create-call access-token
               (template-button-body recipient text items)))


(defn text-location [access-token recipient text]
  (create-call access-token
               (assoc-in (text-body recipient text)
                         [:message :quick_replies]
                         [{:content_type "location"}])))


(defn- persistent-menu-body [items composer-input-disabled]
  {:get_started {:payload "get_started"}
   :persistent_menu
   [{:locale                  "default",
     :composer_input_disabled composer-input-disabled,
     :call_to_actions
     (mapv postback-button-body items)}]})


(defn persistent-menu
  ([access-token items]
   (persistent-menu access-token items true))
  ([access-token items composer-input-disabled]
   (create-call access-token
                (persistent-menu-body items composer-input-disabled)
                messenger-profile-url)))
