package br.java.sail;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class SailApplication {

	public static void main(String[] args) {
		SpringApplication.run(SailApplication.class, args);
	}

}
