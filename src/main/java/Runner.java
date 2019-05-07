import algorithms.Algorithm;
import algorithms.AntColonyAlgorithm;
import algorithms.GeneticAlgorithm;
import model.Solution;
import model.TravelingThiefProblem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

class Runner {
    private static final ClassLoader LOADER = Runner.class.getClassLoader();

    public static void main(String[] args) throws IOException {
        //List<String> instanceToRun = Arrays.asList("a280-n279", "a280-n1395", "a280-n2790");
        List<String> instanceToRun = Arrays.asList("fnl4461-n44600");
        //List<String> instanceToRun = Competition.INSTANCES;
        for (String instance : instanceToRun) {
            System.out.println("Running on " + instance);
            String fname = String.format("resources/%s.txt", instance);
            InputStream is = LOADER.getResourceAsStream(fname);

            TravelingThiefProblem problem = Util.readProblem(is);
            System.out.println(problem.numOfCities + " " + problem.numOfItems);
            problem.name = instance;
            int popSize = instance.charAt(0) == 'p' ? 10 : 100;
            int eliteSize = instance.charAt(0) == 'p' ? 3 : 20;
            int generations = instance.charAt(0) == 'p' ? 10 : 100;
            double evalProbability = problem.numOfItems > 34000 ? 10.0 / problem.numOfItems : 0.04;
            //Algorithm algorithm = new GeneticAlgorithm(popSize, eliteSize,0.02, 1, evalProbability);

            Algorithm algorithm = new AntColonyAlgorithm.Builder()
                    .withHistoryCoefficient(0.1)
                    .withHeuristicCoefficient(2.5)
                    .withTotalAnts(40)
                    .build();
            List<Solution> nds = algorithm.solve(problem);
            System.out.println(problem.name + " " + nds.size() + " " + Competition.numberOfSolutions(problem));
            File dir = new File("results");
            Util.writeSolutions("results", Competition.TEAM_NAME, problem, nds);
        }
    }

    private static List<Solution> discardSomeSolutions(List<Solution> nds, int numberOfSolutions) {
        if (nds.size() <= numberOfSolutions) {
            return nds;
        }
        List<Solution> selectedNDS = new ArrayList<>(numberOfSolutions);
        Collections.shuffle(nds);
        while(selectedNDS.size() < numberOfSolutions) selectedNDS.add(nds.get(selectedNDS.size()));
        return selectedNDS;
    }
}
