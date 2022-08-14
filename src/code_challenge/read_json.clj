(ns code-challenge.read-json
  (:require [clojure.data.json :as json]))

(defn reading-json []
  "Essa função faz a leitura do json e transforma a chave em keyword"
  (json/read-str (slurp "src/code_challenge/json.json") :key-fn keyword))