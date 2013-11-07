package no.bekk.bekkopen.elvis;


public class LogLineBean {

	public static int idCounter = 0;

	public String id;
	public String client;
	public String identuser;
	public String authuser;
	public String timestamp;
	public String monthofyear;
	public String weeknumber;
	public String dayofmonth;
	public String dayofweek;
	public String hourofday;
	public String method;
	public String url;
	public String filetype;
	public String protocol;
	public String responsecode;
	public String bytes;
	public String referrer;
	public String useragent;
	
	public LogLineBean() {
		idCounter++;
		id = "" + idCounter;
	}	
}
