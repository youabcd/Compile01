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
    public long getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public void setOpt(Operation opt) {
        this.opt = opt;
    }
    public void setX(long x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }

    public boolean hasParam(){
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
