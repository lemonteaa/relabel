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

;(def ^:dynamic *config* { :automap-seq true })

; Credit: http://stackoverflow.com/questions/24443985/get-replacement-that-throws-exception-on-not-found
(defn from [label & {:keys [then]
                     :or { then identity }}]
  (fn [x]
    {:pre [(contains? x label)
           (clj-test/function? then)]}
    (then (get x label))))

(defn one-or-more [f]
  (fn [x]
    (if (sequential? x)
      (map f x)
      (f x))))

(defn converter [domain]
  (fn [x]
    (into {} (for [[k v] domain]
               (cond
                 (clj-test/function? v) [k (v x)]
                 (map? v) [k ((converter v) x)]
                 :else (throw (Exception. "Unknown value type")))))))
