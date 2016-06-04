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
;; => raise AssertionError
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

## TODO

- Support something like XPath for modifier from (use Specter?)
- ~~Support nested map for schema declaration in converter~~
- Control on strict/loose setting when key cannot be mapped

## License

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
