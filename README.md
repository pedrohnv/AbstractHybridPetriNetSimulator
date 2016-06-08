# Abstract Hybrid Petri Net Simulator
This is an open source simulator of hybrid Petri nets written in Java.

It is under development, currently there is no GUI (TODO).

Current status is: first testing run. Check if it does the correct logic.

## About Petri net
See https://en.wikipedia.org/wiki/Petri_net for Petri nets.

A hybrid Petri net has continuous places and transitions, and also a time evolution (if desired).

## Author's definition of Hybrid Petri net

The author's definition of hybrid Petri net is a tuple N={τ,P,T,A} in which

τ ∈ (R(≥0)×Z(≥0) ) is the iteration (or time) of the net;

P = {pn} is a collection of places;

T = {ti} is a collection of transitions;

A = {ak} is a collection of arcs.

By making τ bidimensional, Zeno behavior can be reproduced. The integer axis represents a iteration step from a beginning iteration; and the real axis, time evolution from an initial time.

Each place is a set of markings contained in a capacity set, pn={mn ∈ Cn }.

Each transition is a set determined by a speed function, an enabling status, and a priority, ti = {sf(∙), en, pri(∙)}. The priority can be a function as long as it returns a real number.

Each arc is a set of: a single place, a single transition, a weight, and a Boolean disabling function, ak = {pn, ti, w(∙), df(∙)}. The weight can be a function. Every arc has a default, immutable, disabling function that tests if the firing of the transition causes the markings in the place out of its capacity. That way, there will be no firing of a transition that sets the markings in a place out of its capacity. Other disabling functions can be added; the final disabling function will be a Boolean OR test with all the disabling functions.

There is nothing that forbids two arcs to connect the same place and transition. That is, a place and a transition can have more than one arc connecting them.

The net can be characterized by the union of all its arcs, and how the markings in the places change as τ evolves.
A net N1 is said to be equal to a net N2 if
A1 = A2  | A1 ⊂ N1, A2 ⊂ N2
that is, they have exactly the same arcs.

### Behavior

At a point τ, the iterations are done until there is no more enabled transition, then time advances.

If a transition is enabled (not disabled), then it fires. The firing of a transition, t_i, causes the markings of all places, p_n, connected to that transition by an arc, a_k, to change according to the speed function of the transition and the weight of each arc. That is, the new marking in a place will be its previous marking plus the change caused by the firing of the transition.

mn[τ] = mn[τ_enabled ] + ∫ sfi  wk dτ

The markings in a place at a iteration (time) τ will be the previous value plus the total change the speed function and weight causes up to that iteration (time).

A transition is considered disabled if at least one arc (connecting to that transition) has its disabling function returning true.

A situation of conflict arises when the simultaneous firing of two or more transitions would cause the markings of a place to go out of its capacity. In that case the transitions will fire one by one, in order, from the highest priority to the lowest. If multiple transitions have the same priority, the one that fires is selected randomly.

### Common Petri net elements

Discrete place: is one such that its capacity is a subset of the non-negative integers, i.e., C ⊆ Z(≥0). A colored Petri net can be achieved by making the markings and capacity multidimensional, i.e., a marking, m, is a vector where each element corresponds to a different color.

Continuous place: is one that its capacity is a subset of the real numbers, C ⊆ R.

Discrete transition: the speed function is an integer constant.

Continuous transition: the speed function is any real number.

Normal arc: the default arc. The weight determines the direction of the arc (place to transition if w > 0, and from transition to place if w < 0). The disabling function tests if the statement
mn [τ_enabled + dτ] ∉ Cn
is true. That is, it checks if after the transition fires, the markings in the place get out of the capacity of that place.

Test arc: the weight is zero, w = 0, and there is a disabling function that tests if the markings in the place are lesser than a threshold, mn < x.

Inhibitor arc: similar to the test arc, but tests if the markings are greater than a threshold, mn > x.
