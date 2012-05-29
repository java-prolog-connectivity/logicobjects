:- object(metro).

	:- info([
		author is 'Sergio Castro',
		comment is 'The Londom metro. Taken from the "Simply logic" book (Peter Flach).'
	]).
	:- public([connected/3, nearby/2, reachable/3, line/1]).

	connected(station(bond_street),station(oxford_circus),line(central)).
	connected(station(oxford_circus),station(tottenham_court_road),line(central)).
	connected(station(bond_street),station(green_park),line(jubilee)).
	connected(station(green_park),station(charing_cross),line(jubilee)).
	connected(station(green_park),station(piccadilly_circus),line(piccadilly)).
	connected(station(piccadilly_circus),station(leicester_square),line(piccadilly)).
	connected(station(green_park),station(oxford_circus),line(victoria)).
	connected(station(oxford_circus),station(piccadilly_circus),line(bakerloo)).
	connected(station(piccadilly_circus),station(charing_cross),line(bakerloo)).
	connected(station(tottenham_court_road),station(leicester_square),line(northern)).
	connected(station(leicester_square),station(charing_cross),line(northern)).

	nearby(X,Y):-connected(X,Y,L).
	nearby(X,Y):-connected(X,Z,L),connected(Z,Y,L).
	
	reachable(X,Y,[]):-connected(X,Y,L).
	reachable(X,Y,[Z|R]):-connected(X,Z,L),reachable(Z,Y,R).

	line(Name) :- setof(L, connected(_,_,L), AllLines), list::member(line(Name), AllLines).
    	
:- end_object.
