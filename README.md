# Abstract Hybrid Petri Net Simulator
This is an open source simulator of hybrid Petri nets written in Java. It is released under the MIT license.

It was conceived to be as abstract and extensible as possible. That is, if the concept was done right, this software will be able to simulate every variety of Petri net that exists.

To ensure this, try to fit -Petri net type- in the same definition of hybrid Petri net the author has given.

## Author's definition of hybrid Petri net

\more on this later...

## Software Status
Hopefully, the code is commented enough to be easily understood.

In future versions a PNML framework will be implemented. This will allow export/import of Petri nets to/from other software.

A default support for colored Petri nets was not done yet. A recommendation is to duplicate (or tri, or whatever number of colors you have), make the necessary changes; so each net represents a color.

### TODO
- improve GUI, add animations;

- [PNML](http://pnml.lip6.fr) framework;

- check if the program can truly implement EVERY Petri net that exists;

- a wiki;

- make default support for colored nets (e.g. markings as vectors).
