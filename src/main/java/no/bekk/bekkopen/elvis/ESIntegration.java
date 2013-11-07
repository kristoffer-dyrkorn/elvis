package no.bekk.bekkopen.elvis;

import java.util.List;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ESIntegration {

	private Client client = null;
	private boolean isSimulated = false;
	private ObjectMapper mapper = new ObjectMapper();
	
	public static final String INDEX_NAME = "logs";
	public static final String TYPE_NAME = "logs";
	public static final String CLUSTER_NAME = "elvis";	
	
	static final Logger log = Logger.getLogger(ESIntegration.class);

	public ESIntegration(boolean isSimulated) {
		this.isSimulated = isSimulated;

		Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", CLUSTER_NAME).build();
		client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress("localhost", 9300));

		boolean hasIndex = client.admin().indices().exists(new IndicesExistsRequest(INDEX_NAME)).actionGet().isExists();
		if (!hasIndex) {
			CreateIndexResponse create = client.admin().indices().create(new CreateIndexRequest(INDEX_NAME).mapping(TYPE_NAME, getTypeMapping())).actionGet();
			if (!create.isAcknowledged()) {
				log.error("Could not create index!");
			}
		}
	}
	
	public XContentBuilder getTypeMapping() {
		XContentBuilder builder = null;
		try {
			builder = XContentFactory.jsonBuilder().startObject().startObject(TYPE_NAME).startObject("properties");

			builder.startObject("timestamp").field("type", "date").endObject()
					.startObject("weeknumber").field("type", "integer").endObject()
					.startObject("dayofmonth").field("type", "integer").endObject()
					.startObject("hourofday").field("type", "integer").endObject()
					.startObject("responsecode").field("type", "integer").endObject()
					.startObject("bytes").field("type", "integer").endObject()
			.endObject().endObject().endObject();

			log.info(builder.prettyPrint());
		} catch (Exception e) {
			log.error("Could not define type mapping: " + e);
		}
		return builder;
	}
	
	
	public void send(List<LogLineBean> docs) {
		
		try {
			if (isSimulated) {
				log.info("Simulating sending " + docs.size() + " docs.");
			} else {
				BulkRequestBuilder bulkRequest = client.prepareBulk();

				for (LogLineBean doc : docs) {
					String json = mapper.writeValueAsString(doc);
					IndexRequest req = new IndexRequest(INDEX_NAME, TYPE_NAME).source(json);
					bulkRequest.add(req);
				}

				long ts = System.currentTimeMillis();
				BulkResponse bulkResponse = bulkRequest.execute().actionGet();
				if (bulkResponse.hasFailures()) {
					log.error(bulkResponse.buildFailureMessage());
				}
				
				long duration = System.currentTimeMillis() - ts;
				
				log.info("Sent " + docs.size() + " docs in " + duration + " ms, rate: " + 1000.0f * docs.size() / duration);
			}
		} catch (Exception e) {
			log.error("Error sending log lines: " + e);
		}
		

	}

}
