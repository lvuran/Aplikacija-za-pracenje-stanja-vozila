package hr.java.exeptions;

import static hr.java.application.LoginScreen.logger;

public class UsernameDuplicateExeption extends Exception{
    public UsernameDuplicateExeption() {
    }

    public UsernameDuplicateExeption(String message) {
        super(message);
        logger.error(message);
    }

    public UsernameDuplicateExeption(String message, Throwable cause) {
        super(message, cause);
        logger.error(message,cause);

    }

    public UsernameDuplicateExeption(Throwable cause) {
        super(cause);
        logger.error(String.valueOf(cause));

    }
}
