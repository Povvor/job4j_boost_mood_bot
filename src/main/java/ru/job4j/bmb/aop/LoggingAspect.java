package ru.job4j.bmb.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* ru.job4j.bmb.services.*.*(..))")
    private void serviceLayer() {
    }

    @Before("serviceLayer()")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("Вызов метода: " + joinPoint.getSignature().getName());
        if (joinPoint.getArgs().length > 0) {
            System.out.println("Переданые аргументы:");
            Arrays.stream(joinPoint.getArgs()).map(Object::toString).forEach(System.out::println);
        } else {
            System.out.println("Переданные аргументы отсутсвуют.");
        }
    }
}