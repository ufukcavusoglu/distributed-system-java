package com.example.DistributedImageParser.dao;

import com.example.DistributedImageParser.utility.MachineUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Repository
public class ListRepo {

    public MachineUtility utility;
    private final BlockingQueue<String> waitingList = new LinkedBlockingQueue<String>();
    private BlockingQueue<String> storedList = new LinkedBlockingQueue<String>();

    @Autowired
    public ListRepo(MachineUtility utility) {
        this.utility = utility;
    }

    public String getFromQue() {
        return getWaitingList().size() != 0 ? utility.getOneFromQueList(getWaitingList()) : null;
    }

    public <T> String getFromQue(T o) {
        return getWaitingList().size() != 0 ? utility.getOneFromQueList(getWaitingList()) : null;
    }

    public BlockingQueue<String> getWaitingList() {
        return waitingList;
    }

    public void addToWaitingList(BlockingQueue<String> paths) {
        waitingList.addAll(paths);
    }

    public Boolean store(BlockingQueue<String> paths) {
        storedList = paths;
        return Boolean.TRUE;
    }

    public Boolean fromStoreToWaitingList() {
        return waitingList.addAll(storedList);
    }

    public BlockingQueue<String>  getStore() {
        return storedList;
    }
}
