import algorithms.Algorithm;
import algorithms.GeneticAlgorithm;
import model.Solution;
import model.TravelingThiefProblem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

class Runner {
    static final ClassLoader LOADER = Runner.class.getClassLoader();

    public static void main(String[] args) throws IOException {
        List<String> instanceToRun = Arrays.asList("test-example-n4");
        //List<String> instanceToRun = Competition.INSTANCES;
        for (String instance : instanceToRun) {
            String fname = String.format("resources/%s.txt", instance);
            InputStream is = LOADER.getResourceAsStream(fname);

            TravelingThiefProblem problem = Util.readProblem(is);
            problem.name = instance;
            int numOfSolutions = Competition.numberOfSolutions(problem);
            Algorithm algorithm = new GeneticAlgorithm(100, 20,0.01, 500);
            List<Solution> nds = algorithm.solve(problem);
            nds.sort(Comparator.comparing(a -> a.time));

            System.out.println(nds.size());
            for (Solution s : nds) {
                System.out.println(s.time + " " + s.profit);
            }
            Util.printSolutions(nds, true);
            System.out.println(problem.name + " " + nds.size());
            File dir = new File("results");
            Util.writeSolutions("results", Competition.TEAM_NAME, problem, nds);
        }
    }
}
