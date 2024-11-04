package simulation;

public class SimulationTest {
    public static void main(String[] args) {
        try {
            // Initialize CloudSim
            CloudSimInitializer initializer = new CloudSimInitializer();
            initializer.initialize(1); // Start with 1 user

            // Create DatacenterManager and set up a datacenter
            DatacenterManager dcManager = new DatacenterManager();
            dcManager.createDatacenter(
                    "TestCenter",
                    2,      // 2 hosts
                    4,      // 4 cores per host
                    1000,   // 1000 MIPS per core
                    4096,   // 4GB RAM per host
                    1000000,// 1TB storage per host
                    10000   // 10Gbps network
            );

            // Print configuration
            System.out.println("Simulation Configuration:");
            System.out.println("Total Hosts: " + dcManager.getTotalHostCount());
            System.out.println("Total Cores: " + dcManager.getTotalCoreCount());

            // Start simulation
            org.cloudbus.cloudsim.core.CloudSim.startSimulation();

            // Stop simulation
            org.cloudbus.cloudsim.core.CloudSim.stopSimulation();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}