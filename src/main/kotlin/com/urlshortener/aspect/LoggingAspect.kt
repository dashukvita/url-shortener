package com.urlshortener.aspect

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*

/**
 * Aspect to log methods annotated with {@link Loggable}.
 * Logs method entry, exit with return value, and exceptions.
 */

@Aspect
@Component
class LoggingAspect {
    private val log = LoggerFactory.getLogger(LoggingAspect::class.java)

    @Before("@annotation(com.urlshortener.aspect.Loggable)")
    fun logBefore(joinPoint: JoinPoint) {
        val methodName = joinPoint.signature.toShortString()
        val args = joinPoint.args
        log.info("[ENTER] $methodName with args ${Arrays.toString(args)}")
    }

    @AfterReturning(pointcut = "@annotation(com.urlshortener.aspect.Loggable)", returning = "result")
    fun logAfterReturning(joinPoint: JoinPoint, result: Any?) {
        val methodName = joinPoint.signature.toShortString()
        log.info("[EXIT] $methodName returned: $result")
    }

    @AfterThrowing(pointcut = "@annotation(com.urlshortener.aspect.Loggable)", throwing = "ex")
    fun logAfterThrowing(joinPoint: JoinPoint, ex: Throwable) {
        val methodName = joinPoint.signature.toShortString()
        log.error("[ERROR] $methodName threw exception: ${ex.message}", ex)
    }
}