package hello.service;

import hello.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    BCryptPasswordEncoder mockEncoder;

    @Mock
    UserMapper userMapper;

    @InjectMocks
    UserService userService;

    @Test
    public void testSave(){
        Mockito.when(mockEncoder.encode("mypassword")).thenReturn("myEncodedPassword");
        userService.save("myusername","mypassword");
        Mockito.verify(userMapper).save("myusername","myEncodedPassword", null);
    }



}
