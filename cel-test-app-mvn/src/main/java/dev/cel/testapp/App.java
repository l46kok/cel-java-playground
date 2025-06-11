package dev.cel.testapp;

import dev.cel.common.types.CelTypes;
import dev.cel.common.CelAbstractSyntaxTree;
import dev.cel.common.CelProtoAbstractSyntaxTree;
import dev.cel.expr.CheckedExpr;
import dev.cel.expr.Constant;
import dev.cel.expr.Expr;
import dev.cel.runtime.CelLiteRuntime;
import dev.cel.runtime.CelLiteRuntimeFactory;
import dev.cel.runtime.CelStandardFunctions;
import java.util.Map;

/** Hello world! */
public class App {

  private static final CelLiteRuntime CEL_RUNTIME = CelLiteRuntimeFactory.newLiteRuntimeBuilder()
      .setStandardFunctions(CelStandardFunctions.ALL_STANDARD_FUNCTIONS)
      .build();

  public static void main(String[] args) throws Exception {
    // Manually constructed checkedExpr.
    // In the real world, this should be produced by a compiler (go, cpp or in Java)
    CheckedExpr checkedExpr = CheckedExpr.newBuilder()
        .putTypeMap(1, dev.cel.expr.Type.newBuilder().setPrimitive(dev.cel.expr.Type.PrimitiveType.STRING).build())
        .setExpr(
            Expr.newBuilder()
                .setConstExpr(Constant.newBuilder().setStringValue("Hello world!").build())
                .setId(1)
                .build())
        .setSourceInfo(
            dev.cel.expr.SourceInfo.newBuilder()
                .setLocation("<input>")
                .addLineOffsets(15)
                .putPositions(1, 0)
                .build())
        .build();

    CelAbstractSyntaxTree ast = CelProtoAbstractSyntaxTree.fromCheckedExpr(checkedExpr).getAst();

    Object result = CEL_RUNTIME.createProgram(ast).eval();

    System.out.println(result);
  }
}
