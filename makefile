# Any tasks that don't create a file should be listed in .PHONY
.PHONY: codox

codox:
	rm -rf doc
	lein codox
	mkdir doc
	mv target/doc/* doc
	rm -rf target/doc