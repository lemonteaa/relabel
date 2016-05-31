# relabel

An (almost) trivial declarative domain converter based on the idea of doing destructuring in reverse.

## Note

This is currently alpha-quality. Also, as the problem this library is trying to solve (clean declarative domain conversion) appear difficult when one begin to consider all sorts of edge case (or just not-so-basic usage), one namespace is allocated for each solution approach.

## Usage

```clojure
[lemonteaa/relabel "0.0.1"]

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

See unit tests for more advanced examples.

## TODO

- Support something like XPath for modifier from (use Specter?)
- Support nested map for schema declaration in converter
- Control on strict/loose setting when key cannot be mapped

## License

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
