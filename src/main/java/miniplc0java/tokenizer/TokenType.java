package miniplc0java.tokenizer;

public enum TokenType {
    /** fn */
    Fn,
    /** let */
    Let,
    /** as */
    As,
    /** Const */
    Const,
    /* while */
    While,
    /* if */
    If,
    /* else */
    Else,
    /* return */
    Return,
    /* break */
    Break,
    /* continue */
    Continue,

    /** 空 */
    None,
    /** 无符号整数 */
    Uint,
    /* 浮点数 */
    Double,
    /** 标识符 */
    Ident,
    /* 字符串常量 */
    Str,
    /* 字符常量 */
    Char,

    /** 加号 +*/
    Plus,
    /** 减号 -*/
    Minus,
    /** 乘号 **/
    Mult,
    /** 除号 /*/
    Div,
    /** 等号 =*/
    Assign,
    /* eq == */
    Eq,
    /* neq != */
    Neq,
    /* lt < */
    Lt,
    /* gt > */
    Gt,
    /* le <= */
    Le,
    /* gr >= */
    Ge,
    /** 左括号 (*/
    LParen,
    /** 右括号 )*/
    RParen,
    /* lbrace { */
    Lbrace,
    /* rbrace } */
    Rbrace,
    /* arrow -> */
    Arrow,
    /* comma , */
    Comma,
    /*  colon : */
    Colon,
    /** 分号 ；*/
    Semicolon,
    /** 注释 // */
    Comment,
    /* 系统 */
    Ty,
    EOF;

    @Override
    public String toString() {
        switch (this) {
            case Fn:
                return "Fn";
            case Let:
                return "Let";
            case As:
                return "As";
            case Const:
                return "Const";
            case While:
                return "While";
            case If:
                return "If";
            case Else:
                return "Else";
            case Return:
                return "Return";
            case Break:
                return "Break";
            case Continue:
                return "Continue";

            case None:
                return "NullToken";
            case Ident:
                return "Identifier";
            case Uint:
                return "UnsignedInteger";
            case Double:
                return "Double";
            case Str:
                return "Str";
            case Char:
                return "Char";

            case Plus:
                return "PlusSign";
            case Minus:
                return "MinusSign";
            case Mult:
                return "MultiplicationSign";
            case Div:
                return "DivisionSign";
            case Assign:
                return "Assign";
            case Eq:
                return "Eq";
            case Neq:
                return "Neq";
            case Lt:
                return "Lt";
            case Gt:
                return "Gt";
            case Le:
                return "Le";
            case Ge:
                return "Ge";
            case LParen:
                return "LeftBracket";
            case RParen:
                return "RightBracket";
            case Lbrace:
                return "Lbrace";
            case Rbrace:
                return "Rbrace";
            case Arrow:
                return "Arrow";
            case Comma:
                return "Comma";
            case Colon:
                return "Colon";
            case Semicolon:
                return "Semicolon";
            case Comment:
                return "Comment";
            case Ty:
                return "Ty";
            case EOF:
                return "EOF";
            default:
                return "InvalidToken";
        }
    }
}
