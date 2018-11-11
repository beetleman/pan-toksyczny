(ns pan-toksyczny.http
  (:require [clj-http.client :as client]
            [promesa.core :as p]))

(def http-methods {:get  client/get
                   :post client/post})

(defn execute [{:keys [method url options]}]
  (p/promise
   (fn [resolve reject]
     ((http-methods method) url
                            (merge options
                                   {:async? true
                                    :accept :json
                                    :as     :json})
                            resolve
                            reject))))
