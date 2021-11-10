package hello.service;

import hello.entity.User;
import hello.mapper.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.ZonedDateTime;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    BCryptPasswordEncoder mockEncoder;

    @Mock
    UserMapper userMapper;

    @InjectMocks
    UserService userService;
    private Object UsernameNotFoundException;

    @Test
    public void testSave(){
        Mockito.when(mockEncoder.encode("mypassword")).thenReturn("myEncodedPassword");
        userService.save("myusername","mypassword");
        Mockito.verify(userMapper).save("myusername","myEncodedPassword", null);
    }

    @Test
    public void testGetUserByUsername(){
        userService.getUserByUsername("myUsername");
        // 确认真的调用了userMapper的findUserByUsername方法
        Mockito.verify(userMapper).findUserByUsername("myUsername");
    }

    @Test
    public void throwExceptionWhenUserNotFound(){
        Throwable exception = Assertions.assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("aaa"));

        Assertions.assertTrue(exception.getMessage().contains("不存在"));
    }

    @Test
    public void returnUserDetailsWhenUserFound(){
        ZonedDateTime time = ZonedDateTime.now();
        // 设置条件
        Mockito.when(userMapper.findUserByUsername("aaa")).thenReturn(new User(123, "aaa",null, time, time, "encodedPassword"));

        // 执行行为
        UserDetails userDetails = userService.loadUserByUsername("aaa");

        // 验证返回值是否符合预期
        Assertions.assertEquals("aaa", userDetails.getUsername());
        Assertions.assertEquals("encodedPassword", userDetails.getPassword());

    }



}
