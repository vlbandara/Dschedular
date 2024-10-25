package simulation;

import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.core.CloudSim;

import java.util.List;
import java.util.ArrayList;

public class SimulationRunner {

    public static void main(String[] args) {
        // Step 1: Initialize CloudSim
        CloudSimInitializer initializer = new CloudSimInitializer();
        initializer.initializeCloudSim(false);

        // Step 2: Create Datacenter
        DatacenterManager datacenterManager = new DatacenterManager();
        Datacenter datacenter = datacenterManager.createDatacenter("Datacenter_0");

        // Step 3: Create Broker
        com.yourorganization.simulation.BrokerManager brokerManager = new com.yourorganization.simulation.BrokerManager();
        DatacenterBroker broker = brokerManager.createBroker("Broker_0");
        int brokerId = broker.getId();

        // Additional setup can be done here (e.g., creating VMs, submitting tasks)

        // Step 4: Start Simulation
        CloudSim.startSimulation();

        // Step 5: Stop Simulation
        CloudSim.stopSimulation();

        System.out.println("Simulation finished.");
    }
}
