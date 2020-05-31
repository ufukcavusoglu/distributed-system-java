package com.example.DistributedImageParser.controller;

import com.example.DistributedImageParser.dao.ListRepo;
import com.example.DistributedImageParser.service.ParseService;
import com.example.DistributedImageParser.service.RequestService;
import com.example.DistributedImageParser.utility.MachineUtility;
import com.example.DistributedImageParser.utility.RxJavaExp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

@Service
public class MachineSyncron {

    public MachineUtility utility;
    private RequestService request;
    private ListRepo repo;
    private Function<Boolean, Boolean> sendDefaultRequest;
    private Function<Boolean, Boolean> getDemander;
    private Function<Boolean, String> decideTakerUrl;
    private Function<String, Boolean> channelEmitter;
    private Function<String, String> demanderRequest;

    @Autowired
    public MachineSyncron(MachineUtility utility, ParseService parseService, RequestService request, ListRepo repo) {
        this.utility = utility;
        this.decideTakerUrl = request::getTakersUrl;
        this.request = request;
        this.repo = repo;
        this.sendDefaultRequest = request::sendDefaultRequest;
        this.getDemander = request::getDemander;
        this.demanderRequest = request::demanderRequest;
        this.channelEmitter = parseService::channelEmitter;
    }

    Boolean addToQue(String[] pathMap) {
        repo.addToWaitingList(new LinkedBlockingQueue<String>() {{
            addAll(Arrays.asList(pathMap));
        }});
        if (((pathMap.length == 1 && repo.getWaitingList().size() == 2) || (pathMap.length > 1))) {
            getSuccessorWakeUp();
        }
        repo.getWaitingList()
                .forEach(s -> RxJavaExp.getInstance().responsive(((Function<Boolean, String>) Objects.requireNonNull(repo)::getFromQue).andThen(channelEmitter)));
        return Boolean.TRUE;
    }

    void getSuccessorWakeUp() {
        if (utility.getSuccessorMachineId() < utility.getTheAvailableMachines().size()) {
            RxJavaExp.getInstance().doSilently(getDemander.andThen(sendDefaultRequest));
        }
    }

    private Boolean storeVideos(Boolean connector) {
        return connector ? repo.store(new LinkedBlockingQueue<String>() {{
            addAll(Arrays.asList(request.getWaitinglistFromCompanion()));
        }}) : Boolean.FALSE;
    }

    Boolean demandersParser(Boolean status) {
        try { demanderRecursive(status);
        } catch (Exception ignored) { }
        return Boolean.TRUE;
    }

    private void demanderRecursive(Boolean status) throws Exception {
        Function<Boolean, Boolean> parser = this::parser;
        if (RxJavaExp.getInstance().responsive(parser)) {
            parser.apply(Boolean.TRUE);
        } else throw new Exception("Parsing is finished");
    }

    private Boolean parser(Boolean status) {
        return status ?
                RxJavaExp.getInstance().responsive(decideTakerUrl.andThen(demanderRequest).andThen(channelEmitter).andThen(this::storeVideos)) :
                Boolean.FALSE;
    }

}
