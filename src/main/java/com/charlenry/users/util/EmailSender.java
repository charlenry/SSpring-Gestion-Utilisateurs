package com.charlenry.users.util;

/**
 * The line `public interface EmailSender` is declaring an interface named `EmailSender`. This interface defines 
 * a contract for classes that implement it to provide a method `sendEmail` that takes 
 * two `String` parameters - `toEmail` and `body`. Classes that implement this interface must provide 
 * an implementation for the `sendEmail` method.
 */
public interface EmailSender {
	void sendEmail(String toEmail, String body);
}
