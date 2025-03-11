package hr.java.exeptions;

import static hr.java.application.LoginScreen.logger;

public class UserNotRegisteredExeption extends RuntimeException{
    public UserNotRegisteredExeption() {
    }

    public UserNotRegisteredExeption(String message) {
        super(message);
        logger.error(message);

    }

    public UserNotRegisteredExeption(String message, Throwable cause) {
        super(message, cause);
        logger.error(message, cause);

    }

    public UserNotRegisteredExeption(Throwable cause) {
        super(cause);
        logger.error(String.valueOf(cause));

    }
}
