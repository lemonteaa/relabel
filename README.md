# relabel

[![Build Status](https://travis-ci.org/lemonteaa/relabel.svg?branch=master)](https://travis-ci.org/lemonteaa/relabel)

An (almost) trivial declarative domain converter based on the idea of doing destructuring in reverse.

## Note

This is currently alpha-quality. Also, as the problem this library is trying to solve (clean declarative domain conversion) appear difficult when one begin to consider all sorts of edge case (or just not-so-basic usage), one namespace is allocated for each solution approach.

## Latest Version

![](https://clojars.org/lemonteaa/relabel/latest-version.svg)

## Usage

```clojure
;; In ns:
(ns my-project.core
  (:require [relabel.lazy :refer :all]))
```

Basic mapping:

```clojure
(def college-to-fb (converter { :username (from :name)
                                :age (from :age)
                                :school (from :college) }))

(college-to-fb { :name "Peter" :age 24 :college "test" })
;; => { :username "Peter" :age 24 :school "test" }
```

Schema can be nested:

```clojure
(def addressbook (converter { :name (from :username)
                              :address { :country (from :loc-country)
                                         :region (from :loc-region)
                                         :location (from :loc-rest) }}))

(addressbook { :username "Brown"
               :loc-country "Switzerland" :loc-region "Aargau" :loc-rest "test 1234" })
;; => { :name "Brown"
;;      :address { :country "Switzerland"
;;                 :region "Aargau"
;;                 :location "test 1234" }}
```

The modifiers serves as a form of DSL. For now we focus on providing their functional form (functions that either produce a function or transform another function) as they are more composable in this way. Macro could eventually be added once a good interface has solidified.

The `from` modifier extract values by key:

```clojure
((from :tags) { :title "Hello world!" :tags ["test" "first post"] })
;; => ["test" "first post"]

((from :tags) { :title "Nothing" :tags nil })
;; => nil

((from :tags) { :title "Another post" })
;; => raise Exception
```

To extract values nested in the source object, `from` also supports extraction through a [Specter](https://github.com/nathanmarz/specter) path:

```clojure
(require '[com.rpl.specter :as spct])

(let [data { :reqId 123
             :req { :type :buy-scissor
                    :remarks [{ :msg "Hello world!" :by "Chris" }
                              { :msg "Me too" :by "Katie" }
                              { :msg "We've got too many paper here, would be nice if we can cut 'em all ;)" :by "Carol" }
                              { :msg "So that we can throw a party?" :by "Cindy" }]
                    :by "Tom" }
             :state :pending }]
  ((from [:req :remarks spct/LAST :msg]) data))
;; => "So that we can throw a party?"
```

`from` accept an optional parameter `:then`, which is a function to apply to after extracting value:

```clojure
((from :tags :then #(clojure.string/join ", " %)) { :title "TDD in action" :tags ["TDD" "Best practice" "Experience Sharing"] })
;; => "TDD, Best practice, Experience Sharing"
```

It also accept an optional parameter `:default`, which can be used to avoid Exception if a matching value is not found, by using the value of the parameter instead:

```clojure
((from :tags :default "#blogging") { :title "Another post" })
;; => "#blogging"
```

Exception can also be avoided by using loose mode, see [Configs](#configs) section for details.

The `literal` modifier allows you to set constant/fixed field:

```clojure
(def test-convert (converter { :user-id (from :user-key)
                               :version (literal "2.3") }))

(test-convert { :user-key 4 })
;; => { :user-id 4 :version "2.3" }
(test-convert { :user-key 5 :version "1.2" })
;; => { :user-id 5 :version "2.3" }
```

The `one-or-more` modifier change a function so that it can deal with single object and a sequence in the same way:

```clojure
(def test-convert (converter { :foo (from :bar) }))

((one-or-more test-convert) {:bar 3})
;; => {:foo 3}
((one-or-more test-convert) [{:bar 3} {:bar 42}])
;; => [{:foo 3} {:foo 42}]
```

See unit tests for these examples.

## Configs

The library has a global config stored in the dynamic variable `*config*`, which is a map of configuration options vs value. One can either change them "permanantly" through the standard function `alter-var-root`, or apply change to a range of converters using the scoped version `binding`.

Currently there are two config options:

`:automap-seq` determines whether post-processing is applied flexibly to values extracted from source object's field in the `from` modifier. If true, then the function specified in the `:then` optional parameter will be changed with the `one-or-more` modifier before applying to extracted value.

```clojure
;; Suppose *config* is { :automap-seq false }

((from :param-vals :then #(Integer/parseInt %)) { :param-vals ["25" "4" "56"] })
;; => raise Exception
(binding [*config* { :automap-seq true }]
  ((from :param-vals :then #(Integer/parseInt %)) { :param-vals ["25" "4" "56"] })
  ;; => [25 4 56]
  ((from :param-vals :then #(Integer/parseInt %)) { :param-vals "116" })
  ;; => 116
)
```

To allow more fine-grained control, the config can also be specified in the `from` modifier itself through the optional parameter `:automap?`. As it is more specific, it will override the global config if present.

```clojure
;; Suppose *config* is { :automap-seq false }

((from :param-vals :then #(Integer/parseInt %) :automap? true)
  { :param-vals ["25" "4" "56"] })
;; => [25 4 56]
```

`:strict` controls the mode for the `from` modifier. It is in strict mode if `:strict` is true, and loose mode if false. In strict mode, `from` will raise an Exception if a value cannot be extracted from the object:

+ When selecting by keywords, this happens if the object has no key named by the keyword.
+ When selecting by a Specter path, this happens if there is no matching value or more than one match.

In loose mode, a default value of `nil` will be returned if there is no match. This default value can be overriden by the `:default` optional parameter. Note that if this parameter is present, loose mode will be used for that call of `from` even if we are in strict mode otherwise.

## TODO

- ~~Support something like XPath for modifier from (use Specter?)~~
- ~~Support nested map for schema declaration in converter~~
- ~~Control on strict/loose setting when key cannot be mapped~~

## Contact

This project is currently developed and maintained by @lemonteaa, which can be reached through email listed on the [github account](https://github.com/lemonteaa).

## License

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
