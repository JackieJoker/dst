package dst.ass3.elastic.impl;

import dst.ass3.elastic.ContainerException;
import dst.ass3.elastic.ContainerInfo;
import dst.ass3.elastic.IContainerService;
import dst.ass3.elastic.IElasticityController;
import dst.ass3.messaging.IWorkloadMonitor;
import dst.ass3.messaging.Region;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ElasticityController implements IElasticityController {
    private final IContainerService containerService;
    private final IWorkloadMonitor workloadMonitor;
    /**
     * Defined maximum waiting time for a request, in seconds.
     */
    private final Map<Region, Integer> maximumWaitTime;
    private static final double scaleOutThreshold = 0.1;
    private static final double scaleDownThreshold = 0.05;


    public ElasticityController(IContainerService containerService, IWorkloadMonitor workloadMonitor) {
        this.containerService = containerService;
        this.workloadMonitor = workloadMonitor;
        maximumWaitTime = new HashMap<>();
        maximumWaitTime.put(Region.AT_LINZ, 30 * 1000);
        maximumWaitTime.put(Region.AT_VIENNA, 30 * 1000);
        maximumWaitTime.put(Region.DE_BERLIN, 120 * 1000);
    }

    @Override
    public void adjustWorkers() throws ContainerException {
        Map<Region, Long> workerCount = workloadMonitor.getWorkerCount();
        Map<Region, Long> requestCount = workloadMonitor.getRequestCount();
        Map<Region, Double> averageProcessingTime = workloadMonitor.getAverageProcessingTime();
        for (Region region : Region.values()) {
            double expectedWaitTime = (requestCount.get(region) * averageProcessingTime.get(region)) / workerCount.get(region);
            System.out.println(region + ": expectedWaitTime " + expectedWaitTime);
            if (expectedWaitTime > maximumWaitTime.get(region) * (1 + scaleOutThreshold)) {
                // Scale Out
                System.out.println("Scaling out: expectedWaitTime = " + expectedWaitTime
                        + " > maximumWaitTime * (1 + scaleOutThreshold) = "
                        + (maximumWaitTime.get(region) * (1 + scaleOutThreshold)));
                double wantedWorkerCount = Math.ceil((requestCount.get(region) * averageProcessingTime.get(region)) / maximumWaitTime.get(region));
                System.out.println("Spawning " + (wantedWorkerCount - workerCount.get(region)) + " new workers.");
                for (int i = 0; i < wantedWorkerCount - workerCount.get(region); i++) {
                    containerService.startWorker(region);
                }
            } else if (expectedWaitTime < maximumWaitTime.get(region) * (1 - scaleDownThreshold)) {
                // Scale Down
                System.out.println("Scaling down: expectedWaitTime = " + expectedWaitTime
                        + " < maximumWaitTime * (1 - scaleDownThreshold) = "
                        + (maximumWaitTime.get(region) * (1 + scaleOutThreshold)));
                double wantedWorkerCount = Math.ceil((requestCount.get(region) * averageProcessingTime.get(region)) / maximumWaitTime.get(region));
                List<ContainerInfo> containers = containerService.listContainers();
                // Filter only the containers with workers running on the wanted Region
                List<ContainerInfo> workersRegion = containers.stream().filter(c -> region.equals(c.getWorkerRegion())).collect(Collectors.toList());
                System.out.println("Stopping " + (workerCount.get(region) - wantedWorkerCount) + " workers.");
                for (int i = 0; i < workerCount.get(region) - wantedWorkerCount; i++) {
                    containerService.stopContainer(workersRegion.remove(0).getContainerId());
                }
            } else System.out.println("expectedWaitTime in the range. No needs to scale.");
        }
    }
}
