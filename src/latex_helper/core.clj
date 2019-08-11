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
  The function is NOT idempotent, so don't apply it more than once to the same string.

  Examples:
    (escape-string \"$\")
    ; => \"{\\\\dollarchar}\"
    ; Success!

    (escape-string (escape-string \"$\"))
    ; => \"{\\\\leftbracechar}{\\\\backslashchar}{\\\\rightbracechar}\"
    ; Oops! Not idempotent.


    (escape-string \"#$%&~_^\\\\{}\")
    ; => \"{\\\\hashchar}{\\\\dollarchar}{\\\\percentchar}{\\\\ampersandchar}{\\\\tildechar}{\\\\underscorechar}{\\\\circumflexchar}{\\\\backslashchar}{\\\\leftbracechar}{\\\\rightbracechar}\"

    (escape-string nil)
    ; => \"\"
  "
  [s]
  (->> s
       (map char-escapes ,,,)
       (apply str ,,,)))

(comment
  (println (escape-string "#$%&~_^\\{}"))
  ; {\hashchar}{\dollarchar}{\percentchar}{\ampersandchar}{\tildechar}{\underscorechar}{\circumflexchar}{\backslashchar}{\leftbracechar}{\rightbracechar}

  (escape-string "#$%&~_^\\{}")
  ; =>
  ;"{\\hashchar}{\\dollarchar}{\\percentchar}{\\ampersandchar}{\\tildechar}{\\underscorechar}{\\circumflexchar}{\\backslashchar}{\\leftbracechar}{\\rightbracechar}"

  (escape-string nil)
  ; => ""

  (escape-string "$")
  ; => "{\\dollarchar}"
  ; Success!

  (escape-string (escape-string "$"))
  ; => "{\\leftbracechar}{\\backslashchar}{\\rightbracechar}"
  ; Oops! Not idempotent.
  :pass)


(defn- multi-replace
  "Given a seq of [match replacement] pairs applies all replacements in order
  to the given string"
  [s match-replacement-pairs]
  (reduce (fn [s1 [match replacement]]
            (str/replace s1 match replacement))
          s match-replacement-pairs))

(comment
  (multi-replace "foo bar baz zod" [["bar" "BAR"]
                                    ["baz" "BAZ"]]))


(defn- curly-quotes->dumb-quotes
  "Take curly quotes like ‘ ’  “ ” and replace them with their
  straight quotes equivalents"
  [s]
  (multi-replace s [["‘" "'"]
                    ["’" "'"]
                    ["“" "\""]
                    ["”" "\""]]))

(comment
  (println (curly-quotes->dumb-quotes "a ‘big’ “bright” day"))) ; a 'big' "bright" day


(defn- dumb-quotes->smart-html-quotes
  "Convert dumb quotes in a string to smart html quotes.
  Based on https://gist.github.com/davidtheclark/5521432"
  [s]
  (multi-replace s
                 [
                  ; Find dumb double quotes coming directly after letters or punctuation,
                  ; and replace them with right double quotes.
                  [#"([a-zA-Z0-9.,?!;:\"\'])\"" "$1&#8221;"]
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


(defn- smart-html-quotes->smart-latex-quotes
  "Convert a string that has smart html quotes to a string that has
  smart latex quotes"
  [s]
  (multi-replace s
                 [
                  ["&#8221;"   "''"]   ; closing double quote
                  ["&#8220;"   "``"]   ; opening double quote
                  ["&#8217;"   "'"]    ; closing single quote
                  ["&#8216;"   "`"]])) ; opening single quote


(defn smart-quotes
  "Given a string with dumb quotes or curly quotes, replace them with latex smart quotes.

  Examples:
    (smart-quotes \"They say “It's ‘forever’ a bug’s life” don’t they?\")
    ; => \"They say ``It's `forever' bug's life'' don't they?\"

    (smart-quotes \"They say \\\"It's 'forever' a bug's life\\\" don't they?\")
    ; => \"They say ``It's `forever' bug's life'' don't they?\""
  [s]
  (-> s
      curly-quotes->dumb-quotes
      dumb-quotes->smart-html-quotes
      smart-html-quotes->smart-latex-quotes))

(comment
  ; Check that it works on input with curly quotes:
  (smart-quotes "They say “It's ‘forever’ a bug’s life” don’t they?")
  ; => "They say ``It's `forever' bug's life'' don't they?"

  ; Check that it works on input with dumb quotes:
  (smart-quotes "They say \"It's 'forever' a bug's life\" don't they?")
  ; => "They say ``It's `forever' bug's life'' don't they?"
  :pass)


