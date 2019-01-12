(ns pan-toksyczny.test.db.core
  (:require [pan-toksyczny.db.core :refer [*db*] :as db]
            [luminus-migrations.core :as migrations]
            [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [conman.core :as conman]
            [pan-toksyczny.config :refer [env]]
            [mount.core :as mount]))

(use-fixtures
  :once
  (fn [f]
    (mount/start
      #'pan-toksyczny.config/env
      #'pan-toksyczny.db.core/*db*)
    (migrations/migrate ["reset"] (select-keys env [:database-url]))
    (binding [*ns* 'pan-toksyczny.db.core]
      (conman/bind-connection pan-toksyczny.db.core/*db* "sql/queries.sql"))
    (f)
    (mount/stop #'pan-toksyczny.db.core/*db*)))


(deftest test-users
  (jdbc/with-db-transaction [t-conn *db*]
    (jdbc/db-set-rollback-only! t-conn)
    (let [psid                 (str (rand-int 9999))
          {id :id :as created} (db/create-user!
                                t-conn
                                {:psid psid})]
      (testing "user creation"
        (is (= #{:id
                 :created_at}
               (-> created keys set) ))

        (is (= psid
               (:psid (db/get-user t-conn {:psid psid})))))

      (testing "get or create user, user exists"
        (is (= (db/get-user t-conn {:psid psid})
               (db/get-or-create-user! t-conn {:psid psid}))))

      (testing "get or create user, user not exists"
        (let [psid     (str psid "42")
              new-user (db/get-or-create-user! t-conn {:psid psid})]
          (is (= (db/get-user t-conn {:psid psid})
                 new-user))))

      (let [location {:lat  48.2242784
                      :long 12.2228064
                      :psid psid}]

        (testing "add location and get location"
          (is (= 1 (db/set-location! t-conn location)))
          (is (= location (db/get-location t-conn {:psid psid}))))

        (testing "delete location"
          (is (= 1 (db/delete-location! t-conn {:psid psid})))
          (is (= nil (db/get-location t-conn {:psid psid})))))

      (let [aqi {:aqi         12
                 :dominentpol "pm25",
                 :iaqi        {:no2  {:v 24.1}
                               :pm10 {:v 35}
                               :w    {:v 0.3}
                               :wg   {:v 0.3}
                               :pm25 {:v 154}
                               :co   {:v 11.7}
                               :so2  {:v 6}
                               :t    {:v 2.2}
                               :p    {:v 1007.7}}
                 :psid        psid}]
        (testing "add and get aqi"
          (is (= 1 (db/set-aqi! t-conn aqi)))
          (is (= aqi (db/get-aqi t-conn {:psid psid}))))))))
