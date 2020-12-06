package miniplc0java.vm;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import miniplc0java.instruction.Instruction;

public class MiniVm {
    private List<Instruction> instructions;
    private PrintStream out;

    /**
     * @param instructions
     * @param out
     */
    public MiniVm(List<Instruction> instructions, PrintStream out) {
        this.instructions = instructions;
        this.out = out;
    }

    public MiniVm(List<Instruction> instructions) {
        this.instructions = instructions;
        this.out = System.out;
    }

    private ArrayList<Integer> stack = new ArrayList<>();

    private int ip;

    public void Run() {
        ip = 0;
        while (ip < instructions.size()) {
            var inst = instructions.get(ip);
            RunStep(inst);
            ip++;
        }
    }

    private Integer pop() {
        var val = this.stack.get(this.stack.size() - 1);
        this.stack.remove(this.stack.size() - 1);
        return val;
    }

    private void push(Integer i) {
        this.stack.add(i);
    }

    private void RunStep(Instruction inst) {
        switch (inst.getOpt()) {
            case CallName:{}
            case Store64:{}
            case PrintLN:{}
            case BrFalse:{}
            case PrintS:{}
            case PrintI:{}
            case PrintF:{}
            case PrintC:{}
            case Load64:{}
            case BrTrue:{}
            case SetLT:{}
            case SetGT:{}
            case ScanI:{}
            case ScanF:{}
            case ScanC:{}
            case Panic:{}
            case GlobA:{}
            case SubI:{}
            case SubF:{}
            case NegI:{}
            case NegF:{}
            case MulI:{}
            case MulF:{}
            case LocA:{}
            case ItoF:{}
            case FtoI:{}
            case DivI:{}
            case DivF:{}
            case CmpI:{}
            case CmpF:{}
            case Call:{}
            case AddI:{}
            case AddF:{}
            case Ret:{}
            case Not:{}
            case Br:{}
            case Pop:{}
            case Push:{}
                break;
            default:
                break;
        }
    }
}
