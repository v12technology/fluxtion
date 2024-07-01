package com.fluxtion.runtime.server.dutycycle;

import com.fluxtion.runtime.service.Service;
import lombok.Value;
import org.agrona.concurrent.Agent;

@Value
public class ServerAgent<T> {

    //unique identifier
    String agentGroup;
    //proxy - exported service
    Service<T> exportedService;
    //adds to EP agent
    Agent delegate;
}
