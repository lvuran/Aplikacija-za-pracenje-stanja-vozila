package hr.java.exeptions;

import static hr.java.application.LoginScreen.logger;

public class RegistrationNumberDuplicateExeption extends Exception{
    public RegistrationNumberDuplicateExeption() {
    }

    public RegistrationNumberDuplicateExeption(String message) {
        super(message);
        logger.error(message);
    }

    public RegistrationNumberDuplicateExeption(String message, Throwable cause) {
        super(message, cause);
        logger.error(message, cause);
    }

    public RegistrationNumberDuplicateExeption(Throwable cause) {
        super(cause);
        logger.error(String.valueOf(cause));
    }
}
