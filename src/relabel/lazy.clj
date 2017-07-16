(ns relabel.lazy
  (:require [clojure.test :as clj-test]
            [com.rpl.specter :as spct]))

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

(def ^:dynamic *config* { :automap-seq true :strict true })

(defn literal [c]
  (constantly c))

(defn one-or-more [f]
  (fn [x]
    (if (sequential? x)
      (map f x)
      (f x))))

(defn- extract-by-label [x label default]
  [(contains? x label)
   (get x label default)])

(defn- extract-by-selector [x path default]
  (let [y (spct/select* path x)
        has-one-elem (and (vector? y) (= (count y) 1))
        [v] y]
    [has-one-elem
     (if has-one-elem v default)]))

; Credit: http://stackoverflow.com/questions/24443985/get-replacement-that-throws-exception-on-not-found
(defn from [label & {:keys [default then automap?]
                     :or { default nil then identity automap? nil}
                     :as conf}]
  (fn [x]
    {:pre [(or (keyword? label) (vector? label))
           (clj-test/function? then)]}
    (let [[found? v] (if (vector? label)
                       (extract-by-selector x label default)
                       (extract-by-label x label default))
          loose-mode (or (not (:strict *config*)) (contains? conf :default))
          do-automap? (if (nil? automap?) (:automap-seq *config*) automap?)]
      (if-not (or found? loose-mode)
        (throw (Exception. "No matching value found in strict mode")))
      (if do-automap?
        ((one-or-more then) v)
        (then v)))))

(defn converter [domain]
  (fn [x]
    (into {} (for [[k v] domain]
               (cond
                 (clj-test/function? v) [k (v x)]
                 (map? v) [k ((converter v) x)]
                 :else (throw (Exception. "Unknown value type")))))))
