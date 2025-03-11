package hr.java.threads;

import hr.java.models.Changes;

public class SerializeThread extends AccessSerializedThread implements Runnable {

    Changes change;
    @Override
    public void run() {
        super.serializeFile(change);
    }

    public SerializeThread(Changes change) {
        this.change = change;
    }
}
