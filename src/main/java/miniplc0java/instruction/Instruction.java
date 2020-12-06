package miniplc0java.instruction;

import java.util.Objects;

public class Instruction {
    private Operation opt;
    long x;

    public Instruction(Operation opt) {
        this.opt = opt;
        this.x = 0;
    }

    public Instruction(Operation opt, long x) {
        this.opt = opt;
        this.x = x;
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

    public void setX(long x) {
        this.x = x;
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
            default:
                return "Panic";
        }
    }
}
