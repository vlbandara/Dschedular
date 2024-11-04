package algorithms;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import simulation.CloudSimInitializer;

import java.util.ArrayList;
import java.util.List;

public class SchedulerTest {
    public static void main(String[] args) {
        try {
            // Initialize CloudSim with multiple VMs
            int numUsers = 8; // Number of VMs
            CloudSimInitializer initializer = new CloudSimInitializer();
            initializer.initialize(numUsers);

            // Create test cloudlets
            List<Cloudlet> cloudlets = createTestCloudlets(10);
            List<Vm> vms = initializer.getVmList();

            // Initialize GA Scheduler
            GA_Scheduler scheduler = new GA_Scheduler();
            AlgorithmParameters params = new AlgorithmParameters(
                    50,     // population size
                    0.8,    // crossover rate
                    0.1,    // mutation rate
                    100,    // max generations
                    0.001   // convergence threshold
            );
            scheduler.initializeParameters(params);

            // Schedule cloudlets
            List<Cloudlet> scheduledCloudlets = scheduler.scheduleCloudlets(cloudlets, vms);

            // Print results
            System.out.println("Scheduling Results:");
            for (Cloudlet cloudlet : scheduledCloudlets) {
                System.out.println("Cloudlet " + cloudlet.getCloudletId() +
                        " assigned to VM " + cloudlet.getVmId());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Cloudlet> createTestCloudlets(int numCloudlets) {
        List<Cloudlet> cloudlets = new ArrayList<>();

        // Cloudlet properties
        long length = 1000;
        long fileSize = 300;
        long outputSize = 300;
        int pesNumber = 1;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        for (int i = 0; i < numCloudlets; i++) {
            Cloudlet cloudlet = new Cloudlet(
                    i, length, pesNumber, fileSize, outputSize,
                    utilizationModel, utilizationModel, utilizationModel
            );
            cloudlet.setUserId(0); // Assign to the first broker
            cloudlets.add(cloudlet);
        }

        return cloudlets;
    }
}
