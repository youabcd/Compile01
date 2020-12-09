package miniplc0java.instruction;

import java.util.Objects;
import java.util.HashMap;

public class Instruction {
    private Operation opt;
    long x;
    int y;

    public Instruction(Operation opt) {
        this.opt = opt;
        this.x = 0;
        this.y=0;
    }

    public Instruction(Operation opt, long x) {
        this.opt = opt;
        this.x = x;
        this.y=0;
    }

    public Instruction(Operation opt, long x,int y) {
        this.opt = opt;
        this.x = x;
        this.y=y;
    }

    public static HashMap<String, Number> INSTRUCT = new HashMap<>();
    static{
        INSTRUCT.put("Nop", 0x00);INSTRUCT.put("Push", 0x01);
        INSTRUCT.put("Pop", 0x02);INSTRUCT.put("Popn", 0x03);
        INSTRUCT.put("Dup", 0x04);INSTRUCT.put("LocA", 0x0a);
        INSTRUCT.put("ArgA", 0x0b);INSTRUCT.put("GlobA", 0x0c);
        INSTRUCT.put("Load8", 0x10);INSTRUCT.put("Load16", 0x11);
        INSTRUCT.put("Load32", 0x12);INSTRUCT.put("Load64", 0x13);
        INSTRUCT.put("Store8", 0x14);INSTRUCT.put("Store16", 0x15);
        INSTRUCT.put("Store32", 0x16);INSTRUCT.put("Store64", 0x17);
        INSTRUCT.put("Alloc", 0x18);INSTRUCT.put("Free", 0x19);
        INSTRUCT.put("StackAlloc", 0x1a);INSTRUCT.put("AddI", 0x20);
        INSTRUCT.put("SubI", 0x21);INSTRUCT.put("MulI", 0x22);
        INSTRUCT.put("DivI", 0x23);INSTRUCT.put("AddF", 0x24);
        INSTRUCT.put("SubF", 0x25);INSTRUCT.put("MulF", 0x26);
        INSTRUCT.put("DivF", 0x27);INSTRUCT.put("DivU", 0x28);
        INSTRUCT.put("Shl", 0x29);INSTRUCT.put("Shr", 0x2a);
        INSTRUCT.put("And", 0x2b);INSTRUCT.put("Or", 0x2c);
        INSTRUCT.put("Xor", 0x2d);INSTRUCT.put("Not", 0x2e);
        INSTRUCT.put("CmpI", 0x30);
        INSTRUCT.put("CmpF", 0x32);INSTRUCT.put("CmpU", 0x31);
        INSTRUCT.put("NegI", 0x34);INSTRUCT.put("NegF", 0x35);
        INSTRUCT.put("ItoF", 0x36);INSTRUCT.put("FtoI", 0x37);
        INSTRUCT.put("Shrl", 0x38);INSTRUCT.put("SetLt", 0x39);
        INSTRUCT.put("SetGt", 0x3a);INSTRUCT.put("Br", 0x41);
        INSTRUCT.put("BrFalse", 0x42);INSTRUCT.put("BrTrue", 0x43);
        INSTRUCT.put("Call", 0x48);INSTRUCT.put("Ret", 0x49);
        INSTRUCT.put("CallName", 0x4a);INSTRUCT.put("ScanI", 0x50);
        INSTRUCT.put("ScanC", 0x51);INSTRUCT.put("ScanF", 0x52);
        INSTRUCT.put("PrintI", 0x54);INSTRUCT.put("PrintC", 0x55);
        INSTRUCT.put("PrintF", 0x56);INSTRUCT.put("PrintS", 0x57);
        INSTRUCT.put("PrintLN", 0x58);INSTRUCT.put("Panic", 0xfe);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Instruction that = (Instruction) o;
        return opt == that.opt && Objects.equals(x, that.x);
    }

    @Override
    public int hashCode() {
        return Objects.hash(opt, x);
    }

    public Operation getOpt() {
        return opt;
    }

    public void setOpt(Operation opt) {
        this.opt = opt;
    }

    public long getX() {
        return x;
    }

    public int getOptValue(){
        return INSTRUCT.get(String.valueOf(this.opt)).intValue();
    }

    public int getIntX(){
        return (int)x;
    }

    public void setX(long x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean hasX(){
        switch (this.opt) {
            case Pop:

            case Not:
            case Ret:
            case AddF:
            case AddI:
            case CmpF:
            case CmpI:
            case DivF:
            case DivI:
            case FtoI:
            case ItoF:
            case MulF:
            case MulI:
            case NegF:
            case NegI:
            case SubF:
            case SubI:
            case Store64:
            case SetGt:
            case SetLt:
            case PrintC:
            case PrintF:
            case PrintI:
            case ScanC:
            case PrintS:
            case ScanF:
            case PrintLN:
            case ScanI:
            case Panic:
                return false;
            case Call:
            case Push:
            case GlobA:
            case LocA:
            case StackAlloc:
            case BrTrue:
            case Br:
            case Load64:
            case BrFalse:
            case ArgA:
            case CallName:
                return true;
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        switch (this.opt) {
            case Push:
                return String.format("%s(%s)",this.opt,this.x);
            case Pop:
                return String.format("%s",this.opt);
            case Br:
                return String.format("%s(%s)",this.opt,this.x);
            case Not:
                return String.format("%s",this.opt);
            case Ret:
                return String.format("%s",this.opt);
            case AddF:
                return String.format("%s",this.opt);
            case AddI:
                return String.format("%s",this.opt);
            case Call:
                return String.format("%s(%s)",this.opt,this.x);
            case CmpF:
                return String.format("%s",this.opt);
            case CmpI:
                return String.format("%s",this.opt);
            case DivF:
                return String.format("%s",this.opt);
            case DivI:
                return String.format("%s",this.opt);
            case FtoI:
                return String.format("%s",this.opt);
            case ItoF:
                return String.format("%s",this.opt);
            case LocA:
                return String.format("%s(%s)",this.opt,this.x);
            case MulF:
                return String.format("%s",this.opt);
            case MulI:
                return String.format("%s",this.opt);
            case NegF:
                return String.format("%s",this.opt);
            case NegI:
                return String.format("%s",this.opt);
            case SubF:
                return String.format("%s",this.opt);
            case SubI:
                return String.format("%s",this.opt);
            case GlobA:
                return String.format("%s(%s)",this.opt,this.x);
            case Panic:
                return String.format("%s",this.opt);
            case ScanC:
                return String.format("%s",this.opt);
            case ScanF:
                return String.format("%s",this.opt);
            case ScanI:
                return String.format("%s",this.opt);
            case SetGt:
                return String.format("%s",this.opt);
            case SetLt:
                return String.format("%s",this.opt);
            case StackAlloc:
                return String.format("%s(%s)",this.opt,this.x);
            case BrTrue:
                return String.format("%s(%s)",this.opt,this.x);
            case Load64:
                return String.format("%s",this.opt);
            case PrintC:
                return String.format("%s",this.opt);
            case PrintF:
                return String.format("%s",this.opt);
            case PrintI:
                return String.format("%s",this.opt);
            case PrintS:
                return String.format("%s",this.opt);
            case BrFalse:
                return String.format("%s(%s)",this.opt,this.x);
            case PrintLN:
                return String.format("%s",this.opt);
            case Store64:
                return String.format("%s",this.opt);
            case CallName:
                return String.format("%s(%s)",this.opt,this.x);
            case ArgA:
                return String.format("%s(%s)",this.opt,this.x);
            default:
                return "Panic";
        }
    }
}
