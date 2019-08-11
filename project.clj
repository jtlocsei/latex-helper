(defproject latex-helper "0.1.0"
  :description "A tiny library with some helpers for writing LaTeX"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]]
  :repl-options {:init-ns latex-helper.core}
  :plugins [[lein-codox "0.10.7"]]
  :codox {:doc-paths ["doc-src"]})
