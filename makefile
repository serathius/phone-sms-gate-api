all :

test : tests/test_simple.cpp libgtest.a
	@g++ -isystem gtest-1.7.0/include -pthread tests/test_simple.cpp libgtest.a -o test.o

libgtest.a:
	@g++ -isystem gtest-1.7.0/include -I gtest-1.7.0  -pthread -c gtest-1.7.0/src/gtest-all.cc
	@ar -rv libgtest.a gtest-all.o

clean:
	@-rm test.o 2>/dev/null || true
	@-rm libgtest.a 2>/dev/null || true
	@-rm gtest-all.o 2>/dev/null || true

