import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import java.text.DecimalFormat;
import java.util.*;

public class PSOCloudSimExample {
    private static List<Cloudlet> cloudletList;
    private static List<Vm> vmList;
    private static int cloudletNum = 40;
    private static int vmNum = 10;

    public static void main(String[] args) {
        Log.printLine("Starting PSO CloudSim Example...");

        try {
            // Initialize the CloudSim package
            int num_user = 1;
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false;
            CloudSim.init(num_user, calendar, trace_flag);

            // Create Datacenter
            Datacenter datacenter0 = createDatacenter("Datacenter_0");

            // Create PSO instance
            PSO pso = new PSO(20, 100, 0.7, 1.5, 1.5);

            // Create Broker
            DatacenterBroker broker = new PSODatacenterBroker("Broker_0", pso);
            int brokerId = broker.getId();

            // Create VMs and Cloudlets
            vmList = createVMs(brokerId);
            cloudletList = createCloudlets(brokerId);

            // Submit VM list to the broker
            broker.submitVmList(vmList);

            // Submit Cloudlet list to the broker
            broker.submitCloudletList(cloudletList);

            // Start the simulation
            CloudSim.startSimulation();

            // Stop the simulation
            CloudSim.stopSimulation();

            // Print results
            List<Cloudlet> newList = broker.getCloudletReceivedList();
            printCloudletList(newList);

            // Print the best solution found by PSO
            printPSOSolution((PSODatacenterBroker) broker);

            Log.printLine("PSO CloudSim Example finished!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("The simulation has been terminated due to an unexpected error");
        }
    }

    private static Datacenter createDatacenter(String name) {
        // Create a list to store our machine
        List<Host> hostList = new ArrayList<Host>();

        // Machine specifications
        int mips = 1000;
        int hostId = 0;
        int ram = 16384; // 16 GB
        long storage = 1000000; // 1 GB
        int bw = 10000;

        // Create PEs
        List<Pe> peList = new ArrayList<Pe>();
        peList.add(new Pe(0, new PeProvisionerSimple(mips)));
        peList.add(new Pe(1, new PeProvisionerSimple(mips)));
        peList.add(new Pe(2, new PeProvisionerSimple(mips)));
        peList.add(new Pe(3, new PeProvisionerSimple(mips)));

        // Create Host with its id and list of PEs and add them to the list of machines
        hostList.add(
                new Host(
                        hostId,
                        new RamProvisionerSimple(ram),
                        new BwProvisionerSimple(bw),
                        storage,
                        peList,
                        new VmSchedulerTimeShared(peList)
                )
        );

        // Create a DatacenterCharacteristics object that stores the properties of a data center
        String arch = "x86";
        String os = "Linux";
        String vmm = "Xen";
        double time_zone = 10.0;
        double cost = 3.0;
        double costPerMem = 0.05;
        double costPerStorage = 0.001;
        double costPerBw = 0.0;
        LinkedList<Storage> storageList = new LinkedList<Storage>();

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);

        // Create a PowerDatacenter object
        Datacenter datacenter = null;
        try {
            datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return datacenter;
    }

    private static List<Vm> createVMs(int brokerId) {
        List<Vm> vms = new ArrayList<Vm>();

        // VM description
        int mips = 250;
        long size = 10000; // 10 GB
        int ram = 512; // 512 MB
        long bw = 1000;
        int pesNumber = 1; // number of cpus
        String vmm = "Xen"; // VMM name

        for (int i = 0; i < vmNum; i++) {
            Vm vm = new Vm(i, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
            vms.add(vm);
        }

        return vms;
    }

    private static List<Cloudlet> createCloudlets(int brokerId) {
        List<Cloudlet> cloudlets = new ArrayList<Cloudlet>();

        // Cloudlet properties
        long length = 1000;
        long fileSize = 300;
        long outputSize = 300;
        int pesNumber = 1;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        for (int i = 0; i < cloudletNum; i++) {
            Cloudlet cloudlet = new Cloudlet(i, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            cloudlet.setUserId(brokerId);
            cloudlets.add(cloudlet);
        }

        return cloudlets;
    }

    private static void printCloudletList(List<Cloudlet> list) {
        int size = list.size();
        Cloudlet cloudlet;

        String indent = "    ";
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
                "Data center ID" + indent + "VM ID" + indent + "Time" + indent + "Start Time" + indent + "Finish Time");

        DecimalFormat dft = new DecimalFormat("###.##");
        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
            Log.print(indent + cloudlet.getCloudletId() + indent + indent);

            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS){
                Log.print("SUCCESS");

                Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId() +
                        indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent + dft.format(cloudlet.getExecStartTime())+
                        indent + indent + dft.format(cloudlet.getFinishTime()));
            }
        }
    }

    private static void printPSOSolution(PSODatacenterBroker broker) {
        List<Integer> bestSolution = broker.getBestSolution();
        Log.printLine();
        Log.printLine("PSO Best Solution:");
        Log.printLine("Cloudlet ID" + "\t" + "VM ID");
        for (int i = 0; i < bestSolution.size(); i++) {
            Log.printLine(i + "\t\t" + bestSolution.get(i));
        }
    }
}