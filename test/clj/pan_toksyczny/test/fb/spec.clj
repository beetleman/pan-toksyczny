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
    (is (= (s/conform ::spec/page {})
           ::s/invalid)))

  (testing "location"
    (let [conformed       (s/conform ::spec/page data/location)
          [type location] (messaging conformed)]
      (is (= type :location))
      (is (= location (messaging data/location)))))

  (testing "corrupted location file"
    (let [data            (update-in data/location
                                     [:entry 0 :messaging 0]
                                     dissoc :sender)
          conformed       (s/conform ::spec/page data)
          [type location] (messaging conformed)]
      (is (= type :unknown))
      (is (= location (messaging data)))))

  (testing "postback"
    (let [conformed       (s/conform ::spec/page data/postback)
          [type postback] (messaging conformed)]
      (is (= type :postback))
      (is (= postback (messaging data/postback))))))
