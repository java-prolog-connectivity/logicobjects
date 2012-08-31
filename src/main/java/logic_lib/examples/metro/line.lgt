:- object(line(_Name)).

	:- info([
		author is 'Sergio Castro',
		comment is 'A metro line',
		parameters is [
			'Name' - 'The name of the metro line']
	]).
	
	:- public([name/1, connects/2]).

	name(Name) :- parameter(1, Name).
	
	connects(Station1, Station2) :- self(Self), metro::connected(Station1, Station2, Self).
	
:- end_object.
