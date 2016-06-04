(ns lt.plugins.lt-css-lint
  (:require [lt.object :as object])
  (:require-macros [lt.macros :refer [behavior]]))

(object/object* ::css-lint
                :behaviors [::do-css-lint]
                :linter-name "csslint"
                :timeout 20000)

(defn- ->lint-message
  [{:keys [type line col message evidence rule]}]
  (let [to (if-not (clojure.string/blank? evidence)
             (+ (dec col) (count evidence))
             col)]
    {:message (str message "\n (Rule: " (:name rule) ")")
     :severity (keyword type)
     :from [(dec line) (dec col)]
     :to [(dec line) to]}))

(behavior ::do-css-lint
          :triggers #{:lt.plugins.lt-lint/validate}
          :reaction (fn [obj editor-text callback _]
                      (let [{:keys [messages]} (js->clj (.verify js/CSSLint editor-text) :keywordize-keys true)]
                        (callback (map ->lint-message messages)))))

