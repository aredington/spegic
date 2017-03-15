(ns spegic.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [spegic.core :as spegic]
            [clojure.spec :as s]
            [clojure.core.logic :as logic]
            [clojure.core.logic.fd :as fd]
            [clojure.test.check.generators :as gen]))

(defn even-into
  [int]
  (logic/fresh [z]
    (fd/quot int 2 z)
    (fd/in int (fd/interval 0 Integer/MAX_VALUE))))

(s/def ::even-int-relation-test (spegic/logic-spec-impl even-into))

(deftest even-into-test
  (testing "Conforms with an even int"
    (is (not= ::invalid (s/conform ::even-int-relation-test 2)))
    (is (= 2 (s/conform ::even-int-relation-test 2))))
  (testing "Does not conform with an odd int"
    (is (= ::s/invalid (s/conform ::even-int-relation-test 1))))
  (testing "Creates a valid generator"
    (is (even? (gen/generate (s/gen ::even-int-relation-test))))))
