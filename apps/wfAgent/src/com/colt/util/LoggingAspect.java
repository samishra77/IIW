package com.colt.util;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.util.StopWatch;

public class LoggingAspect {

	private Log log = LogFactory.getLog(LoggingAspect.class);

	public Object logAround(final ProceedingJoinPoint joinPoint) throws Throwable {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		Object retVal = joinPoint.proceed();
		stopWatch.stop();

		StringBuffer logMessage = new StringBuffer();
		if(joinPoint.getTarget() != null && joinPoint.getTarget().getClass() != null && joinPoint.getTarget().getClass().getName() != null && !"".equals(joinPoint.getTarget().getClass().getName())) {
			log.info("Class: " + joinPoint.getTarget().getClass().getName() + ".");
		}
		if(joinPoint.getSignature() != null && joinPoint.getSignature().getName() != null && !"".equals(joinPoint.getSignature().getName())) {
			log.info("Method: " + joinPoint.getSignature().getName() + ".");
		}
	
		Object[] args = joinPoint.getArgs();
		if(args != null && args.length > 0) {
			logMessage.append("Args: ");
			logMessage.append("(");
			for (int i = 0; i < args.length; i++) {
				logMessage.append(args[i]).append(",");
			}
			logMessage.append(")");
			log.info(logMessage.toString());
		}
		log.info("Execution time: " + stopWatch.getTotalTimeMillis() + " ms");
		return retVal;
	}

	public void logExitAfterThrowing(Exception e) {
		log.error(e,e);
	}
}
