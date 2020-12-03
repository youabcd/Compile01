package miniplc0java.analyser;

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

public final class Analyser {

    Tokenizer tokenizer;
    ArrayList<Instruction> instructions;

    /** 当前偷看的 token */
    Token peekedToken = null;

    /** 符号表 */
    HashMap<String, SymbolEntry> symbolTable = new HashMap<>();

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
     * 获取下一个变量的栈偏移
     * 
     * @return
     */
    private int getNextVariableOffset() {
        return this.nextOffset++;
    }

    /**
     * 往符号表里添加一个符号
     *
     * @param name          名字
     * @param isInitialized 是否已赋值
     * @param isConstant    是否是常量
     * @param curPos        当前 token 的位置（报错用）
     * @throws AnalyzeError 如果重复定义了则抛异常
     */
    private void addSymbol(String name, boolean isInitialized, boolean isConstant, Pos curPos) throws AnalyzeError {
        if (this.symbolTable.get(name) != null) {
            throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
        } else {
            this.symbolTable.put(name, new SymbolEntry(isConstant, isInitialized, getNextVariableOffset()));
        }
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
        } else {
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

    /**
     * <程序> ::= 'begin'<主过程>'end'
     */
    private void analyseProgram() throws CompileError {
        // 示例函数，示例如何调用子程序
        // 'begin'
        expect(TokenType.Fn);

        AnalyseExpression();

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
    private void AnalyseAssign() throws CompileError{
        AnalyseCmp();
        while(check(TokenType.Assign)){
            next();
            AnalyseCmp();
            instructions.add(new Instruction(Operation.ASSIGN));
        }
    }
    private void AnalyseCmp() throws CompileError{
        AnalyseExpression();
        while(check(TokenType.Gt)||check(TokenType.Lt)||check(TokenType.Ge)||check(TokenType.Le)||check(TokenType.Eq)||check(TokenType.Neq)){
            next();
            AnalyseExpression();
            if(check(TokenType.Gt)){
                instructions.add(new Instruction(Operation.GT));
            }
            else if(check(TokenType.Lt)){
                instructions.add(new Instruction(Operation.LT));
            }
            else if(check(TokenType.Ge)){
                instructions.add(new Instruction(Operation.GE));
            }
            else if(check(TokenType.Le)){
                instructions.add(new Instruction(Operation.LE));
            }
            else if(check(TokenType.Eq)){
                instructions.add(new Instruction(Operation.EQ));
            }
            else if(check(TokenType.Neq)){
                instructions.add(new Instruction(Operation.NEQ));
            }
        }
    }

    private void AnalyseExpression() throws CompileError {//
        //<表达式>::=<项>{(+|-)<项>}
        AnalyseItem();
        while(check(TokenType.Minus)==true||check(TokenType.Plus)==true){
            if(nextIf(TokenType.Plus)!=null) {
                AnalyseItem();
                instructions.add(new Instruction(Operation.ADD));
            }
            else{
                nextIf(TokenType.Minus);
                AnalyseItem();
                instructions.add(new Instruction(Operation.SUB));
            }
        }
    }

    private void AnalyseItem() throws CompileError {
        //<项>::=<因子>{(*|/)<因子>}
        AnalyseAs();
        while(check(TokenType.Mult)||check(TokenType.Div)){
            if(nextIf(TokenType.Mult)!=null){
                AnalyseAs();
                instructions.add(new Instruction(Operation.MUL));
            }
            else{
                nextIf(TokenType.Div);
                AnalyseAs();
                instructions.add(new Instruction(Operation.DIV));
            }
        }
        //throw new Error("Not implemented");
    }

    private void AnalyseAs() throws CompileError{
        AnalyseFactor();
        while(check(TokenType.As)){
            next();
            Token t=next();
            if(t.getValueString().equals("int")){
                instructions.add(new Instruction(Operation.AS1));
            }
            else{
                instructions.add(new Instruction(Operation.AS2));
            }
        }
    }

    private void AnalyseFactor() throws CompileError {
        //<因子>::=Ident|Uint|(<表达式>)
        boolean negate;
        if (nextIf(TokenType.Minus) != null) {//负数显示为0-(Uint|Ident|<***>)
            negate = true;
            // 计算结果需要被 0 减
            instructions.add(new Instruction(Operation.LIT, 0));
        } else {//防止a=+1;此类情况
            nextIf(TokenType.Plus);
            negate = false;
        }

        if (check(TokenType.Ident)) {
            // 调用相应的处理函数
            var a=expect(TokenType.Ident);
            if(check(TokenType.LParen)){
                next();
                AnalyseAssign();
                while(check(TokenType.Comma)){
                    next();
                    AnalyseAssign();
                }
                expect(TokenType.RParen);
                //TODO
            }
            instructions.add(new Instruction(Operation.LOD,getOffset(a.getValueString(),a.getStartPos())));
        }
        else if (check(TokenType.Uint)) {
            // 调用相应的处理函数
            var b=expect(TokenType.Uint);
            instructions.add(new Instruction(Operation.LIT,Integer.valueOf(b.getValueString())));
        }
        else if(check(TokenType.Double)){
            var b=expect(TokenType.Double);
            double x=Double.parseDouble(b.getValue().toString());
            instructions.add(new Instruction(Operation.LIT,new Double(x).longValue()));
        }
        else if(check(TokenType.Str)){
            var b=expect(TokenType.Str);
            //TODO
        }
        else if (check(TokenType.LParen)) {
            // 调用相应的处理函数
            expect(TokenType.LParen);
            AnalyseAssign();
            expect(TokenType.RParen);
        }
        else {
            // 都不是，摸了
            throw new ExpectedTokenError(List.of(TokenType.Ident, TokenType.Uint, TokenType.LParen), next());
        }

        if (negate) {
            instructions.add(new Instruction(Operation.SUB));
        }
        //throw new Error("Not implemented");
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
        if(t.getValueString().equals("int")){
            if(check(TokenType.Assign)){
                AnalyseAssign();
            }
        }
        else if(t.getValueString().equals("double")){
            if(check(TokenType.Assign)){
                AnalyseAssign();
            }
        }
        else{
//TODO
        }
        expect(TokenType.Semicolon);
    }

    private void AnalyseConst_decl_stmt() throws CompileError{
        expect(TokenType.Const);
        Token k=expect(TokenType.Ident);
        expect(TokenType.Colon);
        Token t=expect(TokenType.Ty);
        if(t.getValueString().equals("int")){
            if(check(TokenType.Assign)){
                AnalyseAssign();
            }
        }
        else if(t.getValueString().equals("double")){
            if(check(TokenType.Assign)){
                AnalyseAssign();
            }
        }
        else{

        }
        expect(TokenType.Semicolon);
    }

    private void AnalyseIf() throws CompileError{
        expect(TokenType.If);
        AnalyseAssign();
        AnalyseBlock();
        if(check(TokenType.Else)){
            next();
            while (check(TokenType.If)){
                expect(TokenType.If);
                AnalyseAssign();
                AnalyseBlock();
                if (!check(TokenType.Else)){
                    break;
                }
                else{
                    next();
                }
            }
            AnalyseBlock();
        }
    }

    private void AnalyseWhile() throws CompileError{
        expect(TokenType.While);
        AnalyseAssign();
        AnalyseBlock();
    }

    private void AnalyseReturn() throws CompileError{
        expect(TokenType.Return);
        AnalyseAssign();
        expect(TokenType.Semicolon);
    }

    private void AnalyseBlock() throws CompileError{
        expect(TokenType.Lbrace);
        while(!check(TokenType.Rbrace)){
            AnalyseExpression();
        }
        expect(TokenType.Rbrace);
    }

    private void AnalyseEmpty() throws CompileError{
        expect(TokenType.Semicolon);
    }


    private void AnalyseFunction() throws CompileError{
        expect(TokenType.Fn);
        Token ident=expect(TokenType.Ident);
        expect(TokenType.LParen);
        while (check(TokenType.RParen)){
            AnalyseFunctionParam();
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
        AnalyseBlock();
    }

    private void AnalyseFunctionParam() throws CompileError{
        if(check(TokenType.Const)){
            next();
        }
        Token ident=expect(TokenType.Ident);
        expect(TokenType.Colon);
        Token ty=expect(TokenType.Ty);
    }

}
