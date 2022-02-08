package com.rocketmq.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class SourceWithDynamicDestinationController {
    @Autowired
    private BinderAwareChannelResolver resolver;

    /**
     *
     * @param body
     * @param target
     *
     * curl -H "Content-Type: application/json" -X POST -d "customer-1" http://localhost:8080/customers
     * curl -H "Content-Type: application/json" -X POST -d "order -1" http://localhost:8080/orders
     */
    @RequestMapping(value="/{target}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void send(@RequestBody String body, @PathVariable("target") String target){
        resolver.resolveDestination(target).send(new GenericMessage<String>(body));
    }
}

