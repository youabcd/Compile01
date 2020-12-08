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
        INSTRUCT.put("nop", 0x00);INSTRUCT.put("push", 0x01);
        INSTRUCT.put("pop", 0x02);INSTRUCT.put("popn", 0x03);
        INSTRUCT.put("dup", 0x04);INSTRUCT.put("loca", 0x0a);
        INSTRUCT.put("arga", 0x0b);INSTRUCT.put("globa", 0x0c);
        INSTRUCT.put("load_8", 0x10);INSTRUCT.put("load_16", 0x11);
        INSTRUCT.put("load_32", 0x12);INSTRUCT.put("load_64", 0x13);
        INSTRUCT.put("store_8", 0x14);INSTRUCT.put("store_16", 0x15);
        INSTRUCT.put("store_32", 0x16);INSTRUCT.put("store_64", 0x17);
        INSTRUCT.put("alloc", 0x18);INSTRUCT.put("free", 0x19);
        INSTRUCT.put("stackalloc", 0x1a);INSTRUCT.put("add_i", 0x20);
        INSTRUCT.put("sub_i", 0x21);INSTRUCT.put("mul_i", 0x22);
        INSTRUCT.put("div_i", 0x23);INSTRUCT.put("add_f", 0x24);
        INSTRUCT.put("sub_f", 0x25);INSTRUCT.put("mul_f", 0x26);
        INSTRUCT.put("div_f", 0x27);INSTRUCT.put("div_u", 0x28);
        INSTRUCT.put("shl", 0x29);INSTRUCT.put("shr", 0x2a);
        INSTRUCT.put("and", 0x2b);INSTRUCT.put("or", 0x2c);
        INSTRUCT.put("xor", 0x2d);INSTRUCT.put("not", 0x2e);
        INSTRUCT.put("cmp_i", 0x30);
        INSTRUCT.put("cmp_f", 0x32);INSTRUCT.put("cmp_u", 0x31);
        INSTRUCT.put("neg_i", 0x34);INSTRUCT.put("neg_f", 0x35);
        INSTRUCT.put("itof", 0x36);INSTRUCT.put("ftoi", 0x37);
        INSTRUCT.put("shrl", 0x38);INSTRUCT.put("set_lt", 0x39);
        INSTRUCT.put("set_gt", 0x3a);INSTRUCT.put("br", 0x41);
        INSTRUCT.put("br_false", 0x42);INSTRUCT.put("br_true", 0x43);
        INSTRUCT.put("call", 0x48);INSTRUCT.put("ret", 0x49);
        INSTRUCT.put("callname", 0x4a);INSTRUCT.put("scan_i", 0x50);
        INSTRUCT.put("scan_c", 0x51);INSTRUCT.put("scan_f", 0x52);
        INSTRUCT.put("print_i", 0x54);INSTRUCT.put("print_c", 0x55);
        INSTRUCT.put("print_f", 0x56);INSTRUCT.put("print_s", 0x57);
        INSTRUCT.put("println", 0x58);INSTRUCT.put("panic", 0xfe);
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
            case Push:
            case Pop:
            case Br:
            case Not:
            case Ret:
            case AddF:
            case AddI:
            case Call:
            case CmpF:
            case CmpI:
            case DivF:
            case DivI:
            case FtoI:
            case ItoF:
            case LocA:
            case MulF:
            case MulI:
            case NegF:
            case NegI:
            case SubF:

            case SubI:

            case GlobA:

            case Panic:
                return false;
            case ScanC:

            case ScanF:

            case ScanI:

            case SetGT:

            case SetLT:

            case StackAlloc:

            case BrTrue:

            case Load64:

            case PrintC:

            case PrintF:

            case PrintI:

            case PrintS:

            case BrFalse:

            case PrintLN:

            case Store64:

            case CallName:
                return true;
            case Arga:

            default:
                return false;
        }
    }

    @Override
    public String toString() {
        switch (this.opt) {
            case Push:
                return String.format("%s %s",this.opt,this.x);
            case Pop:
                return String.format("%s",this.opt);
            case Br:
                return String.format("%s %s",this.opt,this.x);
            case Not:
                return String.format("%s",this.opt);
            case Ret:
                return String.format("%s",this.opt);
            case AddF:
                return String.format("%s",this.opt);
            case AddI:
                return String.format("%s",this.opt);
            case Call:
                return String.format("%s %s",this.opt,this.x);
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
                return String.format("%s %s",this.opt,this.x);
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
                return String.format("%s %s",this.opt,this.x);
            case Panic:
                return String.format("%s",this.opt);
            case ScanC:
                return String.format("%s",this.opt);
            case ScanF:
                return String.format("%s",this.opt);
            case ScanI:
                return String.format("%s",this.opt);
            case SetGT:
                return String.format("%s",this.opt);
            case SetLT:
                return String.format("%s",this.opt);
            case StackAlloc:
                return String.format("%s %s",this.opt,this.x);
            case BrTrue:
                return String.format("%s %s",this.opt,this.x);
            case Load64:
                return String.format("%s %s",this.opt,this.x);
            case PrintC:
                return String.format("%s",this.opt);
            case PrintF:
                return String.format("%s",this.opt);
            case PrintI:
                return String.format("%s",this.opt);
            case PrintS:
                return String.format("%s",this.opt);
            case BrFalse:
                return String.format("%s %s",this.opt,this.x);
            case PrintLN:
                return String.format("%s",this.opt);
            case Store64:
                return String.format("%s",this.opt);
            case CallName:
                return String.format("%s %s",this.opt,this.x);
            case Arga:
            default:
                return "Panic";
        }
    }
}
