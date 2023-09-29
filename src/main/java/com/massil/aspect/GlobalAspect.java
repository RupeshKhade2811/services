package com.massil.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class GlobalAspect {


    Logger log= LoggerFactory.getLogger(GlobalAspect.class);

    @AfterThrowing(value="execution(* com.massil.*.*.*(..))", throwing="ex")
    public void afterThrowingAdvice(JoinPoint joinPoint, Exception ex)
    {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        log.error("After Throwing exception in method: {}",joinPoint.getSignature());
        log.error("Exception is: {}" ,ex.getMessage());
        String[] params=signature.getParameterNames();
        Object[] args=joinPoint.getArgs();
        if (null != params && null!=args) {

            for (int i = 0; i < params.length; i++) {
                String av = null!=args[i]? args[i].toString():null;

                    log.error("method argument name is  : {} and value is {}",params[i],av);
            }
        }

    }

}
