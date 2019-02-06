package com.khemarachuon.openshift.common.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public final class JsonUtils {
	private static final ObjectMapper INDENTING_OBJECT_MAPPER;
	private static final ObjectMapper NONINDENTING_OBJECT_MAPPER;

	static {
		final JsonFactory jsonFactory = new JsonFactory();
		jsonFactory.configure(JsonFactory.Feature.FAIL_ON_SYMBOL_HASH_OVERFLOW, false);
		jsonFactory.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
		jsonFactory.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true);
		jsonFactory.configure(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT, false);
		jsonFactory.configure(JsonParser.Feature.IGNORE_UNDEFINED, true);
		jsonFactory.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		jsonFactory.configure(JsonParser.Feature.STRICT_DUPLICATE_DETECTION, true);

		INDENTING_OBJECT_MAPPER = new ObjectMapper(jsonFactory);
		INDENTING_OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);

		NONINDENTING_OBJECT_MAPPER = new ObjectMapper(jsonFactory);
		NONINDENTING_OBJECT_MAPPER.disable(SerializationFeature.INDENT_OUTPUT);
	}

	public static final String generateJson(final Object object, final boolean indentFlag) {
		try {
			if (indentFlag) {
				return INDENTING_OBJECT_MAPPER.writeValueAsString(object);
			} else {
				return NONINDENTING_OBJECT_MAPPER.writeValueAsString(object);
			}
		} catch (final JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public static final <T> T parseJson(final String json, final Class<T> classType) {
		try {
			return NONINDENTING_OBJECT_MAPPER.readValue(json, classType);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
}
