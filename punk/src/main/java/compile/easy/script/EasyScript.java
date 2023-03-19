package compile.easy.script;

import compile.easy.parser.ASTNode;
import compile.easy.parser.ASTNodeType;
import compile.easy.parser.EasyParser;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class EasyScript {

    private final Map<String, Integer> variables = new HashMap<>();
    private static boolean verbose = false;

    public static void main(String[] args) {
        if (args.length > 0 && StringUtils.equals(args[0], "-v")) {
            verbose = true;
            System.out.println("Enter verbose mode");
        }
        System.out.println("EasyScript language...");
        prompt();

        EasyScript script = new EasyScript();
        EasyParser parser = new EasyParser();
        StringBuilder scriptText = new StringBuilder("");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            try {
                String line = reader.readLine();

                if (StringUtils.isBlank(line)) {
                    prompt();
                    continue;
                }

                line = line.trim();
                scriptText.append(line).append("\n");

                if (StringUtils.equals(line, "exit();")) {
                    System.out.println("Bye...");
                    break;
                }

                if (line.endsWith(";")) {
                    ASTNode node = parser.parse(scriptText.toString());

                    if (verbose) {
                        parser.dump(node, "");
                    }

                    script.evaluate(node, "");
                    scriptText = new StringBuilder();
                }
                prompt();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println(ex.getLocalizedMessage());
                prompt();
                scriptText = new StringBuilder("");
            }
        }
    }

    private static void prompt() {
        System.out.print("> ");
    }

    public Integer evaluate(ASTNode node, String indent) throws Exception {
        int result = 0;

        if (verbose) {
            System.out.println(indent + "calculating: " + node.getType());
        }

        switch (node.getType()) {
            case Program:
                for (ASTNode child : node.getChildren()) {
                    result = evaluate(child, indent + "\t");
                }
                break;
            case Additive:
                Integer v1 = evaluate(node.getChildren().get(0), indent + "\t");
                Integer v2 = evaluate(node.getChildren().get(1), indent + "\t");

                if (StringUtils.equals(node.getText(), "+")) {
                    result = v1 + v2;
                } else {
                    result = v1 - v2;
                }
                break;
            case Multiplicative:
                v1 = evaluate(node.getChildren().get(0), indent + "\t");
                v2 = evaluate(node.getChildren().get(1), indent + "\t");

                if (StringUtils.equals(node.getText(), "*")) {
                    result = v1 * v2;
                } else {
                    result = v1 / v2;
                }
                break;
            case IntLiteral:
                result = Integer.parseInt(node.getText());
                break;
            case Identifier:
                String varName = node.getText();

                if (!variables.containsKey(varName)) {
                    throw new Exception("unknown variable: " + varName);
                }

                if (variables.get(varName) == null) {
                    throw new Exception("variable " + varName + " has not been initialized");
                }
                result = variables.get(varName);
                break;
            case Unary:
                String operator = node.getText();

                if (StringUtils.equals(operator, "+")) {
                    result = evaluate(node.getChildren().get(0), indent + "\t");
                } else {
                    result = -evaluate(node.getChildren().get(0), indent + "\t");
                }
                break;
            case IntDeclaration:
                varName = node.getText();

                if (node.getChildren().size() > 0) {
                    ASTNode child = node.getChildren().get(0);
                    result = evaluate(child, indent + "\t");
                    variables.put(varName, result);
                } else {
                    variables.put(varName, null);
                }
                break;
            case AssignmentStmt:
                varName = node.getChildren().get(0).getText();

                if (variables.containsKey(varName)) {
                    ASTNode child = node.getChildren().get(1);
                    result = evaluate(child, indent + '\t');
                    variables.put(varName, result);
                } else {
                    throw new Exception("unknown variable: " + varName);
                }
                break;
        }

        if (verbose) {
            System.out.println(indent + "result: " + result);
        } else if (StringUtils.equals(indent, "")) {
            if (node.getType() == ASTNodeType.IntDeclaration || node.getType() == ASTNodeType.AssignmentStmt) {
                System.out.println(node.getText() + ": " + result);
            } else if (node.getType() != ASTNodeType.Program) {
                System.out.println(result);
            }
        }

        return result;
    }
}
