package com.example.DistributedImageParser.cron;


import com.example.DistributedImageParser.controller.Controller;
import com.example.DistributedImageParser.controller.MachineSyncron;
import com.example.DistributedImageParser.dao.ListRepo;
import com.example.DistributedImageParser.service.RequestService;
import com.example.DistributedImageParser.utility.MachineUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Schedule {

    private Controller controller;
    private ListRepo listRepo;
    private Boolean isInitalDemandSended = Boolean.FALSE;
    private MachineUtility utility;
    private RequestService request;
    private MachineSyncron syncron;

    @Autowired
    public Schedule(MachineUtility utility, ListRepo listRepo, RequestService request, MachineSyncron syncron, Controller controller) {
        this.utility = utility;
        this.listRepo = listRepo;
        this.request = request;
        this.syncron = syncron;
        this.controller = controller;
    }

    @Scheduled(cron = "*/2 * * * * *", zone = "Europe/Istanbul")
    public void checkOtherCompanionMachines() {
    /*    Machine currentMachine = utility.getTheCurentMachine();

        if (listRepo.getWaitingList().isEmpty() && currentMachine.getId() != 0) {
            AtomicReference<Boolean> isFoundWorkingPredecessor = new AtomicReference<>();// it checks whether it is leading machine or not // leading machine : machine that accepted request in first place
            isFoundWorkingPredecessor.set(false);
            Machine machineIterator = currentMachine;
            while (!isFoundWorkingPredecessor.get()) {// search for machines those are alive // 1->do nothing || 2->1 || 3->2 ... etc the flow of machines in case of one of them gone broke
                machineIterator = utility.getThePredecessorMachine(machineIterator);
                try {
                    if (utility.companionCheck(machineIterator, "/taker/callback")) {
                        request.setDemandingPath(machineIterator.getAdress());
                        isFoundWorkingPredecessor.set(true);
                    }
                } catch (Exception ignore) { // if predecessor was not responding try again until finding
                    isFoundWorkingPredecessor.set(false);//make it search for working predecessor, again
                    if (machineIterator.getId() < 1) {
                        isFoundWorkingPredecessor.set(true);// if the que is over while trying to find, let the working machine take the mission
                        BlockingQueue<String> queue = listRepo.getWaitingList(); // it cannot find any working predecessor machine and then it's starting to behave like leading machine
                        syncron.addToQue(queue.toArray(new String[queue.size()]));
                    }
                }
            }
        } */
    }

    @Scheduled(cron = "* * * * * *", zone = "Europe/Istanbul")
    public void initialDemand() {
    /*    try {
           if (!isInitalDemandSended && utility.getTheCurentMachine().getId() != 0) {
                isInitalDemandSended = demanderController.demander() ? Boolean.TRUE : Boolean.FALSE;
            }
        } catch (Exception ignore) {
        } */
    }
}
