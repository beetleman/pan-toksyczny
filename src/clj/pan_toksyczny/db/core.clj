(ns pan-toksyczny.db.core
  (:require
    [clojure.java.jdbc :as jdbc]
    [conman.core :as conman]
    [java-time.pre-java8 :as jt]
    [mount.core :refer [defstate]]
    [pan-toksyczny.config :refer [env]]))

(defstate ^:dynamic *db*
          :start (conman/connect! {:jdbc-url (env :database-url)})
          :stop (conman/disconnect! *db*))

(conman/bind-connection *db* "sql/queries.sql")


(extend-protocol jdbc/IResultSetReadColumn
  java.sql.Timestamp
  (result-set-read-column [v _2 _3]
    (.toLocalDateTime v))

  java.sql.Date
  (result-set-read-column [v _2 _3]
    (.toLocalDate v))

  java.sql.Time
  (result-set-read-column [v _2 _3]
    (.toLocalTime v))

  java.sql.Clob
  (result-set-read-column [v _2 _3]
    (let [s (slurp (.getCharacterStream v))]
      (try
        (read-string s)
        (catch Exception e
          s)))))


(extend-protocol jdbc/ISQLValue
  java.util.Date
  (sql-value [v]
    (java.sql.Timestamp. (.getTime v)))

  java.time.LocalTime
  (sql-value [v]
    (jt/sql-time v))

  java.time.LocalDate
  (sql-value [v]
    (jt/sql-date v))

  java.time.LocalDateTime
  (sql-value [v]
    (jt/sql-timestamp v))

  java.time.ZonedDateTime
  (sql-value [v]
    (jt/sql-timestamp v))

  clojure.lang.IPersistentMap
  (sql-value [v]
    (pr-str v)))


(defn get-or-create-user!
  ([data]
   (get-or-create-user! *db* data))
  ([db data]
   (if-let [user (get-user db data)]
     user
     (do (create-user! db data)
         (get-user db data)))))
