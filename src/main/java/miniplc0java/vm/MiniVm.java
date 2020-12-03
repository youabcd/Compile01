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
            case ADD: {//加栈顶的两位，push到栈顶
                var a = pop();
                var b = pop();
                push(a + b);
            }
                break;
            case DIV: {
                var b = pop();
                var a = pop();
                push(a / b);
            }
                break;
            case ILL: {//报错
                throw new Error("Illegal instruction");
            }
            case LIT: {//压栈至栈顶
                //push(inst.getX());
            }
                break;
            case LOD: {//找到栈内的值再push到栈顶
               // var x = stack.get(inst.getX());
                //push(x);
            }
                break;
            case MUL: {
                var b = pop();
                var a = pop();
                push(a * b);
            }
                break;
            case STO: {//pop栈顶，将值赋给栈内某个位置
                //var x = pop();
                //stack.set(inst.getX(), x);
            }
                break;
            case SUB: {
                var b = pop();
                var a = pop();
                push(a - b);
            }
                break;
            case WRT: {//输出
                var b = pop();
                out.printf("%d\n", b);
            }
                break;
            default:
                break;

        }
    }
}
