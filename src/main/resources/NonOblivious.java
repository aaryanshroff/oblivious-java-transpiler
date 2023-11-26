public class NonOblivious {
  public static void main(String[] args) {
    boolean condition = true;
    int valueToAssign = 0;

    if (condition) {
      valueToAssign = 42;
    }

    System.out.println("The value after assignment is: " + valueToAssign);
  }
}
