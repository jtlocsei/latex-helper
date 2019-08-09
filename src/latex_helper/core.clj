(ns latex-helper.core
  (:require [clojure.string :as str]))


(def ^:private char-escapes
  {\# "{\\hashchar}"
   \$ "{\\dollarchar}"
   \% "{\\percentchar}"
   \& "{\\ampersandchar}"
   \~ "{\\tildechar}"
   \_ "{\\underscorechar}"
   \^ "{\\circumflexchar}"
   \\ "{\\backslashchar}"
   \{ "{\\leftbracechar}"
   \} "{\\rightbracechar}"})

(comment (get char-escapes \#))


(defn escape-string
  "Escape special characters in a string so that it's safe to use inside a LaTeX document.
  NOT idempotent. Assumes that there are no escaped chars in the string yet. "
  [s]
  (->> s
       (map char-escapes ,,,)
       (apply str ,,,)))

(comment
  (println (escape-string "#$%&~_^\\{}"))
  ; {\hashchar}{\dollarchar}{\percentchar}{\ampersandchar}{\tildechar}{\underscorechar}{\circumflexchar}{\backslashchar}{\leftbracechar}{\rightbracechar}

  (escape-string nil)
  ; => ""
  :pass)


(defn ^:private curly-quotes->dumb-quotes
  "Take curly quotes like ‘ ’  “ ” and replace them with their
  straight quotes equivalents"
  [s]
  (-> s
    (str/replace "‘" "'")
    (str/replace "’" "'")
    (str/replace "“" "\"")
    (str/replace "”" "\"")))

(comment
  (curly-quotes->dumb-quotes "a ‘big’ “bright” day")) ; => "a 'big' \"bright\" day"

(defn ^:private dumb-quotes->smart-html-quotes
  "Convert dumb quotes in a string to smart html quotes.
  Based on https://gist.github.com/davidtheclark/5521432"
  [s]
  (-> s
      ; Find dumb double quotes coming directly after letters or punctuation,
      ; and replace them with right double quotes.
      (str/replace ,,, #"([a-zA-Z0-9.,?!;:\"\'])\"" "$1&#8221;")
      ; Find any remaining dumb double quotes and replace them with
      ; left double quotes.
      (str/replace ,,, #"\"" "&#8220;")
      ; # Follow the same process with dumb/smart single quotes
      (str/replace ,,, #"([a-zA-Z0-9.,?!;:\"\'])'" "$1&#8217;")
      (str/replace ,,, #"'" "&#8216;")))

(dumb-quotes->smart-html-quotes "This is \"not\" fun")

; TODO Apply curly-quotes->dumb-quotes, then dumb-quotes->smart-html-quotes, then smart-html-quotes->smart-latex-quotes
; TODO write a multi-replace function and simplify the multiple calls to str/replace with a single call of multi-replace.