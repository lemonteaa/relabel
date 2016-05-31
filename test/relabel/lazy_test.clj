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
  (let [in1 { :username "nick" :age 34 :college "abc" }
        res1 { :name "nick" :age 34 :school "abc" }
        in2 { :username "mary"
              :submissions [{ :title "abc" :keyword "lab" :submission-date nil }
                            { :title "def" :keyword "Dunning-Kruger effect" :submission-date "44" }] }
        res2 { :name "mary"
               :posts [{ :title "abc" :hashtag "lab" :date nil }
                       { :title "def" :hashtag "Dunning-Kruger effect" :date "44" }] }]
    (is (= res1 ((converter { :name (from :username)
                              :age (from :age)
                              :school (from :college) }) in1)))
    (is (= res2 ((converter { :name (from :username)
                              :posts (comp (partial map (converter { :title (from :title)
                                                                     :hashtag (from :keyword)
                                                                     :date (from :submission-date) }))
                                           (from :submissions)) }) in2)))))