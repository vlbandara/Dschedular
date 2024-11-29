package examples;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;

import java.util.List;

public class PSODatacenterBroker extends DatacenterBroker implements PSODatacenterBroker_01 {

    public PSODatacenterBroker(String name) throws Exception {
        super(name);
    }

    @Override
    public void processEvent(SimEvent ev) {
        switch (ev.getTag()) {
            case CloudSimTags.CLOUDLET_SUBMIT:
                processCloudletSubmit(ev, false);
                break;
            // Handle other events as in the superclass
            default:
                super.processEvent(ev);
        }
    }

    @Override
    public void processCloudletSubmit(SimEvent ev, boolean ack) {
        Cloudlet cloudlet = (Cloudlet) ev.getData();
        if (cloudlet.getVmId() == -1) {
            int vmId = scheduleCloudletToVm(cloudlet);
            cloudlet.setVmId(vmId);
        }

        sendNow(getVmsToDatacentersMap().get(cloudlet.getVmId()), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);

        if (ack) {
            sendNow(cloudlet.getUserId(), CloudSimTags.CLOUDLET_SUBMIT_ACK, cloudlet);
        }

        cloudletsSubmitted++;
        getCloudletSubmittedList().add(cloudlet);
    }

    private int scheduleCloudletToVm(Cloudlet cloudlet) {
        // Implement your PSO algorithm here to find the best VM for the cloudlet
        // This is a placeholder implementation
        List<Vm> vmList = getVmsCreatedList();
        if (vmList.isEmpty()) {
            return -1; // No VMs available
        }

        // Simple round-robin scheduling
        int vmId = (cloudlet.getCloudletId() % vmList.size());
        return vmList.get(vmId).getId();
    }
}