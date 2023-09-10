package com.postinforg.annoscoreapi.fw;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

	@Value("${mail.smtp}")
	private String mailsmtp;
	@Value("${mail.authuser}")
	private String mailuser;
	@Value("${mail.authpw}")
	private String mailpw;

	@Autowired
	CommonInterceptor commonInterceptor;

	@Bean
	public JavaMailSenderImpl mailSender() {
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

//        javaMailSender.setProtocol("smtp");
//        javaMailSender.setHost("127.0.0.1");
//        javaMailSender.setPort(25);

		javaMailSender.setHost(mailsmtp);
		javaMailSender.setUsername(mailuser);
		javaMailSender.setPassword(mailpw);

		javaMailSender.setPort(465);
		javaMailSender.setJavaMailProperties(getMailProperties());

		return javaMailSender;
	}

	private Properties getMailProperties() {
		Properties properties = new Properties();
		properties.setProperty("mail.transport.protocol", "smtp");
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.starttls.enable", "true");
		properties.setProperty("mail.debug", "true");
		properties.setProperty("mail.smtp.ssl.trust", mailsmtp);
		properties.setProperty("mail.smtp.ssl.enable", "true");
		return properties;
	}
}
