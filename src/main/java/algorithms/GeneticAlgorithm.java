package algorithms;

import Structs.Pair;
import model.NonDominatedSet;
import model.Solution;
import model.TravelingThiefProblem;

import java.util.*;

public class GeneticAlgorithm implements Algorithm {
    private final int popSize;
    private final int generations;
    private final double mutationRate;
    private final int eliteSize;
    private double evalProbability;

    public GeneticAlgorithm(int popSize, int eliteSize, double mutationRate, int generations, double evalProbability) {
        this.popSize = popSize;
        this.eliteSize = eliteSize;
        this.mutationRate = mutationRate;
        this.generations = generations;
        this.evalProbability = evalProbability;
    }

    @Override
    public List<Solution> solve(TravelingThiefProblem problem) {
        NonDominatedSet nds = new NonDominatedSet();
        List<List<Integer>> pop = generateRandomTSPPopulation(problem.numOfCities);
        if (problem.numOfItems > 10000) this.evalProbability = 0.001;
        for (int generation = 0; generation < this.generations; ++generation) {
            System.out.println("Current Generation: " + generation);
            pop = generateNextGeneration(pop, problem);
            evaluatePopulation(nds, pop, problem);
        }
        return nds.entries;
    }

    private List<List<Integer>> generateNextGeneration(List<List<Integer>> pop, TravelingThiefProblem problem) {
        List<Pair<Integer, Double>> rank = new ArrayList<>(pop.size());
        for (List<Integer> tour : pop) {
            rank.add(new Pair<>(rank.size(), problem.evaluateTour(tour)));
        }
        rank.sort(Comparator.comparing(a -> a.y));
        List<List<Integer>> orderedPop = new ArrayList<>(pop.size());
        for (Pair<Integer, Double> p : rank) {
            orderedPop.add(pop.get(p.x));
        }
        pop = orderedPop;

        List<List<Integer>> newPop = new ArrayList<>(2 * this.popSize);
        List<Pair<Integer, Double>> newPopRank = new ArrayList<>(2 * this.popSize);
        for (int i = 0; i < this.eliteSize; ++i) {
            newPop.add(pop.get(i));
            newPopRank.add(new Pair<>(i, rank.get(i).y));
        }
        while (newPop.size() < 2 * this.popSize) {
            List<Integer> newTour = crossOver(pop, rank, problem);
            newPopRank.add(new Pair<>(newPop.size() - 1, problem.evaluateTour(newTour)));
            newPop.add(newTour);
        }
        newPopRank.sort(Comparator.comparing(a -> a.y));
        List<List<Integer>> orderedNewPop = new ArrayList<>(newPop.size());
        for (Pair<Integer, Double> p : newPopRank) {
            orderedNewPop.add(newPop.get(p.x));
        }
        while (orderedNewPop.size() > this.popSize) orderedNewPop.remove(orderedNewPop.size() - 1);
        return orderedNewPop;
    }

    private List<Integer> crossOver(List<List<Integer>> pop, List<Pair<Integer, Double>> rank, TravelingThiefProblem problem) {
        double sumTime = 0;
        for (Pair<Integer, Double> p : rank) {
            sumTime += p.y;
        }
        double motherScore = Math.random() * sumTime;
        double fatherScore = Math.random() * sumTime;
        List<Integer> mother = null;
        List<Integer> father = null;
        for (int i = 0; i < pop.size(); ++i) {
            if (motherScore > 0 && motherScore - rank.get(i).y <= 0) {
                motherScore = -1;
                mother = pop.get(i);
            }
            if (fatherScore > 0 && fatherScore - rank.get(i).y <= 0) {
                fatherScore = -1;
                father = pop.get(i);
            }
            motherScore -= rank.get(i).y;
            fatherScore -= rank.get(i).y;
        }

        int l = (int) (Math.random() * (problem.numOfCities - 1)) + 1;
        int r = (int) (Math.random() * (problem.numOfCities - l)) + l;
        Set<Integer> added = new HashSet<>();
        List<Integer> child = new ArrayList<>(problem.numOfCities);
        child.add(0);
        for (int i = l; i <= r; ++i) {
            added.add(mother.get(i));
            child.add(mother.get(i));
        }
        for (int i = 1; i < father.size(); ++i) {
            if (!added.contains(father.get(i))) {
                added.add(father.get(i));
                child.add(father.get(i));
            }
        }
        if (Math.random() < this.mutationRate) {
            int x = (int) (Math.random() * (problem.numOfCities - 1)) + 1;
            int y = (int) (Math.random() * (problem.numOfCities - 1)) + 1;
            Collections.swap(child, x, y);
        }
        return child;
    }

    private void evaluatePopulation(NonDominatedSet nds, List<List<Integer>> pop, TravelingThiefProblem problem) {
        for (List<Integer> tour : pop) {
            Solution s = problem.evaluate(tour);
            nds.add(s);
            List<Boolean> items = new ArrayList<>(Collections.nCopies(problem.numOfItems, false));
            List<Integer> randPerm = generateRandomPermutation(problem.numOfItems);
            double weight = 0.0;
            for (int item : randPerm) {
                if (weight + problem.weight[item] <= problem.maxWeight) {
                    items.set(item, true);
                    weight += problem.weight[item];
                    if (Math.random() < this.evalProbability) {
                        s = problem.evaluate(tour, items, true);
                        nds.add(s);
                    }
                }
            }
            s = problem.evaluate(tour, items, true);
            nds.add(s);
        }
    }

    private List<List<Integer>> generateRandomTSPPopulation(int numOfCities) {
        List<List<Integer>> list = new ArrayList<>(this.popSize);
        for (int i = 0; i < this.popSize; ++i) {
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

    private List<Integer> generateRandomPermutation(int size) {
        List<Integer> randPerm = new ArrayList<>(size);
        while (randPerm.size() < size) randPerm.add(randPerm.size());
        Collections.shuffle(randPerm);
        return randPerm;
    }
}
