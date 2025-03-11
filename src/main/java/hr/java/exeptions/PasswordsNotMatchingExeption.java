package hr.java.exeptions;

import static hr.java.application.LoginScreen.logger;

public class PasswordsNotMatchingExeption extends RuntimeException{
    public PasswordsNotMatchingExeption() {


    }

    public PasswordsNotMatchingExeption(String message) {
        super(message);
        logger.error(message);

    }

    public PasswordsNotMatchingExeption(String message, Throwable cause) {
        super(message, cause);
        logger.error(message, cause);

    }

    public PasswordsNotMatchingExeption(Throwable cause) {
        super(cause);
        logger.error(String.valueOf(cause));

    }
}
