package no.bekk.bekkopen.elvis;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;

import no.bekk.bekkopen.elvis.ESIntegration;
import no.bekk.bekkopen.elvis.FileHandler;
import no.bekk.bekkopen.elvis.LogFileParser;
import no.bekk.bekkopen.elvis.LogLineBean;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.*;

public class LogFileParserTest {

	private LogFileParser logFileParser = null;

	private String logFile150lines = "./src/test/resources/logfiles/logfile-150lines.log";
	private String logFile150linesGzipped = "./src/test/resources/logfiles/logfile-150lines.log.gz";
	private String logFileDirectory = "./src/test/resources/logfiles/";
	
    @Before
	public void setup() {
		DOMConfigurator.configureAndWatch("log4j.xml", 5000);
	}
    
	@Test
	public void readSingleLogFileBatch() {
		int batchSize = 150;
		logFileParser = new LogFileParser(null);
		FileHandler handler = new FileHandler(new File(logFile150lines));
		ArrayList<LogLineBean> docs = logFileParser.readBatch(handler, batchSize);
		assertEquals(docs.size(), batchSize);
	}
	
	@Test
	public void readMultipleLogFileBatches() {
		int batchSize = 100;
		logFileParser = new LogFileParser(null);
		FileHandler handler = new FileHandler(new File(logFile150lines));
		ArrayList<LogLineBean> docs = logFileParser.readBatch(handler, batchSize);
		assertEquals(docs.size(), batchSize);
		docs = logFileParser.readBatch(handler, batchSize);
		assertEquals(docs.size(), 50);		
	}

	@Test
	public void simulateSendingFiles() {
		ESIntegration esIntegration = new ESIntegration(true);
		logFileParser = new LogFileParser(esIntegration);
		int sentFiles = logFileParser.sendFiles(logFileDirectory);
		assertEquals(sentFiles, 2);
	}

	@Test
	public void readGzippedLogFile() {
		int batchSize = 150;
		logFileParser = new LogFileParser(null);
		FileHandler handler = new FileHandler(new File(logFile150linesGzipped));
		ArrayList<LogLineBean> docs = logFileParser.readBatch(handler, batchSize);
		assertEquals(docs.size(), batchSize);
	}

}
