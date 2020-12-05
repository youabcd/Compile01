package miniplc0java.instruction;

import miniplc0java.analyser.SymbolEntry;
import miniplc0java.error.AnalyzeError;
import miniplc0java.error.CompileError;
import miniplc0java.error.ErrorCode;
import miniplc0java.error.ExpectedTokenError;
import miniplc0java.error.TokenizeError;
import miniplc0java.instruction.Instruction;
import miniplc0java.instruction.Operation;
import miniplc0java.tokenizer.Token;
import miniplc0java.tokenizer.TokenType;
import miniplc0java.tokenizer.Tokenizer;
import miniplc0java.util.Pos;

import java.util.*;

public class FunctionList {
    private String funcName;//函数名
    private String funcType;//函数返回值类别
    private ArrayList<Token> funcParams=new ArrayList<Token>();//函数传入值
    private int begin;//函数操作开始位置（instruction中的位置）
    private int end;//函数返回位置（instruction中的位置）
    HashMap<String, SymbolEntry> symbolTable = new HashMap<>();
    int nextOffset = 0;

    private void addSymbol(String name, boolean isInitialized, boolean isConstant, String type, Pos curPos) throws AnalyzeError {
        if (this.symbolTable.get(name) != null) {
            throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
        }
        else {
            this.symbolTable.put(name, new SymbolEntry(isConstant, isInitialized, type, getNextVariableOffset()));
        }
    }

    private void removeSymbol(String name,Pos curPos) throws AnalyzeError{//删除某个元素
        if(this.symbolTable.get(name)==null){
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        }
        else{
            this.symbolTable.remove(name);
        }
    }

    private int getNextVariableOffset() {
        return this.nextOffset++;
    }

    private void declareSymbol(String name, Pos curPos) throws AnalyzeError {
        var entry = this.symbolTable.get(name);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        }
        else {
            entry.setInitialized(true);
        }
    }

    private int getOffset(String name, Pos curPos) throws AnalyzeError {
        var entry = this.symbolTable.get(name);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            return entry.getStackOffset();
        }
    }

    private boolean isConstant(String name, Pos curPos) throws AnalyzeError {
        var entry = this.symbolTable.get(name);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            return entry.isConstant();
        }
    }

    public FunctionList(String name,String type,ArrayList<Token> t){
        this.funcName=name;
        this.funcType=type;
        this.funcParams=t;
    }
    public FunctionList(String name,String type,ArrayList<Token> t,int begin){
        this.funcName=name;
        this.funcType=type;
        this.funcParams=t;
        this.begin=begin;
    }
    public FunctionList(String name,String type,ArrayList<Token> t,int begin,int end){
        this.funcName=name;
        this.funcType=type;
        this.funcParams=t;
        this.begin=begin;
        this.end=end;
    }

    public void setFuncName(String name){
        this.funcName=name;
    }
    public void setFuncType(String type){
        this.funcType=type;
    }
    public void setFuncParams(ArrayList<Token> t){
        this.funcParams=t;
    }
    public void setBegin(int begin){
        this.begin=begin;
    }
    public void setEnd(int end){
        this.end=end;
    }

    public String getFuncName(){
        return this.funcName;
    }
    public String getFuncType(){
        return this.funcType;
    }
    public ArrayList<Token> getFuncParams(){
        return this.funcParams;
    }
    public int getBegin(){
        return this.begin;
    }
    public int getEnd(){
        return this.end;
    }
}
