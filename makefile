# Any tasks that don't create a file should be listed in .PHONY
.PHONY: codox

codox:
	rm -rf docs
	lein codox
	mkdir docs
	mv target/doc/* docs
	rm -rf target/doc