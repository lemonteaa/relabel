(ns relabel.lazy-test
  (:require [clojure.test :refer :all]
            [relabel.lazy :refer :all]))

(deftest from-modifier
  (are [value] (= value ((from :foo) {:bar 3 :foo value}))
       4
       nil
       [1 2 3])
  (is (thrown? AssertionError ((from :baz) {:bar 1 :foo "c"}))))

(deftest converter-main
  (let [in { :username "nick" :age 34 :college "abc" }
        res { :name "nick" :age 34 :school "abc" }]
  (is (= res ((converter { :name (from :username)
                           :age (from :age)
                           :school (from :college) }) in)))))
