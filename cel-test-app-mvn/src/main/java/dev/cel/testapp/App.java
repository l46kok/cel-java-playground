package dev.cel.testapp;

import dev.cel.common.CelProtoAbstractSyntaxTree;
import dev.cel.common.CelProtoV1Alpha1AbstractSyntaxTree;
import dev.cel.common.types.CelTypes;
import dev.cel.common.types.SimpleType;
import dev.cel.expr.CheckedExpr;
import dev.cel.expr.Constant;
import dev.cel.expr.Expr;
import dev.cel.expr.SourceInfo;
import dev.cel.runtime.CelRuntime;
import dev.cel.runtime.CelRuntimeFactory;

/** Hello world! */
public class App {
  public static void main(String[] args) throws Exception {
    evaluateWithCanonicalCelProto();
    evaluateWithDeprecatedV1Alpha1Proto();
  }

  private static void evaluateWithCanonicalCelProto() throws Exception {
    // Manually constructed checkedExpr.
    // In the real world, this should be produced by a compiler (go, cpp or in Java when made
    // available)
    CheckedExpr checkedExpr =
        CheckedExpr.newBuilder()
            .putTypeMap(1, CelTypes.STRING)
            .setExpr(
                Expr.newBuilder()
                    .setConstExpr(
                        Constant.newBuilder()
                            .setStringValue("Hello world! (With canonical protos)")
                            .build())
                    .setId(1)
                    .build())
            .setSourceInfo(
                SourceInfo.newBuilder()
                    .setLocation("<input>")
                    .addLineOffsets(15)
                    .putPositions(1, 0)
                    .build())
            .build();

    CelRuntime celRuntime = CelRuntimeFactory.standardCelRuntimeBuilder().build();
    CelRuntime.Program program =
        celRuntime.createProgram(CelProtoAbstractSyntaxTree.fromCheckedExpr(checkedExpr).getAst());

    Object evaluatedResult = program.eval();
    System.out.println(evaluatedResult);
    System.out.println(SimpleType.INT);
  }

  private static void evaluateWithDeprecatedV1Alpha1Proto() throws Exception {
    // Manually constructed checkedExpr.
    // In the real world, this should be produced by a compiler (go, cpp or in Java when made
    // available)
    com.google.api.expr.v1alpha1.CheckedExpr checkedExpr =
        com.google.api.expr.v1alpha1.CheckedExpr.newBuilder()
            .putTypeMap(
                1,
                com.google.api.expr.v1alpha1.Type.newBuilder()
                    .setPrimitive(com.google.api.expr.v1alpha1.Type.PrimitiveType.STRING)
                    .build())
            .setExpr(
                com.google.api.expr.v1alpha1.Expr.newBuilder()
                    .setConstExpr(
                        com.google.api.expr.v1alpha1.Constant.newBuilder()
                            .setStringValue("Hello world! (With v1alpha1 protos)")
                            .build())
                    .setId(1)
                    .build())
            .setSourceInfo(
                com.google.api.expr.v1alpha1.SourceInfo.newBuilder()
                    .setLocation("<input>")
                    .addLineOffsets(15)
                    .putPositions(1, 0)
                    .build())
            .build();

    CelRuntime celRuntime = CelRuntimeFactory.standardCelRuntimeBuilder().build();
    CelRuntime.Program program =
        celRuntime.createProgram(
            CelProtoV1Alpha1AbstractSyntaxTree.fromCheckedExpr(checkedExpr).getAst());

    Object evaluatedResult = program.eval();
    System.out.println(evaluatedResult);
    System.out.println(SimpleType.INT);
  }
}
