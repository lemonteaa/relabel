(ns relabel.lazy-test
  (:require [clojure.test :refer :all]
            [relabel.lazy :refer :all]))

(deftest from-modifier
  (are [value] (= value ((from :foo) {:bar 3 :foo value}))
       4
       nil
       [1 2 3])
  (is (thrown? AssertionError ((from :baz) {:bar 1 :foo "c"}))))

(deftest one-or-more-modifier
  (let [enriched-f (one-or-more (converter { :hello (from :world)
                                             :test (from :tee) }))]
    (is (= { :hello 1 :test 3 } (enriched-f { :world 1 :tee 3 })))
    (is (= [{ :hello 12 :test 5 } { :hello 27 :test 9 }]
           (enriched-f [{ :world 12 :tee 5 } { :world 27 :tee 9 }])))))

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
                              :posts (comp (one-or-more (converter { :title (from :title)
                                                                     :hashtag (from :keyword)
                                                                     :date (from :submission-date) }))
                                           (from :submissions)) }) in2)))))

(deftest converter-nesting
  (let [in { :username "Brown" :loc-country "Switzerland" :loc-region "Aargau"
             :loc-rest "test 1234" :contact-phone "43622556" }
        res { :name "Brown"
              :address { :country "Switzerland"
                         :region "Aargau"
                         :location "test 1234" }
              :phone "43622556" }]
    (is (= res ((converter { :name (from :username)
                             :address { :country (from :loc-country)
                                        :region (from :loc-region)
                                        :location (from :loc-rest) }
                             :phone (from :contact-phone) }) in)))))
