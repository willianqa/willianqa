(ns code-challenge.read-json
  (:use clojure.pprint)
  (:require [clojure.data.json :as json]))

(defn reading-json []
  "Essa função faz a leitura do json e transforma a chave em keyword"
  (json/read-str (slurp "src/code_challenge/json.json") :key-fn keyword))
(pprint (reading-json))

(def reading-json-atom (atom (reading-json)))

;(defn create-output
;  ([transacao]
;   (println "Executou com 1 param")
;   (if (:account transacao)
;     {:account {:active-card (get-in transacao [:account :active-card]), :available-limit (get-in transacao [:account :available-limit])}, :violations []}
;     {:account {} :violations ["account-not-initialized"]}))
;  ([ultima transacao]
;   (println "Executou com 2 param")
;
;   (if (:account transacao)
;     (if (= (get-in ultima [:account :active-card])true)
;       {:account {} :violations ["account-already-initialized"]}
;       (pprint "caiu aki"))
;     (pprint "caiu aki>>"))))
;
;(defn iterando [funcao transacoes]
;  (println ">>>>>") (pprint transacoes)
;
;  (if-let [primeiro (first transacoes)]
;   (let [ultima (funcao primeiro)]
;     (println ">>ultima>>>") (pprint ultima)
;
;     (recur (funcao ultima) (rest transacoes)))
;   ))
;
;(pprint (iterando create-output (seq (reading-json))))

(defn active-card [map]
  "Essa funcão filtra os cartões ativos"
  (filter #(= (-> % :account :active-card) true) map))

(defn create-account [line map]
  "Essa função faz a lógica da conta"
  (if (> (count (active-card map)) 1)
    (assoc line :violations ["account-already-initialized"])
    (assoc line :violations [])))

(defn update-limit [item]
  (if (get item :account)
    0
    (- (get-in item [:transaction :amount]) (get-in item [:account :available-limit]))))

;(pprint (update-limit reading-json))

(defn valid-limit [line map]
  "Essa função faz a lógica da transação"
  (let [transacoes (filter #(> (-> % :transaction :amount) :account :available-limit) map)]
    (if transacoes
      (assoc line :violations [])
      (assoc line :violations ["insuficient-limit"]))))


(defn process-line [line]
  "Essa função verifica se a linha é uma conta, ou uma transação"
  (cond
    (some? (:account line)) (create-account line (reading-json))
    (some? (:transaction line)) (valid-limit line (reading-json))))


(defn process-data []
  (map process-line (reading-json)))

(pprint (process-data))


















;(>= (:available-limit):amount) "limite maior ou igual ao valor da compra"
;(some? (:account)) "conta iniciada"
;(some? (active-card map)) "cartao ativo"
;(<= (:transaction)3) "max 3 transaçoes qqer comerciante no intervalo de 2 min"
;() "max 1 transacao similar(valor e comerciante) no intervalo de 2 min"





;(let [violations (cond-> [(reading-json)]
;                         true (assoc line :violations ["account-not-initialized"])
;                         true (assoc line :violations ["card-not-active"])
;                         true (assoc line :violations ["insuficient-limit"])
;                         true (assoc line :violations ["high-frequency-small-interval"])
;                         true (assoc line :violations ["double-transaction"]))]
;  pprint violations)




;
;(let [limit   (get-in (reading-json) [0 :account :available-limit])
;      amount  (get-in (reading-json) [1 :transaction :amount])
;      calcula (- limit amount)]
;
;  pprint calcula)