package miniplc0java.analyser;

import miniplc0java.error.AnalyzeError;
import miniplc0java.error.CompileError;
import miniplc0java.error.ErrorCode;
import miniplc0java.error.ExpectedTokenError;
import miniplc0java.error.TokenizeError;
import miniplc0java.instruction.FunctionList;
import miniplc0java.instruction.Instruction;
import miniplc0java.instruction.Operation;
import miniplc0java.tokenizer.Token;
import miniplc0java.tokenizer.TokenType;
import miniplc0java.tokenizer.Tokenizer;
import miniplc0java.util.Pos;

import java.util.*;

public final class Analyser {

    Tokenizer tokenizer;
    ArrayList<Instruction> instructions;
    ArrayList<Token> temporaryTable;

    /** 当前偷看的 token */
    Token peekedToken = null;

    /** 符号表 */
    HashMap<String, SymbolEntry> symbolTable = new HashMap<>();
    HashMap<String, FunctionList> funcTable=new HashMap<>();

    /** 下一个变量的栈偏移 */
    int nextOffset = 0;

    public Analyser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        this.instructions = new ArrayList<>();
    }

    public List<Instruction> analyse() throws CompileError {
        analyseProgram();
        return instructions;
    }

    /**
     * 查看下一个 Token
     * 
     * @return
     * @throws TokenizeError
     */
    private Token peek() throws TokenizeError {
        if (peekedToken == null) {
            peekedToken = tokenizer.nextToken();
        }
        return peekedToken;
    }

    /**
     * 获取下一个 Token
     * 
     * @return
     * @throws TokenizeError
     */
    private Token next() throws TokenizeError {
        if (peekedToken != null) {
            var token = peekedToken;
            peekedToken = null;
            return token;
        } else {
            return tokenizer.nextToken();
        }
    }

    /**
     * 如果下一个 token 的类型是 tt，则返回 true
     * 
     * @param tt
     * @return
     * @throws TokenizeError
     */
    private boolean check(TokenType tt) throws TokenizeError {
        var token = peek();
        return token.getTokenType() == tt;
    }

    /**
     * 如果下一个 token 的类型是 tt，则前进一个 token 并返回这个 token
     * 
     * @param tt 类型
     * @return 如果匹配则返回这个 token，否则返回 null
     * @throws TokenizeError
     */
    private Token nextIf(TokenType tt) throws TokenizeError {
        var token = peek();
        if (token.getTokenType() == tt) {
            return next();
        } else {
            return null;
        }
    }

    /**
     * 如果下一个 token 的类型是 tt，则前进一个 token 并返回，否则抛出异常
     * 
     * @param tt 类型
     * @return 这个 token
     * @throws CompileError 如果类型不匹配
     */
    private Token expect(TokenType tt) throws CompileError {
        var token = peek();
        if (token.getTokenType() == tt) {
            return next();
        } else {
            throw new ExpectedTokenError(tt, token);
        }
    }

    /**
     * 往符号表里添加一个符号
     *
     * @param name          名字
     * @param isInitialized 是否已赋值
     * @param isConstant    是否是常量
     * @param type          类型
     * @param curPos        当前 token 的位置（报错用）
     * @throws AnalyzeError 如果重复定义了则抛异常
     */
    private void addSymbol(String name, boolean isInitialized, boolean isConstant, String type, Pos curPos) throws AnalyzeError {
        if (this.symbolTable.get(name) != null) {
            throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
        }
        else {
            this.symbolTable.put(name, new SymbolEntry(isConstant, isInitialized, type, getNextVariableOffset()));
        }
    }

    private void addFunc(String name,Pos curPos, String n1,String type,ArrayList<Token> funcParams, int begin,int end) throws AnalyzeError {
        if (this.funcTable.get(name) != null) {
            throw new AnalyzeError(ErrorCode.DuplicateDeclaration,curPos);
        }
        else {
            this.funcTable.put(name, new FunctionList(n1, type, funcParams,begin,end));
        }
    }

    private int getFuncBegin(String name){
        return this.funcTable.get(name).getBegin();
    }

    private int getFuncEnd(String name){
        return this.funcTable.get(name).getEnd();
    }

    private void setFuncEnd(String name,int end){
        this.funcTable.get(name).setEnd(end);
    }

    /**
     * 获取下一个变量的栈偏移
     *
     * @return
     */
    private int getNextVariableOffset() {
        return this.nextOffset++;
    }

    /**
     * 设置符号为已赋值
     * 
     * @param name   符号名称
     * @param curPos 当前位置（报错用）
     * @throws AnalyzeError 如果未定义则抛异常
     */
    private void declareSymbol(String name, Pos curPos) throws AnalyzeError {
        var entry = this.symbolTable.get(name);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        }
        else {
            entry.setInitialized(true);
        }
    }

    /**
     * 获取变量在栈上的偏移
     * 
     * @param name   符号名
     * @param curPos 当前位置（报错用）
     * @return 栈偏移
     * @throws AnalyzeError
     */
    private int getOffset(String name, Pos curPos) throws AnalyzeError {
        var entry = this.symbolTable.get(name);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            return entry.getStackOffset();
        }
    }

    /**
     * 获取变量是否是常量
     * 
     * @param name   符号名
     * @param curPos 当前位置（报错用）
     * @return 是否为常量
     * @throws AnalyzeError
     */
    private boolean isConstant(String name, Pos curPos) throws AnalyzeError {
        var entry = this.symbolTable.get(name);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            return entry.isConstant();
        }
    }

    /*获取变量的类型*/
    private String whichType(String name,Pos curPos) throws AnalyzeError{
        var entry = this.symbolTable.get(name);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            return entry.type;
        }
    }

    /*是否已定义*/
    private boolean isInitialized(String name,Pos curPos) throws AnalyzeError{
        var entry = this.symbolTable.get(name);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            return entry.isInitialized;
        }
    }

    /**
     * <程序> ::= 'begin'<主过程>'end'
     */
    private void analyseProgram() throws CompileError {
        // 示例函数，示例如何调用子程序
        // 'begin'
        while(!check(TokenType.EOF)) {
            if (check(TokenType.Fn)) {
                AnalyseFunction();
            } else {
                AnalyseStatement();
            }
        }

        // 'end'
        expect(TokenType.EOF);
    }

    /*expr ->
      operator_expr
    | negate_expr
    | assign_expr
    | as_expr
    | call_expr
    | literal_expr
    | ident_expr
    | group_expr*/
    private String AnalyseAssign() throws CompileError{
        String type="",type1="";
        Token t=peek();
        type=AnalyseCmp();
        if(check(TokenType.Assign)){
            if(t.getTokenType()==TokenType.Ident){
                instructions.remove(instructions.size()-1);
                if(isConstant(t.getValueString(),t.getStartPos())&&isInitialized(t.getValueString(),t.getStartPos())){
                    throw new ExpectedTokenError(List.of(TokenType.Ident, TokenType.Uint, TokenType.LParen), next());
                }
                next();
                type1 = AnalyseCmp();
                if (!type.equals(type1)) {
                    throw new AnalyzeError(ErrorCode.NotDeclared, t.getStartPos());
                } else {
                    instructions.add(new Instruction(Operation.Store64));
                }
            }
            else{
                throw new AnalyzeError(ErrorCode.NotDeclared, t.getStartPos());
            }
            expect(TokenType.Semicolon);
        }
        return type;
    }
    private String AnalyseCmp() throws CompileError{
        String type="";
        type=AnalyseExpression();
        if(check(TokenType.Gt)||check(TokenType.Lt)||check(TokenType.Ge)||check(TokenType.Le)||check(TokenType.Eq)||check(TokenType.Neq)) {
            Token t = next();
            String type1 = AnalyseExpression();
            if (!type.equals(type1)) {
                throw new AnalyzeError(ErrorCode.NotDeclared, t.getStartPos());
            }
            if (type.equals("int")) {
                instructions.add(new Instruction(Operation.CmpI));
            }
            else if(type.equals("double")){
                instructions.add(new Instruction(Operation.CmpF));
            }
            else {
                throw new AnalyzeError(ErrorCode.NotDeclared, t.getStartPos());
            }
            if (t.getTokenType() == TokenType.Gt) {
                instructions.add(new Instruction(Operation.SetGT));
            } else if (t.getTokenType() == TokenType.Lt) {
                instructions.add(new Instruction(Operation.SetLT));
            } else if (t.getTokenType() == TokenType.Ge) {
                instructions.add(new Instruction(Operation.SetLT));
                instructions.add(new Instruction(Operation.Not));
            } else if (t.getTokenType() == TokenType.Le) {
                instructions.add(new Instruction(Operation.SetGT));
                instructions.add(new Instruction(Operation.Not));
            } else if (t.getTokenType() == TokenType.Eq) {
                instructions.add(new Instruction(Operation.Not));
            } else if (t.getTokenType() == TokenType.Neq) {
            }
        }
        return type;
    }

    private String AnalyseExpression() throws CompileError {
        String type="";
        type=AnalyseItem();
        while(check(TokenType.Minus)||check(TokenType.Plus)){
            Token t=next();
            if(type.equals("int")) {
                if (t.getTokenType()==TokenType.Plus) {
                    String type1 = AnalyseItem();
                    if(!type.equals(type1)){
                        throw new AnalyzeError(ErrorCode.NotDeclared, t.getStartPos());
                    }
                    instructions.add(new Instruction(Operation.AddI));
                } else {
                    String type1 = AnalyseItem();
                    if(!type.equals(type1)){
                        throw new AnalyzeError(ErrorCode.NotDeclared, t.getStartPos());
                    }
                    instructions.add(new Instruction(Operation.SubI));
                }
            }
            else if(type.equals("double")) {
                if (t.getTokenType()==TokenType.Plus) {
                    String type1 = AnalyseItem();
                    if(!type.equals(type1)){
                        throw new AnalyzeError(ErrorCode.NotDeclared, t.getStartPos());
                    }
                    instructions.add(new Instruction(Operation.AddF));
                } else {
                    String type1 = AnalyseItem();
                    if(!type.equals(type1)){
                        throw new AnalyzeError(ErrorCode.NotDeclared, t.getStartPos());
                    }
                    instructions.add(new Instruction(Operation.SubF));
                }
            }
            else{
                throw new AnalyzeError(ErrorCode.NotDeclared, t.getStartPos());
            }
        }
        return type;
    }

    private String AnalyseItem() throws CompileError {
        //<项>::=<因子>{(*|/)<因子>}
        String type="";
        type=AnalyseAs();
        while(check(TokenType.Mult)||check(TokenType.Div)){
            Token t=next();
            if(type.equals("int")) {
                if (t.getTokenType()==TokenType.Mult) {
                    String type1 = AnalyseItem();
                    if(!type.equals(type1)){
                        throw new AnalyzeError(ErrorCode.NotDeclared, t.getStartPos());
                    }
                    instructions.add(new Instruction(Operation.MulI));
                } else {
                    String type1 = AnalyseItem();
                    if(!type.equals(type1)){
                        throw new AnalyzeError(ErrorCode.NotDeclared, t.getStartPos());
                    }
                    instructions.add(new Instruction(Operation.DivI));
                }
            }
            else if(type.equals("double")) {
                if (t.getTokenType()==TokenType.Mult) {
                    String type1 = AnalyseItem();
                    if(!type.equals(type1)){
                        throw new AnalyzeError(ErrorCode.NotDeclared, t.getStartPos());
                    }
                    instructions.add(new Instruction(Operation.MulF));
                } else {
                    String type1 = AnalyseItem();
                    if(!type.equals(type1)){
                        throw new AnalyzeError(ErrorCode.NotDeclared, t.getStartPos());
                    }
                    instructions.add(new Instruction(Operation.DivF));
                }
            }
            else{
                throw new AnalyzeError(ErrorCode.NotDeclared, t.getStartPos());
            }
        }
        return type;
    }

    private String AnalyseAs() throws CompileError{
        String type="";
        type=AnalyseFactor();
        if(check(TokenType.As)){
            next();
            Token t=next();
            if(t.getValueString().equals("int")){
                instructions.add(new Instruction(Operation.FtoI));
                type="int";
            }
            else if(t.getValueString().equals("double")){
                instructions.add(new Instruction(Operation.ItoF));
                type="double";
            }
            else{
                throw new AnalyzeError(ErrorCode.NotDeclared, t.getStartPos());
            }
        }
        return type;
    }

    private String AnalyseFactor() throws CompileError {
        //<因子>::=Ident|Uint|(<表达式>)
        String type="";
        boolean negate;
        if (nextIf(TokenType.Minus) != null) {//负数显示为0-(Uint|Ident|<***>)
            negate = true;
            // 计算结果需要被 0 减
        }
        else {//防止a=+1;此类情况
            nextIf(TokenType.Plus);
            negate = false;
        }

        if (check(TokenType.Ident)) {
            // 调用相应的处理函数
            var a=expect(TokenType.Ident);
            if(check(TokenType.LParen)){
                next();
                instructions.add(new Instruction(Operation.StackAlloc,0));
                AnalyseAssign();
                while(check(TokenType.Comma)){
                    next();
                    AnalyseAssign();
                }
                expect(TokenType.RParen);
                instructions.add(new Instruction(Operation.Call,getOffset(a.getValueString(),a.getStartPos())));
            }
            else {
                type = whichType(a.getValueString(), a.getStartPos());
                instructions.add(new Instruction(Operation.GlobA, getOffset(a.getValueString(), a.getStartPos())));
                instructions.add(new Instruction(Operation.Load64));
            }
        }
        else if (check(TokenType.Uint)) {
            // 调用相应的处理函数
            var b=expect(TokenType.Uint);
            type="int";
            instructions.add(new Instruction(Operation.Push,Integer.valueOf(b.getValueString())));
        }
        else if(check(TokenType.Double)){
            var b=expect(TokenType.Double);
            double x=Double.parseDouble(b.getValue().toString());
            type="double";
            instructions.add(new Instruction(Operation.Push,new Double(x).longValue()));
        }
        else if(check(TokenType.Str)){
            var b=expect(TokenType.Str);
            //TODO
        }
        else if (check(TokenType.LParen)) {
            // 调用相应的处理函数
            expect(TokenType.LParen);
            type=AnalyseAssign();
            expect(TokenType.RParen);
        }
        else {
            throw new ExpectedTokenError(List.of(TokenType.Ident, TokenType.Uint, TokenType.LParen), next());
        }

        if (negate) {
            if(type.equals("int")) {
                instructions.add(new Instruction(Operation.NegI));
            }
            else if(type.equals("double")){
                instructions.add(new Instruction(Operation.NegI));
            }
            else{
                throw new ExpectedTokenError(List.of(TokenType.Ident, TokenType.Uint, TokenType.LParen), next());
            }
        }
        return type;
    }


    /*stmt ->
      expr_stmt
    | decl_stmt
    | if_stmt
    | while_stmt
    | return_stmt
    | block_stmt
    | empty_stmt*/

    private void AnalyseStatement() throws CompileError{
        if(check(TokenType.Let)){
            AnalyseLet_decl_stmt();
        }
        else if(check(TokenType.Const)){
            AnalyseConst_decl_stmt();
        }
        else if(check(TokenType.If)){
            AnalyseIf();
        }
        else if(check(TokenType.While)){
            AnalyseWhile();
        }
        else if(check(TokenType.Return)){
            AnalyseReturn();
        }
        else if(check(TokenType.Lbrace)){//代码块
            AnalyseBlock();
        }
        else if(check(TokenType.Semicolon)){
            AnalyseEmpty();
        }
        else{
            AnalyseAssign();
        }
    }

    private void AnalyseLet_decl_stmt() throws CompileError{//let声明
        expect(TokenType.Let);
        Token k=expect(TokenType.Ident);
        expect(TokenType.Colon);
        Token t=expect(TokenType.Ty);
        String type="";
        if(t.getValueString().equals("int")){
            addSymbol(k.getValueString(), false, false, "int", k.getStartPos());
            if(check(TokenType.Assign)){
                next();
                instructions.add(new Instruction(Operation.GlobA,getOffset(k.getValueString(),k.getStartPos())));
                type=AnalyseAssign();
                declareSymbol(k.getValueString(),k.getStartPos());
                if(type=="int") {
                    instructions.add(new Instruction(Operation.Store64));
                }
                else{
                    throw new AnalyzeError(ErrorCode.NotDeclared,k.getStartPos());
                }
            }
        }
        else if(t.getValueString().equals("double")){
            addSymbol(k.getValueString(), false, false, "double", k.getStartPos());
            if(check(TokenType.Assign)){
                next();
                instructions.add(new Instruction(Operation.GlobA,getOffset(k.getValueString(),k.getStartPos())));
                type=AnalyseAssign();
                if(type=="double") {
                    instructions.add(new Instruction(Operation.Store64));
                }
                else{
                    throw new AnalyzeError(ErrorCode.NotDeclared,k.getStartPos());
                }
            }
        }
        else{
            throw new AnalyzeError(ErrorCode.NotDeclared, t.getStartPos());
        }
        expect(TokenType.Semicolon);
    }

    private void AnalyseConst_decl_stmt() throws CompileError{
        String type="";
        expect(TokenType.Const);
        Token k=expect(TokenType.Ident);
        expect(TokenType.Colon);
        Token t=expect(TokenType.Ty);
        expect(TokenType.Assign);
        if(t.getValueString().equals("int")){
            addSymbol(k.getValueString(), false, true, "int", k.getStartPos());
            instructions.add(new Instruction(Operation.GlobA,getOffset(k.getValueString(),k.getStartPos())));//取出地址
            type=AnalyseAssign();
            if(type=="int") {
                instructions.add(new Instruction(Operation.Store64));//存贮数据
            }
            else{
                throw new AnalyzeError(ErrorCode.NotDeclared,k.getStartPos());
            }
        }
        else if(t.getValueString().equals("double")){
            addSymbol(k.getValueString(), false, true, "double", k.getStartPos());
            instructions.add(new Instruction(Operation.GlobA,getOffset(k.getValueString(),k.getStartPos())));
            type=AnalyseAssign();
            if(type=="double") {
                instructions.add(new Instruction(Operation.Store64));
            }
            else{
                throw new AnalyzeError(ErrorCode.NotDeclared,k.getStartPos());
            }
        }
        else{
            throw new AnalyzeError(ErrorCode.NotDeclared, t.getStartPos());
        }
        expect(TokenType.Semicolon);
    }

    private void AnalyseIf() throws CompileError{
        expect(TokenType.If);
        AnalyseAssign();
        ArrayList<Integer> add=new ArrayList<Integer>();
        ArrayList<Integer> nextAdd=new ArrayList<Integer>();
        ArrayList<Integer> end=new ArrayList<Integer>();
        int end1;
        int k1=0;
        instructions.add(new Instruction(Operation.BrTrue,1));
        add.add(instructions.size());//需要修改跳转地址的位置
        instructions.add(new Instruction(Operation.Br,0));
        AnalyseBlock();
        end.add(instructions.size());//跳出if else语句需要修改的跳转地址位置
        instructions.add(new Instruction(Operation.Br,0));
        if(check(TokenType.Else)){
            next();
            while (check(TokenType.If)){
                expect(TokenType.If);
                nextAdd.add(instructions.size());//回填地址
                AnalyseAssign();
                instructions.add(new Instruction(Operation.BrTrue,1));
                add.add(instructions.size());//需要修改跳转地址的位置
                instructions.add(new Instruction(Operation.Br,0));
                AnalyseBlock();
                end.add(instructions.size());
                instructions.add(new Instruction(Operation.Br,0));
                if (!check(TokenType.Else)){
                    k1=1;
                    break;
                }
                else{
                    next();
                }
            }
            nextAdd.add(instructions.size());
            if(k1==0){
                AnalyseBlock();
            }
            end1=instructions.size();//结束地址
            for(int i=0;i<add.size();i++){
                instructions.set(add.get(i),new Instruction(Operation.Br,nextAdd.get(i)-add.get(i)-1));
            }
            for(int i=0;i<end.size();i++){
                instructions.set(end.get(i),new Instruction(Operation.Br,end1-end.get(i)-1));
            }
        }
    }

    private void AnalyseWhile() throws CompileError{
        expect(TokenType.While);
        int begin=instructions.size();
        instructions.add(new Instruction(Operation.Br,0));
        AnalyseAssign();
        instructions.add(new Instruction(Operation.BrTrue,1));
        int add=instructions.size();
        instructions.add(new Instruction(Operation.Br,0));
        AnalyseBlock();
        instructions.add(new Instruction(Operation.Br,begin-instructions.size()));
        int end=instructions.size();
        instructions.set(add,new Instruction(Operation.Br,end-add-1));
    }

    private void AnalyseReturn() throws CompileError{
        expect(TokenType.Return);
        AnalyseAssign();
        expect(TokenType.Semicolon);
    }

    private void AnalyseBlock() throws CompileError{
        expect(TokenType.Lbrace);
        while(!check(TokenType.Rbrace)){
            AnalyseStatement();
        }
        expect(TokenType.Rbrace);
    }

    private void AnalyseEmpty() throws CompileError{
        expect(TokenType.Semicolon);
    }


    private void AnalyseFunction() throws CompileError{
        expect(TokenType.Fn);
        Token ident=expect(TokenType.Ident);
        addSymbol(ident.getValueString(),true,false,"Func",ident.getStartPos());
        ArrayList<Token> token=new ArrayList<>();
        expect(TokenType.LParen);
        while (!check(TokenType.RParen)){
            token.add(AnalyseFunctionParam());
            if(check(TokenType.Comma)){
                next();
            }
            else{
                break;
            }
        }
        expect(TokenType.RParen);
        expect(TokenType.Arrow);
        Token ty=expect(TokenType.Ty);
        addFunc(ident.getValueString(),ident.getStartPos(),ident.getValueString(),ty.getValueString(),token,instructions.size(),0);
        AnalyseBlock();
        setFuncEnd(ident.getValueString(),instructions.size());
        instructions.add(new Instruction(Operation.Ret,0));
    }

    private Token AnalyseFunctionParam() throws CompileError{
        if(check(TokenType.Const)){
            next();
        }
        Token ident=expect(TokenType.Ident);
        expect(TokenType.Colon);
        Token ty=expect(TokenType.Ty);
        return ident;
    }
}