(ns pan-toksyczny.air-quality.aqicn
  (:require [pan-toksyczny.config :refer [env]]
            [pan-toksyczny.http :as http]))

(def url-base "http://api.waqi.info")

(defn search [keyword token]
  {:method  :get
   :url     (str url-base "/search/")
   :options {:query-params {:keyword keyword
                            :token   token}}})


(defn city-feed [uid token]
  {:method  :get
   :url     (str url-base "/feed/@" uid "/")
   :options {:query-params {:token token}}})


(defn coordinates-feed [{long :long lat :lat} token]
  {:method  :get
   :url     (str url-base "/feed/geo:" lat ";" long "/")
   :options {:query-params {:token token}}})


#_(let [coordinates {:long 20.0160684, :lat 50.087988}
      token       (:aqicn-token env)
      feed        (-> coordinates
                      (coordinates-feed token)
                      http/execute)
      data        (time (-> feed
                            deref
                            :body))]
  (clojure.pprint/pprint data)
  data)


#_(let [token    (:aqicn-token env)
      krk      (-> "kraków"
                   (search token)
                   http/execute)
      uid      (time (-> krk deref :body :data first :uid))
      krk-feed (-> uid
                   (city-feed token)
                   http/execute)
      krk-data (time (-> krk-feed
                         deref
                         :body
                         :data))]
  (println krk-data))
