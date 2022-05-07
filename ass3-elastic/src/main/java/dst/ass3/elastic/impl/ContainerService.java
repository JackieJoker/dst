package dst.ass3.elastic.impl;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import dst.ass3.elastic.*;
import dst.ass3.messaging.Region;

import java.util.ArrayList;
import java.util.List;

public class ContainerService implements IContainerService {
    DockerClient dockerClient;

    public ContainerService() {
        DefaultDockerClientConfig config
                = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://localhost:2375")
                .build();

        dockerClient = DockerClientBuilder.getInstance(config).build();
    }

    @Override
    public List<ContainerInfo> listContainers() throws ContainerException {
        List<ContainerInfo> infos = new ArrayList<>();
        List<Container> containers;
        try {
            containers = dockerClient.listContainersCmd().exec();
        } catch (DockerException e) {
            throw new ContainerException();
        }
        for (Container c : containers) {
            ContainerInfo i = getContainerInfo(c);
            infos.add(i);
        }
        return infos;
    }

    @Override
    public void stopContainer(String containerId) throws ContainerException {
        try {
            try {
                dockerClient.stopContainerCmd(containerId).exec();
            } catch (NotModifiedException | NotFoundException e) {
                throw new ContainerNotFoundException();
            }
            dockerClient.removeContainerCmd(containerId).exec();
        } catch (DockerException e) {
            throw new ContainerException();
        }
    }

    @Override
    public ContainerInfo startWorker(Region region) throws ContainerException {
        try {
            CreateContainerResponse containerResponse;
            try {
                containerResponse = dockerClient
                        .createContainerCmd("dst/ass3-worker")
                        .withCmd(region.name().toLowerCase())
                        .withHostConfig(HostConfig.newHostConfig().withNetworkMode("dst"))
                        .withArgsEscaped(true)
                        .exec();
            } catch (NotFoundException e) {
                throw new ImageNotFoundException();
            }
            dockerClient.startContainerCmd(containerResponse.getId()).exec();
            List<String> idS = new ArrayList<>();
            idS.add(containerResponse.getId());
            Container container = dockerClient.listContainersCmd().withIdFilter(idS).exec().get(0);
            return getContainerInfo(container);
        } catch (DockerException e) {
            e.printStackTrace();
            throw new ContainerException();
        }
    }

    private ContainerInfo getContainerInfo(Container c) {
        ContainerInfo i = new ContainerInfo();
        i.setContainerId(c.getId());
        i.setImage(c.getImage());
        // We are retrieving only the running containers
        i.setRunning(c.getState().equals("running"));
        Region region = null;
        if (c.getImage().equals("dst/ass3-worker")) {
            String[] command = c.getCommand().split(" ");
            if (command.length >= 2) {
                String regionString = command[2];
                switch (regionString) {
                    case "at_vienna":
                        region = Region.AT_VIENNA;
                        break;
                    case "at_linz":
                        region = Region.AT_LINZ;
                        break;
                    case "de_berlin":
                        region = Region.DE_BERLIN;
                        break;
                }
            }
        }
        i.setWorkerRegion(region);
        return i;
    }
}
