package hello;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class Application {
	@PostConstruct
	void setDefaultTimezone() {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}


//	@Bean
//	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
//		return args -> {
//			System.out.println("Let's inspect the beans provided by Spring Boot:");
//			String[] beans = ctx.getBeanDefinitionNames();
//			Arrays.sort(beans);
//			for (String beanName: beans) {
//				System.out.println(beanName);
//			}
//		};
//	}

}
