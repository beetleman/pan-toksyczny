(ns pan-toksyczny.fb.preprocessing)

(defn get-coordinates [attachemnts]
  (->> attachemnts
       (map #(get-in % [:payload :coordinates]))
       (filter identity)
       first))

(defn message-type [message]
  (let [attachments (get-in message [:message :attachments])
        text        (get-in message [:message :text])
        coordinates (get-coordinates attachments)
        new-message (cond
                      coordinates
                      {::type ::coordinates
                       ::data coordinates}

                      text
                      {::type ::text
                       ::data text}

                      :default
                      {})]
    (merge message new-message)))
