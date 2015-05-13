package com.colt.aopwf;

import java.util.List;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.util.StringUtils;

@Aspect
public class ActivityTransitionAspect {

	@SuppressWarnings("unchecked")
	@Around("execution(public java.lang.Object com.colt.aopwf.*.invoke(..)) && args(proxyMethodInvocation) && target(filter)")
	public Object intercept(ProceedingJoinPoint thisJoinPoint, MethodInvocation proxyMethodInvocation, ActivityFilterInterceptor filter) throws Throwable {
		Object ret = null;

		Map<String,Object> input = (Map<String,Object>) proxyMethodInvocation.getArguments()[0];
		List<String> wfState = (List<String>) input.get("WORKFLOW-STATE");

		IWorkflowProcessActivity activity = filter.getActivity();
		String[] invokeState = filter.getInvokeState();

		if (grantInvocation(activity, wfState, invokeState)) {
			System.out.println("===> Invoking activity: " + ((org.springframework.aop.framework.Advised) activity).getTargetSource().getTarget());
			ret = thisJoinPoint.proceed(); 
		} else {
			System.out.println("---> Skipping activity: " + ((org.springframework.aop.framework.Advised) activity).getTargetSource().getTarget());
			ret = proxyMethodInvocation.proceed();
		}

		return ret;
	}

	private boolean grantInvocation(IWorkflowProcessActivity activity, List<String> wfState, String[] invokeState) {
		boolean grant = true;

		for (String state : invokeState) {
			if (state.startsWith("!")) {
				state = StringUtils.trimLeadingCharacter(state, '!');
				grant = !wfState.contains(state);
			} else {
				grant = wfState.contains(state);
			}
			if (!grant) {
				break;
			}
		}

		return grant;
	}
}
