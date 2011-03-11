CC=g++
#CFLAGS=-c -Wall -g -Isrc
CFLAGS=-c -O3 -Isrc
LDFLAGS=-lm -lcomplexnet -L.
LIBSOURCES=src/CLParser.cpp src/Generator.cpp src/Params.cpp src/Network.cpp src/Node.cpp src/Histogram2D.cpp src/GA.cpp src/emd.cpp
LIBOBJECTS=$(LIBSOURCES:.cpp=.o)
LIB=libcomplexnet.a

PROGS=evc netgen evonet

all: $(LIBSOURCES) $(LIB) $(PROGS)
    
$(LIB): $(LIBOBJECTS) 
	ar rcs $(LIB) $(LIBOBJECTS)

.cpp.o:
	$(CC) $(CFLAGS) $< -o $@

clean:
	rm -f src/*.o $(LIB) $(PROGS)

evc: $(LIB) src/evc.o
	$(CC) src/evc.o $(LDFLAGS) -o $@
netgen: $(LIB) src/netgen.o
	$(CC) src/netgen.o $(LDFLAGS) -o $@
evonet: $(LIB) src/evonet.o
	$(CC) src/evonet.o $(LDFLAGS) -o $@
