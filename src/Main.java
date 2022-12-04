import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Main {
  public static void main(String[] args) {
    try {
      //Program p = new Parser(System.in).Start();
      Program p = new Parser(new FileInputStream("src/inputs/t_bisection.txt")).Start();
      SymTable t = new SymTable(10000);
      TypeVisitor type = new TypeVisitor(p, t);
      EvalVisitor eval = new EvalVisitor(p, t);
  	  Object res = eval.visit(new Header("main", null));
    }
    catch(ParseException ex) {
      System.out.println("Fail!\n" + ex.getMessage());
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
