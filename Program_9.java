// Kat Calcagno
// CMPS 1617
// 11/11/2026
// Program 9

//Typing negative numbers doesn't work

import java.util.Scanner;
import java.util.ArrayList;

public class Program_9{
    //classses
    static public class Operation {
        @FunctionalInterface
        interface Function {
            double x(double x, double y);
        }

        public String symbol = "";
        public int priority = 0;
        public int argument_count = 0;
        public Function fn;

        public String desc;

        public Operation(Function _fn, String _symbol, int _priority, int _argument_count, String _desc){
            symbol = _symbol;
            priority = _priority;
            argument_count = _argument_count;
            fn = _fn;
            desc = _desc;
        }

        public void println(){
            System.out.println(symbol+": "+desc);
        }
    }


    //clac object
    static public class Calculator {
        public Operation[] operations;
        public Calculator(){
            operations = new Operation[]{ 
                new Operation((x, y) -> {return x + y;}, "+", 0, 2, "Addition (x + y)"),
                new Operation((x, y) -> {return x - y;}, "-", 0, 2, "Subtraction (x - y)"),
                new Operation((x, y) -> {return x * y;}, "*", 1, 2, "Multiply (x * y)"),
                new Operation((x, y) -> {if(y==0){System.out.println("Error: Division by 0."); System.exit(-1);} return x / y;}, "/", 1, 2, "Divide (x / y)"),
                new Operation((x, y) -> {if(y==0){System.out.println("Error: Division by 0."); System.exit(-1);} return x % y;}, "%", 1, 2, "Modulous (x % y)"),
                new Operation((x, y) -> {return Math.pow(x, y);}, "^", 3, 2, "Exponent (x ^ y)"),
                new Operation((x, y) -> {return (x+y)/2;}, "A", 1, 2, "Average (x A y)"),
                new Operation((x, y) -> {return Math.sqrt(x*x + y*y);}, "H", 0, 2, "Hypotenuse (x H y)"),
                new Operation((x, y) -> {return Math.sin(y);}, "sin", 3, 1, "Sine (sin x)"),
                new Operation((x, y) -> {return Math.cos(y);}, "cos", 3, 1, "Cosine (cos x)"),
                new Operation((x, y) -> {return Math.tan(y);}, "tan", 3, 1, "Tangent (tan x)"),
                new Operation((x, y) -> {return Math.log(y);}, "ln", 3, 1, "Natural Log (ln x)"),
                new Operation((x, y) -> {return Math.log(y)/Math.log(x);}, "log", 3, 2, "Base Log (b log x)"),
                new Operation((x, y) -> {if(y<0){System.out.println("Error: Sqrt of negative number."); System.exit(-1);} return Math.sqrt(y);}, "S", 3, 1, "Square Root (S x)"),
                new Operation((x, y) -> {return Math.max(x, y);}, "max", 1, 2, "Maximum (x max y)"),
                new Operation((x, y) -> {return Math.min(x, y);}, "min", 1, 2, "Minimum (x min y)"),
            };
        }
        private int match(String sym){
            for(int i = 0; i < operations.length; i++) if( operations[i].symbol.equalsIgnoreCase(sym) ) return i;
            return -1;
        }

        private int find_argument(ArrayList<String> tokens, int index){
            String sym = tokens.get(index);
            if(sym.equals("(")){
                int block_counter = 1;
                for(int i = index+1; i < tokens.size(); i++){
                    if(tokens.get(i).equals("(")) block_counter++;
                    if(tokens.get(i).equals(")")) block_counter--;
                    if(block_counter==0) return i;
                }
            }else if(sym.equals(")")){
                int block_counter = 1;
                for(int i = index-1; i >= 0; i--){
                    if(tokens.get(i).equals(")")) block_counter++;
                    if(tokens.get(i).equals("(")) block_counter--;
                    if(block_counter==0) return i;
                }
            }
            return index;   
        }

        private boolean isNumber(String s){
            for(int i = 0; i < s.length(); i++){
                char c = s.charAt(i);
                if((c < '0' || c > '9')&&(c != '.')) return false;
            }
            return true;
        }

        private ArrayList<String> validate(ArrayList<String> tokens){
            int block_counter = 0;
            for (int i = 0; i < tokens.size(); i++){
                String sym = tokens.get(i);
                if(sym.equals("(")) block_counter++;
                else if(sym.equals(")")) block_counter--;
                else if(isNumber(sym)) continue;
                else if(match(sym)==-1) {
                    // break up if multiple ops together
                    // throw error if not
                    String op1 = "";
                    int j = 0;
                    for(; j < sym.length(); j++){ 
                        op1+=sym.charAt(j);
                        if(match(op1)!=-1||op1.equals("(")||op1.equals(")"))break;
                    }
                    if(op1.length()==sym.length()){System.out.println("Invalid symbol of '"+sym+"'."); System.exit(-1);}
                    String op2 = sym.substring(j+1, sym.length());
                    tokens.set(i, op1);
                    tokens.add(i+1, op2);
                    i--;
                }
            }
            if(block_counter!=0){System.out.println("Incorrect paren blocks."); System.exit(-1);}
            return tokens;
        }

        private ArrayList<String> paren_priority(ArrayList<String> tokens){
            ArrayList<String> final_tokens = new ArrayList<>();
            for (String s : tokens) final_tokens.add(s);

            // get highest priority 
            int highest_priority = -2;
            for (int i = 0; i < final_tokens.size(); i++){
                String sym = final_tokens.get(i);
                int opid = match(sym);
                if(opid != -1){
                    Operation op = operations[opid];
                    if(op.priority > highest_priority) highest_priority = op.priority;
                }
            }

            // go through priorities and paren
            for(int pri = highest_priority; pri >= 0; pri--){
                for (int i = 0; i < final_tokens.size(); i++){
                    String sym = final_tokens.get(i);
                    int opid = match(sym);
                    if(opid == -1) continue;
                    Operation op = operations[opid];
                    if(op.argument_count == 2 && op.priority==pri){
                        int left = find_argument(final_tokens, i-1);
                        int right = find_argument(final_tokens, i+1)+1;
                        final_tokens.add(right, ")");
                        final_tokens.add(left, "(");
                        i++;
                    } 
                    if(op.argument_count == 1 && op.priority==pri){ 
                        int left = find_argument(final_tokens, i);
                        int right = find_argument(final_tokens, i+1)+1;
                        final_tokens.add(right, ")");
                        final_tokens.add(left, "(");
                        i++;
                    }
                }
            }

            return final_tokens; //add parenthetical
        }

        //simple tokenizer
        private ArrayList<String> tokenize(String eq){
            String[] initial_tokens = eq.split(" ");
            ArrayList<String> final_tokens = new ArrayList<>();
            String current_token = "";
            for (String s : initial_tokens){
                if (!current_token.equals("")){ 
                    final_tokens.add(current_token); 
                    current_token = "";
                }
                boolean isnumber = true;
                for (int i = 0; i < s.length(); i++){
                    char c = s.charAt(i);
                    if((c == '.')||(c >= '0' && c <= '9')){
                        if (!isnumber&&!current_token.equals("")){ 
                            final_tokens.add(current_token); 
                            current_token = "";
                        }
                        current_token+=c;
                        isnumber = true;
                    } else{
                        if (isnumber&&!current_token.equals("")){ 
                            final_tokens.add(current_token); 
                            current_token = "";
                        }
                        current_token+=c;
                        isnumber = false;
                    }
                }
            }
            if (!current_token.equals("")){ 
                final_tokens.add(current_token); 
            }

            return final_tokens;
        }

        private String evaluate_simple_exp(ArrayList<String> tokens, int start, int end){
            if(start == end){
                return tokens.get(start);
            }
            int opid = match(tokens.get(end-1));
            Operation op = operations[opid];
            if(op.argument_count == 2){
                double x = Double.valueOf(tokens.get(start));
                double y = Double.valueOf(tokens.get(end));
                double z = op.fn.x(x, y);
                return Double.toString(z);
            }
            else if (op.argument_count == 1){
                double x = 0;
                double y = Double.valueOf(tokens.get(end));
                double z = op.fn.x(x, y);
                return Double.toString(z);
            }
            return "ERROR";
        }
        
        public double execute(String eq){
            ArrayList<String> cleaned_eq = paren_priority(validate(tokenize(eq)));

            // evaluate
            int open_paren = -1;
            for(int i = 0; i < cleaned_eq.size(); i++){
                String sym = cleaned_eq.get(i);
                if(sym.equals("(")){
                    open_paren = i;
                } else if(sym.equals(")")){
                    String number = evaluate_simple_exp(cleaned_eq, open_paren+1, i-1);
                    cleaned_eq.subList(open_paren, i+1).clear();
                    cleaned_eq.add(open_paren, number);
                    i = -1;
                }
            }
            return Double.valueOf(cleaned_eq.get(0));
        }
    }


    //methods
    public static void print_intro(Calculator calc){
        System.out.println("=== HONORS CALCULATOR MENU ===");
        for(Operation op : calc.operations){
            op.println();
        }
        System.out.println("To exit the program, type 'E'.\n\nNOTE: This calculator does not support negative numbers.\n\tAs differentiation of the same symbol to have different\n\tmeanings based on context, is not in the scope of this project.\n");
    }

    public static void main(String[] args){
        Calculator calc = new Calculator();
        Scanner scan = new Scanner(System.in);

        print_intro(calc);
        while(true){
            System.out.print("Type a valid expression: ");
            String expression = scan.nextLine();
            if (expression.trim().equalsIgnoreCase("E")){
                System.out.println("Thank you for using the program!");
                System.exit(0);
            }
            if(!expression.equals("")) System.out.println("Evaluated to be " + calc.execute(expression));
        }
    }
}