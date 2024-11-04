package algorithms;

// Class to hold algorithm parameters
public class AlgorithmParameters {
    private int populationSize;
    private double crossoverRate;
    private double mutationRate;
    private int maxGenerations;
    private double convergenceThreshold;

    // Constructor
    public AlgorithmParameters(int populationSize, double crossoverRate,
                               double mutationRate, int maxGenerations,
                               double convergenceThreshold) {
        this.populationSize = populationSize;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.maxGenerations = maxGenerations;
        this.convergenceThreshold = convergenceThreshold;
    }

    // Getters
    public int getPopulationSize() { return populationSize; }
    public double getCrossoverRate() { return crossoverRate; }
    public double getMutationRate() { return mutationRate; }
    public int getMaxGenerations() { return maxGenerations; }
    public double getConvergenceThreshold() { return convergenceThreshold; }
}
