package org.logicobjects.flags;



public abstract class LogtalkFlag {
	public static Unknown UNKNOWN = new Unknown();
	public static Portability PORTABILITY = new Portability();
	public static Report REPORT = new Report();
	public static Optimize OPTIMIZE = new Optimize();
	public static StartupMessage STARTUP_MESSAGE = new StartupMessage();
	
	
	static class Unknown extends LogtalkFlag {
		public static final String WARNING = "warning";
		public static final String SILENT = "silent";
		
		@Override
		public String toString() {
			return "unknown";
		}
	} 
	
	
	static class Portability extends LogtalkFlag {
		public static final String WARNING = "warning";
		public static final String SILENT = "silent";
		
		@Override
		public String toString() {
			return "portability";
		}
	} 
	
	static class Report extends LogtalkFlag {
		public static final String ON = "on";
		public static final String OFF = "off";
		public static final String WARNINGS = "warnings";
		
		@Override
		public String toString() {
			return "report";
		}
	} 

	
	static class Optimize extends LogtalkFlag {
		public static final String ON = "on";
		public static final String OFF = "off";
		
		@Override
		public String toString() {
			return "optimize";
		}
	} 
	
	static class StartupMessage extends LogtalkFlag {
		public static final String FLAGS = "flags";
		public static final String BANNER = "banner";
		public static final String NONE = "none";
		
		@Override
		public String toString() {
			return "startup_message";
		}
	} 
	
	
}
