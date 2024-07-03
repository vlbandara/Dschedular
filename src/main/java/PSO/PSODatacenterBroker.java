package PSO;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.lists.VmList;

import java.util.ArrayList;
import java.util.List;

public class PSODatacenterBroker extends DatacenterBroker {
    private PSO pso;
    private List<Integer> bestSolution;

    public PSODatacenterBroker(String name, PSO pso) throws Exception {
        super(name);
        this.pso = pso;
    }

    @Override
    protected void processResourceCharacteristics(SimEvent ev) {
        DatacenterCharacteristics characteristics = (DatacenterCharacteristics) ev.getData();
        getDatacenterCharacteristicsList().put(characteristics.getId(), characteristics);

        if (getDatacenterCharacteristicsList().size() == getDatacenterIdsList().size()) {
            setDatacenterRequestedIdsList(new ArrayList<Integer>());
            createVmsInDatacenter(getDatacenterIdsList().get(0));
        }
    }

    @Override
    protected void submitCloudlets() {
        // Run PSO.PSO to get the best task-to-VM mapping
        bestSolution = pso.optimize(getCloudletList(), getVmList());

        // Assign cloudlets to VMs based on PSO.PSO solution
        for (int i = 0; i < getCloudletList().size(); i++) {
            Cloudlet cloudlet = getCloudletList().get(i);
            Vm vm = getVmList().get(bestSolution.get(i));
            cloudlet.setVmId(vm.getId());
            sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
        }

        // Empty the submission list
        getCloudletList().clear();
    }

    @Override
    protected void processCloudletReturn(SimEvent ev) {
        Cloudlet cloudlet = (Cloudlet) ev.getData();
        getCloudletReceivedList().add(cloudlet);
        Log.printLine(CloudSim.clock() + ": " + getName() + ": Cloudlet " + cloudlet.getCloudletId() + " received");
        cloudletsSubmitted--;
        if (getCloudletList().size() == 0 && cloudletsSubmitted == 0) {
            scheduleTasksToVms();
        }
    }

    protected void scheduleTasksToVms() {
        int dcId = getDatacenterIdsList().get(0);
        double delay = 0;
        for (int i = 0; i < getCloudletList().size(); i++) {
            Cloudlet cloudlet = getCloudletList().get(i);
            Vm vm = getVmList().get(bestSolution.get(i));
            Log.printLine(CloudSim.clock() + ": " + getName() + ": Sending cloudlet " + cloudlet.getCloudletId() + " to VM #" + vm.getId());
            cloudlet.setVmId(vm.getId());
            sendNow(dcId, CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
            cloudletsSubmitted++;
            delay += 0.1;
        }

        // Remove submitted cloudlets from waiting list
        getCloudletList().clear();
    }

    @Override
    protected void processVmCreate(SimEvent ev) {
        int[] data = (int[]) ev.getData();
        int datacenterId = data[0];
        int vmId = data[1];
        int result = data[2];

        if (result == CloudSimTags.TRUE) {
            getVmsToDatacentersMap().put(vmId, datacenterId);
            getVmsCreatedList().add(VmList.getById(getVmList(), vmId));
            Log.printLine(CloudSim.clock() + ": " + getName() + ": VM #" + vmId + " has been created in Datacenter #" + datacenterId + ", Host #" + VmList.getById(getVmsCreatedList(), vmId).getHost().getId());
        } else {
            Log.printLine(CloudSim.clock() + ": " + getName() + ": Creation of VM #" + vmId + " failed in Datacenter #" + datacenterId);
        }

        incrementVmsAcks();

        if (getVmsCreatedList().size() == getVmList().size() - getVmsDestroyed()) {
            submitCloudlets();
        } else if (getVmsRequested() == getVmsAcks()) {
            // All the requested VMs have been created
            createVmsInDatacenter(getDatacenterIdsList().get(0));
        }
    }

    @Override
    protected void createVmsInDatacenter(int datacenterId) {
        // send as much vms as possible for this datacenter before trying the next one
        int requestedVms = 0;
        String datacenterName = CloudSim.getEntityName(datacenterId);
        for (Vm vm : getVmList()) {
            if (!getVmsToDatacentersMap().containsKey(vm.getId())) {
                Log.printLine(CloudSim.clock() + ": " + getName() + ": Trying to Create VM #" + vm.getId() + " in " + datacenterName);
                sendNow(datacenterId, CloudSimTags.VM_CREATE_ACK, vm);
                requestedVms++;
            }
        }
        setVmsRequested(requestedVms);
        setVmsAcks(0);
    }

    @Override
    protected void processOtherEvent(SimEvent ev) {
        if (ev == null) {
            Log.printLine(getName() + ".processOtherEvent(): " + "Error - an event is null.");
            return;
        }

        Log.printLine(getName() + ".processOtherEvent(): " + "Error - event unknown by this DatacenterBroker.");
    }

    // Other utility methods...

    public List<Integer> getBestSolution() {
        return bestSolution;
    }
}