package miniplc0java.analyser;

import miniplc0java.error.AnalyzeError;
import miniplc0java.error.CompileError;
import miniplc0java.error.ErrorCode;
import miniplc0java.error.ExpectedTokenError;
import miniplc0java.error.TokenizeError;
import miniplc0java.instruction.Instruction;
import miniplc0java.instruction.Operation;
import miniplc0java.instruction.FunctionList;
import miniplc0java.instruction.GlobalSymbol;
import miniplc0java.instruction.FunctionParam;
import miniplc0java.instruction.MidCode;
import miniplc0java.tokenizer.Token;
import miniplc0java.tokenizer.TokenType;
import miniplc0java.tokenizer.Tokenizer;
import miniplc0java.util.Pos;
import org.checkerframework.checker.units.qual.C;

import java.util.*;

public final class Analyser {

    Tokenizer tokenizer;
    //ArrayList<Instruction> instructions;
    ArrayList<SymbolEntry> temporaryTable=new ArrayList<>();//每层临时变量表
    MidCode midCode=MidCode.getMidCode();

    /** 当前偷看的 token */
    Token peekedToken = null;

    /** 符号表 */
    //HashMap<String, SymbolEntry> symbolTable = new HashMap<>();

    /** 下一个变量的栈偏移 */
    int nextOffset = 0;

    public Analyser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    /*public List<Instruction> analyse() throws CompileError {
        analyseProgram();
        //return midCode;
        return null;
    }*/
    public MidCode analyse() throws CompileError{
        analyseProgram();
        return midCode;
    }

    /**
     * 符号表中查找当前层级的符号
     * @param name
     * @param depth
     * @return 存在相同变量名：true 不存在：false
     */
    public boolean findSymbol(String name, int depth){
        for(SymbolEntry s: temporaryTable){
            if(s.name.equals(name) && s.getDepth()==depth){
                return true;
            }
        }
        return false;
    }

    /**
     * 从后往前查找符号，不是全局符号的
     * @param name
     * @param depth
     * @return
     */
    public SymbolEntry findBSymbol(String name, int depth){
        for(int i=temporaryTable.size()-1; i>=0; i--){
            if(temporaryTable.get(i).getName().equals(name) &&
                    temporaryTable.get(i).getDepth()<=depth && temporaryTable.get(i).getDepth()!=0){
                return temporaryTable.get(i);
            }
        }
        return null;
    }

    /**
     * 根据变量名和等级获取变量
     * @param name
     * @param depth
     * @return
     */
    public SymbolEntry getSymbol(String name, int depth){
        for(SymbolEntry s: temporaryTable){
            if(s.name.equals(name) && s.getDepth()==depth){
                return s;
            }
        }
        return null;
    }

    /**
     * 调用某个变量，若未定义过则抛出异常，若定义过则返回变量
     * @param name
     * @param depth
     * @return
     */
    public SymbolEntry useSymbol(String name, int depth, Pos curPos) throws AnalyzeError {
        for(int i=temporaryTable.size()-1; i>=0; i--){
            if(temporaryTable.get(i).getName().equals(name) && temporaryTable.get(i).getDepth() <= depth){
                return temporaryTable.get(i);
            }
        }
        throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
    }

    /**
     * 将某个rank的变量全都pop掉
     * @param depth
     */
    public void popRank(int depth){
        temporaryTable.removeIf(s -> s.getDepth() == depth);
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
    private void addSymbol(String name, boolean isInitialized, boolean isConstant, String type, Pos curPos,int depth,int offSet) throws AnalyzeError {
        if (findSymbol(name,depth)) {
            throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
        }
        else {
            this.temporaryTable.add(new SymbolEntry(isConstant, isInitialized, type, offSet,name,depth));
        }
    }


    /**
     * 获取下一个变量的栈偏移
     *
     * @return
     */
    /*private int getNextVariableOffset() {
        return this.nextOffset++;
    }*/

    /**
     * 设置符号为已赋值
     * 
     * @param name   符号名称
     * @param curPos 当前位置（报错用）
     * @throws AnalyzeError 如果未定义则抛异常
     */
    private void declareSymbol(String name, Pos curPos,int depth) throws AnalyzeError {
        var entry = getSymbol(name,depth);
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
    private int getOffset(String name, int depth,Pos curPos) throws AnalyzeError {
        var entry = useSymbol(name,depth,curPos);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            return entry.getStackOffset();
        }
    }

    /**
     * 根据SymbolEntry返回符号的偏移量
     * @param sy
     * @return
     * @throws AnalyzeError
     */
    private int getOffset(SymbolEntry sy) throws AnalyzeError {
        return temporaryTable.indexOf(sy);
    }

    /**
     * 获取局部变量的位置
     * @param sy
     * @return
     */
    private int getVarThisRankOffset(SymbolEntry sy){
        for(SymbolEntry s : temporaryTable){
            if(s.getName().equals(sy.getName()) &&
                    s.getDepth() == sy.getDepth() ){
                return s.getStackOffset();

            }
        }
        return -1;
    }

    /**
     * 获取当前rank的下一个局部变量的位置
     * @param rank
     * @return
     */
    private int getThisRankOffset(int rank){
        if(rank == 0){
            int num=0;
            for(SymbolEntry s: temporaryTable){
                if(s.getDepth() == 0){
                    num ++;
                }
            }
            return num;
        }
        else {
            int num = 0;
            for (SymbolEntry s : temporaryTable) {
                if (s.getDepth() <= rank && s.getDepth()!=0) {
                    num++;
                }
            }
            return num;
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
    private boolean isConstant(FunctionList func,String name, Pos curPos,int depth) throws AnalyzeError {
        SymbolEntry sy;
        int offset=0;
        if ((sy=findBSymbol(name, depth))!=null) {
            return sy.isConstant();
        }
        // 查找函数参数表
        else if ((offset=(func.getParamOffset(name))) >= 0) {
            return func.getOffsetParam(offset).isConst();
        }
        // 查找变量表
        else {
            sy = useSymbol(name, 0, curPos);
            return sy.isConstant();
        }
    }

    /*
    *获取变量的类型*
    */
    private String whichType(FunctionList func,String name,Pos curPos,int depth) throws AnalyzeError{
        SymbolEntry sy;
        int offset=0;
        if ((sy=findBSymbol(name, depth))!=null) {
            return sy.Type();
        }
        // 查找函数参数表
        else if ((offset=(func.getParamOffset(name))) >= 0) {
            return func.getOffsetParam(offset).getType();
        }
        // 查找变量表
        else {
            sy = useSymbol(name, 0, curPos);
            return sy.Type();
        }
    }

    /*是否已定义*/
    private boolean isInitialized(FunctionList func,String name,Pos curPos,int depth) throws AnalyzeError{
        SymbolEntry sy;
        int offset=0;
        if ((sy=findBSymbol(name, depth))!=null) {
            return sy.isInitialized();
        }
        // 查找函数参数表
        else if ((offset=(func.getParamOffset(name))) >= 0) {
            return true;
        }
        // 查找变量表
        else {
            sy = useSymbol(name, 0, curPos);
            return sy.isInitialized();
        }
    }

    /**
     * 主程序
     */
    private void analyseProgram() throws CompileError {
        // 'begin'
        FunctionList funcStart=new FunctionList("_start");
        while(!check(TokenType.EOF)){
            if(check(TokenType.Fn)){
                AnalyseFunction();
            }
            else{
                // 全局变量
                if(check(TokenType.Const)){ // decl_stmt const
                    AnalyseConst_decl_stmt(funcStart, 0);
                }
                else if(check(TokenType.Let)){ // decl_stmt let
                    AnalyseLet_decl_stmt(funcStart, 0);
                }
            }
        }

        MidCode.getMidCode().addFunction(funcStart);
        FunctionList mid=midCode.getFn("main",peek().getStartPos());
        funcStart.addInstruction(new Instruction(Operation.StackAlloc,mid.getReturnSlots(),4));
        int start=midCode.getFnAddress("main");
        funcStart.addInstruction(new Instruction(Operation.Call,start,4));
        midCode.addGlobalSymbol("_start",peek().getStartPos());

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
    private String AnalyseAssign(FunctionList func,int depth) throws CompileError{
        String type="void",type1="void";
        Token t=peek();
        type=AnalyseCmp(func,depth);
        if(check(TokenType.Assign)){
            if(t.getTokenType()==TokenType.Ident){
                func.removeInstruction(func.getInstructionsLength()-1);
                if(isConstant(func,t.getValueString(),t.getStartPos(),depth)&&isInitialized(func,t.getValueString(),t.getStartPos(),depth)){
                    throw new ExpectedTokenError(List.of(TokenType.Ident, TokenType.Uint, TokenType.LParen), next());
                }
                next();
                type1 = AnalyseCmp(func,depth);
                if (!type.equals(type1)) {
                    throw new AnalyzeError(ErrorCode.NotDeclared, t.getStartPos());
                } else {
                    func.addInstruction(new Instruction(Operation.Store64));
                }
            }
            else{
                throw new AnalyzeError(ErrorCode.NotDeclared, t.getStartPos());
            }
            expect(TokenType.Semicolon);
        }
        return type;
    }

    private String AnalyseCmp(FunctionList func,int depth) throws CompileError{
        String type="void";
        type=AnalyseExpression(func,depth);
        if(check(TokenType.Gt)||check(TokenType.Lt)||check(TokenType.Ge)||check(TokenType.Le)||check(TokenType.Eq)||check(TokenType.Neq)) {
            Token t = next();
            String type1 = AnalyseExpression(func,depth);
            if (!type.equals(type1)) {
                throw new AnalyzeError(ErrorCode.NotDeclared, t.getStartPos());
            }
            if (type.equals("int")) {
                func.addInstruction(new Instruction(Operation.CmpI));
            }
            else if(type.equals("double")){
                func.addInstruction(new Instruction(Operation.CmpF));
            }
            else {
                throw new AnalyzeError(ErrorCode.NotDeclared, t.getStartPos());
            }
            if (t.getTokenType() == TokenType.Gt) {
                func.addInstruction(new Instruction(Operation.SetGt));
            } else if (t.getTokenType() == TokenType.Lt) {
                func.addInstruction(new Instruction(Operation.SetLt));
            } else if (t.getTokenType() == TokenType.Ge) {
                func.addInstruction(new Instruction(Operation.SetLt));
                func.addInstruction(new Instruction(Operation.Not));
            } else if (t.getTokenType() == TokenType.Le) {
                func.addInstruction(new Instruction(Operation.SetGt));
                func.addInstruction(new Instruction(Operation.Not));
            } else if (t.getTokenType() == TokenType.Eq) {
                func.addInstruction(new Instruction(Operation.Not));
            } else if (t.getTokenType() == TokenType.Neq) {
            }
        }
        return type;
    }

    private String AnalyseExpression(FunctionList func,int depth) throws CompileError {
        String type="void";
        type=AnalyseItem(func,depth);
        while(check(TokenType.Minus)||check(TokenType.Plus)){
            Token t=next();
            if(type.equals("int")) {
                if (t.getTokenType()==TokenType.Plus) {
                    String type1 = AnalyseItem(func,depth);
                    if(!type.equals(type1)){
                        throw new AnalyzeError(ErrorCode.NotDeclared, t.getStartPos());
                    }
                    func.addInstruction(new Instruction(Operation.AddI));
                } else {
                    String type1 = AnalyseItem(func,depth);
                    if(!type.equals(type1)){
                        throw new AnalyzeError(ErrorCode.NotDeclared, t.getStartPos());
                    }
                    func.addInstruction(new Instruction(Operation.SubI));
                }
            }
            else if(type.equals("double")) {
                if (t.getTokenType()==TokenType.Plus) {
                    String type1 = AnalyseItem(func,depth);
                    if(!type.equals(type1)){
                        throw new AnalyzeError(ErrorCode.NotDeclared, t.getStartPos());
                    }
                    func.addInstruction(new Instruction(Operation.AddF));
                } else {
                    String type1 = AnalyseItem(func,depth);
                    if(!type.equals(type1)){
                        throw new AnalyzeError(ErrorCode.NotDeclared, t.getStartPos());
                    }
                    func.addInstruction(new Instruction(Operation.SubF));
                }
            }
            else{
                throw new AnalyzeError(ErrorCode.NotDeclared, t.getStartPos());
            }
        }
        return type;
    }

    private String AnalyseItem(FunctionList func,int depth) throws CompileError {
        //<项>::=<因子>{(*|/)<因子>}
        String type="void";
        type=AnalyseAs(func, depth);
        while(check(TokenType.Mult)||check(TokenType.Div)){
            Token t=next();
            if(type.equals("int")) {
                if (t.getTokenType().toString().equals(TokenType.Mult.toString())) {
                    String type1 = AnalyseAs(func, depth);
                    if(!type.equals(type1)){
                        throw new AnalyzeError(ErrorCode.NotDeclared, t.getStartPos());
                    }
                    func.addInstruction(new Instruction(Operation.MulI));
                } else {
                    String type1 = AnalyseAs(func, depth);
                    if(!type.equals(type1)){
                        throw new AnalyzeError(ErrorCode.NotDeclared, t.getStartPos());
                    }
                    func.addInstruction(new Instruction(Operation.DivI));
                }
            }
            else if(type.equals("double")) {
                if (t.getTokenType().equals(TokenType.Mult)) {
                    String type1 = AnalyseAs(func, depth);
                    if(!type.equals(type1)){
                        throw new AnalyzeError(ErrorCode.NotDeclared, t.getStartPos());
                    }
                    func.addInstruction(new Instruction(Operation.MulF));
                } else {
                    String type1 = AnalyseAs(func, depth);
                    if(!type.equals(type1)){
                        throw new AnalyzeError(ErrorCode.NotDeclared, t.getStartPos());
                    }
                    func.addInstruction(new Instruction(Operation.DivF));
                }
            }
            else{
                throw new AnalyzeError(ErrorCode.NotDeclared, t.getStartPos());
            }
        }
        return type;
    }

    private String AnalyseAs(FunctionList func,int depth) throws CompileError{
        String type="void";
        type=AnalyseFactor(func, depth);
        while(check(TokenType.As)){
            next();
            Token t=next();
            if(t.getValueString().equals("int")){
                func.addInstruction(new Instruction(Operation.FtoI));
                type="int";
            }
            else if(t.getValueString().equals("double")){
                func.addInstruction(new Instruction(Operation.ItoF));
                type="double";
            }
            else{
                throw new AnalyzeError(ErrorCode.NotDeclared, t.getStartPos());
            }
        }
        return type;
    }

    private String AnalyseFactor(FunctionList func,int depth) throws CompileError {
        //<因子>::=Ident|Uint|(<表达式>)
        String type="void";
        int negate=0;

        while(check(TokenType.Minus)){
            expect(TokenType.Minus);
            negate++;
        }
        if(check(TokenType.Plus)){
            expect(TokenType.Plus);
            negate=0;
        }

        if (check(TokenType.Ident)) {
            // 调用相应的处理函数
            Token a=expect(TokenType.Ident);
            //调用函数
            if(check(TokenType.LParen)){
                if(FunctionList.libFunc.get(a.getValueString())!=null){
                    next();
                    int offset=midCode.insertLibFunctionBefore(func.getFnName(),a.getValueString());
                    switch (a.getValueString()){
                        case "getdouble":
                            type="double";
                            func.addInstruction(new Instruction(Operation.StackAlloc, 1, 4));
                            break;
                        case "getint":
                        case "getchar":
                            type="int";
                            func.addInstruction(new Instruction(Operation.StackAlloc, 1, 4));
                            break;
                        case "putstr":
                            func.addInstruction(new Instruction(Operation.StackAlloc, 0, 4));
                            if (check(TokenType.RParen)) {
                                throw new AnalyzeError(ErrorCode.ExpectedToken, a.getStartPos());
                            }
                            Token t=expect(TokenType.Str);
                            midCode.addGlobalSymbolToLastPos(t.getValueString(), t.getStartPos());
                            offset=midCode.getSymbolAddress(t.getValueString());
                            func.addInstruction(new Instruction(Operation.Push, offset, 8));
                            offset=midCode.getSymbolAddress("putstr");
                            break;
                        case "putln":
                            func.addInstruction(new Instruction(Operation.StackAlloc, 0, 4));
                            break;
                        default:
                            if (check(TokenType.RParen)) {
                                throw new AnalyzeError(ErrorCode.ExpectedToken, a.getStartPos());
                            }
                            func.addInstruction(new Instruction(Operation.StackAlloc, 0, 4));
                            if (a.getValueString().equals("putint") || a.getValueString().equals("putchar")) {
                                if (!AnalyseAssign(func, depth).equals("int")) {
                                    throw new AnalyzeError(ErrorCode.ExpectedToken, a.getStartPos());
                                }
                            } else if (a.getValueString().equals("putdouble")) {
                                if (!AnalyseAssign(func,depth).equals("double")) {
                                    throw new AnalyzeError(ErrorCode.ExpectedToken, a.getStartPos());
                                }
                            }
                            break;
                    }
                    expect(TokenType.RParen);
                    func.addInstruction(new Instruction(Operation.CallName,offset,4));
                }
                else {
                    FunctionList calledFunc = midCode.getFn(a.getValueString(), a.getStartPos());
                    func.addInstruction(new Instruction(Operation.StackAlloc, calledFunc.getReturnSlots(), 4));
                    next();
                    ArrayList<String> paramType = new ArrayList<>();
                    /*while(!check(TokenType.RParen)){
                        paramType.add(AnalyseAssign(func,depth));
                        if(check(TokenType.Comma)){
                            next();
                        }
                        else {
                            break;
                        }
                    }
                    expect(TokenType.RParen);*/
                    if (check(TokenType.RParen)) {
                        expect(TokenType.RParen);
                    }
                    else {
                        paramType.add(AnalyseAssign(func, depth));
                        while (check(TokenType.Comma)) {
                            next();
                            paramType.add(AnalyseAssign(func, depth));
                        }
                        expect(TokenType.RParen);
                    }
                    //TODO 0-ac11 ac3-1 ac4-1 ac4 ac6 ac9
                    calledFunc.checkParams(a.getStartPos(), paramType);
                    func.addInstruction(new Instruction(Operation.Call, midCode.getFnAddress(calledFunc.getFnName()) ,4));
                    type = calledFunc.getReturnType();
                }
            }
            //加载变量
            else {
                int offset;
                SymbolEntry sy;
                if ((sy=findBSymbol(a.getValueString(), depth))!=null) {
                    offset=sy.getStackOffset();
                    type=sy.Type();
                    func.addInstruction(new Instruction(Operation.LocA, offset, 4));
                }
                // 查找函数参数表
                else if ((offset=func.getParamOffset(a.getValueString())) >= 0) {
                    type=func.getOffsetParam(offset).getType();
                    if(func.haveRet()){
                        offset++;
                    }
                    func.addInstruction(new Instruction(Operation.ArgA, offset, 4));
                }
                // 查找变量表
                else {
                    sy = useSymbol(a.getValueString(), 0, a.getStartPos());
                    type=sy.Type();
                    offset=sy.getStackOffset();
                    func.addInstruction(new Instruction(Operation.GlobA, offset, 4));
                }
                func.addInstruction(new Instruction(Operation.Load64));
            }
        }
        else if (check(TokenType.Uint)) {
            // 调用相应的处理函数
            var b=expect(TokenType.Uint);
            type="int";
            func.addInstruction(new Instruction(Operation.Push,Long.parseLong(b.getValueString()),8));
        }
        else if(check(TokenType.Double)){
            var b=expect(TokenType.Double);
            double x=Double.parseDouble(b.getValue().toString());
            type="double";
            func.addInstruction(new Instruction(Operation.Push,Double.doubleToLongBits(x),8));
        }
        else if(check(TokenType.Char)){//TODO 2-ac1 ac2
            Token b=expect(TokenType.Char);
            type="int";
            func.addInstruction(new Instruction(Operation.Push,(int)(b.getValue()), 8 ));
        }
        else if(check(TokenType.Str)){}
        else if (check(TokenType.LParen)) {
            // 调用相应的处理函数
            expect(TokenType.LParen);
            type=AnalyseAssign(func, depth);
            expect(TokenType.RParen);
        }
        else if(check(TokenType.None)){}
        else {
            throw new ExpectedTokenError(List.of(TokenType.Ident), next());
        }

        if (negate%2==1) {
            if(type.equals("int")) {
                func.addInstruction(new Instruction(Operation.NegI));
            }
            else if(type.equals("double")){
                func.addInstruction(new Instruction(Operation.NegF));
            }
            else{
                throw new ExpectedTokenError(List.of(TokenType.Ident), next());
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

    private void AnalyseStatement(FunctionList func,int depth) throws CompileError{
        if(check(TokenType.Let)){
            AnalyseLet_decl_stmt(func,depth);
        }
        else if(check(TokenType.Const)){
            AnalyseConst_decl_stmt(func, depth);
        }
        else if(check(TokenType.If)){
            AnalyseIf(func,depth);
        }
        else if(check(TokenType.While)){
            AnalyseWhile(func,depth);
        }
        else if(check(TokenType.Return)){
            AnalyseReturn(func,depth);
        }
        else if(check(TokenType.Lbrace)){//代码块
            AnalyseBlock(func,depth);
        }
        else if(check(TokenType.Semicolon)){
            AnalyseEmpty(func,depth);
        }
        else{
            AnalyseAssign(func,depth);
        }
    }

    private void AnalyseLet_decl_stmt(FunctionList func,int depth) throws CompileError{//let声明
        expect(TokenType.Let);
        Token k=expect(TokenType.Ident);
        expect(TokenType.Colon);
        Token t=expect(TokenType.Ty);
        String type="";
        int offset;
        if(depth==0){
            midCode.addGlobalVar(k.getValueString(),k.getStartPos());
            midCode.addGlobalVar(new GlobalSymbol(k.getValueString(),false));
            offset=getThisRankOffset(depth);
        }
        else{
            if(depth==1){
                func.notInFnParams(k.getValueString(),k.getStartPos());
            }
            func.addLoc();
            offset=func.getLocSlots()-1;
        }
        addSymbol(k.getValueString(),false,false,t.getValueString(),k.getStartPos(),depth,offset);
        if(check(TokenType.Assign)){
            next();
            if(depth==0){
                offset=MidCode.getMidCode().getNextGlobalVarOffset();
                func.addInstruction(new Instruction(Operation.GlobA,offset-1,4));
            }
            else{
                offset=func.getNextLocOffset();
                func.addInstruction(new Instruction(Operation.LocA,offset,4));
            }
            type=AnalyseAssign(func,depth);
            if(type.equals(t.getValueString())) {//TODO 3-ac2
                func.addInstruction(new Instruction(Operation.Store64));
            }
            else{
                throw new AnalyzeError(ErrorCode.NotDeclared,k.getStartPos());
            }
            declareSymbol(k.getValueString(),k.getStartPos(),depth);
        }
        expect(TokenType.Semicolon);
    }

    private void AnalyseConst_decl_stmt(FunctionList func,int depth) throws CompileError{
        String type="";
        expect(TokenType.Const);
        Token k=expect(TokenType.Ident);
        expect(TokenType.Colon);
        Token t=expect(TokenType.Ty);
        expect(TokenType.Assign);
        int offset;
        if(depth==0){
            midCode.addGlobalVar(k.getValueString(),k.getStartPos());
            midCode.addGlobalVar(new GlobalSymbol(k.getValueString(),false));
            offset=getThisRankOffset(depth);
        }
        else{
            if(depth==1){
                func.notInFnParams(k.getValueString(),k.getStartPos());
            }
            func.addLoc();
            offset=func.getLocSlots()-1;
        }
        addSymbol(k.getValueString(),true,true,t.getValueString(),k.getStartPos(),depth,offset);
        if(depth==0){
            offset=MidCode.getMidCode().getNextGlobalVarOffset();
            func.addInstruction(new Instruction(Operation.GlobA,offset-1,4));
        }
        else{
            offset=func.getNextLocOffset();
            func.addInstruction(new Instruction(Operation.LocA,offset,4));
        }
        type=AnalyseAssign(func,depth);
        if(type.equals(t.getValueString())) {
            func.addInstruction(new Instruction(Operation.Store64));
        }
        else{
            throw new AnalyzeError(ErrorCode.NotDeclared,k.getStartPos());
        }
        //declareSymbol(k.getValueString(),k.getStartPos(),depth);
        expect(TokenType.Semicolon);
    }

    private void AnalyseIf(FunctionList func,int depth) throws CompileError{
        expect(TokenType.If);
        AnalyseAssign(func, depth);
        ArrayList<Integer> add=new ArrayList<Integer>();
        ArrayList<Integer> nextAdd=new ArrayList<Integer>();
        ArrayList<Integer> end=new ArrayList<Integer>();
        int end1;
        int k1=0;
        func.addInstruction(new Instruction(Operation.BrTrue,1,4));
        add.add(func.getInstructionsLength());//需要修改跳转地址的位置
        func.addInstruction(new Instruction(Operation.Br,0,4));
        AnalyseBlock(func,depth);
        if(check(TokenType.Else)){
            next();
            end.add(func.getInstructionsLength());//跳出if else语句需要修改的跳转地址位置
            func.addInstruction(new Instruction(Operation.Br,0,4));
            while (check(TokenType.If)){
                expect(TokenType.If);
                nextAdd.add(func.getInstructionsLength());//回填地址
                AnalyseAssign(func, depth);
                func.addInstruction(new Instruction(Operation.BrTrue,1,4));
                add.add(func.getInstructionsLength());//需要修改跳转地址的位置
                func.addInstruction(new Instruction(Operation.Br,0,4));
                AnalyseBlock(func, depth);
                end.add(func.getInstructionsLength());
                func.addInstruction(new Instruction(Operation.Br,0,4));
                if (!check(TokenType.Else)){
                    k1=1;
                    break;
                }
                else{
                    next();
                }
            }
            nextAdd.add(func.getInstructionsLength());
            if(k1==0){
                AnalyseBlock(func, depth);
            }
            end1=func.getInstructionsLength();//结束地址
            for(int i=0;i<add.size();i++){
                func.setBrInstructionValue(add.get(i),new Instruction(Operation.Br,nextAdd.get(i)-add.get(i)-1,4));
            }
            for(int i=0;i<end.size();i++){
                func.setBrInstructionValue(end.get(i),new Instruction(Operation.Br,end1-end.get(i)-1,4));
            }
        }
        else{
            func.setBrInstructionValue(add.get(0),new Instruction(Operation.Br,func.getInstructionsLength()-add.get(0)-1,4));
        }
    }

    private void AnalyseWhile(FunctionList func,int depth) throws CompileError{
        expect(TokenType.While);
        int begin=func.getInstructionsLength();
        func.addInstruction(new Instruction(Operation.Br,0,4));
        AnalyseAssign(func, depth);
        func.addInstruction(new Instruction(Operation.BrTrue,1,4));
        int add=func.getInstructionsLength();
        func.addInstruction(new Instruction(Operation.Br,0,4));
        AnalyseBlock(func, depth);
        func.addInstruction(new Instruction(Operation.Br,begin-func.getInstructionsLength(),4));
        int end=func.getInstructionsLength();
        func.setBrInstructionValue(add,new Instruction(Operation.Br,end-add-1,4));
    }

    private void AnalyseReturn(FunctionList func,int depth) throws CompileError{
        expect(TokenType.Return);
        if(!func.getReturnType().equals("void")){
            func.addInstruction(new Instruction(Operation.ArgA,0,4));
        }
        String type="void";
        if(!check(TokenType.Semicolon)){
            type=AnalyseAssign(func, depth);
            func.addInstruction(new Instruction(Operation.Store64));
        }
        func.returnFn(type, peek().getStartPos());
        func.addInstruction(new Instruction(Operation.Ret));
        expect(TokenType.Semicolon);
    }

    private void AnalyseBlock(FunctionList func,int depth) throws CompileError{
        depth++;
        expect(TokenType.Lbrace);
        while(!check(TokenType.Rbrace)){
            AnalyseStatement(func, depth);
        }
        expect(TokenType.Rbrace);
        popRank(depth);
    }

    private void AnalyseEmpty(FunctionList func,int depth) throws CompileError{
        expect(TokenType.Semicolon);
    }

    private void AnalyseFunction() throws CompileError{
        expect(TokenType.Fn);
        Token ident=expect(TokenType.Ident);
        FunctionList func=new FunctionList((ident.getValueString()));
        midCode.addGlobalSymbol(ident.getValueString(),ident.getStartPos());
        midCode.addFunction(func);
        expect(TokenType.LParen);
        while (!check(TokenType.RParen)){
            AnalyseFunctionParam(func);
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
        func.setReturn(ty.getValueString());
        AnalyseBlock(func,0);
        if(func.isReturned()){
            if(!func.checkReturnRoutes()){
                throw new AnalyzeError(ErrorCode.ExpectedToken, peek().getStartPos());
            }
        }
        else {
            func.addInstruction(new Instruction(Operation.Ret));
            func.returnFn("void",ident.getStartPos());
        }
    }

    private void AnalyseFunctionParam(FunctionList func) throws CompileError{
        boolean isConst=false;
        if(check(TokenType.Const)){
            isConst=true;
            next();
        }
        Token ident=expect(TokenType.Ident);
        expect(TokenType.Colon);
        Token ty=expect(TokenType.Ty);
        func.addParam(ident.getValueString(),isConst,ty.getValueString(),ident.getStartPos());
    }
}