% loader of all libraries

:- initialization((
	set_logtalk_flag(report, off),
	%set_logtalk_flag(report, warnings),  %(possible values are: on, warnings, off),
	%set_logtalk_flag(portability, warning),
	logtalk_load(metro),
	logtalk_load(station),
	logtalk_load(line)
)).
