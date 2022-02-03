package com.smartcodeltd.jenkinsci.plugins.buildmonitor.extensions;

import org.jenkinsci.plugins.workflow.support.steps.build.RunWrapper;

import hudson.model.Run;

public class RunWrapperFactory {

	private static RunWrapperFactory instance;
	
	public static RunWrapperFactory getInstance() {
		if(instance == null) {
			instance = new RunWrapperFactory();
		}
		
		return instance;
	}
	
	public static void setInstance(RunWrapperFactory instance) {
		RunWrapperFactory.instance = instance;
	}
	
	public RunWrapper of(Run<?, ?> build, boolean currentBuild) {
		return new RunWrapper(build, currentBuild);
	}
	
}
