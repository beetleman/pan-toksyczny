(ns pan-toksyczny.air-quality.aqicn
  (:require [pan-toksyczny.config :refer [env]]
            [pan-toksyczny.http :as http]))

(def url-base "http://api.waqi.info")

(defn search [token keyword]
  {:method  :get
   :url     (str url-base "/search/")
   :options {:query-params {:keyword keyword
                            :token   token}}})


(defn city-feed [token uid]
  {:method  :get
   :url     (str url-base "/feed/@" uid "/")
   :options {:query-params {:token token}}})


(defn coordinates-feed [token {long :long lat :lat}]
  {:method  :get
   :url     (str url-base "/feed/geo:" lat ";" long "/")
   :options {:query-params {:token token}}})
