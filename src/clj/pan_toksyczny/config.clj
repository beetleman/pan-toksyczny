(ns pan-toksyczny.config
  (:require [cprop.core :refer [load-config]]
            [cprop.source :as source]
            [mount.core :refer [args defstate]]))

(defstate env
  :start
  (load-config
    :merge
    [(args)
     (source/from-system-props)
     (source/from-env)]))


(defstate check-limit
  :start (get env :check-limit 100))

(defstate max-diff
  :start (get env :max-diff 10))
