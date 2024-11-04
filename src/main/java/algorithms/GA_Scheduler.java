package algorithms;

import org.cloudbus.cloudsim.*;
import java.util.*;

public class GA_Scheduler extends BaseSchedulingAlgorithm {
    private Random random;
    private List<Chromosome> population;
    private int numberOfVMs;
    private List<Cloudlet> cloudlets; // Added member variable
    private List<Vm> vms;             // Added member variable

    public GA_Scheduler() {
        super("Genetic Algorithm Scheduler");
        this.random = new Random();
    }

    @Override
    public List<Cloudlet> scheduleCloudlets(List<Cloudlet> cloudlets, List<Vm> vms) {
        this.cloudlets = cloudlets; // Initialize cloudlets
        this.vms = vms;             // Initialize VMs
        this.numberOfVMs = vms.size(); // Initialize the number of VMs

        // Initialize population
        initializePopulation(cloudlets, vms);

        int generation = 0;
        double previousBestFitness = Double.MAX_VALUE;
        int stagnantGenerations = 0;

        while (generation < params.getMaxGenerations()) {
            // Evaluate fitness for all chromosomes
            evaluatePopulation();

            // Check convergence
            double currentBestFitness = getBestChromosome().getFitness();
            if (Math.abs(previousBestFitness - currentBestFitness) < params.getConvergenceThreshold()) {
                stagnantGenerations++;
                if (stagnantGenerations >= 10) { // Convergence criterion
                    break;
                }
            } else {
                stagnantGenerations = 0;
            }
            previousBestFitness = currentBestFitness;

            // Selection
            List<Chromosome> selectedChromosomes = selection();

            // Crossover
            List<Chromosome> offspring = crossover(selectedChromosomes);

            // Mutation
            mutation(offspring);

            // Replace population
            population = offspring;

            generation++;
        }

        // Get best solution
        Chromosome bestSolution = getBestChromosome();
        return applyScheduling(cloudlets, vms, bestSolution);
    }

    private void initializePopulation(List<Cloudlet> cloudlets, List<Vm> vms) {
        population = new ArrayList<>();
        for (int i = 0; i < params.getPopulationSize(); i++) {
            Chromosome chromosome = new Chromosome(cloudlets.size());
            // Randomly assign VMs to cloudlets
            for (int j = 0; j < cloudlets.size(); j++) {
                chromosome.genes[j] = random.nextInt(vms.size());
            }
            population.add(chromosome);
        }
    }

    private void evaluatePopulation() {
        for (Chromosome chromosome : population) {
            chromosome.fitness = calculateFitness(chromosome);
        }
    }

    private double calculateFitness(Chromosome chromosome) {
        // Initialize an array to hold the completion time for each VM
        double[] vmCompletionTimes = new double[numberOfVMs];

        // Iterate over each cloudlet and assign its execution time to the corresponding VM
        for (int i = 0; i < chromosome.genes.length; i++) {
            int vmIndex = chromosome.genes[i];
            Cloudlet cloudlet = cloudlets.get(i);
            double executionTime = calculateExecutionTime(cloudlet, vms.get(vmIndex));
            vmCompletionTimes[vmIndex] += executionTime;
        }

        // The makespan is the maximum completion time across all VMs
        double makespan = Arrays.stream(vmCompletionTimes).max().orElse(0);

        // Optionally, incorporate other metrics into the fitness calculation
        // For simplicity, we'll use makespan as the sole metric
        return makespan;
    }


    private List<Chromosome> selection() {
        List<Chromosome> selected = new ArrayList<>();
        // Tournament selection
        while (selected.size() < params.getPopulationSize()) {
            Chromosome parent1 = tournamentSelect();
            Chromosome parent2 = tournamentSelect();
            selected.add(parent1);
            selected.add(parent2);
        }
        return selected;
    }

    private Chromosome tournamentSelect() {
        int tournamentSize = 3;
        Chromosome best = null;
        double bestFitness = Double.MAX_VALUE;

        for (int i = 0; i < tournamentSize; i++) {
            Chromosome candidate = population.get(random.nextInt(population.size()));
            if (best == null || candidate.getFitness() < bestFitness) {
                best = candidate;
                bestFitness = candidate.getFitness();
            }
        }
        return best.clone();
    }

    private List<Chromosome> crossover(List<Chromosome> parents) {
        List<Chromosome> offspring = new ArrayList<>();

        for (int i = 0; i < parents.size() - 1; i += 2) {
            if (random.nextDouble() < params.getCrossoverRate()) {
                // Single-point crossover
                Chromosome[] children = singlePointCrossover(
                        parents.get(i),
                        parents.get(i + 1)
                );
                offspring.add(children[0]);
                offspring.add(children[1]);
            } else {
                offspring.add(parents.get(i));
                offspring.add(parents.get(i + 1));
            }
        }

        return offspring;
    }

    private Chromosome[] singlePointCrossover(Chromosome parent1, Chromosome parent2) {
        int crossoverPoint = random.nextInt(parent1.genes.length);
        Chromosome child1 = parent1.clone();
        Chromosome child2 = parent2.clone();

        for (int i = crossoverPoint; i < parent1.genes.length; i++) {
            child1.genes[i] = parent2.genes[i];
            child2.genes[i] = parent1.genes[i];
        }

        return new Chromosome[]{child1, child2};
    }

    private void mutation(List<Chromosome> chromosomes) {
        for (Chromosome chromosome : chromosomes) {
            for (int i = 0; i < chromosome.genes.length; i++) {
                if (random.nextDouble() < params.getMutationRate()) {
                    // Randomly change VM assignment within valid range
                    chromosome.genes[i] = random.nextInt(this.numberOfVMs);
                }
            }
        }
    }

    private Chromosome getBestChromosome() {
        return Collections.min(population, Comparator.comparingDouble(c -> c.fitness));
    }

    private List<Cloudlet> applyScheduling(List<Cloudlet> cloudlets, List<Vm> vms, Chromosome chromosome) {
        // Apply the scheduling solution to the cloudlets
        for (int i = 0; i < cloudlets.size(); i++) {
            Cloudlet cloudlet = cloudlets.get(i);
            int vmIndex = chromosome.genes[i];
            cloudlet.setVmId(vms.get(vmIndex).getId());
        }
        return cloudlets;
    }

    // Inner class representing a chromosome in the genetic algorithm
    private static class Chromosome {
        private int[] genes; // VM assignments for each cloudlet
        private double fitness;

        public Chromosome(int size) {
            this.genes = new int[size];
        }

        public double getFitness() {
            return fitness;
        }

        @Override
        public Chromosome clone() {
            Chromosome copy = new Chromosome(genes.length);
            copy.genes = Arrays.copyOf(genes, genes.length);
            copy.fitness = this.fitness;
            return copy;
        }
    }
}
