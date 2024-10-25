package com.yourorganization.simulation;

import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.core.CloudSim;
import java.util.List;

public class BrokerManager {

    /**
     * Creates a DatacenterBroker.
     *
     * @param name The name of the broker.
     * @return The created DatacenterBroker object.
     */
    public DatacenterBroker createBroker(String name) {
        DatacenterBroker broker = null;
        try {
            broker = new DatacenterBroker(name);
            System.out.println("Broker " + name + " created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return broker;
    }
}
