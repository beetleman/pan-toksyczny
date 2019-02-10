(ns pan-toksyczny.air-quality.interpreter)


(def health-levels
  "data from http://waqi.info/"
  {:good           {:level  "Good"
                    :emoji  "🌱"
                    :health "Air quality is considered satisfactory, and air pollution poses little or no risk"}
   :moderate       {:level  "Moderate"
                    :emoji  "👌"
                    :health "Air quality is acceptable; however, for some pollutants there may be a moderate health concern for a very small number of people who are unusually sensitive to air pollution."}
   :sensitive      {:level  "Unhealthy for Sensitive Groups"
                    :emoji  "🤔"
                    :health "Members of sensitive groups may experience health effects. The general public is not likely to be affected."}
   :unhealthy      {:level  "Unhealthy"
                    :emoji  "😞"
                    :health "Everyone may begin to experience health effects; members of sensitive groups may experience more serious health effects"}
   :very-unhealthy {:level  "Very Unhealthy"
                    :emoji  "👎"
                    :health "Health warnings of emergency conditions. The entire population is more likely to be affected."}
   :hazardous      {:level  "Hazardous"
                    :emoji  "☢️"
                    :health "Health alert: everyone may experience more serious health effects"}})


(defn aqi->keyword [aqi]
  (condp > aqi
      50 :good
      100 :moderate
      150 :sensitive
      200 :unhealthy
      300 :very-unhealthy
      :hazardous))

(defn aqi-> [fn aqi]
  (-> aqi
      aqi->keyword
      health-levels
      fn))

(defn aqi->level [aqi]
  (aqi-> :level aqi))

(defn aqi->health [aqi]
  (aqi-> :health aqi))

(defn aqi->emoji [aqi]
  (aqi-> :emoji aqi))


(defn aqi->text [aqi]
  (str
   (aqi->emoji aqi) (aqi->level aqi) "(aqi: " aqi ")" ".\n"
   (aqi->health aqi)))


(def iaqi-keys-mapping
  {:pm25 "PM2.5:"
   :pm10 "PM10:"
   :o3   "O₃:"
   :no2  "NO₂:"
   :so2  "SO₂:"
   :co   "CO:"})

(defn aqi-data->text [{dominentpol :dominentpol
                       iaqi        :iaqi}]
  (let [dominentpol-key (keyword dominentpol)]
    (clojure.string/join (->> iaqi
                              (map (fn [[key {value :v}]]
                                     (when-let [name (iaqi-keys-mapping key)]
                                       (format (if (= key dominentpol-key)
                                                 "%-10s %s!\n"
                                                 "%-10s %s\n")
                                               name value))))
                              (filter identity)))))
