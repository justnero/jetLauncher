package ru.justnero.jetlauncher.util;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UtilHash {
    
    public static String md5(File file) {
        StringBuilder hexString = new StringBuilder();
        MessageDigest md5;
        FileInputStream fis = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(file);
            byte[] dataBytes = new byte[1024];
            int nread = 0; 
            while((nread = fis.read(dataBytes)) != -1)
                md5.update(dataBytes, 0, nread);
            byte[] mdbytes = md5.digest();
            for (int i = 0; i < mdbytes.length; i++) {
                String hex = Integer.toHexString(0xff & mdbytes[i]);
                if(hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            fis.close();
        } catch (Exception e) {
            try {
                fis.close();
            } catch (Exception ex) {}
            return e.toString();
        }
        return hexString.toString();
    }
    
    public static String md5(String str) {
        return md5(str.getBytes());
    }
    
    public static String md5(byte[] str) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("md5");
            md5.reset();
            md5.update(str); 
            byte byteData[] = md5.digest();
            return byteArrayToHexString(byteData);                                                                     
        } catch (NoSuchAlgorithmException e) {                        
            return e.toString();
        }
    }
    
    public static String sha1(File file) {
        StringBuilder hexString = new StringBuilder();
        MessageDigest md5;
        FileInputStream fis = null;
        try {
            md5 = MessageDigest.getInstance("SHA-1");
            fis = new FileInputStream(file);
            byte[] dataBytes = new byte[1024];
            int nread = 0;
            while((nread = fis.read(dataBytes)) != -1)
                md5.update(dataBytes, 0, nread);
            byte[] mdbytes = md5.digest();
            for (int i = 0; i < mdbytes.length; i++) {
                String hex = Integer.toHexString(0xff & mdbytes[i]);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            fis.close();
        } catch (Exception e) {
            try {
                fis.close();
            } catch (Exception ex) {}
            return e.toString();
        }
        return hexString.toString();
    }
    
    public static String sha1(String str) {
        return sha1(str.getBytes());
    }
    
    public static String sha1(byte[] str) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch(NoSuchAlgorithmException e) {
            return e.toString();
        } 
        return byteArrayToHexString(md.digest(str));
    }
    
    public static String byteArrayToHexString(byte[] byteData) {
        StringBuilder  hexString = new StringBuilder();
        for(int i=0;i<byteData.length;i++) 
            hexString.append(Integer.toString((byteData[i]&0xff)+0x100,16).substring(1));
        return hexString.toString();
    }
    
}
