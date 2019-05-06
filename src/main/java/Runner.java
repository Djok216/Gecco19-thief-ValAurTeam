import algorithms.Algorithm;
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
        //List<String> instanceToRun = Arrays.asList("a280-n1395", "fnl4461-n44600");
        List<String> instanceToRun = Arrays.asList("fnl4461-n44600");
        //List<String> instanceToRun = Competition.INSTANCES;
        for (String instance : instanceToRun) {
            System.out.println("Running on " + instance);
            String fname = String.format("resources/%s.txt", instance);
            InputStream is = LOADER.getResourceAsStream(fname);

            TravelingThiefProblem problem = Util.readProblem(is);
            System.out.println(problem.numOfCities + " " + problem.numOfItems);
            problem.name = instance;
            Algorithm algorithm = new GeneticAlgorithm(100, 20,0.02, 500, 1);
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
