package com.eurodyn.uns.util.uid;


import java.security.SecureRandom;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UidGenerator {
    
    private static SecureRandom prng;
    private final static Object mutex = new Object();
    
    static {
        try {
            synchronized (mutex) {
                prng = SecureRandom.getInstance("SHA1PRNG");
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
        
    
    public synchronized  static String generate() throws Exception {
        try {

          String randomNum = new Integer( prng.nextInt() ).toString();

          MessageDigest sha = MessageDigest.getInstance("SHA-1");
          byte[] result =  sha.digest( randomNum.getBytes() );

          return hexEncode(result);
        }
        catch (NoSuchAlgorithmException e) {
          e.printStackTrace();
          throw new Exception(e);
        }
    }
    
    private static String hexEncode( byte[] aInput){
        StringBuffer result = new StringBuffer();
        char[] digits = {'0', '1', '2', '3', '4','5','6','7','8','9','a','b','c','d','e','f'};
        for ( int idx = 0; idx < aInput.length; ++idx) {
          byte b = aInput[idx];
          result.append( digits[ (b&0xf0) >> 4 ] );
          result.append( digits[ b&0x0f] );
        }
        return result.toString();
      }
}
