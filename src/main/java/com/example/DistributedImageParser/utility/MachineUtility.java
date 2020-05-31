package com.example.DistributedImageParser.utility;

import com.example.DistributedImageParser.model.Machine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.Query;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
public class MachineUtility {

    private RestTemplate restTemplate;
    private Machine currentMachine;

    @Autowired
    public MachineUtility() {
        this.restTemplate = new RestTemplate();
        this.currentMachine = getTheCurrentMachine();
    }

    public String getOneFromQueList(BlockingQueue<String> waitingList) {
        try {
            return waitingList.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return String.valueOf(false);
        }
    }

    public List<Machine> getTheAvailableMachines() {
        List<Machine> machineList = new ArrayList<Machine>();
        List<String> machine_list = Arrays.asList(System.getenv("MACHINE_LIST").split(","));
        IntStream.range(0, machine_list.size()).forEach(machineId -> {
            machineList.add(new Machine().setId(machineId).setAdress(machine_list.get(machineId)));
        });
        return machineList;
    }

    public Machine getTheCurrentMachine() {
        Stream<Machine> sortedMachines = sortMachines(getTheAvailableMachines());
        return sortedMachines.filter(machine -> {
            try {
                return machine.getAdress().equalsIgnoreCase(getTheUrlOfCurrentMachine().toString());
            } catch (MalformedObjectNameException | UnknownHostException e) {
                e.printStackTrace();
                return false;
            }
        }).collect(Collectors.toList()).get(0);
    }

    public Stream<Machine> sortMachines(List<Machine> machines) {
        return !machines.isEmpty() ? machines.stream().sorted(Comparator.comparingLong(Machine::getId)) : null;
    }

    public Stream<Machine> reverseSortMachines(List<Machine> machines) {
        return machines.stream().sorted(Comparator.comparing(Machine::getId).reversed());
    }

    public URI getTheUrlOfCurrentMachine() throws MalformedObjectNameException, UnknownHostException {
        return UriComponentsBuilder.fromHttpUrl(
                "http" + "://" + InetAddress.getLocalHost().getHostAddress()
                        + ":" + ManagementFactory.getPlatformMBeanServer().queryNames(new ObjectName("*:type=Connector,*"),
                        Query.match(Query.attr("protocol"), Query.value("HTTP/1.1"))).iterator().next().getKeyProperty("port")).build().toUri();
    }

    public Integer getSuccessorMachineId() {
        return currentMachine.getId() + 1;
    }

    public void sendDefaultRequest(String url) {
        this.restTemplate.exchange(RequestEntity.get(UriComponentsBuilder.fromUri(URI.create(url)).build(false).toUri())
                .accept(MediaType.APPLICATION_JSON).build(), new ParameterizedTypeReference<Boolean>() {
        }).getBody();
    }

    public Boolean companionCheck(Machine machineIterator, String directionUrl) {
        try {
            return restTemplate.exchange(RequestEntity.get(URI.create(String.format("%s" + directionUrl, machineIterator.getAdress()))).build(),
                    new ParameterizedTypeReference<Boolean>() {
                    })
                    .getBody() == Boolean.TRUE;
        } catch (Exception ignore) {
            return Boolean.FALSE;
        }
    }

    public String getPredecessorUrl() {
        return getTheAvailableMachines().get(currentMachine.getId() - 1).getAdress();
    }

}
