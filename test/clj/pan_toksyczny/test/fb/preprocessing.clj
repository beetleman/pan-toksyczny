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
    (is (= [{:id                                 "322784211649293",
             :time                                1545572708246,
             :sender                              {:id "2192766900756024"},
             :recipient                           {:id "322784211649293"},
             :timestamp                           1545572708246,
             :payload                             :pan-toksyczny.fb.core/aqi,
             :title                               "AQI?",
             :pan-toksyczny.fb.preprocessing/type :postback}]
           (preprocessing/message-flatten conformed-postback))))

  (testing "flatten location message"
    (is (= [{:id                  "322784211649293",
             :time                1545574887587,
             :sender              {:id "2192766900756024"},
             :recipient           {:id "322784211649293"},
             :timestamp           1545574886717,
             :lat                 48.2242784,
             :long                12.2228064,
             ::preprocessing/type :location}]
           (preprocessing/message-flatten conformed-location)))))
