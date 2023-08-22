package net.lakis.cerebro.web;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import net.lakis.cerebro.annotations.Service;
import net.lakis.cerebro.web.config.WebServerConfig;

@Provider
@Service
public class WebJsonProvider implements ContextResolver<ObjectMapper> {

	public ObjectMapper objectMapper;

	public WebJsonProvider() {
//		System.out.println(ExceptionUtils.getFullStackTrace(new Exception("new instance of WebJsonProvider")));
	}

	public ObjectMapper getContext(Class<?> type) {
		return objectMapper;

	}

	public String reload(WebServerConfig config) {
		objectMapper = new ObjectMapper();

		objectMapper.setSerializationInclusion(Include.ALWAYS);

		if (config != null && config.isIdentJson())
			objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		else
			objectMapper.disable(SerializationFeature.INDENT_OUTPUT);

		objectMapper.disable(MapperFeature.USE_GETTERS_AS_SETTERS);

		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
		objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

		objectMapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		return "webJsonProvider succefully reloaded";
	}

}