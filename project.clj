(defproject latex-helper "0.1.0"
  :description "A tiny clojure library with some helpers for writing LaTeX, e.g. escaping special characters, and turning dumb quotes to smart quotes."
  :url "https://github.com/jtlocsei/latex-helper"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]]
  :repl-options {:init-ns latex-helper.core}
  :plugins [[lein-codox "0.10.7"]])
