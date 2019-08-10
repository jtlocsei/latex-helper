# Any tasks that don't create a file should be listed in .PHONY
.PHONY: codox

codox:
	rm -rf doc/codox
	lein codox
	mkdir -p doc/codox
	mv target/doc/* doc/codox
	rm -rf target/doc