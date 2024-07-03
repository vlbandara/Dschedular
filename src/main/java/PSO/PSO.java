package PSO;

import org.cloudbus.cloudsim.*;
import java.util.*;


class PSO {
    private int particleCount;
    private int maxIterations;
    private double w; // Inertia weight
    private double c1; // Cognitive coefficient
    private double c2; // Social coefficient
    private List<Particle> particles;
    private List<Integer> globalBest;
    private double globalBestFitness;
    private Random random;

    public PSO(int particleCount, int maxIterations, double w, double c1, double c2) {
        this.particleCount = particleCount;
        this.maxIterations = maxIterations;
        this.w = w;
        this.c1 = c1;
        this.c2 = c2;
        this.random = new Random();
        this.globalBestFitness = Double.MAX_VALUE;
    }

    public List<Integer> optimize(List<Cloudlet> cloudlets, List<Vm> vms) {
        int dimensions = cloudlets.size();
        initializeParticles(dimensions, vms.size());

        for (int iteration = 0; iteration < maxIterations; iteration++) {
            for (Particle particle : particles) {
                double fitness = calculateFitness(particle.position, cloudlets, vms);

                // Update personal best
                if (fitness < particle.personalBestFitness) {
                    particle.personalBest = new ArrayList<>(particle.position);
                    particle.personalBestFitness = fitness;
                }

                // Update global best
                if (fitness < globalBestFitness) {
                    globalBest = new ArrayList<>(particle.position);
                    globalBestFitness = fitness;
                }
            }

            // Update velocities and positions
            for (Particle particle : particles) {
                updateVelocityAndPosition(particle, vms.size());
            }
        }

        return globalBest;
    }

    private void initializeParticles(int dimensions, int vmCount) {
        particles = new ArrayList<>();
        for (int i = 0; i < particleCount; i++) {
            Particle particle = new Particle(dimensions);
            for (int j = 0; j < dimensions; j++) {
                particle.position.set(j, random.nextInt(vmCount));
                particle.velocity.set(j, random.nextInt(5) - 2); // Velocity between -2 and 2
            }
            particles.add(particle);
        }
    }

    private void updateVelocityAndPosition(Particle particle, int vmCount) {
        for (int i = 0; i < particle.position.size(); i++) {
            // Update velocity
            double r1 = random.nextDouble();
            double r2 = random.nextDouble();
            int velocity = (int) (w * particle.velocity.get(i) +
                    c1 * r1 * (particle.personalBest.get(i) - particle.position.get(i)) +
                    c2 * r2 * (globalBest.get(i) - particle.position.get(i)));
            particle.velocity.set(i, velocity);

            // Update position
            int position = particle.position.get(i) + particle.velocity.get(i);
            // Ensure position is within bounds [0, vmCount-1]
            position = Math.max(0, Math.min(position, vmCount - 1));
            particle.position.set(i, position);
        }
    }

    private double calculateFitness(List<Integer> solution, List<Cloudlet> cloudlets, List<Vm> vms) {
        // Initialize execution time for each VM
        double[] vmExecutionTimes = new double[vms.size()];

        // Calculate total execution time (makespan)
        for (int i = 0; i < solution.size(); i++) {
            int vmIndex = solution.get(i);
            Cloudlet cloudlet = cloudlets.get(i);
            Vm vm = vms.get(vmIndex);

            double executionTime = cloudlet.getCloudletLength() / vm.getMips();
            vmExecutionTimes[vmIndex] += executionTime;
        }

        // Makespan is the maximum execution time among all VMs
        double makespan = Arrays.stream(vmExecutionTimes).max().orElse(0);

        // Calculate resource utilization
        double totalExecutionTime = Arrays.stream(vmExecutionTimes).sum();
        double averageExecutionTime = totalExecutionTime / vms.size();
        double utilizationVariance = Arrays.stream(vmExecutionTimes)
                .map(time -> Math.pow(time - averageExecutionTime, 2))
                .sum() / vms.size();

        // Fitness is a combination of makespan and utilization variance
        // Lower values are better
        return makespan + utilizationVariance;
    }
}