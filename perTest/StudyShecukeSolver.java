import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class StudyShecukeSolver {
    public static void main(String[] args) {
        // model for container for the proulem
         Model model = new Model("Friday Schedule");

         // Define variables
        // In senario = Ragne 0 to 4
        IntVar mathStart = model.intVar("Math Start",0,4);
        IntVar scienceStart = model.intVar("Science Start",0,4);

        // Fix duarations
        int mathDuration = 2;
        int scienceDuration = 1;

        // Define constraints
        // mathStart + 2 <= 4

        mathStart.add(mathDuration).le(4).post();
       //ScienceStart +1 <= 4
        scienceStart.add(scienceDuration).le(4).post();
        // no overlap and have a order like Math finish first then science start
        //(mathStart + mathDulation) <= Science start
       mathStart.add(mathDuration).le(scienceStart).post();

       // solve
        System.out.println("Shedule you looking for ");

        if(model.getSolver().solve()){
            System.out.println("Solution found");
            System.out.println("Math Start: "+ mathStart.getValue()+" End: "+ mathStart.getValue()+ mathDuration);
            System.out.println("Science Start: "+ scienceStart.getValue()+" End: "+scienceStart.getValue() + scienceDuration);

        }else {
            System.out.println("No solution found");
        }
    }
}