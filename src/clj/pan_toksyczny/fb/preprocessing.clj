(ns pan-toksyczny.fb.preprocessing)

(defn get-coordinates [attachemnts]
  (->> attachemnts
       (map #(get-in % [:payload :coordinates]))
       (filter identity)
       first))


(defn reduce-level [m keys]
  (merge (dissoc m (first keys)) (get-in m keys)))


(defmulti -messaging-flatten (fn [[type _] _] type))

(defmethod -messaging-flatten :postback
  [[type itm] ctx]
  (merge ctx (reduce-level itm [:postback])
         {::type type}))

(defmethod -messaging-flatten :location
  [[type itm] ctx]
  (merge ctx (reduce-level itm [:message
                                :attachments
                                0
                                :payload
                                :coordinates])
         {::type type}))

(defmethod -messaging-flatten :default
  [[type _] _]
  {::type type})


(defn- messaging-flatten [{messaging :messaging :as entry}]
  (let [ctx (dissoc entry :messaging)]
    (map #(-messaging-flatten % ctx) messaging)))


(defn message-flatten [{entry :entry}]
  (mapcat messaging-flatten entry))
