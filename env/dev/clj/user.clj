(ns user
  (:require [pan-toksyczny.config :refer [env]]
            [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [mount.core :as mount]
            [pan-toksyczny.core :refer [start-app]]
            [pan-toksyczny.db.core]
            [conman.core :as conman]
            [luminus-migrations.core :as migrations]))

(alter-var-root #'s/*explain-out* (constantly expound/printer))

(defn start []
  (mount/start-without #'pan-toksyczny.core/repl-server))

(defn stop []
  (mount/stop-except #'pan-toksyczny.core/repl-server))

(defn restart []
  (stop)
  (start))

(defn restart-db []
  (mount/stop #'pan-toksyczny.db.core/*db*)
  (mount/start #'pan-toksyczny.db.core/*db*)
  (binding [*ns* 'pan-toksyczny.db.core]
    (conman/bind-connection pan-toksyczny.db.core/*db* "sql/queries.sql")))

(defn reset-db []
  (migrations/migrate ["reset"] (select-keys env [:database-url])))

(defn migrate []
  (migrations/migrate ["migrate"] (select-keys env [:database-url])))

(defn rollback []
  (migrations/migrate ["rollback"] (select-keys env [:database-url])))

(defn create-migration [name]
  (migrations/create name (select-keys env [:database-url])))


