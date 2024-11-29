package examples;
import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
public class PSOExample_01 {
    public static void main(String[] args){
        try{
            int numUser = 1; // number of cloud users
            Calendar calendar = Calendar.getInstance(); // set start time
            boolean traceFlag = false; // mean trace events , track login events

            // Initialize the CloudSim library
            CloudSim.init(numUser, calendar, traceFlag);

            // Create Datacenters
            Datacenter datacenter = createDatacenter("Datacenter_0");


            // Create PSODatacenterBroker instead of DatacenterBroker
            PSODatacenterBroker broker = new PSODatacenterBroker("PSOBroker");

            // Create VMs and Cloudlets and send them to broker
            int vmCount = 5;
            int cloudletCount = 10;

            List<Vm> vmList = new ArrayList<>();
            List<Cloudlet> cloudletList = new ArrayList<>();

            // VM description
            int mips = 1000;
            int ram = 512; // VM memory (MB)
            long bw = 1000; // bandwidth
            long size = 10000; // image size (MB)
            int pesNumber = 1; // number of CPUs
            String vmm = "Xen"; // VMM name

            for (int i = 0; i < vmCount; i++) {
                Vm vm = new Vm(i, broker.getId(), mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
                vmList.add(vm);
            }

            // Create Cloudlets
            long length = 40000;
            long fileSize = 300;
            long outputSize = 300;
            UtilizationModel utilizationModel = new UtilizationModelFull();

            for (int i = 0; i < cloudletCount; i++) {
                Cloudlet cloudlet = new Cloudlet(i, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                cloudlet.setUserId(broker.getId());
                cloudletList.add(cloudlet);
            }

            // Submit VM list to the broker
            broker.submitVmList(vmList);

            // Submit Cloudlet list to the broker
            broker.submitCloudletList(cloudletList);

            // Start the simulation
            CloudSim.startSimulation();

            // Stop the simulation
            CloudSim.stopSimulation();

            // Print results when simulation is over
            List<Cloudlet> newList = broker.getCloudletReceivedList();
            printCloudletList(newList);






        }catch(Exception e){
            e.printStackTrace();

        }
    }

    private static Datacenter createDatacenter(String name) throws Exception {

        List<Host> hostList = new ArrayList<>(); // host == pc

        // Create Hosts with its id and list of PEs (Processing Elements or CPUs)
        int mips = 1000;
        int ram = 2048; // host memory (MB)
        long storage = 1000000; // host storage
        int bw = 10000;


        for (int i = 0; i < 10; i++) {
            List<Pe> peList = new ArrayList<>();
            peList.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating

            hostList.add(new Host(i, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, peList, new VmSchedulerTimeShared(peList)));
        }

        DatacenterCharacteristics characteristics = getDatacenterCharacteristics(hostList);

        return new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), new LinkedList<Storage>(), 0);

    }

    private static DatacenterCharacteristics getDatacenterCharacteristics(List<Host> hostList) {
        String arch = "x86"; // system architecture
        String os = "Linux"; // operating system
        String vmm = "Xen";
        double time_zone = 10.0; // time zone this resource located
        double cost = 3.0; // the cost of using processing in this resource
        double costPerMem = 0.05; // the cost of using memory in this resource
        double costPerStorage = 0.1; // the cost of using storage in this resource
        double costPerBw = 0.1; // the cost of using bandwidth in this resource

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);
        return characteristics;
    }

    private static void printCloudletList(List<Cloudlet> list) {
        int size = list.size();
        Cloudlet cloudlet;

        String indent = "    ";
        System.out.println();
        System.out.println("========== OUTPUT ==========");
        System.out.println("Cloudlet ID" + indent + "STATUS" + indent + "Data center ID" + indent + "VM ID" + indent + "Time" + indent + "Start Time" + indent + "Finish Time");

        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
            System.out.print(indent + cloudlet.getCloudletId() + indent + indent);

            if (cloudlet.getStatus() == Cloudlet.SUCCESS) {
                System.out.print("SUCCESS");

                System.out.println(indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId() + indent + indent + indent + cloudlet.getActualCPUTime() + indent + indent + cloudlet.getExecStartTime() + indent + indent + cloudlet.getFinishTime());
            }
        }
    }


}
