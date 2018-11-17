(ns pan-toksyczny.test.handler
  (:require [clojure.test :refer :all]
            [ring.mock.request :refer :all]
            [pan-toksyczny.handler :refer :all]
            [pan-toksyczny.middleware.formats :as formats]
            [muuntaja.core :as m]
            [mount.core :as mount]))

(defn parse-json [body]
  (m/decode formats/instance "application/json" body))

(use-fixtures
  :once
  (fn [f]
    (mount/start #'pan-toksyczny.config/env
                 #'pan-toksyczny.handler/app)
    (f)))

(deftest test-app
  (testing "main route"
    (let [response (app (request :get "/"))]
      (is (= 301 (:status response)))))

  (testing "not-found route"
    (let [response (app (request :get "/invalid"))]
      (is (= 404 (:status response))))))
