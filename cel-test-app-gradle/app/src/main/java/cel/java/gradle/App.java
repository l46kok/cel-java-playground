package cel.java.gradle;

import dev.cel.common.CelAbstractSyntaxTree;
import dev.cel.common.CelValidationException;
import dev.cel.common.types.SimpleType;
import dev.cel.compiler.CelCompiler;
import dev.cel.compiler.CelCompilerFactory;
import dev.cel.parser.CelUnparserFactory;
import dev.cel.runtime.CelEvaluationException;
import dev.cel.runtime.CelRuntime;
import dev.cel.runtime.CelRuntimeFactory;
import dev.cel.optimizer.CelOptimizationException;
import dev.cel.optimizer.CelOptimizerFactory;
import dev.cel.optimizer.optimizers.ConstantFoldingOptimizer;
import dev.cel.policy.CelPolicy;
import dev.cel.policy.CelPolicyParser;
import dev.cel.policy.CelPolicyParserFactory;
import dev.cel.policy.CelPolicyValidationException;
import dev.cel.policy.CelPolicyCompiler;
import dev.cel.policy.CelPolicyCompilerFactory;
import java.util.Map;

public class App {
  // Construct the compilation and runtime environments.
  // These instances are immutable and thus trivially thread-safe and amenable to
  // caching.
  private static final CelCompiler CEL_COMPILER = CelCompilerFactory.standardCelCompilerBuilder()
      .addVar("my_var", SimpleType.STRING).build();
  private static final CelRuntime CEL_RUNTIME = CelRuntimeFactory.standardCelRuntimeBuilder().build();

  private static final CelPolicyParser CEL_POLICY_PARSER = CelPolicyParserFactory.newYamlParserBuilder().build();
  private static final CelPolicyCompiler CEL_POLICY_COMPILER = CelPolicyCompilerFactory
      .newPolicyCompiler(CEL_COMPILER, CEL_RUNTIME).build();

  public static void main(String[] args) throws Exception {
    System.out.println("hi");
    CelAbstractSyntaxTree ast = CEL_COMPILER.compile("my_var in ['H','O']").getAst();

    var celOptimizer = CelOptimizerFactory.standardCelOptimizerBuilder(CEL_COMPILER, CEL_RUNTIME)
        .addAstOptimizers(

            ConstantFoldingOptimizer.getInstance())
        .build();

    ast = celOptimizer.optimize(ast);

    CelRuntime.Program program = CEL_RUNTIME.createProgram(ast);

    Boolean result = (Boolean) program.eval(Map.of("my_var", "H"));
    System.out.println(result);

    String policyOutput = executePolicy();
    System.out.println("Policy output: " + policyOutput);
  }

  private static String executePolicy() throws Exception {
    String policyDoc = """
         name: "context_pb"
         rule:
           match:
             - output: "'hello world'"
        """;
    CelPolicy celPolicy = CEL_POLICY_PARSER.parse(policyDoc);
    CelAbstractSyntaxTree ast = CEL_POLICY_COMPILER.compile(celPolicy);
    return (String) CEL_RUNTIME.createProgram(ast).eval();
  }
}
