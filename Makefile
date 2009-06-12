TARGETS = dist clean test

doxygen:
	ant doc

$(TARGETS):
	ant $@

install:
	return;

default:
	test

.PHONY: clean