package com.colt.aopwf;

import java.util.List;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class ActivityFilterInterceptor implements MethodInterceptor {

	private IWorkflowProcessActivity activity;
	private String[] invokeState;

	public String[] getInvokeState() {
		return invokeState;
	}

	public void setInvokeState(String[] invokeState) {
		this.invokeState = invokeState;
	}

	public ActivityFilterInterceptor(IWorkflowProcessActivity activity){
		this.activity = activity;
	}

	public IWorkflowProcessActivity getActivity(){
		return activity;
	}

	@SuppressWarnings("unchecked")
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Map<String,Object> input = (Map<String,Object>) invocation.getArguments()[0];
		String[] state = activity.process(input);
		if (state != null) {
			for (String s : state) {
				List<String> wfState = (List<String>) input.get("WORKFLOW-STATE");
				wfState.add(s);
			}
		}
		return invocation.proceed();
	}
}
