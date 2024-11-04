package algorithms;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

import java.util.List;

// Abstract base class for scheduling algorithms
public abstract class BaseSchedulingAlgorithm implements SchedulingAlgorithm {
    protected AlgorithmParameters params;
    protected String algorithmName;

    public BaseSchedulingAlgorithm(String name) {
        this.algorithmName = name;
    }

    @Override
    public String getAlgorithmName() {
        return algorithmName;
    }

    @Override
    public void initializeParameters(AlgorithmParameters params) {
        this.params = params;
    }

    // Utility methods for scheduling
    protected double calculateExecutionTime(Cloudlet cloudlet, Vm vm) {
        return (double) cloudlet.getCloudletLength() / vm.getMips();
    }

    protected double calculateResourceUtilization(Vm vm, List<Cloudlet> assignedCloudlets) {
        double totalMipsRequired = assignedCloudlets.stream()
                .mapToDouble(Cloudlet::getCloudletLength)
                .sum();
        return totalMipsRequired / (vm.getMips() * vm.getNumberOfPes());
    }

    // Abstract method to be implemented by subclasses
    public abstract List<Cloudlet> scheduleCloudlets(List<Cloudlet> cloudlets, List<Vm> vms);
}
