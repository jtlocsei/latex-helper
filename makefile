# Any tasks that don't create a file should be listed in .PHONY
.PHONY: docs help clojars

# help should be the first target so that is the default
help:  ## List commands and what they do
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

docs: ## Use codox to build documentation into the docs folder. Overwrites all docs contents.
	lein codox
	rm -rf docs
	mkdir docs
	mv target/doc/* docs

clojars: docs ## Push the project to clojars
	lein deploy clojars