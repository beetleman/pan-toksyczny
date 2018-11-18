(ns pan-toksyczny.http
  (:require [clj-http.client :as client]
            [promesa.core :as p]))

(def http-methods {:get  client/get
                   :post client/post})

(defn execute [{:keys [method url options]}]
  (p/promise
   (fn [resolve reject]
     (let [http-method (get http-methods
                            method
                            (constantly nil))]
       (http-method url
                    (merge options
                           {:async? true
                            :accept :json
                            :as     :json})
                    resolve
                    reject)))))
