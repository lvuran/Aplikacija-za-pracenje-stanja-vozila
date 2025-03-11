package hr.java.utilities;

import hr.java.models.Changes;
import hr.java.models.User;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import static hr.java.application.LoginScreen.logger;

public class FilesUtilities {

    public static HashMap getUsersFromFile() throws IOException {
        String username, password;
        HashMap<String, String> userMap = new HashMap();
        File userFile = new File("dat/users.txt");
        BufferedReader br = new BufferedReader(new FileReader(userFile));
        while ((username = br.readLine()) != null)
        {
            password=br.readLine();
            userMap.put(username, password);
        }
        return  userMap;
    }
    public static void  registerUser(String username, String password) throws IOException {

        PrintWriter printWriter = new PrintWriter(new FileWriter("dat/users.txt", true));
        printWriter.append(username + "\n");
        printWriter.append(passwords(password) + "\n");
        printWriter.close();
    }

    public static String passwords(String password) {

        String generatedPassword = null;

        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            md.update(password.getBytes());

            byte[] bytes = md.digest();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error(String.valueOf(e));
            e.printStackTrace();
        }

    return generatedPassword;
    }

    public static void deleteUser(User toDelete) throws IOException {
        HashMap users = getUsersFromFile();
        users.remove(toDelete.getName());
        PrintWriter writer = new PrintWriter("dat/users.txt");


        for(Object name: users.keySet())
        {
        writer.println(name.toString());
        writer.println(users.get(name).toString());
        }
        writer.close();
    }








    //BINARY FILE
    public static final String SERIALIZATION_FILE_NAME = "dat/changes.dat";


    public static void serializeFile(Changes change) throws IOException {
        List<Changes> serialize = new ArrayList<>();

            serialize = deserializeFile();


        serialize.add(change);
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SERIALIZATION_FILE_NAME));
        out.writeObject(serialize);
        out.close();
    }

    public static List<Changes> deserializeFile() throws IOException {
        List<Changes> results = new ArrayList<>();
        ObjectInputStream in = null;

        try{
            in = new ObjectInputStream(new FileInputStream(SERIALIZATION_FILE_NAME));
            results = (List<Changes>) in.readObject();
            in.close();
        } catch (FileNotFoundException ex)
        {
            logger.error(String.valueOf(ex));
            return results;
        } catch (IOException ex)
        {
            logger.error(String.valueOf(ex));
            System.err.println(ex);
        }
        catch (ClassNotFoundException ex){
            logger.error(String.valueOf(ex));
            System.err.println(ex);
        }


        return results;
    }



}
