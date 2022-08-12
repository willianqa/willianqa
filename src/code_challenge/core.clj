(ns code-challenge.core
  (:use clojure.pprint)
  (:require [clojure.data.json :as json]
            [java-time :as jt]))

(defn reading-json []
  "Essa função faz a leitura do json e transforma a chave em keyword"
  (json/read-str (slurp "src/code_challenge/json.json") :key-fn keyword))
(pprint (reading-json))

(defn account-create-operation? [operation]
  "Verifica se é uma operação de criação da conta"
  (if (:account operation) true
                           false))

(defn account-initialized? [history]
  "Verifica se a conta já foi inicializada"
  (if (> (count (filter #(:account %) (:input history))) 0) true
                                                            false))

(defn account-not-initialized? [history]
  "Conta nao inicializada?"
  (if (false? (account-initialized? history)) true
                                              false))
(defn account-already-initialized? [history]
  "Conta inicializada?"
  (if (account-initialized? history) true
                                     false))

(defn card-not-active? [history]
  "Cartao inativo"
  (if (false? (= (get-in (last (:output history)) [:account :active-card]) true)) true
                                                                 false))
(defn card-active? [history]
  "Cartao ativo"
  (if (= (get-in (last (:output history)) [:account :active-card]) true) true
                                                        false))
(defn double-transaction? []
  false)

(defn high-frequency-small-interval? []
  false)

(defn insufficient-limit? [history current-operation]
  "Valida se o valor do limite é maior ou igual ao valor da compra"
  (if (<= (get-in (last (:output history)) [:account :available-limit])
        (get-in current-operation [:transaction :amount]))true
                                                          false))

(defn merge-violation [violations invalid violation-tag]
  (if invalid
    (conj violations violation-tag)
    violations))

(defn account-initialized [history]
  {:account    {:active-card     (:active-card (last (:output history)))
                :available-limit (:available-limit (last (:output history)))}
   :violations ["account-already-initialized"]})

(defn account-not-initialized [current-operation]
  {:account    {:active-card     (get-in current-operation [:account :active-card])
                :available-limit (get-in current-operation [:account :available-limit])}
   :violations []})

(defn contains-violations [history violations]
  {:account    {:active-card  (get-in (last (:output history)) [:account :active-card])
                :available-limit (get-in (last (:output history)) [:account :available-limit])}
   :violations violations})

(defn not-contains-violations [history current-operation]
  {:account    {:active-card (get-in (last (:output history)) [:account :active-card])
                :available-limit
                (- (get-in (last (:output history)) [:account :available-limit])
                   (get-in current-operation [:transaction :amount]))}
   :violations []})

(defn card-active [history]
  {:account    {:active-card     (:active-card (last (:output history)))
                :available-limit (:available-limit (last (:output history)))}
   :violations []})

(defn card-not-active [current-operation]
  {:account    {:active-card     (get-in current-operation [:account :active-card])
                :available-limit (get-in current-operation [:account :available-limit])}
   :violations ["card-not-active"]})

(defn check-transaction [history current-operation]

  (if (account-create-operation? current-operation)
    (cond (account-already-initialized? history) (account-initialized history)
          (account-not-initialized? history) (account-not-initialized current-operation)
          (card-active? history) (card-active current-operation)
          (card-not-active? history) (card-not-active history))
    (let [violations (-> []
                         (merge-violation (account-not-initialized? history) "account-not-initialized")
                         (merge-violation (card-not-active? history) "card-not-active")
                         (merge-violation (double-transaction?) "double-transaction")
                         (merge-violation (high-frequency-small-interval?) "high-frequency-small-interval")
                         (merge-violation (insufficient-limit? history current-operation) "insufficient-limit"))
          output (if (> (count violations) 0)
                   (contains-violations history violations)
                   (not-contains-violations history current-operation))] output)))

(defn process-trans [state current]
  (let [output (check-transaction state current)]
    (-> state
        (update-in [:input] conj current)
        (update-in [:output] conj output))))



(defn init-system []
  (reduce process-trans {:input [] :output []} (seq (reading-json))))
(pprint (:output (init-system)))