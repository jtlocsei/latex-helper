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


(defn ^:private multi-replace
  "Given a seq of [match replacement] pairs applies all replacements in order
  to the given string"
  [s match-replacement-pairs]
  (reduce (fn [s1 [match replacement]]
            (str/replace s1 match replacement))
          s match-replacement-pairs))

(comment
  (multi-replace "foo bar baz zod" [["bar" "BAR"]
                                    ["baz" "BAZ"]]))

(defn ^:private curly-quotes->dumb-quotes
  "Take curly quotes like ‘ ’  “ ” and replace them with their
  straight quotes equivalents"
  [s]
  (multi-replace s [["‘" "'"]
                    ["’" "'"]
                    ["“" "\""]
                    ["”" "\""]]))

(comment
  (println (curly-quotes->dumb-quotes "a ‘big’ “bright” day"))) ; a 'big' "bright" day


(defn ^:private dumb-quotes->smart-html-quotes
  "Convert dumb quotes in a string to smart html quotes.
  Based on https://gist.github.com/davidtheclark/5521432"
  [s]
  (multi-replace s
                 ; Find dumb double quotes coming directly after letters or punctuation,
                 ; and replace them with right double quotes.
                 [[#"([a-zA-Z0-9.,?!;:\"\'])\"" "$1&#8221;"]
                  ; Find any remaining dumb double quotes and replace them with
                  ; left double quotes.
                  [#"\"" "&#8220;"]
                  ; # Follow the same process with dumb/smart single quotes
                  [#"([a-zA-Z0-9.,?!;:\"\'])'" "$1&#8217;"]
                  [#"'" "&#8216;"]]))

(comment
  (dumb-quotes->smart-html-quotes "This is \"super\" 'duper' fun")
  ; => "This is &#8220;super&#8221; &#8216;duper&#8217; fun"
  :pass)

; TODO Apply curly-quotes->dumb-quotes, then dumb-quotes->smart-html-quotes, then smart-html-quotes->smart-latex-quotes
