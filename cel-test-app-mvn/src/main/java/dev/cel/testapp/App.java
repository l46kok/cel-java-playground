package dev.cel.testapp;

import dev.cel.common.CelAbstractSyntaxTree;
import dev.cel.common.CelValidationException;
import dev.cel.common.types.SimpleType;
import dev.cel.compiler.CelCompiler;
import dev.cel.compiler.CelCompilerFactory;
import dev.cel.runtime.CelEvaluationException;
import dev.cel.runtime.CelRuntime;
import dev.cel.runtime.CelRuntimeFactory;
import dev.cel.parser.CelUnparserFactory;
import dev.cel.optimizer.CelOptimizationException;
import dev.cel.optimizer.CelOptimizer;
import dev.cel.optimizer.CelOptimizerFactory;
import dev.cel.optimizer.optimizers.ConstantFoldingOptimizer;
import dev.cel.validator.CelValidator;
import dev.cel.validator.CelValidatorFactory;
import dev.cel.validator.validators.TimestampLiteralValidator;
import java.util.Map;

/** Hello world! */
public class App {
  // Construct the compilation and runtime environments.
  // These instances are immutable and thus trivially thread-safe and amenable to caching.
  private static final CelCompiler CEL_COMPILER =
      CelCompilerFactory.standardCelCompilerBuilder().addVar("my_var", SimpleType.STRING).build();
  private static final CelRuntime CEL_RUNTIME =
      CelRuntimeFactory.standardCelRuntimeBuilder().build();
  private static final CelValidator CEL_VALIDATOR =
      CelValidatorFactory.standardCelValidatorBuilder(CEL_COMPILER, CEL_RUNTIME)
          .addAstValidators(TimestampLiteralValidator.INSTANCE)
          .build();
  private static final CelOptimizer CEL_OPTIMIZER =
    CelOptimizerFactory.standardCelOptimizerBuilder(CEL_COMPILER, CEL_RUNTIME)
        .addAstOptimizers(ConstantFoldingOptimizer.getInstance())
        .build();

  public static void main(String[] args) throws Exception {
    // Compile the expression into an Abstract Syntax Tree.
    CelAbstractSyntaxTree ast = CEL_COMPILER.compile("my_var + '!'").getAst();

    // Plan an executable program instance.
    CelRuntime.Program program = CEL_RUNTIME.createProgram(ast);

    // Evaluate the program with an input variable.
    String result = (String) program.eval(Map.of("my_var", "Hello World"));
    System.out.println(result); // 'Hello World!'

    // Validate/Optimize
    System.out.println("Validation result: " + CEL_VALIDATOR.validate(ast).hasError());
    ast = CEL_OPTIMIZER.optimize(ast);

    // Unparse
    System.out.println("Unparsed: " + CelUnparserFactory.newUnparser().unparse(ast));
  }
}
