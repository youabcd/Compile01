package miniplc0java.tokenizer;

import miniplc0java.error.TokenizeError;
import miniplc0java.error.ErrorCode;
import miniplc0java.util.Pos;

public class Tokenizer {

    private StringIter it;

    public Tokenizer(StringIter it) {
        this.it = it;
    }

    // 这里本来是想实现 Iterator<Token> 的，但是 Iterator 不允许抛异常，于是就这样了
    /**
     * 获取下一个 Token
     * 
     * @return
     * @throws TokenizeError 如果解析有异常则抛出
     */
    public Token nextToken() throws TokenizeError {
        it.readAll();

        // 跳过之前的所有空白字符
        skipSpaceCharacters();

        if (it.isEOF()) {//是否结束
            return new Token(TokenType.EOF, "", it.currentPos(), it.currentPos());
        }

        char peek = it.peekChar();
        System.out.print(peek);
        if (Character.isDigit(peek)) {//下一个是否为数字
            return lexUInt();
        }
        else if (Character.isAlphabetic(peek)||peek=='_') {//是否为字母或下划线
            return lexIdentOrKeyword();
        }
        else if(peek=='"'){
            return lexString();
        }
        else if(peek=='\''){
            return lexChar();
        }
        else {
            return lexOperatorOrUnknown();
        }
    }

    private Token lexUInt() throws TokenizeError {//判断数字
        Pos ptrstart=it.previousPos();
        int isD=0,isE=0;
        StringBuffer uint=new StringBuffer("");
        while(Character.isDigit(it.peekChar())||it.peekChar()=='.'||it.peekChar()=='E'||it.peekChar()=='e'){
            if(it.peekChar()=='.'){
                isD++;
            }
            if(it.peekChar()=='E'||it.peekChar()=='e'){
                isE=1;
                uint.append(it.nextChar());
                if(it.peekChar()=='-'){
                    uint.append(it.nextChar());
                    while(Character.isDigit(it.peekChar())){
                        uint.append(it.nextChar());
                    }
                    break;
                }
            }
            uint.append(it.nextChar());
        }
        String uint1=new String(uint);
        if(isD==0) {
            if(isE==0) {
                long a=Long.parseLong(uint1);//TODO 3-ac1 暂时已修改
                return new Token(TokenType.Uint, a, ptrstart, it.currentPos());
            }
            else{
                StringBuffer a=new StringBuffer(""),b=new StringBuffer("");
                int k=0,isnegate=0;
                for(int i=0;i<uint1.length();i++){
                    if(uint1.charAt(i)=='E'||uint1.charAt(i)=='e'){
                        k=1;
                    }
                    else if(uint1.charAt(i)=='-'){
                        isnegate=1;
                    }
                    else {
                        if (k == 0) {
                            a.append(uint1.charAt(i));
                        }
                        else{
                            b.append(uint1.charAt(i));
                        }
                    }
                }
                String a1=new String(a);
                String b1=new String(b);
                double a2=Double.parseDouble(a1);
                double b2=Double.parseDouble(b1);
                double x;
                if(isnegate==0){
                    x=a2*(Math.pow(10,b2));
                }
                else{
                    x=a2*(Math.pow(0.1,b2));
                }
                return new Token(TokenType.Double, x, ptrstart, it.currentPos());
            }
        }
        else if(isD==1){
            if(isE==0) {
                double a = Double.parseDouble(uint1);
                return new Token(TokenType.Double, a, ptrstart, it.currentPos());
            }
            else{
                StringBuffer a=new StringBuffer(""),b=new StringBuffer("");
                int k=0,isnegate=0;
                for(int i=0;i<uint1.length();i++){
                    if(uint1.charAt(i)=='E'||uint1.charAt(i)=='e'){
                        k=1;
                    }
                    else if(uint1.charAt(i)=='-'){
                        isnegate=1;
                    }
                    else {
                        if (k == 0) {
                            a.append(uint1.charAt(i));
                        }
                        else{
                            b.append(uint1.charAt(i));
                        }
                    }
                }
                String a1=new String(a);
                String b1=new String(b);
                double a2=Double.parseDouble(a1);
                double b2=Double.parseDouble(b1);
                double x;
                if(isnegate==0){
                    x=a2*(Math.pow(10,b2));
                }
                else{
                    x=a2*(Math.pow(0.1,b2));
                }
                return new Token(TokenType.Double, x, ptrstart, it.currentPos());
            }
        }
        else{
            throw new TokenizeError(ErrorCode.InvalidInput,ptrstart);
        }
    }

    private Token lexString() throws TokenizeError{//判断是否为字符串
        Pos ptrstart=it.previousPos();
        StringBuffer sb=new StringBuffer("");
        it.nextChar();//读取第一个"
        while(it.peekChar()!='"'){
            if(it.peekChar()=='\\'){
                char ch;
                it.nextChar();
                if(it.peekChar()=='\''){
                    ch='\'';
                }
                else if(it.peekChar()=='"'){
                    ch='"';
                }
                else if(it.peekChar()=='\\'){
                    ch='\\';
                }
                else if(it.peekChar()=='n'){
                    ch='\n';
                }
                else if(it.peekChar()=='t'){
                    ch='\t';
                }
                else if(it.peekChar()=='r'){
                    ch='\r';
                }
                else{
                    throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
                }
                sb.append(ch);
                it.nextChar();
            }
            else {
                sb.append(it.nextChar());
            }
        }
        it.nextChar();//读取第二个"
        String str=new String(sb);
        return new Token(TokenType.Str, str, ptrstart, it.currentPos());
    }

    private Token lexChar() throws TokenizeError{//判断是否为字符
        Pos ptrstart=it.previousPos();
        StringBuffer sb=new StringBuffer("");
        it.nextChar();//读取第一个'
        int k=-1;
        while(it.peekChar()!='\''){
            if(it.peekChar()=='\\'){
                char ch;
                it.nextChar();
                if(it.peekChar()=='\''){
                    ch='\'';
                    k=('\'');
                }
                else if(it.peekChar()=='"'){
                    ch='"';
                    k='"';
                }
                else if(it.peekChar()=='\\'){
                    ch='\\';
                    k='\\';
                }
                else if(it.peekChar()=='n'){
                    ch='\n';
                    k='\n';
                }
                else if(it.peekChar()=='t'){
                    ch='\t';
                    k='\t';
                }
                else if(it.peekChar()=='r'){
                    ch='\r';
                    k='\r';
                }
                else{
                    throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
                }
                sb.append(ch);
                it.nextChar();
            }
            else {
                char c=it.nextChar();
                sb.append(c);
                k=c;
            }
        }
        it.nextChar();//读取第二个'
        String str=new String(sb);
        char[] ch=str.toCharArray();
        if(ch.length!=1){
            throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
        }
        //TODO 已修改
        if(k!=-1){
            return new Token(TokenType.Char,k,ptrstart,it.currentPos());
        }
        return new Token(TokenType.Char, (int)str.charAt(0), ptrstart, it.currentPos());
    }

    private Token lexIdentOrKeyword() throws TokenizeError {
        Pos ptrstart=it.previousPos();
        StringBuffer x=new StringBuffer("");
        // -- 前进一个字符，并存储这个字符
        while(Character.isAlphabetic(it.peekChar())||Character.isDigit(it.peekChar())||it.peekChar()=='_'){
            x.append(it.nextChar());
        }
        //
        // 尝试将存储的字符串解释为关键字
        // -- 如果是关键字，则返回关键字类型的 token
        // -- 否则，返回标识符
        String a=new String(x);
        if(a.equals("fn")){
            Token k1=new Token(TokenType.Fn,"fn",ptrstart,it.currentPos());
            return k1;
        }
        else if(a.equals("let")){
            Token k2=new Token(TokenType.Let,"let",ptrstart,it.currentPos());
            return k2;
        }
        else if(a.equals("as")){
            Token k3=new Token(TokenType.As,"as",ptrstart,it.currentPos());
            return k3;
        }
        else if(a.equals("const")){
            Token k4=new Token(TokenType.Const,"const",ptrstart,it.currentPos());
            return k4;
        }
        else if(a.equals("while")){
            Token k5=new Token(TokenType.While,"while",ptrstart,it.currentPos());
            return k5;
        }
        else if(a.equals("if")){
            Token k6=new Token(TokenType.If,"if",ptrstart,it.currentPos());
            return k6;
        }
        else if(a.equals("else")){
            Token k7=new Token(TokenType.Else,"else",ptrstart,it.currentPos());
            return k7;
        }
        else if(a.equals("return")){
            Token k8=new Token(TokenType.Return,"return",ptrstart,it.currentPos());
            return k8;
        }
        else if(a.equals("break")){
            Token k9=new Token(TokenType.Break,"break",ptrstart,it.currentPos());
            return k9;
        }
        else if(a.equals("continue")){
            Token k10=new Token(TokenType.Continue,"continue",ptrstart,it.currentPos());
            return k10;
        }
        else if(a.equals("int")||a.equals("double")||a.equals("void")){
            Token k11=new Token(TokenType.Ty,a,ptrstart,it.currentPos());
            return k11;
        }
        else{
            Token k12=new Token(TokenType.Ident,a,ptrstart,it.currentPos());
            return k12;
        }
        //
        // Token 的 Value 应填写标识符或关键字的字符串
        //throw new Error("Not implemented");
    }

    private Token lexOperatorOrUnknown() throws TokenizeError {//返回
        Pos ptrstart=it.previousPos();
        switch (it.nextChar()) {//读入下一个
            case '+':
                return new Token(TokenType.Plus, '+', it.previousPos(), it.currentPos());

            case '-':
                if(it.peekChar()=='>'){
                    it.nextChar();
                    return new Token(TokenType.Arrow, "->", ptrstart, it.currentPos());
                }
                return new Token(TokenType.Minus, '-', it.previousPos(), it.currentPos());

            case '*':
                return new Token(TokenType.Mult, '*', it.previousPos(), it.currentPos());

            case '/':
                if(it.peekChar()=='/'){
                    it.nextChar();
                    while(it.peekChar()!='\n'){
                        it.nextChar();
                    }
                    it.nextChar();
                    return nextToken();
                }
                return new Token(TokenType.Div, '/', it.previousPos(), it.currentPos());

            case '=':
                if(it.peekChar()=='='){
                    it.nextChar();
                    return new Token(TokenType.Eq, "==", ptrstart, it.currentPos());
                }
                return new Token(TokenType.Assign, '=', it.previousPos(), it.currentPos());

            case '!':
                if(it.peekChar()=='='){
                    it.nextChar();
                    return new Token(TokenType.Neq, "!=", ptrstart, it.currentPos());
                }
                throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());

            case '<':
                if(it.peekChar()=='='){
                    it.nextChar();
                    return new Token(TokenType.Le, "<=", ptrstart, it.currentPos());
                }
                return new Token(TokenType.Lt, '<', it.previousPos(), it.currentPos());

            case '>':
                if(it.peekChar()=='='){
                    it.nextChar();
                    return new Token(TokenType.Ge, ">=", ptrstart, it.currentPos());
                }
                return new Token(TokenType.Gt, '>', it.previousPos(), it.currentPos());

            case '(':
                return new Token(TokenType.LParen, '(', it.previousPos(), it.currentPos());

            case ')':
                return new Token(TokenType.RParen, ')', it.previousPos(), it.currentPos());

            case '{':
                return new Token(TokenType.Lbrace, '{', it.previousPos(), it.currentPos());

            case '}':
                return new Token(TokenType.Rbrace, '}', it.previousPos(), it.currentPos());

            case ',':
                return new Token(TokenType.Comma, ',', it.previousPos(), it.currentPos());

            case ':':
                return new Token(TokenType.Colon, ':', it.previousPos(), it.currentPos());

            case ';':
                return new Token(TokenType.Semicolon, ';', it.previousPos(), it.currentPos());

            default:
                // 不认识这个输入，摸了
                throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
        }
    }

    private void skipSpaceCharacters() {
        while (!it.isEOF() && Character.isWhitespace(it.peekChar())) {
            it.nextChar();
        }
    }
}
