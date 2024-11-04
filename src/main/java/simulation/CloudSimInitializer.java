package simulation;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class CloudSimInitializer {
    private List<Datacenter> datacenters;
    private List<DatacenterBroker> brokers;
    private List<Vm> vmList;

    public CloudSimInitializer() {
        this.datacenters = new ArrayList<>();
        this.brokers = new ArrayList<>();
        this.vmList = new ArrayList<>();
    }

    public void initialize(int numUsers) {
        try {
            // Initialize CloudSim library
            CloudSim.init(numUsers, Calendar.getInstance(), false);

            // Create Datacenters
            createDatacenter("Datacenter_0");

            // Create Broker
            createBroker();

            // Create VMs
            createVMs(numUsers); // Pass numUsers to createVMs

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Datacenter createDatacenter(String name) throws Exception {
        // Create server characteristics
        List<Pe> peList = new ArrayList<>();
        int mips = 1000;
        peList.add(new Pe(0, new PeProvisionerSimple(mips)));

        // Create Host characteristics
        int hostId = 0;
        int ram = 2048; // 2GB
        long storage = 1000000; // 1TB
        int bw = 10000; // 10Gbps

        Host host = new Host(
                hostId,
                new RamProvisionerSimple(ram),
                new BwProvisionerSimple(bw),
                storage,
                peList,
                new VmSchedulerTimeShared(peList)
        );

        List<Host> hostList = new ArrayList<>();
        hostList.add(host);

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
                name, characteristics,
                new VmAllocationPolicySimple(hostList),
                new LinkedList<Storage>(), 0
        );

        this.datacenters.add(datacenter);
        return datacenter;
    }

    private void createBroker() throws Exception {
        DatacenterBroker broker = new DatacenterBroker("Broker_0");
        this.brokers.add(broker);
    }

    private void createVMs(int numUsers) {
        // VM description
        int mips = 250;
        long size = 10000; // 10GB
        int ram = 512; // 512MB
        long bw = 1000;
        int pesNumber = 1;
        String vmm = "Xen";

        for (int i = 0; i < numUsers; i++) { // Create a VM for each user
            Vm vm = new Vm(
                    i, brokers.get(0).getId(), mips, pesNumber, ram, bw, size,
                    vmm, new CloudletSchedulerTimeShared()
            );
            this.vmList.add(vm);
        }
        brokers.get(0).submitVmList(vmList);
    }

    // Getters
    public List<Datacenter> getDatacenters() {
        return datacenters;
    }

    public List<DatacenterBroker> getBrokers() {
        return brokers;
    }

    public List<Vm> getVmList() {
        return vmList;
    }
}
