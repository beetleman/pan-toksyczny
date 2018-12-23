(ns pan-toksyczny.fb.spec
  (:require [clojure.spec.alpha :as s]))

(s/def ::id string?)
(s/def ::time pos-int?)
(s/def ::timestamp pos-int?)

(s/def ::sender (s/keys :req-un [::id]))
(s/def ::recipient (s/keys :req-un [::id]))
(s/def ::object #{"page"})

(s/def :postback/payload string?)
(s/def :postback/title string?)
(s/def ::postback
  (s/keys :req-un [:postback/payload
                   :postback/title]))

(s/def :message/mid string?)
(s/def :message/seq pos-int?)

(s/def :attachments/title string?)
(s/def :attachments/url string?)
(s/def :attachments/type #{"location"})

(s/def :coordinates/lat float?)
(s/def :coordinates/long float?)

(s/def :payload/coordinates
  (s/keys :req-un [:coordinates/lat
                   :coordinates/long]))

(s/def :attachments/payload
  (s/keys :req-un [:payload/coordinates]))
(s/def :message/attachments
  (s/+
   (s/keys :req-un [:attachments/title
                    :attachments/url
                    :attachments/type
                    :attachments/payload])))
(s/def ::message
  (s/keys :req-un [:message/mid
                   :message/seq
                   :message/attachments]))

(s/def :messaging/location
  (s/keys :req-un [::sender
                   ::recipient
                   ::timestamp
                   ::message]))
(s/def :messaging/postback
  (s/keys :req-un [::sender
                   ::recipient
                   ::timestamp
                   ::postback]))

(s/def ::messaging (s/+ (s/or :postback :messaging/postback
                              :location :messaging/location
                              :unknown (constantly true))))

(s/def ::entry
  (s/+ (s/keys :req-un [::id
                        ::time
                        ::messaging])))

(s/def ::page
  (s/keys :req-un [::object
                   ::entry]))
