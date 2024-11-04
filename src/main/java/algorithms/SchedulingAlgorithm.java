package algorithms;

import org.cloudbus.cloudsim.*;
import java.util.List;

// Base interface for all scheduling algorithms
public interface SchedulingAlgorithm {
    /**
     * Schedule the given cloudlets to available VMs
     * @param cloudlets List of cloudlets to be scheduled
     * @param vms List of available VMs
     * @return Map of cloudlet to VM assignments
     */
    List<Cloudlet> scheduleCloudlets(List<Cloudlet> cloudlets, List<Vm> vms);

    /**
     * Get the name of the algorithm
     * @return Algorithm name
     */
    String getAlgorithmName();

    /**
     * Initialize algorithm-specific parameters
     * @param params Algorithm parameters
     */
    void initializeParameters(AlgorithmParameters params);
}

