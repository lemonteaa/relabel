(ns relabel.lazy-test
  (:require [clojure.test :refer :all]
            [relabel.lazy :refer :all]))

(deftest literal-modifier
  (are [value in] (= ((converter { :note (literal value) }) in)
                     { :note value })
       [1 2 3] { :foo "test" }
       [1 2 3] { :note "abc" }
       "hi" { :foo "test" }
       "hi" { :note "abc" }
       55 { :foo "test" }
       55 { :note "abc" }
       nil { :foo "test" }
       nil { :note "abc" }))

(deftest from-modifier-main
  (are [value] (= value ((from :foo) {:bar 3 :foo value}))
       4
       nil
       [1 2 3])
  (is (thrown? AssertionError ((from :baz) {:bar 1 :foo "c"})))
  ; Testing optional arg 'then'
  (is (thrown? AssertionError ((from :baz :then 3) {:baz 2})))
  (is (= 42 ((from :hey :then #(+ % 7)) {:hey 35}))))

(deftest from-modifier-config
  (let [in1 { :url "test.com" :param-foo "16" }
        res1 16
        in2 { :url "side.com" :param-foo ["16" "43" "61"] }
        res2 [16 43 61]
        f (from :param-foo :then #(Integer/parseInt %))]
    (is (= res1 (f in1)))
    (is (= res2 (f in2)))
    (binding [*config* { :automap-seq false }]
      (is (= res1 (f in1)))
      (is (thrown? Exception (f in2))))))

(deftest one-or-more-modifier
  (let [enriched-f (one-or-more (converter { :hello (from :world)
                                             :test (from :tee) }))]
    (is (= { :hello 1 :test 3 } (enriched-f { :world 1 :tee 3 })))
    (is (= [{ :hello 12 :test 5 } { :hello 27 :test 9 }]
           (enriched-f [{ :world 12 :tee 5 } { :world 27 :tee 9 }])))))

(deftest converter-basic
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

;(deftest converter-automap
;  (let [in1 { :url "test.com" :param-foo "16" }
;        res1 { :loc "test.com" :data 16 }
;        in2 { :url "side.com" :param-foo ["16" "43" "61"] }
;        res2 { :loc "side.com" :data [16 43 61] }
;        conv (converter { :loc (from :url)
;                          :data (comp (Integer/parseInt)
;                                      (from :param-foo)) })]
;    (is (= res1 (conv in1)))
;    (is (= res2 (conv in2)))))
