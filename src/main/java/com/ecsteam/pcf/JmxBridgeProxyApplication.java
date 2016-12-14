package com.ecsteam.pcf;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jolokia.http.AgentServlet;
import org.jolokia.jsr160.Jsr160RequestDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication(exclude = DispatcherServletAutoConfiguration.class)
public class JmxBridgeProxyApplication {

	private static final Map<String, String> AGENT_SERVLET_INIT_PARAMS = new HashMap<String, String>() {{
		put("dispatcherClasses", Jsr160RequestDispatcher.class.getName());
	}};

	public static void main(String[] args) {
		SpringApplication.run(JmxBridgeProxyApplication.class, args);
	}

	@Bean
	ServletRegistrationBean jolokiaServlet() {
		ServletRegistrationBean jolokiaServlet = new ServletRegistrationBean();
		jolokiaServlet.addUrlMappings("/*");
		jolokiaServlet.setLoadOnStartup(1);
		jolokiaServlet.setServlet(new AgentServlet());
		jolokiaServlet.setInitParameters(AGENT_SERVLET_INIT_PARAMS);

		return jolokiaServlet;
	}

	@Bean
	@Autowired
	FilterRegistrationBean jolokiaProxyFilterBean(JolokiaProxyFilter filter) {
		FilterRegistrationBean jolokiaProxyFilterBean = new FilterRegistrationBean();
		jolokiaProxyFilterBean.setFilter(filter);
		jolokiaProxyFilterBean.setUrlPatterns(Arrays.asList("/*"));

		return jolokiaProxyFilterBean;
	}

	@Bean
	@Autowired
	JolokiaProxyFilter jolokiaProxyFilter(ObjectMapper objectMapper,
		@Value("${jmx.bridge.host}") String jmxBridgeHost,
		@Value("${jmx.bridge.user}") String jmxBridgeAdminUser,
		@Value("${jmx.bridge.password}") String jmxBridgeAdminPassword) {

		return new JolokiaProxyFilter(objectMapper, jmxBridgeHost, jmxBridgeAdminUser,
			jmxBridgeAdminPassword);
	}
}
