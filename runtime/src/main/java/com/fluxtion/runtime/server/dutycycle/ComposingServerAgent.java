package com.fluxtion.runtime.server.dutycycle;

import com.fluxtion.runtime.annotations.feature.Experimental;
import com.fluxtion.runtime.server.FluxtionServer;
import com.fluxtion.runtime.server.service.scheduler.DeadWheelScheduler;
import com.fluxtion.runtime.server.service.scheduler.SchedulerService;
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
        toStartList.add(server);
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
            exportedService.init();
            serviceRegistry.init();
            serviceRegistry.nodeRegistered(exportedService.instance(), exportedService.serviceName());
            serviceRegistry.registerService(schedulerService);
            fluxtionServer.servicesRegistered().forEach(serviceRegistry::registerService);
            fluxtionServer.registerService(exportedService);
            exportedService.start();
            //
        });
        return super.doWork();
    }

    @Override
    public void onClose() {
        log.info("onClose");
        super.onClose();
    }
}
