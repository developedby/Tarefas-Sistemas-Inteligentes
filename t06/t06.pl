:- dynamic you_have/1, location/2, closed/1, connect/2, ducks_eaten/1, connected/2.

connect(house, yard).
connect(yard, woods).

goto(X) :-
    location(you,L),
    connected(L, X),
    retract(location(you,L)),
    assert(location(you, X)),
    write(' You are in the '), write(X), nl.
goto(X) :-
    write(' You cant get to '), write(X), write(' from here '), nl.

fox :-
    move_fox,
    eat_duck.

move_fox :-
    location(fox, L),
    findall(X0, connected(X0, L), X),
    random_member(Y, X),
    retract(location(fox, L)),
    assert(location(fox, Y)),
    write(' The fox is now in the '), write(Y), nl.

eat_duck :-
    location(fox, A),
    location(ducks, A),
    \+location(you, A),
    write(' The fox has eaten a duck '), nl,
    ducks_eaten(N),
    N1 is N + 1,
    retract(ducks_eaten(N)),
    assert(ducks_eaten(N1)).
eat_duck.

go :-
    restart,
    step.
    
step :- done.
step :-
    write(' -- '),
    read(X),
    call(X),
    ducks,
    fox,
    step.

done :-
    location(you, house),
    you_have(egg),
    write(' Thanks for getting the egg '), nl,
    ducks_eaten(N),
    write(' The fox ate '), write(N), write(' ducks '), nl.

    
take(X) :-
    location(you, L),
    location(X, L),
    assert(you_have(X)),
    retract(location(X, L)),
    write(' You take the '), write(X), nl.
take(X) :-
    write(' Theres no '), write(X), write(' to take '), nl.
    
open(gate) :-
    location(you, yard),
    closed(gate),
    retract(closed(gate)),
    assert(connect(yard, duck_pen)),
    write(' You open the gate '), nl.
open(gate) :-
    location(you, yard),
    write(' The gate is already open'), nl.
open(gate) :-
    write(' Theres no gate to open here '), nl.
    
restart :-  
    retractall(location(ducks, L)),
    assert(location(ducks, duck_pen)),
    
    retractall(location(egg, L)),
    assert(location(egg, duck_pen)),
    
    retractall(location(fox, L)),
    assert(location(fox, woods)),
    
    retractall(location(you, L)),
    assert(location(you, house)),
    
    assert(closed(gate)),
    
    retractall(ducks_eaten(N)),
    assert(ducks_eaten(0)),
    
    retractall(you_have(X)),
    
    retractall(connect(yard, duck_pen)).

connected(X, Y) :- connect(X, Y); connect(Y, X).

ducks :-
    location(you, duck_pen),
    location(ducks, duck_pen),
    retract(location(ducks, duck_pen)),
    assert(location(ducks, yard)),
    write(' The ducks have run into the yard '), nl.
ducks.
