package com.charlenry.users.util;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;


/**
 * The line `public class EmailService implements EmailSender` is defining a class named `EmailService` that implements 
 * the `EmailSender` interface. This means that the `EmailService` class must provide an implementation for all the 
 * methods defined in the `EmailSender` interface.
 */
@AllArgsConstructor
@Service
public class EmailService implements EmailSender {
	
	private final JavaMailSender mailSender;

	public void sendEmail(String to, String email) {
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
			helper.setText(email, true);
			helper.setTo(to);
			helper.setSubject("Confirm your email");
			helper.setFrom("admin@mesproduits.local");
			mailSender.send(mimeMessage);
		} catch (MessagingException e) {
			throw new IllegalStateException("failed to send email");
		}
	}
}
