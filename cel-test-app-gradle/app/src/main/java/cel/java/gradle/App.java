package cel.java.gradle;

import dev.cel.common.CelAbstractSyntaxTree;
import dev.cel.common.CelProtoAbstractSyntaxTree;
import dev.cel.common.ast.CelConstant;
import dev.cel.common.ast.CelExpr;
import dev.cel.common.ast.CelExpr.CelList;
import dev.cel.expr.CheckedExpr;
import dev.cel.runtime.CelRuntime;
import dev.cel.runtime.CelRuntimeFactory;
import java.io.FileInputStream;
import java.util.Map;

public class App {
  private static final CelRuntime CEL_RUNTIME = CelRuntimeFactory.standardCelRuntimeBuilder().build();

  public static void main(String[] args) throws Exception {
    System.out.println("hi");
    // CelAbstractSyntaxTree ast = CEL_COMPILER.compile("my_var in ['H','O']").getAst();
    CelExpr listExpr = CelExpr.newBuilder().setList(CelList.newBuilder().addElements(CelExpr.ofConstant(1L, CelConstant.ofValue(2L))).build()).build();
    System.out.println(listExpr);
    CheckedExpr checkedExpr;
    try (FileInputStream fis = new FileInputStream("checkedExpr.bin")) {
      checkedExpr = CheckedExpr.parseFrom(fis);
    }

    CelAbstractSyntaxTree ast = CelProtoAbstractSyntaxTree.fromCheckedExpr(checkedExpr).getAst();
    CelRuntime.Program program = CEL_RUNTIME.createProgram(ast);

    Boolean result = (Boolean) program.eval(Map.of("my_var", "H"));
    System.out.println(result);
  }
}
