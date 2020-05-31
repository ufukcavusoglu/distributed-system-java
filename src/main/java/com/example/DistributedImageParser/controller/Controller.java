package com.example.DistributedImageParser.controller;

import com.example.DistributedImageParser.dao.ListRepo;
import com.example.DistributedImageParser.model.StandardResponse;
import com.example.DistributedImageParser.service.RequestService;
import com.example.DistributedImageParser.utility.RxJavaExp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Function;

@RestController
@RequestMapping(value = "/taker/")
public class Controller {

    private ListRepo repo;
    private MachineSyncron synchrony;
    private RequestService request;
    private Function<Boolean, Boolean> demandersParser;

    @Autowired
    public Controller(ListRepo repo, MachineSyncron synchrony, RequestService request) {
        this.repo = repo;
        this.synchrony = synchrony;
        this.request = request;
        this.demandersParser = synchrony::demandersParser;
    }

    @RequestMapping(value = "get-waiting-list", method = RequestMethod.GET)
    public String[] getWaitingListArray() {
        return repo.getWaitingList().toArray(new String[repo.getWaitingList().size()]);
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public void addToQue(String[] pathMap) {
        Function<Boolean, Boolean> addToQue = s -> synchrony.addToQue(pathMap);
        RxJavaExp.getInstance().doSilently(addToQue);
    }

    @RequestMapping(value = "remains", method = RequestMethod.GET)
    public StandardResponse getRemains() {
        return new StandardResponse() {{
            setData(repo.getWaitingList().size());
        }};
    }

    @RequestMapping(value = "get-from-que", method = RequestMethod.GET)
    public String getFromQue() {
        Function<Boolean, String> takeUrl = request::getTakersUrl;
        return repo.getWaitingList().isEmpty() ? takeUrl.andThen(request::demanderRequest).apply(Boolean.TRUE) : repo.getFromQue();
    }

    @RequestMapping(value = "callback", method = RequestMethod.GET)
    public Boolean callback() {
        return true;
    }

    @RequestMapping(value = "get-successor-wake-up", method = RequestMethod.GET)
    private void getSuccessorWakeUp() {
        synchrony.getSuccessorWakeUp();
    }

    @RequestMapping(value = "demander", method = RequestMethod.GET)
    public Boolean demander() {
        RxJavaExp.getInstance().doSilently(demandersParser);
        return true;
    }


}