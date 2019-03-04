package algorithms;

import model.NonDominatedSet;
import model.Solution;
import model.TravelingThiefProblem;

import java.util.ArrayList;
import java.util.List;

public class GeneticAlgorithm implements Algorithm {
    private final int popSize;
    private final int generations;
    private final double mutationRate;
    private final int eliteSize;

    public GeneticAlgorithm(int popSize, int eliteSize, double mutationRate, int generations) {
        this.popSize = popSize;
        this.eliteSize = eliteSize;
        this.mutationRate = mutationRate;
        this.generations = generations;
    }

    @Override
    public List<Solution> solve(TravelingThiefProblem problem) {
        NonDominatedSet nds = new NonDominatedSet();
        List<List<Integer>> pop = generateRandomTSPPopulation(problem.numOfCities);
        evaluatePopulation(nds, pop);
        return nds.entries;
    }

    private void evaluatePopulation(NonDominatedSet nds, List<List<Integer>> pop) {
        for (List<Integer> tour : pop) {
            List<Boolean> items = new ArrayList<>(tour.size());
        }
    }

    private List<List<Integer>> generateRandomTSPPopulation(int numOfCities) {
        List<List<Integer>> list = new ArrayList<>(numOfCities);
        for (int i = 0; i < numOfCities; ++i) {
            list.add(generateRandomTour(numOfCities));
        }
        return list;
    }

    private List<Integer> generateRandomTour(int numOfCities) {
        List<Integer> tour = new ArrayList<>();
        for (int i = 1; i < numOfCities; ++i) {
            tour.add(i);
        }
        java.util.Collections.shuffle(tour);
        tour.add(0, 0);
        return tour;
    }
}
