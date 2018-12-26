(ns pan-toksyczny.test.fb.preprocessing
  (:require [clojure.spec.alpha :as s]
            [clojure.test :refer :all]
            [pan-toksyczny.fb.preprocessing :as preprocessing]
            [pan-toksyczny.fb.spec :as spec]
            [pan-toksyczny.test.data :as data]))


(def conformed-postback (s/conform ::spec/page data/postback))
(def conformed-location (s/conform ::spec/page data/location))

(deftest message-flatten
  (testing "flatten postback message"
    (let [flatted (preprocessing/message-flatten conformed-postback)]
      (is (coll? flatted))
      (is (= :postback (-> flatted first ::preprocessing/type)))
      (is (contains? (first flatted) :title))
      (is (contains? (first flatted) :payload))))

  (testing "flatten location message"
    (let [flatted (preprocessing/message-flatten conformed-location)]
      (println flatted)
      (is (coll? flatted))
      (is (= :location (-> flatted first ::preprocessing/type)))
      (is (contains? (first flatted) :lat))
      (is (contains? (first flatted) :long)))))
