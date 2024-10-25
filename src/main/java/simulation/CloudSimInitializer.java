package simulation;

import org.cloudbus.cloudsim.core.CloudSim;
import java.util.Calendar;

public class CloudSimInitializer {

    /**
     * Initializes the CloudSim library.
     *
     * @param traceFlag Indicates whether to trace events.
     */
    public void initializeCloudSim(boolean traceFlag) {
        int numUsers = 1; // Number of cloud users
        Calendar calendar = Calendar.getInstance();
        boolean logging = false; // Enable or disable CloudSim's logging

        // Initialize CloudSim
        CloudSim.init(numUsers, calendar, logging);

        System.out.println("CloudSim initialized successfully.");
    }
}
