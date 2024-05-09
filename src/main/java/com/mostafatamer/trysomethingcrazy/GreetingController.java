package com.mostafatamer.trysomethingcrazy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class GreetingController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private int mostafaCounter = 0;
    private int mahmoudCounter = 0;
//
//    @MessageMapping("/message")
//    public void mostafa(String message) throws Exception {
//
//        System.out.println(message);
//        messagingTemplate.convertAndSend("/mostafa/mo", mostafaCounter++);
//    }
//
//    @MessageMapping("/message")
//    public void mahmoud(String message) throws Exception {
//
//        System.out.println(message);
//        messagingTemplate.convertAndSend("/mahmoud/mo", mahmoudCounter++);
//    }


}