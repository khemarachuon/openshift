package com.khemarachuon.openshift.webapp.endpoint;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContent;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khemarachuon.openshift.common.json.JsonUtils;
import com.khemarachuon.openshift.dao.UserDao;
import com.khemarachuon.openshift.dao.entity.UserEntity;
import com.khemarachuon.openshift.webapp.resources.Acronym;
import com.webcohesion.enunciate.metadata.rs.TypeHint;


@Path("search")
public class ElasticSearchEndpoint {

	private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchEndpoint.class);

//	@Inject
	private UserDao userDao = new UserDao();

	@TypeHint(value=Acronym.class)
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@GET
	public Response search(@QueryParam("term") final String term,
			@QueryParam("start") @DefaultValue("0") Integer start,
			@QueryParam("count") @DefaultValue("10") Integer count) {
		UserEntity testUserA = new UserEntity();
		testUserA.setUsername("userA");
		testUserA.setRoles(new TreeSet<>(Arrays.asList("USER", "ADMIN")));
		userDao.createUser(testUserA);
		
		List<UserEntity> users = userDao.getUsers(null, start, count);
		return Response.ok(JsonUtils.generateJson(users, false)).build();
	}
	
	@TypeHint(value=Acronym.class)
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@GET
	@Path("elastic")
	public Response elasticsearch(@QueryParam("term") final String term) throws UnknownHostException, JsonProcessingException {
		try (final TransportClient client = new PreBuiltTransportClient(Settings.builder()
				.put("client.transport.sniff", true)
				.put("cluster.name", "elasticsearch")
				.build())) {
			client.addTransportAddress(new TransportAddress(InetAddress.getByName("docker-machine"), 9300));

			final Acronym acronym = new Acronym("CD", Arrays.asList("Compact Disk"));
			final String indexName = "acronym";
			final String documentType = "ACRONYM";
			final String documentId = acronym.getTerm();
			
			final JsonFactory jsonFactory = new JsonFactory();
			jsonFactory.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true);
			jsonFactory.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
			jsonFactory.configure(JsonFactory.Feature.FAIL_ON_SYMBOL_HASH_OVERFLOW, false); // this trips on many mappings now...
			// Do not automatically close unclosed objects/arrays in com.fasterxml.jackson.core.json.UTF8JsonGenerator#close() method
			jsonFactory.configure(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT, false);
			jsonFactory.configure(JsonParser.Feature.STRICT_DUPLICATE_DETECTION, XContent.isStrictDuplicateDetectionEnabled());
			final ObjectMapper objectMapper = new ObjectMapper(jsonFactory);
			
			final byte[] documentBytes = objectMapper.writeValueAsBytes(acronym);
			
			if (!client.admin()
					.indices()
					.create(new CreateIndexRequest(indexName, Settings.EMPTY))
					.actionGet(10, TimeUnit.SECONDS)
					.isAcknowledged()) {
				LOG.error("Failed to create index: {}", indexName);
			}
			
			client.index(new IndexRequest(indexName, "type", documentId));
			final DocWriteResponse.Result indexResult = client.prepareIndex(
						indexName,
						documentType,
					documentId)
					.setSource(documentBytes, XContentType.JSON)
					.execute()
					.actionGet(10, TimeUnit.SECONDS)
					.getResult();
			if (!EnumSet.of(DocWriteResponse.Result.CREATED, DocWriteResponse.Result.UPDATED).contains(indexResult)) {
				LOG.error("Failed to index document into ElasticSearch. index={} type={} id={} result={}",
						indexName,
						documentType,
						documentId,
						indexResult.name());
			}
			
			final SearchHits searchHits = client.prepareSearch(indexName).setQuery(
					QueryBuilders.queryStringQuery(term))
				.execute()
				.actionGet(10, TimeUnit.SECONDS)
				.getHits();
			
			final List<Acronym> searchResults = StreamSupport.stream(searchHits.spliterator(), false)
				.map(hit -> {
					try {
						return objectMapper.readValue(hit.getSourceAsString(), Acronym.class);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				})
				.collect(Collectors.toList());
			
			return Response.ok(searchResults.get(0)).build();
		}
	}
}
