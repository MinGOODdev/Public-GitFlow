package org.gitflow.sw.util.security;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class SHA256Encryption {

    public static String encrypt(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] passBytes = str.getBytes();
            md.reset();

            byte[] digested = md.digest(passBytes);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < digested.length; ++i) {
                sb.append(Integer.toHexString(0xff & digested[i]));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return str;
        }
    }

//  /**
//   * 암호화된 문자열을 리턴
//   *
//   * @param rawPassword
//   * @return
//   */
//  @Override
//  public String encode(CharSequence rawPassword) {
//    String rawPasswordStr = (String) rawPassword;
//    return encrypt(rawPasswordStr);
//  }

//  /**
//   * 입력 받은 값과 입력받은 값을 암호화한 것을 비교하여 그 결과를 boolean으로 리턴
//   *
//   * @param rawPassword
//   * @param encodedPassword
//   * @return
//   */
//  @Override
//  public boolean matches(CharSequence rawPassword, String encodedPassword) {
//    String rawPasswordStr = (String) rawPassword;
//    String encodePassword = encrypt(rawPasswordStr);
//    return encodePassword.equals(encodedPassword);
//  }

}
