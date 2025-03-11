package hr.java.threads;

import hr.java.models.Changes;
import hr.java.utilities.FilesUtilities;

import java.io.IOException;
import java.util.List;

import static hr.java.application.LoginScreen.logger;

public class AccessSerializedThread {

        public static Boolean activeConnectionWithFile = false;

        public synchronized void serializeFile(Changes change) {

            while(activeConnectionWithFile) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    logger.error(String.valueOf(e));
                    throw new RuntimeException(e);
                }
            }

            activeConnectionWithFile = true;

            try {
                FilesUtilities.serializeFile(change);
            } catch (IOException e) {
                logger.error(String.valueOf(e));
                throw new RuntimeException(e);
            }

            activeConnectionWithFile = false;

            notifyAll();
        }

        List<Changes> changesList;
        public synchronized void deserializeFile(){

            while(activeConnectionWithFile) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    logger.error(String.valueOf(e));
                    throw new RuntimeException(e);
                }
            }

            activeConnectionWithFile = true;

            try {
                changesList = FilesUtilities.deserializeFile();
            } catch (IOException e) {
                logger.error(String.valueOf(e));
                throw new RuntimeException(e);
            }

            activeConnectionWithFile = false;

            notifyAll();
        }

    public List<Changes> getChangesList() {
        return changesList;
    }
}
