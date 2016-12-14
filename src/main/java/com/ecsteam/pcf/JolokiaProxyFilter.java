package com.ecsteam.pcf;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Adds the necessary <tt>target</tt> information to jolokia REST POST requests
 */
class JolokiaProxyFilter extends GenericFilterBean {

	private static final TypeReference<Map<String, Object>> MAP_TYPE_REFERENCE =
		new TypeReference<Map<String, Object>>() {};

	private static final String RMI_URL_PATTERN = "service:jmx:rmi:///jndi/rmi://%s:44444/jmxrmi";

	private final ObjectMapper objectMapper;

	private final String jmxBridgeHost;

	private final String jmxBridgeAdminUser;

	private final String jmxBridgeAdminPassword;

	JolokiaProxyFilter(ObjectMapper objectMapper, String jmxBridgeHost,	String jmxBridgeAdminUser,
		String jmxBridgeAdminPassword) {
		this.objectMapper = objectMapper;
		this.jmxBridgeHost = jmxBridgeHost;
		this.jmxBridgeAdminUser = jmxBridgeAdminUser;
		this.jmxBridgeAdminPassword = jmxBridgeAdminPassword;
	}


	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
		FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		String method = httpServletRequest.getMethod();

		// Jolokia proxy only works with POST requests
		ServletRequest newRequest = request;
		if ("POST".equalsIgnoreCase(method)) {
			newRequest = mutateRequest(httpServletRequest);
		}

		chain.doFilter(newRequest, response);
	}

	private HttpServletRequest mutateRequest(HttpServletRequest request) throws IOException {
		final Map<String, Object> requestBody =
			objectMapper.readValue(request.getInputStream(), MAP_TYPE_REFERENCE);

		Map<String, String> targetBlock = new HashMap<String, String>() {{
			put("url", String.format(RMI_URL_PATTERN, jmxBridgeHost));
			put("user", jmxBridgeAdminUser);
			put("password", jmxBridgeAdminPassword);
		}};

		requestBody.put("target", targetBlock);

		byte[] newBody = objectMapper.writeValueAsBytes(requestBody);

		return new HttpServletRequestWrapper(request) {
			@Override
			public ServletInputStream getInputStream() throws IOException {
				return new DelegatingServletInputStream(new ByteArrayInputStream(newBody));
			}
		};
	}
}
