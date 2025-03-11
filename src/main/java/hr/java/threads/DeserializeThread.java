package hr.java.threads;

public class DeserializeThread extends AccessSerializedThread implements Runnable{
    @Override
    public void run() {
        super.deserializeFile();

    }
}
