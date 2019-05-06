package algorithms;

import model.NonDominatedSet;
import model.Solution;
import model.TravelingThiefProblem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author <a href="mailto:aurelian.hreapca@gmail.com">Aurelian Hreapca</a> (created on 5/5/19)
 */

public class AlgorithmCommons {

    public static List<Integer> generateRandomTour(int numOfCities) {
        List<Integer> tour = IntStream.range(0, numOfCities)
                .mapToObj(x -> x)
                .collect(Collectors.toList());
        Collections.shuffle(tour.subList(1, numOfCities));

        return tour;
    }

    public static List<Integer> generateRandomPermutation(int n) {
        List<Integer> permutation = IntStream.range(0, n)
                .mapToObj(x -> x)
                .collect(Collectors.toList());
        Collections.shuffle(permutation);

        return permutation;
    }

    public static double getDistance(TravelingThiefProblem problem, int firstCity, int secondCity) {
        return Math.sqrt(Math.pow(problem.coordinates[firstCity][0] - problem.coordinates[secondCity][0], 2)
                + Math.pow(problem.coordinates[firstCity][1] - problem.coordinates[secondCity][1], 2));
    }

    public static void evaluateTourPickingGreedy(NonDominatedSet nds, List<Integer> tour, double evalProbability,
            TravelingThiefProblem problem) {
        Solution s = problem.evaluate(tour);
        nds.add(s);
        List<Boolean> items = new ArrayList<>(Collections.nCopies(problem.numOfItems, false));
        List<Integer> randPerm = generateRandomPermutation(problem.numOfItems);
        double weight = 0.0;
        for (int item : randPerm) {
            if (weight + problem.weight[item] <= problem.maxWeight) {
                items.set(item, true);
                weight += problem.weight[item];
                if (Math.random() < evalProbability) {
                    s = problem.evaluate(tour, items, true);
                    nds.add(s);
                }
            }
        }
        s = problem.evaluate(tour, items, true);
        nds.add(s);
    }
}
