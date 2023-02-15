package com.smart.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
	public boolean sendEmail(String subject, String message, String to) {
		
		    boolean f=false;
		    String from="patidar14pradhyumn@gmail.com";
			String host="smtp.gmail.com";
			//get the system properties
			
			Properties properties=System.getProperties();
			//setting important info to properties object
			//host set
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", "465");
			properties.put("mail.smtp.ssl.enable", "true");
			properties.put("mail.smtp.auth", "true");
			
			//Step 1 :get the session object
			Session session=Session.getInstance(properties, new Authenticator()
					{

						@Override
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication("patidar14pradhyumn@gmail.com","jbyavlsqjjtpzziw");
									
						}
				
					});
			
			//step 2: compose the message[text, multi-media]
			
			MimeMessage m=new MimeMessage(session);
			
			// from email id
			try
			{
			m.setFrom(from);
			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			//adding subject to message
			m.setSubject(subject);
			//adding text to message
			//m.setText(message);
			m.setContent(message,"text/html");
			
			//step:3  send the message using transport class
			
			Transport.send(m);
			f=true;
			System.out.println("send successfully...............");
			}catch(Exception e)
			{
				System.out.println("Error"+e);
				f=false;
			}	
			return f;
		}

}
