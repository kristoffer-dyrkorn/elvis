package no.bekk.bekkopen.elvis;

import org.apache.log4j.xml.DOMConfigurator;

public class LogFileIndexer {
	
	public LogFileIndexer(String directoryName) {
		
		DOMConfigurator.configureAndWatch("log4j.xml", 5000);
		
		ESIntegration esIntegration = new ESIntegration(false);
		LogFileParser fileParser = new LogFileParser(esIntegration);
		fileParser.sendFiles(directoryName);
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("Usage: java -jar elvis.jar [log file directory]");
		} else {
			new LogFileIndexer(args[0]);
		}
	}
	
}
