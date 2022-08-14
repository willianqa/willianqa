(ns code-challenge.validations
  (:use clojure.pprint)
  (:require [clojure.test :refer :all]))

(defn account-create-operation? [operation]
  "Check if it is an account creation operation"
  (if (:account operation) true
                           false))

(defn account-initialized? [history]
  "Check if the account has already been initialized"
  (if (> (count (filter #(:account %) (:input history))) 0) true
                                                            false))

(defn account-not-initialized? [history]
  "Validates if the account has not been initialized"
  (if (false? (account-initialized? history)) true
                                              false))
(defn account-already-initialized? [history]
  "Validates if the account has been initialized"
  (if (account-initialized? history) true
                                     false))

(defn card-active? [history]
  (= (get-in (last (:output history)) [:account :active-card]) true))

(defn card-not-active? [history]
  "Validates if there is any inactive card"
  (if (false? (card-active? history)) true
                                      false))
(defn card-already-active? [history]
  "Validates if there is an active card"
  (if (card-active? history) true
                             false))
(defn insufficient-limit? [history current-operation]
  "Validates if the threshold amount is greater than or equal to the purchase amount"
  (if (<= (get-in (last (:output history)) [:account :available-limit])
          (get-in current-operation [:transaction :amount])) true
                                                             false))

(defn high-frequency-small-interval? []
  false)

(defn double-transaction? []
  false)

(defn not-contains-violations [history current-operation]
  {:account    {:active-card (get-in (last (:output history)) [:account :active-card])
                :available-limit
                (- (get-in (last (:output history)) [:account :available-limit])
                   (get-in current-operation [:transaction :amount]))}
   :violations []})

(defn contains-violations [history violations]
  {:account    {:active-card     (get-in (last (:output history)) [:account :active-card])
                :available-limit (get-in (last (:output history)) [:account :available-limit])}
   :violations violations})



