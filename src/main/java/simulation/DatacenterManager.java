package simulation;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class DatacenterManager {
    private Map<String, Datacenter> datacenters;
    private Map<String, List<Host>> hostsByDatacenter;

    public DatacenterManager() {
        this.datacenters = new HashMap<>();
        this.hostsByDatacenter = new HashMap<>();
    }

    public Datacenter createDatacenter(
            String name,
            int numHosts,
            int numCoresPerHost,
            int mipsPerCore,
            int ramPerHost,
            long storagePerHost,
            int bwPerHost) throws Exception {

        List<Host> hostList = createHosts(
                numHosts,
                numCoresPerHost,
                mipsPerCore,
                ramPerHost,
                storagePerHost,
                bwPerHost
        );

        // Create Datacenter characteristics
        String arch = "x86";
        String os = "Linux";
        String vmm = "Xen";
        double timeZone = 10.0;
        double costPerSec = 3.0;
        double costPerMem = 0.05;
        double costPerStorage = 0.001;
        double costPerBw = 0.0;

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, timeZone, costPerSec,
                costPerMem, costPerStorage, costPerBw
        );

        Datacenter datacenter = new Datacenter(
                name,
                characteristics,
                new VmAllocationPolicySimple(hostList),
                new LinkedList<Storage>(),
                0
        );

        this.datacenters.put(name, datacenter);
        this.hostsByDatacenter.put(name, hostList);

        return datacenter;
    }

    private List<Host> createHosts(
            int numHosts,
            int numCoresPerHost,
            int mipsPerCore,
            int ramPerHost,
            long storagePerHost,
            int bwPerHost) {

        List<Host> hostList = new ArrayList<>();

        for (int hostId = 0; hostId < numHosts; hostId++) {
            // Create PEs (CPU cores)
            List<Pe> peList = new ArrayList<>();
            for (int coreId = 0; coreId < numCoresPerHost; coreId++) {
                peList.add(new Pe(coreId, new PeProvisionerSimple(mipsPerCore)));
            }

            Host host = new Host(
                    hostId,
                    new RamProvisionerSimple(ramPerHost),
                    new BwProvisionerSimple(bwPerHost),
                    storagePerHost,
                    peList,
                    new VmSchedulerTimeShared(peList)
            );

            hostList.add(host);
        }

        return hostList;
    }

    public List<Host> getHostsForDatacenter(String datacenterName) {
        return hostsByDatacenter.get(datacenterName);
    }

    public Datacenter getDatacenter(String name) {
        return datacenters.get(name);
    }

    public Map<String, Datacenter> getAllDatacenters() {
        return new HashMap<>(datacenters);
    }

    public int getTotalHostCount() {
        return hostsByDatacenter.values()
                .stream()
                .mapToInt(List::size)
                .sum();
    }

    public int getTotalCoreCount() {
        return hostsByDatacenter.values()
                .stream()
                .flatMap(List::stream)
                .mapToInt(host -> host.getPeList().size())
                .sum();
    }
}