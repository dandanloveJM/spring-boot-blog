package hello.utils;

import hello.entity.User;
import hello.service.AuthService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Aspect
public class ReadUserIdInSessionAspect {
    private final AuthService authService;

    @Inject
    public ReadUserIdInSessionAspect(AuthService authService) {
        this.authService = authService;
    }

    @Around("@annotation(hello.anno.ReadUserIdInSession)")
    public void ReadUserIdInSession(ProceedingJoinPoint joinPoint) throws Throwable{
        Object[] args = joinPoint.getArgs();
        args[0]=authService.getCurrentUser().map(User::getId).orElse(-1);
        System.out.println(args);
        joinPoint.proceed(args);
    }
}
