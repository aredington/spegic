(ns spegic.core
  (:require [clojure.core.logic :as logic]
            [clojure.spec :as spec]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.rose-tree :as rose]))

(defn logic-spec-impl
  "Returns a core.spec impl based on the logical relation `logic-spec`."
  [form logic-spec]
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
    (explain* [this path via in x]
      (when (spec/invalid? (spec/conform* this x))
        [{:path path :pred form :val x :via via :in in}]))
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
    (describe* [_] form)))

(defmacro spec
  "Take a single relation form, e.g. can be the name of a relation,
  like emptyo, or a relation literal. The relation should be unary;
  that is it succeeds by measuring a single argument. If the form is
  properly relational, it will imply a generator by running the
  relation to find successive values that succeed. If the relation has
  a finite number of successful goal values, generation will cycle
  through them repeatedly."
  [form]
  (when form
    `(logic-spec-impl '~(#'clojure.spec/res form) ~form)))
