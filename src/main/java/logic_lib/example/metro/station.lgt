:- object(station(_Name)).

	:- info([
		author is 'Sergio Castro',
		comment is 'A metro station',
		parameters is [
			'Name' - 'The name of the station']
	]).
	
	:- public([name/1, connected/1, connected/2, nearby/1, reachable/1, reachable/2]).

	name(Name) :- parameter(1, Name).
	
	connected(Station) :- connected(Station, _).
	connected(Station, L) :- self(Self), metro::connected(Self, Station, L).
	
	nearby(Station) :- self(Self), metro::nearby(Self, Station).
	
	reachable(Station) :- reachable(Station, _).
	reachable(Station, IntermediateStations) :- self(Self), metro::reachable(Self, Station, IntermediateStations).
	
:- end_object.
