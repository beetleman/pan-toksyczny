(ns pan-toksyczny.test.fb.spec
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [pan-toksyczny.test.data :as data]
            [pan-toksyczny.fb.spec :as spec]))

(defn messaging [data]
  (-> data
      :entry
      first
      :messaging
      first))


(deftest page
  (testing "empty map is invalid"
    (is (= ::s/invalid
           (s/conform ::spec/page {}))))

  (testing "location"
    (let [conformed       (s/conform ::spec/page data/location)
          [type location] (messaging conformed)]
      (is (= :location
             type))
      (is (= (messaging data/location)
             location))))

  (testing "corrupted location file"
    (let [data            (update-in data/location
                                     [:entry 0 :messaging 0]
                                     dissoc :sender)
          conformed       (s/conform ::spec/page data)
          [type location] (messaging conformed)]
      (is (= :unknown
             type))
      (is (= (messaging data)
             location))))

  (testing "postback"
    (let [conformed       (s/conform ::spec/page data/postback)
          [type postback] (messaging conformed)]
      (is (= :postback
             type))
      (is (= {:sender    {:id "2192766900756024"}
              :recipient {:id "322784211649293"}
              :timestamp 1545572708246
              :postback
              {:payload :pan-toksyczny.fb.core/aqi
               :title   "AQI?"}}
             postback)))))
