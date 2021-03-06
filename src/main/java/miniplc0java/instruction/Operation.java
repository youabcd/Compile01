package miniplc0java.instruction;

public enum Operation {
    Push,
    Pop,
    LocA,
    GlobA,
    Load64,
    Store64,
    AddI,
    SubI,
    MulI,
    DivI,
    AddF,
    SubF,
    MulF,
    DivF,
    Not,
    CmpI,
    CmpF,
    NegI,
    NegF,
    ItoF,
    FtoI,
    SetLt,
    SetGt,
    Br,
    BrTrue,
    BrFalse,
    Call,
    Ret,
    CallName,
    ScanI,
    ScanC,
    ScanF,
    PrintI,
    PrintC,
    PrintF,
    PrintS,
    PrintLN,
    StackAlloc,
    ArgA,
    Panic
}
