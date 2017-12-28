package com.spy.demo;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.file.Files;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	// Download and run Apache MINA.
		// Ftp Client to Apache MINA local instance.
		@Bean
		DefaultFtpSessionFactory ftpSessionFactory(
				@Value("${ftp.port:2121}") int port,
				@Value("${ftp.username:admin}") String username,
				@Value("${ftp.password:admin}") String pw
				) {
			DefaultFtpSessionFactory ftpSessionFactory = new DefaultFtpSessionFactory();
			ftpSessionFactory.setPort(port);
			ftpSessionFactory.setPassword(pw);
			ftpSessionFactory.setUsername(username);
			return ftpSessionFactory;
			
		}
		
		// Choose directory you want to spy
		// Remote directory 'test' is under home directory of the local ftp server (Apache MINA)
		@Bean
		IntegrationFlow files(@Value("${input-directory:C:/Spy}") File in,
									DefaultFtpSessionFactory ftpSessionFactory) {
			return IntegrationFlows.from(Files.inboundAdapter(in).autoCreateDirectory(true).preventDuplicates(true).patternFilter("*.txt"),
					poller -> poller.poller(pm -> pm.fixedRate(1000)))
					.handleWithAdapter(adapters -> adapters.ftp(ftpSessionFactory)
							.remoteDirectory("test")
							.autoCreateDirectory(true)
					).get();
		}

}
