package com.yourorganization.maven_sample;

import java.nio.file.Paths;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;

public class ObliviousTranspiler {
  public static void main(String[] args) {
    // JavaParser has a minimal logging class that normally logs nothing.
    // Let's ask it to write to standard out:
    Log.setAdapter(new Log.StandardOutStandardErrorAdapter());

    // SourceRoot is a tool that read and writes Java files from packages on a
    // certain root directory.
    // In this case the root directory is found by taking the root from the current
    // Maven module,
    // with src/main/resources appended.
    SourceRoot sourceRoot = new SourceRoot(
        CodeGenerationUtils.mavenModuleRoot(ObliviousTranspiler.class).resolve("src/main/resources"));

    // Our sample is in the root of this directory, so no package name.
    CompilationUnit cu = sourceRoot.parse("", "NonOblivious.java");

    Log.info("Transpiling to oblivious!");

    cu.accept(new ModifierVisitor<Void>() {
      @Override
      public Visitable visit(IfStmt n, Void arg) {
        Expression cond = n.getCondition();
        Statement thenStmt = n.getThenStmt().clone();
        if (thenStmt.isBlockStmt()) {
          BlockStmt blockStmt = thenStmt.asBlockStmt();
          NodeList<Statement> statements = blockStmt.getStatements();

          if (statements.size() == 1 && statements.get(0) instanceof ExpressionStmt) {
            ExpressionStmt expressionStmt = (ExpressionStmt) statements.get(0);

            if (expressionStmt.getExpression() instanceof AssignExpr) {
              AssignExpr assignExpr = (AssignExpr) expressionStmt.getExpression();
              Expression lhs = assignExpr.getTarget();
              Expression rhs = assignExpr.getValue();
              if (lhs instanceof NameExpr && rhs instanceof IntegerLiteralExpr) {
                Expression obliviousTernary = new BinaryExpr(new BinaryExpr(cond, rhs, BinaryExpr.Operator.BINARY_AND),
                    new BinaryExpr(cond, lhs, BinaryExpr.Operator.BINARY_AND), BinaryExpr.Operator.BINARY_OR);

                ExpressionStmt newStatment = new ExpressionStmt(
                    new AssignExpr(lhs, obliviousTernary, AssignExpr.Operator.ASSIGN));

                n.replace(newStatment);
              } else {
                System.out.println("lhs is not an rhs is not an IntegerLiteralExpr.");
              }

            } else {
              System.out.println("The thenStmt does not contain exactly one assignment.");
            }
          }
        } else {
          System.out.println("thenStmt is not a BlockStmt.");
        }

        return super.visit(n, arg);
      }
    }, null);

    // This saves all the files we just read to an output directory.
    sourceRoot.saveAll(
        // The path of the Maven module/project which contains the LogicPositivizer
        // class.
        CodeGenerationUtils.mavenModuleRoot(ObliviousTranspiler.class)
            // appended with a path to "output"
            .resolve(Paths.get("output")));
  }

}
