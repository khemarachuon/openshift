package com.khemarachuon.openshift.webapp.config;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("rest")
public class ApplicationConfig extends ResourceConfig {
	public ApplicationConfig() {
		packages(true, "com.khemarachuon.openshift.webapp");
	}
}
