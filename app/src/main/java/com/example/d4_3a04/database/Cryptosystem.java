package com.example.d4_3a04.database;

import android.content.Context;
import android.os.StrictMode;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;

import com.example.d4_3a04.BuildConfig;
import com.example.d4_3a04.SingleChatProvider.SingleChatManager;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Cryptosystem extends AppCompatActivity {


    static String dbUser = BuildConfig.DB_USER;
    static String dbPassword = BuildConfig.DB_PASSWORD;
    static String dbConnection = BuildConfig.DB_CONNECTION;
    // Object used for sql connection.
    static Connection mysqlConnection;


    // Temporary, single key encryption/decryption.
    private static String key_val="JustAKey";

    public static void startDB(Context context){

        //Checks if connection is not already made. If so, then create a new connection.

        if (mysqlConnection == null){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try{
                Class.forName("com.mysql.jdbc.Driver");

                // Connect to web server DB
                mysqlConnection = DriverManager.getConnection(dbConnection, dbUser, dbPassword);
            }catch (Exception e){
                throw new RuntimeException(e);
            }

        }
    }

    // Closes the connection for good practice purposes (Can't stay connected forever)
    public static void disconnectDB(){
        try{
            mysqlConnection.close();
            mysqlConnection=null;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    // Adds entry into the table if not already there. Otherwise updates the provider entry.
    public static void updateProvider(SingleChatManager provider, String employee_id, String other_employee){
        try {

            String encrypted_employee_id = encrypt(employee_id);
            String encrypted_other_employee = encrypt(other_employee);

            Statement statement = mysqlConnection.createStatement();
            String serialized = SingleChatManager.serializeToJson(provider);
            String encrypted_provider = encrypt(serialized);

            String COMMAND = String.format("INSERT INTO providers (this_employee, other_employee, provider) VALUES (\"%s\", \"%s\", \"%s\") ON DUPLICATE KEY UPDATE provider=\"%s\"", encrypted_employee_id, encrypted_other_employee, encrypted_provider, encrypted_provider);

            statement.execute(COMMAND);
        } catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    // Retrieve the provider given the ids of the two employees communicating.
    public static List<String> getProvider(String this_employee, String other_employee){

        List<String> output = new ArrayList<>();
        try {
            Statement statement = mysqlConnection.createStatement();

            String encrypted_this_employee = encrypt(this_employee);
            String encrypted_other_employee = encrypt(other_employee);


            String COMMAND = String.format("SELECT provider " +
                    "FROM providers " +
                    "WHERE this_employee=\"%s\" AND other_employee=\"%s\";", encrypted_this_employee, encrypted_other_employee);

            ResultSet result = statement.executeQuery(COMMAND);

            // Move the cursor to the first row
            if (result.next()) {
                // Retrieve the value from the first column of the current row
                String encrypted_provider = result.getString(1);
                String provider = decrypt(encrypted_provider);
                output.add(provider);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return output;

    }

    public static List<String> getOtherEmployee(String employee){
        List<String> output = new ArrayList<>();
        try {
            Statement statement = mysqlConnection.createStatement();

            String encrypted_this_employee = encrypt(employee);

            String COMMAND = String.format("SELECT other_employee " +
                    "FROM providers " +
                    "WHERE this_employee=\"%s\";", encrypted_this_employee);

            ResultSet result = statement.executeQuery(COMMAND);

            while (result.next()){
                String encrypted_provider = result.getString(1);
                String provider = decrypt(encrypted_provider);
                output.add(provider);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return output;
    }

    public static List<String> getUsers(String employee_id){
        try {
            List<String> output = new ArrayList<>();
            Statement statement = mysqlConnection.createStatement();

            String encrypted_this_employee = encrypt(employee_id);

            String COMMAND = String.format("SELECT email " +
                    "FROM users " +
                    "WHERE email<>\"%s\";", encrypted_this_employee);

            ResultSet result = statement.executeQuery(COMMAND);

            while (result.next()){
                String decrypted_email = decrypt(result.getString(1));
                output.add(decrypted_email);
            }
            return output;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static List<String> getNonConvoUsers(String employee_id){
        try {
            List<String> output = new ArrayList<>();
            Statement statement = mysqlConnection.createStatement();

            String encrypted_this_employee = encrypt(employee_id);

            String COMMAND = String.format("SELECT u.email " +
                    "FROM users u " +
                    "WHERE u.email NOT IN (SELECT other_employee FROM providers p WHERE p.this_employee=\"%s\") "+
                    "AND u.email<>\"%s\";", encrypted_this_employee, encrypted_this_employee);

            ResultSet result = statement.executeQuery(COMMAND);

            while (result.next()){
                String decrypted_email = decrypt(result.getString(1));
                output.add(decrypted_email);
            }
            return output;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static boolean authenticateUser(String email, String password){
        try {
            Statement statement = mysqlConnection.createStatement();

            String encrypted_this_employee = encrypt(email);

            String COMMAND = String.format("SELECT password " +
                    "FROM users " +
                    "WHERE email=\"%s\";", encrypted_this_employee);

            ResultSet result = statement.executeQuery(COMMAND);

            if (result.next()){
                String encrypted_password = result.getString(1);
                String provided_encrypted_password = encrypt(password);
                if (encrypted_password.equals(provided_encrypted_password)){
                    return true;
                }else{
                    return false;
                }
            }else{
                return false;
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }

    }


    public static boolean addUser(String email, String password){
        try {
            String output = null;
            Statement statement = mysqlConnection.createStatement();

            String encrypted_this_employee = encrypt(email);

            String COMMAND = String.format("SELECT email " +
                    "FROM users " +
                    "WHERE email=\"%s\";", encrypted_this_employee);

            ResultSet result = statement.executeQuery(COMMAND);

            if (result.next()){
                String encrypted_provider = result.getString(1);
                String provider = decrypt(encrypted_provider);
                return false;
            } else{
                String encrypted_password = encrypt(password);
                String INSERT = String.format("INSERT INTO users (email, password) VALUES (\"%s\", \"%s\");", encrypted_this_employee, encrypted_password);
                statement.execute(INSERT);
                return true;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void addRequest(String requestor, String requestee){
        try {

            String encrypted_requestee = encrypt(requestee);
            String encrypted_requestor = encrypt(requestor);

            Statement statement = mysqlConnection.createStatement();

            String COMMAND = String.format("INSERT INTO requests (requestee, requestor) VALUES (\"%s\", \"%s\") ON DUPLICATE KEY UPDATE requestee=\"%s\", requestor=\"%s\"", encrypted_requestee, encrypted_requestor, encrypted_requestee, encrypted_requestor);

            statement.execute(COMMAND);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static List<String> getRequests(String email){
        try {
            List<String> output = new ArrayList<>();

            Statement statement = mysqlConnection.createStatement();

            String encrypted_this_employee = encrypt(email);

            String COMMAND = String.format("SELECT requestor " +
                    "FROM requests " +
                    "WHERE requestee=\"%s\";", encrypted_this_employee);

            ResultSet result = statement.executeQuery(COMMAND);

            while (result.next()){
                String encrypted_requestor = result.getString(1);
                output.add(decrypt(encrypted_requestor));
            }

            return output;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void removeRequest(String requestor, String requestee){
        try {

            String encrypted_requestee = encrypt(requestee);
            String encrypted_requestor = encrypt(requestor);

            Statement statement = mysqlConnection.createStatement();

            String COMMAND = String.format("DELETE FROM requests where requestor=\"%s\" AND requestee=\"%s\";", encrypted_requestor, encrypted_requestee);

            statement.execute(COMMAND);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }



    // Encrypt and Decrypt code is obtained from open source repository: https://github.com/saeed74/Android-DES-Encryption/tree/master?tab=Apache-2.0-1-ov-file#readme
    // Repository has apache-2.0 licence. So its fine to use their code.
    // Uses DNS protocol.

    private static String decrypt(String value){
        String coded;
        if(value.startsWith("code==")){
            coded = value.substring(6,value.length()).trim();
        }else{
            coded = value.trim();
        }

        String result = null;
        try {
            // Decoding base64
            byte[] bytesDecoded = Base64.decode(coded.getBytes("UTF-8"),Base64.DEFAULT);

            SecretKeySpec key = new SecretKeySpec(key_val.getBytes(), "DES");

            Cipher cipher = Cipher.getInstance("DES/ECB/ZeroBytePadding");

            // Initialize the cipher for decryption
            cipher.init(Cipher.DECRYPT_MODE, key);

            // Decrypt the text
            byte[] textDecrypted = cipher.doFinal(bytesDecoded);

            result = new String(textDecrypted);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "Decrypt Error";
        }
        catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return "Decrypt Error";
        }
        catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            return "Decrypt Error";
        }
        catch (BadPaddingException e) {
            e.printStackTrace();
            return "Decrypt Error";
        }
        catch (InvalidKeyException e) {
            e.printStackTrace();
            return "Decrypt Error";
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "Decrypt Error";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Decrypt Error";
        }
        return result;
    }

    private static String encrypt(String value) {

        String crypted = "";

        try {

            byte[] cleartext = value.getBytes("UTF-8");

            SecretKeySpec key = new SecretKeySpec(key_val.getBytes(), "DES");

            Cipher cipher = Cipher.getInstance("DES/ECB/ZeroBytePadding");

            // Initialize the cipher for decryption
            cipher.init(Cipher.ENCRYPT_MODE, key);

            crypted = Base64.encodeToString(cipher.doFinal(cleartext), Base64.DEFAULT);


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "Encrypt Error";
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return "Encrypt Error";
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            return "Encrypt Error";
        } catch (BadPaddingException e) {
            e.printStackTrace();
            return "Encrypt Error";
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return "Encrypt Error";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "Encrypt Error";
        } catch (Exception e) {
            e.printStackTrace();
            return "Encrypt Error";
        }

        //return "code==" + crypted;
        return crypted;
    }

}
