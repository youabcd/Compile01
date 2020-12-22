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

    public static HashMap<String, Number> OPERATE = new HashMap<>();
    static{
        OPERATE.put("Nop", 0x00);OPERATE.put("Push", 0x01);
        OPERATE.put("Pop", 0x02);OPERATE.put("Popn", 0x03);
        OPERATE.put("Dup", 0x04);OPERATE.put("LocA", 0x0a);
        OPERATE.put("ArgA", 0x0b);OPERATE.put("GlobA", 0x0c);
        OPERATE.put("Load8", 0x10);OPERATE.put("Load16", 0x11);
        OPERATE.put("Load32", 0x12);OPERATE.put("Load64", 0x13);
        OPERATE.put("Store8", 0x14);OPERATE.put("Store16", 0x15);
        OPERATE.put("Store32", 0x16);OPERATE.put("Store64", 0x17);
        OPERATE.put("Alloc", 0x18);OPERATE.put("Free", 0x19);
        OPERATE.put("StackAlloc", 0x1a);OPERATE.put("AddI", 0x20);
        OPERATE.put("SubI", 0x21);OPERATE.put("MulI", 0x22);
        OPERATE.put("DivI", 0x23);OPERATE.put("AddF", 0x24);
        OPERATE.put("SubF", 0x25);OPERATE.put("MulF", 0x26);
        OPERATE.put("DivF", 0x27);OPERATE.put("DivU", 0x28);
        OPERATE.put("Shl", 0x29);OPERATE.put("Shr", 0x2a);
        OPERATE.put("And", 0x2b);OPERATE.put("Or", 0x2c);
        OPERATE.put("Xor", 0x2d);OPERATE.put("Not", 0x2e);
        OPERATE.put("CmpI", 0x30);
        OPERATE.put("CmpF", 0x32);OPERATE.put("CmpU", 0x31);
        OPERATE.put("NegI", 0x34);OPERATE.put("NegF", 0x35);
        OPERATE.put("ItoF", 0x36);OPERATE.put("FtoI", 0x37);
        OPERATE.put("Shrl", 0x38);OPERATE.put("SetLt", 0x39);
        OPERATE.put("SetGt", 0x3a);OPERATE.put("Br", 0x41);
        OPERATE.put("BrFalse", 0x42);OPERATE.put("BrTrue", 0x43);
        OPERATE.put("Call", 0x48);OPERATE.put("Ret", 0x49);
        OPERATE.put("CallName", 0x4a);OPERATE.put("ScanI", 0x50);
        OPERATE.put("ScanC", 0x51);OPERATE.put("ScanF", 0x52);
        OPERATE.put("PrintI", 0x54);OPERATE.put("PrintC", 0x55);
        OPERATE.put("PrintF", 0x56);OPERATE.put("PrintS", 0x57);
        OPERATE.put("PrintLN", 0x58);OPERATE.put("Panic", 0xfe);
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
        return OPERATE.get(String.valueOf(this.opt)).intValue();
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
            case Call:
            case Push:
            case GlobA:
            case LocA:
            case StackAlloc:
            case BrTrue:
            case Br:
            case BrFalse:
            case ArgA:
            case CallName:
                return true;
            /*case Pop:
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
            case Load64:
            case Panic:*/
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        switch (this.opt) {
            case Push:
                return String.format("%s(%s)",this.opt,this.x);
            case Br:
                return String.format("%s(%s)",this.opt,this.x);
            case Call:
                return String.format("%s(%s)",this.opt,this.x);
            case LocA:
                return String.format("%s(%s)",this.opt,this.x);
            case GlobA:
                return String.format("%s(%s)",this.opt,this.x);
            case StackAlloc:
                return String.format("%s(%s)",this.opt,this.x);
            case BrTrue:
                return String.format("%s(%s)",this.opt,this.x);
            case BrFalse:
                return String.format("%s(%s)",this.opt,this.x);
            case CallName:
                return String.format("%s(%s)",this.opt,this.x);
            case ArgA:
                return String.format("%s(%s)",this.opt,this.x);

            case Pop:
                return String.format("%s",this.opt);
            case Not:
                return String.format("%s",this.opt);
            case Ret:
                return String.format("%s",this.opt);
            case AddF:
                return String.format("%s",this.opt);
            case AddI:
                return String.format("%s",this.opt);
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
            case PrintLN:
                return String.format("%s",this.opt);
            case Store64:
                return String.format("%s",this.opt);

            default:
                return "Panic";
        }
    }
}
