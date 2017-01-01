package com.indysfug;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

/**
 * @author russell.scheerer
 */
@Controller
@Slf4j
public class ApplicationController {

    private final ChatHandler chatHandler;

    @Autowired
    public ApplicationController(ChatHandler chatHandler) {
        this.chatHandler = chatHandler;
    }

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String index(Model model, Authentication auth) {
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) auth;
        Map<String, Object> userDetails = (Map<String, Object>) oAuth2Authentication.getUserAuthentication().getDetails();

        model.addAttribute("userDetails", userDetails);
        return "index";
    }

    @RequestMapping(path = "/chat/stream", method = RequestMethod.GET)
    public SseEmitter stream() throws IOException {
        log.info("New SSE stream request");
        SseEmitter emitter = new SseEmitter();
        chatHandler.addEmitter(emitter);
        return emitter;
    }

    @ResponseBody
    @RequestMapping(path = "/chat/message", method = RequestMethod.POST)
    public Message sendMessage(@Valid Message message) throws Exception {
        log.info("Received message from client - publishing event: {}", message);
        chatHandler.publishMessage(message);
        return message;
    }
}
