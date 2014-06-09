SOURCES = $(wildcard src/*.cpp)
OBJECTS = $(SOURCES:.cpp=.o)
CFLAGS = -std=c++11
LDFLAGS = -L/usr/local/lib
LDLIBS = -lcrypto -lssl
EXEC = bin/phone-sms-gate-api
	
$(EXEC) : $(OBJECTS)
	mkdir -p bin
	@g++ $(LDFLAGS) $(OBJECTS) -o $(EXEC) $(LDLIBS)

%.o: %.cpp
	@g++ $(CFLAGS) -c $< -o $@

clean:
	@-rm phone-sms-gate-api.o 2>/dev/null || true
	@-rm $(OBJECTS) 2>/dev/null || true
	@-rm $(TEST_OBJECTS) 2>/dev/null || true
	@-rm $(EXEC) 2>/dev/null || true
