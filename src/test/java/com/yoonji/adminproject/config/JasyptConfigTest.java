package com.yoonji.adminproject.config;


import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class JasyptConfigTest {


    @Autowired
    StringEncryptor jasyptStringEncryptor;


    @Test
    void jasypt(){
        String url = "";
        String username = "";
        String password = "";
        String googleClientId = "";
        String googleSecret = "";
        String naverClientId = "";
        String naverSecret = "";

        String encryptUrl = jasyptEncrypt(url);
        String encryptUsername = jasyptEncrypt(username);
        String encryptPassword = jasyptEncrypt(password);
        String encryptGoogleClientId = jasyptEncrypt(googleClientId);
        String encryptGoogleSecret = jasyptEncrypt(googleSecret);
        String encryptNaverClientId = jasyptEncrypt(naverClientId);
        String encryptNaverSecret = jasyptEncrypt(naverSecret);

        System.out.println("encryptUrl: " + encryptUrl);
        System.out.println("encryptUsername: " + encryptUsername);
        System.out.println("encryptPassword: " + encryptPassword);
        System.out.println("encryptGoogleClientId: " + encryptGoogleClientId);
        System.out.println("encryptGoogleSecret: " + encryptGoogleSecret);
        System.out.println("encryptNaverClientId: " + encryptNaverClientId);
        System.out.println("encryptNaverSecret: " + encryptNaverSecret);

        assertThat(url).isEqualTo(jasyptDecrypt(encryptUrl));
    }

    private String jasyptEncrypt(String input) {
        return jasyptStringEncryptor.encrypt(input);
    }

    private String jasyptDecrypt(String input){
        return jasyptStringEncryptor.decrypt(input);
    }

}