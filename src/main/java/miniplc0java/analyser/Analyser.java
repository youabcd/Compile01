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
        expect(TokenType.Begin);

        analyseMain();

        // 'end'
        expect(TokenType.End);
        expect(TokenType.EOF);
    }

    private void analyseMain() throws CompileError {
        //TODO 主函数
        while(check(TokenType.End)==false&&check(TokenType.EOF)==false) {
            if (check(TokenType.Const)) {
                analyseConstantDeclaration();
            } else if (check(TokenType.Var)) {
                analyseVariableDeclaration();
            } else if (check(TokenType.Print)) {
                analyseOutputStatement();
            } else if (check(TokenType.Ident)) {
                analyseStatementSequence();
            } else {
                throw new Error("Not implemented");
            }
        }
    }

    private void analyseConstantDeclaration() throws CompileError {//TODO 常量声明
        // 示例函数，示例如何解析常量声明
        // 如果下一个 token 是 const 就继续
        //<常量声明>::=const Ident=<常表达式>;
        while (nextIf(TokenType.Const) != null) {
            // 变量名
            var nameToken = expect(TokenType.Ident);

            // 等于号
            expect(TokenType.Equal);

            // 常表达式
            analyseConstantExpression();

            // 分号
            expect(TokenType.Semicolon);

            //添加至符号表 对栈进行操作
            addSymbol(nameToken.getValueString(),true,true,nameToken.getStartPos());
            instructions.add(new Instruction(Operation.STO,getOffset(nameToken.getValueString(),nameToken.getStartPos())));
        }
    }

    private void analyseVariableDeclaration() throws CompileError {//TODO 变量声明
        //<变量声明>::=var Ident=<表达式>;
        while (nextIf(TokenType.Var) != null) {
            var nameToken = expect(TokenType.Ident);
            addSymbol(nameToken.getValueString(),false,false,nameToken.getStartPos());

            if(check(TokenType.Equal)){
                expect(TokenType.Equal);
                analyseExpression();
                expect(TokenType.Semicolon);
                declareSymbol(nameToken.getValueString(),nameToken.getStartPos());
                instructions.add(new Instruction(Operation.STO,getOffset(nameToken.getValueString(),nameToken.getStartPos())));
            }
            else{
                expect(TokenType.Semicolon);
                instructions.add(new Instruction(Operation.LIT,0));
            }

        }
        //throw new Error("Not implemented");
    }

    private void analyseStatementSequence() throws CompileError {//TODO 语句序列
        //<语句序列>::={<语句>}
        analyseStatement();
        //throw new Error("Not implemented");
    }

    private void analyseStatement() throws CompileError {//TODO 语句
        //<语句>::={<赋值语句>}
        analyseAssignmentStatement();
        //throw new Error("Not implemented");
    }

    private void analyseConstantExpression() throws CompileError {//TODO 常量表达式
        //<常量表达式>::=<项>{(+|-)<项>}
        analyseItem();
        while(check(TokenType.Minus)==true||check(TokenType.Plus)==true){
            if(nextIf(TokenType.Plus)!=null){
                analyseItem();
                instructions.add(new Instruction(Operation.ADD));
            }
            else{
                analyseItem();
                instructions.add(new Instruction(Operation.SUB));
            }
        }
        //throw new Error("Not implemented");
    }

    private void analyseExpression() throws CompileError {//TODO 表达式
        //<表达式>::=<项>{(+|-)<项>}
        analyseItem();
        while(check(TokenType.Minus)==true||check(TokenType.Plus)==true){
            if(nextIf(TokenType.Plus)!=null) {
                analyseItem();
                instructions.add(new Instruction(Operation.ADD));
            }
            else{
                nextIf(TokenType.Minus);
                analyseItem();
                instructions.add(new Instruction(Operation.SUB));
            }
        }
        //throw new Error("Not implemented");
    }

    private void analyseAssignmentStatement() throws CompileError {//TODO 赋值
        //<赋值语句>::=<Ident>=<表达式>
        var a=expect(TokenType.Ident);
        expect(TokenType.Equal);
        analyseExpression();
        expect(TokenType.Semicolon);
        instructions.add(new Instruction(Operation.STO, getOffset(a.getValueString(), a.getStartPos())));
        //throw new Error("Not implemented");
    }

    private void analyseOutputStatement() throws CompileError {//输出
        expect(TokenType.Print);
        expect(TokenType.LParen);
        analyseExpression();
        expect(TokenType.RParen);
        expect(TokenType.Semicolon);
        instructions.add(new Instruction(Operation.WRT));
    }

    private void analyseItem() throws CompileError {//TODO 项
        //<项>::=<因子>{(*|/)<因子>}
        analyseFactor();
        while(check(TokenType.Mult)==true||check(TokenType.Div)==true){
            if(nextIf(TokenType.Mult)!=null){
                analyseFactor();
                instructions.add(new Instruction(Operation.MUL));
            }
            else{
                analyseFactor();
                instructions.add(new Instruction(Operation.DIV));
            }
        }
        //throw new Error("Not implemented");
    }

    private void analyseFactor() throws CompileError {//TODO 因子
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
            instructions.add(new Instruction(Operation.LOD,getOffset(a.getValueString(),a.getStartPos())));
        } else if (check(TokenType.Uint)) {
            // 调用相应的处理函数
            var b=expect(TokenType.Uint);
            instructions.add(new Instruction(Operation.LIT,Integer.valueOf(b.getValueString())));
        } else if (check(TokenType.LParen)) {
            // 调用相应的处理函数
            expect(TokenType.LParen);
            analyseExpression();
            expect(TokenType.RParen);
        } else {
            // 都不是，摸了
            throw new ExpectedTokenError(List.of(TokenType.Ident, TokenType.Uint, TokenType.LParen), next());
        }

        if (negate) {
            instructions.add(new Instruction(Operation.SUB));
        }
        //throw new Error("Not implemented");
    }
}
