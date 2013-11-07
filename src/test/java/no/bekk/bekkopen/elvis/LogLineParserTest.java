package no.bekk.bekkopen.elvis;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import no.bekk.bekkopen.elvis.LogLineBean;
import no.bekk.bekkopen.elvis.LogLineParser;

import org.junit.Test;

public class LogLineParserTest {

	private LogLineParser logLineParser = new LogLineParser();

	String loglineNormalDate = "127.0.0.1 - - [01/Jan/2011:03:00:01 +0200] \"GET / HTTP/1.1\" 200 1000 \"-\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:9.0) Gecko/20100101 Firefox/9.0\"";
	String loglineWeek53 = "127.0.0.1 - - [01/Jan/2010:03:00:00 +0200] \"GET / HTTP/1.1\" 200 1000 \"-\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:9.0) Gecko/20100101 Firefox/9.0\"";
	
	@Test
	public void parseSingleLogLine() {
		LogLineBean b = logLineParser.parse(loglineNormalDate);
		assertNotNull(b);
		assertEquals("-", b.authuser);
		assertEquals("1000", b.bytes);
		assertEquals("127.0.0.1", b.client);
		assertEquals("1", b.dayofmonth);
		assertEquals("sun", b.dayofweek);
		assertEquals("html", b.filetype);
		assertEquals("3", b.hourofday);
		assertEquals("-", b.identuser);
		assertEquals("GET", b.method);
		assertEquals("jan", b.monthofyear);
		assertEquals("HTTP/1.1", b.protocol);
		assertEquals("-", b.referrer);
		assertEquals("200", b.responsecode);
		assertEquals("2011-01-01T02:00:01.000", b.timestamp);
		assertEquals("/", b.url);
		assertEquals("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:9.0) Gecko/20100101 Firefox/9.0", b.useragent);
		assertEquals("52", b.weeknumber);
	}
	
	@Test
	public void useISOWeekNumbering() {
		LogLineBean b = logLineParser.parse(loglineWeek53);
		assertNotNull(b);
		assertEquals("53", b.weeknumber);
	}

	@Test
	public void extractFileType() {
		assertEquals("html", logLineParser.getFileType("/index.html"));
		assertEquals("html", logLineParser.getFileType("/"));
		assertEquals("css", logLineParser.getFileType("/print.css?timestamp=12345"));
		assertEquals("gif", logLineParser.getFileType("file.gif"));
	}
}
