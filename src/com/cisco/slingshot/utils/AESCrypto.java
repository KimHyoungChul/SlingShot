package com.cisco.slingshot.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;


public class AESCrypto{
	
    private static final String KEY_ALGORITHM = "AES";  
    
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    
	public static byte[] encrypt(byte[] data,byte[] key)throws Exception{
		
		Key keyObj = createKeyFromData(key);
		//Initialize
        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);  
        cipher.init(Cipher.ENCRYPT_MODE, keyObj); 
        return cipher.doFinal(data);
      
		/*test
		final String password = "123456";
		final String username      = "yuancui";
		
		
		Key keyObj = createKeyFromData(username.getBytes());//new SecretKeySpec(key, KEY_ALGORITHM);  
		
		//Initialize
        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);  
        cipher.init(Cipher.ENCRYPT_MODE, keyObj); 
        
        byte[] encryptedData = cipher.doFinal(password.getBytes());
        
        SortedMap<String, Charset> sets = Charset.availableCharsets();
        
        String strEncryptedData = new String(encryptedData,"UTF-8");//new String(Base64.encode(encryptedData, Base64.DEFAULT));
        
        Key keyObj1 = createKeyFromData(username.getBytes());
        Cipher cipher1 = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);  
        cipher1.init(Cipher.DECRYPT_MODE, keyObj1); 
        
        byte[] byteEncryptedData = new String(strEncryptedData.getBytes("UTF-8"),"ISO-8859-1").getBytes("ISO-8859-1");//Base64.decode(strEncryptedData.getBytes(), Base64.DEFAULT);
        byte[] decryptedData1 = cipher1.doFinal(byteEncryptedData);
       
        String result = new String(decryptedData1);
        return encryptedData;  
        */
        
	}
	
	public static byte[] dencrypt(byte[] data,byte[] key)throws Exception{
		
		Key keyObj = createKeyFromData(key);//new SecretKeySpec(key, KEY_ALGORITHM);  
		 //实例化  
        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);  
        //使用密钥初始化，设置为解密模式  
        cipher.init(Cipher.DECRYPT_MODE, keyObj);  
        //执行操作  
        byte[] encryptedData = cipher.doFinal(data);
        
        return encryptedData;
        
	}
	
	public static Key createKeyFromData(byte[] data)throws Exception{
		/*
		 KeyGenerator kgen = KeyGenerator.getInstance(KEY_ALGORITHM);
         SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
         sr.setSeed(data);
	     kgen.init(128, sr); // 192 and 256 bits may not be available
	     SecretKey skey = kgen.generateKey();
	     byte[] raw = skey.getEncoded();
	     return  new SecretKeySpec(raw, KEY_ALGORITHM);
	     */
	     return  new SecretKeySpec(data, KEY_ALGORITHM);
     
	}
	
	
	private final static String PaddingChar="0000000000000000";
	
	private static byte[] getRawKey(String password){
		 if (password == null){  
			 password = ""; 
		 }
		 if (password.length() < 16){  
			 password = password+PaddingChar.substring(password.length());  
		 }else if (password.length() > 16){
		    password = password.substring(0,16);  
		 } 
		 return password.getBytes();
	 }  
	

	public static String encryptAES(String plaintpwd, String username) {  
		String ciphertext = "";
		try {               
				SecretKeySpec key = new SecretKeySpec(getRawKey(username), "AES");  
				Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// 创建密码器  
				byte[] byteContent = plaintpwd.getBytes("utf-8");  
				cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化  
				byte[] ciphertextByte = cipher.doFinal(byteContent); 
				//ciphertext = Byte2HexUtil.bytes2Hex(ciphertextByte);
				ciphertext = byte2hex(ciphertextByte);
				return ciphertext; // 加密  
	        } catch (NoSuchAlgorithmException e) {  
	                e.printStackTrace();  
	        } catch (NoSuchPaddingException e) {  
	                e.printStackTrace();  
	        } catch (InvalidKeyException e) {  
	                e.printStackTrace();  
	        } catch (UnsupportedEncodingException e) {  
	                e.printStackTrace();  
	        } catch (IllegalBlockSizeException e) {  
	                e.printStackTrace();  
	        } catch (BadPaddingException e) {  
	                e.printStackTrace();  
	        }  
	        return ciphertext;  
	}  
	
	public static String dencryptAES(String cipherpwd, String username) {  
		String plaintpwd = "";
		try {               
				SecretKeySpec key = new SecretKeySpec(getRawKey(username), "AES");  
				Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// 创建密码器  
				//byte[] byteContent = plaintpwd.getBytes("utf-8");  
				
				byte[] contentByte = hex2byte(cipherpwd);
				cipher.init(Cipher.DECRYPT_MODE, key);// 初始化  
				
				byte[] plainttextByte = cipher.doFinal(contentByte); 
				//ciphertext = Byte2HexUtil.bytes2Hex(ciphertextByte);
				return new String(plainttextByte,"utf-8"); // 加密  
	        } catch (NoSuchAlgorithmException e) {  
	                e.printStackTrace();  
	        } catch (NoSuchPaddingException e) {  
	                e.printStackTrace();  
	        } catch (InvalidKeyException e) {  
	                e.printStackTrace();    
	        } catch (IllegalBlockSizeException e) {  
	                e.printStackTrace();  
	        } catch (BadPaddingException e) {  
	                e.printStackTrace();  
	        } catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
	        return plaintpwd;  		
	}
	
	

    public static String byte2hex(byte[] data) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            String temp = Integer.toHexString(((int) data[i]) & 0xFF);
            for(int t = temp.length();t<2;t++)
            {
                sb.append("0");
            }
            sb.append(temp);
        }
        return sb.toString();
    }
    
  //16进制转换为byte数组
  public static byte[] hex2byte(String hexStr){
      byte[] bts = new byte[hexStr.length() / 2];
      for (int i = 0,j=0; j < bts.length;j++ ) {
         bts[j] = (byte) Integer.parseInt(hexStr.substring(i, i+2), 16);
         i+=2;
      }
      return bts;
  }
	
    
    private static String  ByteToString(byte[] data){  
    	/*
        if(null == data){  
            return null;  
        }  
        StringBuilder sb = new StringBuilder("");  
        for(byte b:data){  
            sb.append(b);  
        }  
        return sb.toString();
        */
  

			return  Base64.encodeToString(data, Base64.DEFAULT);
			
	
    }  
}