package com.fluxtion.runtime.server.dutycycle;

import com.fluxtion.runtime.annotations.feature.Experimental;
import com.fluxtion.runtime.server.FluxtionServer;
import com.fluxtion.runtime.server.service.DeadWheelScheduler;
import com.fluxtion.runtime.server.service.SchedulerService;
import com.fluxtion.runtime.server.subscription.EventFlowManager;
import com.fluxtion.runtime.service.Service;
import com.fluxtion.runtime.service.ServiceRegistryNode;
import lombok.extern.java.Log;
import org.agrona.concurrent.DynamicCompositeAgent;
import org.agrona.concurrent.OneToOneConcurrentArrayQueue;

/**
 *
 */
@Experimental
@Log
public class ComposingServerAgent extends DynamicCompositeAgent {

    private final EventFlowManager eventFlowManager;
    private final FluxtionServer fluxtionServer;
    private final DeadWheelScheduler scheduler;
    private final Service<SchedulerService> schedulerService;
    private final OneToOneConcurrentArrayQueue<ServerAgent<?>> toStartList = new OneToOneConcurrentArrayQueue<>(128);
    private final ServiceRegistryNode serviceRegistry = new ServiceRegistryNode();

    public ComposingServerAgent(String roleName,
                                EventFlowManager eventFlowManager,
                                FluxtionServer fluxtionServer,
                                DeadWheelScheduler scheduler) {
        super(roleName, scheduler);
        this.eventFlowManager = eventFlowManager;
        this.fluxtionServer = fluxtionServer;
        this.scheduler = scheduler;
        this.schedulerService = new Service<>(scheduler, SchedulerService.class);
    }


    public <T> void registerServer(ServerAgent<T> server) {
        //register the work function
        toStartList.add(server);
//        tryAdd(server.getDelegate());
//        //export the service
//        fluxtionServer.registerService(server.getExportedService());
    }

    @Override
    public void onStart() {
        log.info("onStart");
        super.onStart();
    }

    @Override
    public int doWork() throws Exception {
        toStartList.drain(serverAgent -> {
            tryAdd(serverAgent.getDelegate());
            Service<?> exportedService = serverAgent.getExportedService();
            fluxtionServer.registerService(exportedService);
            serviceRegistry.init();
            serviceRegistry.nodeRegistered(exportedService.instance(), exportedService.serviceName());
            serviceRegistry.registerService(schedulerService);
        });
        return super.doWork();
    }

    @Override
    public void onClose() {
        log.info("onClose");
        super.onClose();
    }
}
