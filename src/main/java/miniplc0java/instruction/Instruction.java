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

    public Instruction() {
        this.opt = Operation.LIT;
        this.x = 0;
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
            case push://压栈
            case store64://将栈顶元素往具体位置存贮
            case EQ:
            case GE:
            case GT:
            case LE:
            case LT:
            case AS1:
            case AS2:
            case Jmp:
                return String.format("%s %s",this.opt,this.x);
            case NEQ:
            case pop:
            case ASSIGN:
            case ADD:
            case DIV:
            case ILL:
            case MUL:
            case SUB:
            case WRT:
            case LIT:
            case LOD:
            case STO:
                return String.format("%s %s", this.opt, this.x);
            default:
                return "ILL";
        }
    }
}
