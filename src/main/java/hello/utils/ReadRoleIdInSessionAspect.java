package hello.utils;

import hello.entity.RouteResult;
import hello.entity.User;
import hello.service.AuthService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Aspect
public class ReadRoleIdInSessionAspect {
    private final AuthService authService;

    @Inject
    public ReadRoleIdInSessionAspect(AuthService authService) {
        this.authService = authService;
    }

    @Around("@annotation(hello.anno.ReadRoleIdInSession)")
    public Object ReadRoleIdInSession(ProceedingJoinPoint joinPoint) throws Throwable{
        Object[] args = joinPoint.getArgs();
        args[0]=authService.getCurrentUser().map(User::getRoleId).orElse(-1);
        if(args[0].toString().equals("" + -1)) {
            return RouteResult.failure("expire");
        } else {
            System.out.println("----args--role");
            System.out.println(args[0]);
            return joinPoint.proceed(args);
        }

    }
}
