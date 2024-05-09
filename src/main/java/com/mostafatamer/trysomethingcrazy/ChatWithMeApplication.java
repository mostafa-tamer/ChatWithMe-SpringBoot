package com.mostafatamer.trysomethingcrazy;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import jdk.jfr.Registered;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.annotation.security.RunAs;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
@EnableJpaAuditing
@RequiredArgsConstructor
public class ChatWithMeApplication implements CommandLineRunner {

    private final FirebaseApp defaultApp;

    public static void main(String[] args) {
        SpringApplication.run(ChatWithMeApplication.class, args);
    }

    @Override
    public void run(String... args) throws ExecutionException, InterruptedException, FirebaseMessagingException {
        System.out.println("Running...");


Runnable rana = () -> {
    System.out.println("Rana is runnable");
};

Thread t = new Thread(rana);
t.start();

    }
}
