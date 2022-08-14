(ns code-challenge.core
  (:use clojure.pprint)
  (:require [code-challenge.read-json :as json]
            [code-challenge.validations :as validations]
            [code-challenge.outputs :as outputs]))

(defn merge-violation [violations invalid violation-tag]
  (if invalid
    (conj violations violation-tag)
    violations))

(defn conditions-of-account [history current-operation]
  "Rules of a valid account"
  (cond (validations/account-already-initialized? history) (outputs/account-initialized history)
        (validations/account-not-initialized? history) (outputs/account-not-initialized current-operation)
        (validations/card-already-active? history) (outputs/card-active current-operation)
        (validations/card-not-active? history) (outputs/card-not-active history)))


(defn check-transaction [history current-operation]
  "Validates transaction rules"
  (if (validations/account-create-operation? current-operation)
    (conditions-of-account history current-operation)
    (let [violations (-> []
                         (merge-violation (validations/account-not-initialized? history) "account-not-initialized")
                         (merge-violation (validations/card-not-active? history) "card-not-active")
                         (merge-violation (validations/double-transaction?) "double-transaction")
                         (merge-violation (validations/high-frequency-small-interval?) "high-frequency-small-interval")
                         (merge-violation (validations/insufficient-limit? history current-operation) "insufficient-limit"))
          output (if (> (count violations) 0)
                   (validations/contains-violations history violations)
                   (validations/not-contains-violations history current-operation))] output)))

(defn process-transaction [state current]
  "Process transaction data"
  (let [output (check-transaction state current)]
    (-> state
        (update-in [:input] conj current)
        (update-in [:output] conj output))))

(defn init-system []
  "Reduces process-transactions and updates input and output data"
  (reduce process-transaction {:input [] :output []} (seq (json/reading-json))))

(pprint (:input (init-system)))
(pprint (:output (init-system)))
