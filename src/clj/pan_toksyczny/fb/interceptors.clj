(ns pan-toksyczny.fb.interceptors
  (:require [clojure.spec.alpha :as s]
            [clojure.tools.logging :as log]
            [pan-toksyczny.fb.preprocessing :as preprocessing]
            [pan-toksyczny.fb.spec :as spec]
            [pan-toksyczny.db.core :as db]))

(def error
  {:error (fn [{error :error :as ctx}]
            (let [error-data (ex-data error)]
              (case (:type error-data)
                ;TODO: add other errors types here
                ::unconformed (log/debug "Unconformed" (:data error-data))

                (log/error error)))
            (dissoc ctx :error))})


(def conform
  {:enter (fn [{request :request :as ctx}]
            (let [conformed (s/conform ::spec/page request)]
              (if (= ::s/invalid conformed)
                (assoc ctx :error (ex-info "Unconformed data"
                                           {:type ::unconformed
                                            :data (s/explain-data ::spec/page request)}))
                (assoc ctx :request conformed))))})


(defn- -add-user [message]
  (if-let [psid (get-in message [:sender :id])]
    (assoc message ::user (db/get-or-create-user! {:psid psid}))
    message))

(def user
  {:enter (fn [ctx]
            (update ctx :request #(map -add-user %)))})


(def conversation-context
  {:enter identity})


(def message-flatten
  {:enter (fn [ctx]
            (update ctx :request preprocessing/message-flatten))})
