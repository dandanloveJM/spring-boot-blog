package hello.configuration;


import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class WebCORSConfig implements WebMvcConfigurer {
    @Override
    public  void  addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000","http://localhost:3001","http://localhost:8888","http://47.96.175.63","http://121.41.9.16")
                .allowedHeaders("*")
                .allowedMethods("*")
                .allowCredentials(true)
                .exposedHeaders("Set-Cookie")
                .maxAge(3600);
    }
}
