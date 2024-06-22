package com.mostafatamer.trysomethingcrazy;

import com.google.firebase.FirebaseApp;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.net.InetAddress;

@SpringBootApplication
@EnableJpaAuditing
@RequiredArgsConstructor
public class ChatWithMeApplication implements CommandLineRunner {

    private final FirebaseApp defaultApp;

    public static void main(String[] args) {
        SpringApplication.run(ChatWithMeApplication.class, args);
    }

    @SneakyThrows
    @Override
    public void run(String... args) {
        System.out.println("Running...");

        InetAddress ip = InetAddress.getLocalHost();
        System.out.println("Ip: " + ip.getHostAddress());

    }
}
