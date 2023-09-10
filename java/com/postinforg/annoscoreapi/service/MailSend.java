package com.postinforg.annoscoreapi.service;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MailSend {

	@Autowired
	private JavaMailSender mailSender;
	
	@Value("${mail.from}") private String mailfrom;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MailSend.class);
	
	@Async
	public void mailSending(String title, String content, String[] toUser
			, MultipartFile mFile) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
			
			messageHelper.setFrom(mailfrom);  //보내는사람메일:생략불가 
			messageHelper.setTo(toUser); 	 //받는사람메일
			messageHelper.setSubject(title); //제목(생략가능)
			messageHelper.setText(content, true);  //메일내용 (true : html적용, 기본 false)
			if(mFile != null) {
				messageHelper.addAttachment(mFile.getOriginalFilename(), mFile);
			}
			
			LOGGER.debug(">>> send mail : " + toUser);
			mailSender.send(message);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	@Async
	public void mailTemplateSend(String subject, String htmlText, String from
			, String toUser, String[] toUserArr
			, MultipartFile mFile) {
		if((toUser == null || toUser.equals("")) && (toUserArr == null || toUserArr.length == 0)) return;
		
		try {
			StringBuilder contentBuilder = new StringBuilder();
			ClassPathResource resource = new ClassPathResource("egovframework/egovProps/mailTemplate/template_email.html");

		    BufferedReader in = new BufferedReader(new FileReader(resource.getURI().getPath()));
		    String str;
		    while ((str = in.readLine()) != null) {
		        contentBuilder.append(str);
		    }
		    in.close();
		    String content = contentBuilder.toString();
		    String bodyText = htmlText.replaceAll("\n\r","<br/>").replaceAll("\n","<br/>").replaceAll("\r","<br/>");
		    String[] contents = {
		    		propertyService.getString("Globals.frontUrl") + "/resources/img/common/gnb_logo.jpg"
		    		, bodyText
		    };
		    for (int i = 0; i < contents.length; i++) {
				content = content.replace("{" + i + "}", contents[i]);
			}
			
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
			
			String fromemail = from;
			if(from == null || from.equals("")) fromemail = propertyService.getString("Globals.masteremail");
			
			messageHelper.setFrom(fromemail); 
			if(toUserArr != null && toUserArr.length > 0) {
				messageHelper.setTo(toUserArr);
			}
			else {
				messageHelper.setTo(toUser);
			}
			messageHelper.setSubject(subject);
			messageHelper.setText(content, true);
			if(mFile != null) {
				messageHelper.addAttachment(mFile.getOriginalFilename(), mFile);
			}
			
			LOGGER.debug(">>> send mail : " + toUser + ", " + toUserArr);
			mailSender.send(message);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	 */
}
