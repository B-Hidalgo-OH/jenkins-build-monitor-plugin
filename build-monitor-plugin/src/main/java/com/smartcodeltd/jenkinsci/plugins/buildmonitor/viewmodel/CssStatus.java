package com.smartcodeltd.jenkinsci.plugins.buildmonitor.viewmodel;

import hudson.AbortException;
import hudson.EnvVars;
import hudson.console.ConsoleNote;
import hudson.model.Job;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import com.smartcodeltd.jenkinsci.plugins.buildmonitor.BuildMonitorLogger;
import org.jenkinsci.plugins.workflow.support.steps.build.RunWrapper;

import static hudson.model.Result.*;

/**
 * @author Jan Molak
 */
public class CssStatus {

	private static final String A = "A";
	private static final String B = "B";
	private static final String C = "C";
	private static final String D = "D";
	private static final String F = "F";
	    
    private static final Map<Result, String> statuses = new HashMap<Result, String>() {{
        put(SUCCESS,   "successful");
        put(UNSTABLE,  "unstable");
        put(FAILURE,   "failing");
        put(NOT_BUILT, "unknown");
        put(ABORTED,   "aborted");
    }};
    
    private static final Map<String, String> gradeStatuses = new HashMap<String, String>() {{
    	put(A, "grade-a");
        put(B, "grade-b");
        put(C, "grade-c");
        put(D, "grade-d");
        put(F, "grade-f");
    }};

    public static String of(final JobView job) {
        String status = null;
        String testsPassed = null;
        String testsFailed = null;
        Job<?, ?> lastJob = job.getJob();
        Run<?, ?> lastBuild = lastJob != null ? lastJob.getLastBuild() : null;
        RunWrapper lastBuildWrapper = lastBuild != null ? new RunWrapper(lastBuild, false) : null;
        Map<String, String> lastBuildEnvVars;
        
		try {
			lastBuildEnvVars = lastBuildWrapper != null ? lastBuildWrapper.getBuildVariables() : null;
		} catch (AbortException e) {
			lastBuildEnvVars = null;
		}
		
        if(lastBuildEnvVars != null) {
        	testsPassed = lastBuildEnvVars.get("TESTS_PASSED");
			testsFailed = lastBuildEnvVars.get("TESTS_FAILED");
			status = statusOf(job.lastResult(), testsPassed, testsFailed);
        } else {
        	status = statusOf(job.lastResult());
        }
        
        if (job.isDisabled()) {
            status += " disabled";
        }

        if (job.isRunning()) {
            status += " running";
        }

        return status;
    }

    private static String statusOf(Result result) {
        return statuses.containsKey(result) ? statuses.get(result) : "unknown";
    }
    
    private static String statusOf(Result result, String testsPassed, String testsFailed) {
        String status;
        int testsPassedParsed;
        int testsFailedParsed;
        double grade;

        try {
            testsPassedParsed = Integer.parseInt(testsPassed);
            testsFailedParsed = Integer.parseInt(testsFailed);
        } catch (NumberFormatException e) {
            return statusOf(result);
        } catch (NullPointerException e) {
        	return statusOf(result);
        }

        grade = (Double.valueOf(testsPassedParsed) / Double.valueOf((testsPassedParsed + testsFailedParsed))) * 100;
        
        if(grade >= 90) {
            status = A;
        } else if(grade >= 80) {
            status = B;
        } else if(grade >= 70) {
            status = C;
        } else if(grade >= 65) {
            status = D;
        } else {
            status = F;
        }

        return gradeStatuses.getOrDefault(status, "unknown");
    }
}
