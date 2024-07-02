package com.mostafatamer.trysomethingcrazy;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;

@Log
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@RequiredArgsConstructor
public class ChatWithMeApplication implements CommandLineRunner {

    private final Environment environment;


    public static void main(String[] args) {
        SpringApplication.run(ChatWithMeApplication.class, args);
    }

    @SneakyThrows
    @Override
    public void run(String... args) {
        System.out.println("Running...");

        InetAddress ip = InetAddress.getLocalHost();
        String port = environment.getProperty("server.port");

        System.out.println("IP: " + ip.getHostAddress());
        System.out.println("Port: " + port);
    }

    long numberOfRequests = 0;

    @SneakyThrows
    @Scheduled(fixedRate = 180000)
//    @Scheduled(fixedRate = 2000)
    public void keepServerAwake() {
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = environment.getProperty("server.port");

        String dummyUrl = "http://" + ip + ":" + port + "/auth/hello";
//        System.out.println(dummyUrl);
        try {
            String result = new RestTemplate().getForObject(dummyUrl, String.class);
            log.info("Request number " + ++numberOfRequests + ": " + result);

        } catch (Exception e) {
            System.err.println("Failed to send request to dummy route: " + e.getMessage());
        }
    }
}
