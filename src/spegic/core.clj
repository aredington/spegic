(ns spegic.core
  (:require [clojure.core.logic :as logic]
            [clojure.spec :as spec]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.rose-tree :as rose]))

(defn logic-spec-impl
  "Returns a core.spec impl based on the logical relation `logic-spec`."
  [logic-spec]
  (reify
    spec/Specize
    (specize* [s] s)
    (specize* [s _] s)
    spec/Spec
    (conform* [_ x]
      (let [ret (logic/run 1 [_] (logic-spec x))]
        (if (seq ret)
          x
          ::spec/invalid)))
    (unform* [_ x] x)
    (explain* [_ path via in x] [])
    (gen* [_ _ _ _]
      (let [solutions (atom
                       (cycle
                        (logic/run* [q] (logic-spec q))))]
        (gen/->Generator
         (fn [_ _]
           (let [ret (first @solutions)]
             (swap! solutions rest)
             (rose/pure ret))))))
    (with-gen* [_ gfn] nil)
    (describe* [_] nil)))
