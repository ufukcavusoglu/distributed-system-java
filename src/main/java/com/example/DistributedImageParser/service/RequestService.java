package com.example.DistributedImageParser.service;

import com.example.DistributedImageParser.dao.ListRepo;
import com.example.DistributedImageParser.model.Machine;
import com.example.DistributedImageParser.utility.MachineUtility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RequestService {

    @Value("${spring.application.video_storage_path}")
    private String mainSourcePath;
    @Value("${spring.application.demander}")
    private String demanderUrl;
    @Value("${spring.application.taker_wakeup}")
    private String takerWakeUp;
    public RestTemplate restTemplate;
    public MachineUtility utility;
    public String takerUrl;
    private String successorUrl;
    private ListRepo repo;
    private Machine currentMachine;
    private Function<Machine, Boolean> checkCompanion = url -> utility.companionCheck(url, "/taker/callback");
    private Function<Machine, Boolean> isBigger = machine -> machine.getId() > currentMachine.getId();
    private Function<Machine, Boolean> isSmaller = machine -> machine.getId() < currentMachine.getId();

    public RequestService(MachineUtility utility, ListRepo repo) {
        this.restTemplate = new RestTemplate();
        this.utility = utility;
        this.repo = repo;
        this.currentMachine = utility.getTheCurrentMachine();
    }

    public Boolean setSuccessorUrl(String address) {
        successorUrl = address;
        return true;
    }

    public Boolean getDemander(Boolean connector) {
        String adress = null;
        try {
            adress = utility.sortMachines(workingMachines(isBigger)).iterator().next().getAdress();
        } catch (Exception ignore) {
        }
        return adress != null ? setSuccessorUrl(adress) : Boolean.FALSE;
    }

    public String demanderRequest(String takerUrl) {
        try {
            return Objects.requireNonNull(this.restTemplate.exchange(RequestEntity.get(UriComponentsBuilder.fromUri(URI.create(takerUrl + "/taker/get-from-que")).build(false).toUri())
                    .accept(MediaType.APPLICATION_JSON).build(), new ParameterizedTypeReference<String>() {
            }).getBody(), "there is no video left in the que");
        } catch (Exception ignore) {
            return String.valueOf(0);
        }
    }

    public String[] getWaitinglistFromCompanion() {
        return restTemplate.exchange(RequestEntity.get(URI.create(getTakersUrl(Boolean.TRUE) + "/taker/get-waiting-list")).accept(MediaType.ALL).build(), new ParameterizedTypeReference<String[]>() {
        }).getBody();
    }

    public Boolean sendDefaultRequest(Boolean connector) {
        if (connector && repo.getStore().isEmpty()) {
            new ArrayList<String>() {{
                add(demanderUrl);
                add(takerWakeUp);
            }}.forEach(url -> {
                utility.sendDefaultRequest(successorUrl + url);
            });
            return Boolean.TRUE;
        } else return Boolean.FALSE;
    }

    public String getTakersUrl(Boolean connector) {
        try {
            return connector ? utility.reverseSortMachines(workingMachines(isSmaller)).iterator().next().getAdress() : null;
        } catch (Exception ignore) {
            return repo.fromStoreToWaitingList() ? currentMachine.getAdress() : null;
        }
    }

    public List<Machine> workingMachines(Function<Machine, Boolean> comparing) {
        return utility.getTheAvailableMachines()
                .stream().filter(checkCompanion::apply)
                .filter(comparing::apply)
                .collect(Collectors.toList());
    }

}
