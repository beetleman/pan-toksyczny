(ns pan-toksyczny.test.data
  (:require [cheshire.core :refer [parse-string]]))

(defn load-json [path]
  (-> path
      slurp
      (parse-string true)))

(def location (load-json "./test/clj/pan_toksyczny/test/data/location.json"))
(def postback (load-json "./test/clj/pan_toksyczny/test/data/postback.json"))
