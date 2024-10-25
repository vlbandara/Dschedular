package simulation;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.*;
import java.util.ArrayList;
import java.util.List;

public class DatacenterManager {

    /**
     * Creates a datacenter with specified hosts.
     *
     * @param name The name of the datacenter.
     * @return The created Datacenter object.
     */
    public Datacenter createDatacenter(String name) {
        // List to store hosts
        List<Host> hostList = new ArrayList<>();

        // Define the number of processing elements (PE) per host
        int peCount = 4;
        List<Pe> peList = new ArrayList<>();
        for (int i = 0; i < peCount; i++) {
            peList.add(new Pe(i, new PeProvisionerSimple(1000))); // 1000 MIPS per PE
        }

        // Define host specifications
        int ram = 16384; // in MB
        long storage = 1000000; // in MB
        int bw = 10000; // in MB/s

        // Create a host with the above specifications
        Host host = new Host(
                0,
                new RamProvisionerSimple(ram),
                new BwProvisionerSimple(bw),
                storage,
                peList,
                new VmSchedulerTimeShared(peList)
        );

        hostList.add(host);

        // Define datacenter characteristics
        String arch = "x86"; // architecture
        String os = "Linux"; // operating system
        String vmm = "Xen";
        double time_zone = 10.0;
        double cost = 3.0;
        double costPerMem = 0.05;
        double costPerStorage = 0.001;
        double costPerBw = 0.0;

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw
        );

        // Create the datacenter
        Datacenter datacenter = null;
        try {
            datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), new ArrayList<Storage>(), 0);
            System.out.println("Datacenter " + name + " created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return datacenter;
    }
}
