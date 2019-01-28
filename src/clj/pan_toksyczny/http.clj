(ns pan-toksyczny.http
  (:require [clj-http.client :as client]
            [clojure.core.async :as a]))

(def http-methods {:get  client/get
                   :post client/post})

(defn execute [{:keys [method url options]}]
  (let [ch          (a/chan 1)
        http-method (get http-methods
                         method
                         (constantly nil))]
    (http-method url
                 (merge options
                        {:async? true
                         :accept :json
                         :as     :json})
                 #(a/put! ch %)
                 #(a/put! ch (ex-info "http execution failed" {:error %})))
    ch))
