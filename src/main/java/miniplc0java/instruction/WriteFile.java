package miniplc0java.instruction;

import java.io.*;
import java.util.HashMap;

public class WriteFile {
    public static void writeO0File(MidCode midCode, String outFileName){
        try{
            FileOutputStream binaryContent = new FileOutputStream(outFileName);
            binaryContent.write(getBinary(midCode.magic, 4));
            binaryContent.write(getBinary(midCode.version, 4));
            binaryContent.write(getBinary(midCode.getGlobalCounts(), 4));

            for(int i=0; i<midCode.gdList.size(); i++){
                if(midCode.gdList.get(i).getIsConst()){
                    binaryContent.write(getBinary(1, 1));
                }
                else{
                    binaryContent.write(getBinary(0, 1));
                }
                binaryContent.write(getBinary(8, 4));
                binaryContent.write(getBinary(0, 8));
            }
            
            for(int i=midCode.getGlobalVarNum(); i<midCode.globalSymbol.size(); i++){
                binaryContent.write(getBinary(1, 1));
                binaryContent.write(getBinary(midCode.globalSymbol.get(i).length(), 4));
                binaryContent.write(getBinary(midCode.globalSymbol.get(i)));
            }

            binaryContent.write(getBinary(midCode.funcList.size(), 4));

            for(FunctionList f: midCode.funcList){
                if(f.getFuncName().equals("_start")) {
                    binaryContent.write(getBinary(f.getFuncNumber(), 4));
                    binaryContent.write(getBinary(f.getReturnSlots(), 4));
                    binaryContent.write(getBinary(f.getParamSlots(), 4));
                    binaryContent.write(getBinary(f.getLocSlots(), 4));

                    binaryContent.write(getBinary(f.getFuncBodyCount(), 4));

                    for (Instruction ins : f.getFuncBody()) {
                        binaryContent.write(getBinary(OPERATE.get(String.valueOf(ins.getOpt())).intValue(),1));
                        if (ins.hasParam())
                            binaryContent.write(getBinary(ins.getX(), ins.getY()));
                    }
                }
//                else{
//                    binaryContent.write(getBinary(f.getFnNumber(), 4));
//                    binaryContent.write(getBinary(f.getReturnSlots(), 4));
//                    binaryContent.write(getBinary(f.getParamSlots(), 4));
//                    binaryContent.write(getBinary(f.getLocSlots(), 4));
//
//                    binaryContent.write(getBinary(f.getFuncBodyCount(), 4));
//
//                    for (Instruction ins : f.getFuncBody()) {
//                        binaryContent.write(getBinary(ins.getOptValue(), 1));
//                        if (ins.hasX())
//                            binaryContent.write(getBinary(ins.getX(), ins.getY()));
//                    }
//                }
            }
            for(FunctionList f: midCode.funcList){
                if(!f.getFuncName().equals("_start")) {
                    binaryContent.write(getBinary(f.getFuncNumber(), 4));
                    binaryContent.write(getBinary(f.getReturnSlots(), 4));
                    binaryContent.write(getBinary(f.getParamSlots(), 4));
                    binaryContent.write(getBinary(f.getLocSlots(), 4));

                    binaryContent.write(getBinary(f.getFuncBodyCount(), 4));

                    for (Instruction ins : f.getFuncBody()) {
                        binaryContent.write(getBinary(OPERATE.get(String.valueOf(ins.getOpt())).intValue(),1));
                        if (ins.hasParam())
                            binaryContent.write(getBinary(ins.getX(), ins.getY()));
                    }
                }
            }
            binaryContent.close();

        } catch(Exception ignored){}

    }

    public static byte[] getBinary(long x, int y){
        if(y==8){
            return new byte[]{(byte)((x>>56) & 0xFF), (byte)((x>>48) & 0xFF), (byte)((x>>40) & 0xFF), (byte)((x>>32) & 0xFF),
                    (byte)((x>>24) & 0xFF),(byte)((x>>16) & 0xFF),(byte)((x>>8) & 0xFF),(byte)(x&0xFF)};
        }
        else if(y==4){
            x=(int)x;
            return new byte[]{(byte)((x>>24) & 0xFF),(byte)((x>>16) & 0xFF),(byte)((x>>8) & 0xFF),(byte)(x&0xFF)};
        }
        else{
            return new byte[]{(byte)(x&0xFF)};
        }
    }

    public static byte[] getBinary(String s){
        return s.getBytes();
    }

    public static HashMap<String, Number> OPERATE = new HashMap<>();
    static{
        OPERATE.put("Nop",0x00);OPERATE.put("Push",0x01);
        OPERATE.put("Pop",0x02);OPERATE.put("Popn",0x03);
        OPERATE.put("Dup",0x04);OPERATE.put("LocA",0x0a);
        OPERATE.put("ArgA",0x0b);OPERATE.put("GlobA",0x0c);
        OPERATE.put("Load8",0x10);OPERATE.put("Load16",0x11);
        OPERATE.put("Load32",0x12);OPERATE.put("Load64",0x13);
        OPERATE.put("Store8",0x14);OPERATE.put("Store16",0x15);
        OPERATE.put("Store32",0x16);OPERATE.put("Store64",0x17);
        OPERATE.put("Alloc",0x18);OPERATE.put("Free",0x19);
        OPERATE.put("StackAlloc",0x1a);OPERATE.put("AddI",0x20);
        OPERATE.put("SubI",0x21);OPERATE.put("MulI",0x22);
        OPERATE.put("DivI",0x23);OPERATE.put("AddF",0x24);
        OPERATE.put("SubF",0x25);OPERATE.put("MulF",0x26);
        OPERATE.put("DivF",0x27);OPERATE.put("DivU",0x28);
        OPERATE.put("Shl",0x29);OPERATE.put("Shr",0x2a);
        OPERATE.put("And",0x2b);OPERATE.put("Or",0x2c);
        OPERATE.put("Xor",0x2d);OPERATE.put("Not",0x2e);
        OPERATE.put("CmpI",0x30);
        OPERATE.put("CmpF",0x32);OPERATE.put("CmpU",0x31);
        OPERATE.put("NegI",0x34);OPERATE.put("NegF",0x35);
        OPERATE.put("ItoF",0x36);OPERATE.put("FtoI",0x37);
        OPERATE.put("Shrl",0x38);OPERATE.put("SetLt",0x39);
        OPERATE.put("SetGt",0x3a);OPERATE.put("Br",0x41);
        OPERATE.put("BrFalse",0x42);OPERATE.put("BrTrue",0x43);
        OPERATE.put("Call",0x48);OPERATE.put("Ret",0x49);
        OPERATE.put("CallName",0x4a);OPERATE.put("ScanI",0x50);
        OPERATE.put("ScanC",0x51);OPERATE.put("ScanF",0x52);
        OPERATE.put("PrintI",0x54);OPERATE.put("PrintC",0x55);
        OPERATE.put("PrintF",0x56);OPERATE.put("PrintS",0x57);
        OPERATE.put("PrintLN",0x58);OPERATE.put("Panic",0xfe);
    }
}
