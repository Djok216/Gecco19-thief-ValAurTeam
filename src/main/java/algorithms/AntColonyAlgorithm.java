package algorithms;

import model.NonDominatedSet;
import model.Solution;
import model.TravelingThiefProblem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:aurelian.hreapca@gmail.com">Aurelian Hreapca</a> (created on 5/5/19)
 */
public class AntColonyAlgorithm implements Algorithm {
    private double historyCoefficient;
    private double heuristicCoefficient;
    private double decayFactor;
    private double greedinessFactor;
    private double totalAnts;

    private AntColonyAlgorithm(double historyCoefficient, double heuristicCoefficient, double decayFactor,
            double greedinessFactor, double totalAnts) {
        this.historyCoefficient = historyCoefficient;
        this.heuristicCoefficient = heuristicCoefficient;
        this.decayFactor = decayFactor;
        this.greedinessFactor = greedinessFactor;
        this.totalAnts = totalAnts;
    }

    @Override
    public List<Solution> solve(TravelingThiefProblem problem) {
        NonDominatedSet nds = new NonDominatedSet();
        double[][] pheromoneMatrix = new double[problem.numOfCities][problem.numOfCities];

        List<Integer> best = AlgorithmCommons.generateRandomTour(problem.numOfCities);
        AlgorithmCommons.evaluateTourPickingGreedy(nds, best, 0.001, problem);
        double bestCost = problem.evaluateTour(best);
        double initialPheromone =  1.0 / (bestCost * problem.numOfCities);

        for (int i = 0; i < pheromoneMatrix.length; i++) {
            Arrays.fill(pheromoneMatrix[i], initialPheromone);
        }

        int iterationWithoutUpdate = 0;

        // stop condition.
        while (iterationWithoutUpdate < 100) {
            iterationWithoutUpdate = iterationWithoutUpdate + 1;

            for (int i = 0; i < totalAnts; i++) {
                List<Integer> current = generateCurrentTour(pheromoneMatrix, problem);
                AlgorithmCommons.evaluateTourPickingGreedy(nds, current, 0.001, problem);
                double currentCost = problem.evaluateTour(current);

                if (currentCost < bestCost) {
                    best = current;
                    bestCost = currentCost;
                    System.out.println(String.format("Found new best %.2f after %d iterations without success.", bestCost, iterationWithoutUpdate));
                    iterationWithoutUpdate = 0;
                }

                localUpdatePheromone(pheromoneMatrix, current, initialPheromone);
            }

            globalUpdatePheromone(pheromoneMatrix, best, bestCost);
        }

        return nds.entries;
    }

    private List<Integer> generateCurrentTour(double[][] pheromoneMatrix, TravelingThiefProblem problem) {
        List<Integer> current = new ArrayList<>();
        Set<Integer> usedCities = new HashSet<>();
        current.add(0);
        usedCities.add(0);

        for (int i = 1; i < problem.numOfCities; i++) {
            double[] prob = new double[problem.numOfCities];
            double maxProb = -1.0;
            int maxProbCity = -1;

            for (int j = 0; j < prob.length; j++) {
                if (usedCities.contains(j)) {
                    continue;
                }

                int lastCity = current.get(current.size() - 1);
                double histProb = Math.pow(pheromoneMatrix[lastCity][j], historyCoefficient);
                double heurProb = Math.pow((1.0 / AlgorithmCommons.getDistance(problem, lastCity, j)), heuristicCoefficient);
                prob[j] = histProb * heurProb;

                if (prob[j] > maxProb) {
                    maxProb = prob[j];
                    maxProbCity = j;
                }
            }

            if (Math.random() < greedinessFactor) {
                usedCities.add(maxProbCity);
                current.add(maxProbCity);
            } else {
                double p = Math.random();
                double sum = .0;
                boolean added = false;
                int last = -1;
                for (int j = 0; j < prob.length; j++) {
                    if (prob[j] > 0) {
                        sum = sum + prob[j];
                        last = j;
                        if (p < sum) {
                            added = true;
                            current.add(j);
                            usedCities.add(j);
                            break;
                        }
                    }
                }

                if (added == false) {
                    current.add(last);
                    usedCities.add(last);
                }
            }
        }

        return current;
    }

    private void localUpdatePheromone(double[][] pheromoneMatrix, List<Integer> tour, double initialPheromone) {
        for (int i = 0; i < tour.size(); i++) {
            int currentCity = tour.get(i);
            int nextCity = (i == tour.size() - 1) ? tour.get(0) : tour.get(i+1);
            double value = (1.0 - historyCoefficient) * pheromoneMatrix[currentCity][nextCity] + historyCoefficient * initialPheromone;

            pheromoneMatrix[currentCity][nextCity] = value;
            pheromoneMatrix[nextCity][currentCity] = value;
        }
    }

    private void globalUpdatePheromone(double[][] pheromoneMatrix, List<Integer> tour, double tourCost) {
        for (int i = 0; i < tour.size(); i++) {
            int currentCity = tour.get(i);
            int nextCity = (i == tour.size() - 1) ? tour.get(0) : tour.get(i+1);
            double value = (1.0 - decayFactor) * pheromoneMatrix[currentCity][nextCity] + decayFactor * (1.0 / tourCost);

            pheromoneMatrix[currentCity][nextCity] = value;
            pheromoneMatrix[nextCity][currentCity] = value;
        }
    }

    public static final class Builder {
        private double historyCoefficient = 0.1;
        private double heuristicCoefficient = 2.5;
        private double decayFactor = 0.1;
        private double greedinessFactor = 0.9;
        private double totalAnts = 10;

        public Builder withHistoryCoefficient(double historyCoefficient) {
            this.historyCoefficient = historyCoefficient;
            return this;
        }

        public Builder withHeuristicCoefficient(double heuristicCoefficient) {
            this.heuristicCoefficient = heuristicCoefficient;
            return this;
        }

        public Builder withDecayFactor(double decayFactor) {
            this.decayFactor = decayFactor;
            return this;
        }

        public Builder withGreedinesFactor(double greedinessFactor) {
            this.greedinessFactor = greedinessFactor;
            return this;
        }

        public Builder withTotalAnts(double totalAnts) {
            this.totalAnts = totalAnts;
            return this;
        }

        public AntColonyAlgorithm build() {
            return new AntColonyAlgorithm(historyCoefficient, heuristicCoefficient, decayFactor,
                    greedinessFactor, totalAnts);
        }
    }
}
