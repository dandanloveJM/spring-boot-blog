package hello.utils;

import hello.entity.ProjectResult;
import hello.entity.User;
import hello.service.AuthService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Aspect
public class ReadUserIdInSessionAspect {
    private final AuthService authService;

    @Inject
    public ReadUserIdInSessionAspect(AuthService authService) {
        this.authService = authService;
    }

    @Around("@annotation(hello.anno.ReadUserIdInSession)")
    public Object ReadUserIdInSession(ProceedingJoinPoint joinPoint) throws Throwable{
        Object[] args = joinPoint.getArgs();
        args[0]=authService.getCurrentUser().map(User::getId).orElse(-1);
        System.out.println("---args--");
        System.out.println(args[0]);
        return joinPoint.proceed(args);
    }
}
