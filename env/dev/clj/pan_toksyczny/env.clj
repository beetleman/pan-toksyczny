(ns pan-toksyczny.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [pan-toksyczny.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[pan-toksyczny started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[pan-toksyczny has shut down successfully]=-"))
   :middleware wrap-dev})
