

public class LambdaExpressions {
    public LambdaExpressions() {
    }
    
    public static void runComparatorExample() {
        Person[] persons = {new Person("Smith"), new Person("Liza"),new Person("Edward"), new Person("John")};

        Arrays.sort(persons, new Comparator<Person>() {

            @Override
            public int compare(Person first, Person second) {
                return first.getName().compareTo(second.getName());
            }
        });

        // it’s a standard sort but interestingly rather than passing Comparator object, its taking a lambda expression
        Arrays.sort(persons,(first, second) -> first.getName().compareTo(second.getName()));
    }

    public static void runRunnableExample() {
        Runnable printer = new Runnable() {
            @Override
            public void run() {
                System.out.println("Hello inside runnable class...");
            }
        };

        printer.run();

        printer = () -> System.out.println("Hello inside runnable lambda...");

        printer.run();
    }
	
	public static void runOperatorExample() {
        Operator<Integer> addition = (op1, op2) -> op1 + op2;

        System.out.println("Addition result: " + addition.operate(2, 3));
    }

    public static void runOperatorExample2() {
         GenericOperator<Integer> multiply = numbers -> {
            int result = 0;
            for(int num : numbers)
                result *= num;
            return result;
        };

        System.out.println("Multiplication result: " + multiply.operate(2,3,4));
    }
    
}