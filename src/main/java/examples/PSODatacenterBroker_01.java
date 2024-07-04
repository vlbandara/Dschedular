package examples;

import org.cloudbus.cloudsim.core.SimEvent;

public interface PSODatacenterBroker_01 {
    void processCloudletSubmit(SimEvent ev, boolean ack);
}
