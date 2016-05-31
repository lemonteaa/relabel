(ns relabel.lazy
  (:require [clojure.test :as clj-test]))

;;; Sketch of idea:
;;; A declarative domain converter differs from a straight program
;;; only by having the shape of the target object specified as a
;;; map, with the values filled in at "run-time" by lazily
;;; evaluating (i.e. on demand) the associated functions that
;;; generate values. Hence the namespace "lazy".
;;;
;;; It seems to me that the main pro of this approach is the
;;; flexibility and power of function as a way to fill in values.
;;;
;;; Of course, per README, one can also look at this as simply
;;; destructuring in reverse.

(defn converter [domain]
  (fn [x]
    (into {} (for [[k v] x]
               (cond
                 (clj-test/function? v) [k (v x)]
                 :else (throw (Exception. "Unknown value type")))))))

(defn from [label]
  (fn [x]
    {:pre [(contains? x label)]}
    (get x label)))
