(ns code-challenge.outputs)

(defn account-initialized [history]
  {:account    {:active-card     (:active-card (last (:output history)))
                :available-limit (:available-limit (last (:output history)))}
   :violations ["account-already-initialized"]})

(defn account-not-initialized [current-operation]
  {:account    {:active-card     (get-in current-operation [:account :active-card])
                :available-limit (get-in current-operation [:account :available-limit])}
   :violations []})

(defn card-active [history]
  {:account    {:active-card     (:active-card (last (:output history)))
                :available-limit (:available-limit (last (:output history)))}
   :violations []})

(defn card-not-active [current-operation]
  {:account    {:active-card     (get-in current-operation [:account :active-card])
                :available-limit (get-in current-operation [:account :available-limit])}
   :violations ["card-not-active"]})