package com.wanhutong.backend.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.google.common.io.Closer;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;

/**
 * JSON工具
 *
 * @author Ma.Qiang
 */
public class JsonUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static final JsonFactory JSON_FACTORY = objectMapper.getJsonFactory();

	public static class Object2StringSerializer extends JsonSerializer<Object> {
		@Override
		public void serialize(Object object, JsonGenerator paramJsonGenerator,
				SerializerProvider paramSerializerProvider) throws IOException, JsonProcessingException {
			paramJsonGenerator.writeString(String.valueOf(object));
		}

	}

    static {
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        objectMapper.configure(JsonParser.Feature.INTERN_FIELD_NAMES, true);
        objectMapper.configure(JsonParser.Feature.CANONICALIZE_FIELD_NAMES, true);
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
			@Override
			public void serialize(Object object, JsonGenerator jg, SerializerProvider serializer)
					throws IOException, JsonProcessingException {
				jg.writeString(StringUtils.EMPTY);
			}
		});
    }

    public static ObjectMapper getObjectMapperInstance() {
        return objectMapper;
    }

    public static JsonFactory getJsonFactory() {
        return JSON_FACTORY;
    }

	public static String getJson(Object data) throws Exception {
		StringWriter sw = new StringWriter();
		getObjectMapperInstance().writeValue(sw, data);
		return sw.toString();
	}

    /**
     * Json to Object
     * @param json
     * @param c
     * @param <T>
     * @return
     */
	public static <T> T parse(String json, Class<T> c) {
        T t = null;//String转成map
        try {
            t = getObjectMapperInstance().readValue(json, c);
        } catch (IOException e) {
            LOGGER.error("jsonutil parse json error ", e);
        }
        return t;
    }

    /**
     * Json to Object
     * @param json
     * @return
     */
	public static JSONObject parseJson(String json) {
        try {
            return JSONObject.parseObject(json);
        } catch (Exception e) {
            LOGGER.error("jsonutil parse jsonObject error ", e);
        }
        return null;
    }

    public static String generatePureData(Object result) throws Exception {
        StringBuilderWriter sw = new StringBuilderWriter();
		JsonGenerator jg = null;
		Closer closer = Closer.create();
        try {
			jg = closer.register(JSON_FACTORY.createJsonGenerator(sw));
            jg.writeStartObject();
            jg.writeObjectField("data", result);
            jg.writeEndObject();
        } catch (IOException e) {
            LOGGER.warn("generateData exception", e);
            throw e;
        } finally {
			closer.close();
        }
        return sw.toString();
    }

	public static String generateData(Object result, String callback) {
        StringBuilderWriter sw = new StringBuilderWriter();
        JsonGenerator jg = null;
		Closer closer = Closer.create();
        try {
			jg = closer.register(JSON_FACTORY.createJsonGenerator(sw));
            jg.writeStartObject();
            jg.writeBooleanField("ret", true);
            jg.writeObjectField("data", result);
            jg.writeEndObject();
        } catch (IOException e) {
            LOGGER.warn("generateData exception", e);
        } finally {
			try {
				closer.close();
			} catch (IOException e) {
                LOGGER.warn("IOException", e);
			}
        }
        if (StringUtils.isNotBlank(callback)) {
            StringBuilder sb = new StringBuilder();
            sb.append(callback).append("(").append(sw.toString()).append(")");
            return sb.toString();
        }
        return sw.toString();
    }

	public static String generateErrorData(int errCode, String errmsg, String callback) {
        StringBuilderWriter sw = new StringBuilderWriter();
        JsonGenerator jg = null;
		Closer closer = Closer.create();
        try {
			jg = closer.register(JSON_FACTORY.createJsonGenerator(sw));
            jg.writeStartObject();
            jg.writeBooleanField("ret", false);
            jg.writeNumberField("errCode", errCode);
            jg.writeStringField("errmsg", errmsg);
            jg.writeEndObject();
        } catch (IOException e) {
            LOGGER.warn("generateErrorData exception", e);
        } finally {
			try {
				closer.close();
			} catch (IOException e) {
                LOGGER.warn("IOException", e);
			}
        }
        if (StringUtils.isNotBlank(callback)) {
            StringBuilder sb = new StringBuilder();
            sb.append(callback).append("(").append(sw.toString()).append(")");
            return sb.toString();
        }
        return sw.toString();
    }

    /**
     * 将json array反序列化为对象
     *
     * @param json
     * @param jsonTypeReference
     * @return
     */
    public static <T> T parseArray(String json, TypeReference<T> jsonTypeReference) {
        try {
            return (T) objectMapper.readValue(json, jsonTypeReference);
        } catch (Exception e) {
            LOGGER.error("decode(String, JsonTypeReference<T>)", e);
        }
        return null;
    }

}
