import java.util.*;


class Particle {
    List<Integer> position;
    List<Integer> velocity;
    List<Integer> personalBest;
    double personalBestFitness;

    public Particle(int size) {
        position = new ArrayList<>(size);
        velocity = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            position.add(0);
            velocity.add(0);
        }
        personalBest = new ArrayList<>(position);
        personalBestFitness = Double.MAX_VALUE;
    }
}