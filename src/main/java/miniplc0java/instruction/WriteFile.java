package miniplc0java.instruction;

import java.io.*;

public class WriteFile {
    public static void writeO0File(MidCode midCode, String outFileName){
        try{
            FileOutputStream binaryContent = new FileOutputStream(outFileName);
            binaryContent.write(getByteValue(midCode.magic, 4));
            binaryContent.write(getByteValue(midCode.version, 4));
            binaryContent.write(getByteValue(midCode.getGlobalCounts(), 4));

            for(int i=0; i<midCode.gdList.size(); i++){
                if(midCode.gdList.get(i).isConst()){
                    binaryContent.write(getByteValue(1, 1));
                }
                else{
                    binaryContent.write(getByteValue(0, 1));
                }
                binaryContent.write(getByteValue(8, 4));
                binaryContent.write(getByteValue(0, 8));
            }
            for(int i=midCode.getGlobalVarNum(); i<midCode.globalSymbol.size(); i++){
                binaryContent.write(getByteValue(1, 1));
                binaryContent.write(getByteValue(midCode.globalSymbol.get(i).length(), 4));
                binaryContent.write(getByteValue(midCode.globalSymbol.get(i)));
            }

            binaryContent.write(getByteValue(midCode.funcList.size(), 4));

            for(FunctionList f: midCode.funcList){
                if(f.getFuncName().equals("_start")) {
                    binaryContent.write(getByteValue(f.getFnNumber(), 4));
                    binaryContent.write(getByteValue(f.getReturnSlots(), 4));
                    binaryContent.write(getByteValue(f.getParamSlots(), 4));
                    binaryContent.write(getByteValue(f.getLocSlots(), 4));

                    binaryContent.write(getByteValue(f.getFuncBodyCount(), 4));

                    for (Instruction ins : f.getFuncBody()) {
                        binaryContent.write(getByteValue(ins.getOptValue(), 1));
                        if (ins.hasX())
                            binaryContent.write(getByteValue(ins.getX(), ins.getY()));
                    }
                }
//                else{
//                    binaryContent.write(getByteValue(f.getFnNumber(), 4));
//                    binaryContent.write(getByteValue(f.getReturnSlots(), 4));
//                    binaryContent.write(getByteValue(f.getParamSlots(), 4));
//                    binaryContent.write(getByteValue(f.getLocSlots(), 4));
//
//                    binaryContent.write(getByteValue(f.getFuncBodyCount(), 4));
//
//                    for (Instruction i : f.getFuncBody()) {
//                        binaryContent.write(getByteValue(i.getOptValue(), 1));
//                        if (i.hasX())
//                            binaryContent.write(getByteValue(i.getX(), i.getY()));
//                    }
//                }
            }
            for(FunctionList f: midCode.funcList){
                if(!f.getFuncName().equals("_start")) {
                    binaryContent.write(getByteValue(f.getFnNumber(), 4));
                    binaryContent.write(getByteValue(f.getReturnSlots(), 4));
                    binaryContent.write(getByteValue(f.getParamSlots(), 4));
                    binaryContent.write(getByteValue(f.getLocSlots(), 4));

                    binaryContent.write(getByteValue(f.getFuncBodyCount(), 4));

                    for (Instruction i : f.getFuncBody()) {
                        binaryContent.write(getByteValue(i.getOptValue(), 1));
                        if (i.hasX())
                            binaryContent.write(getByteValue(i.getX(), i.getY()));
                    }
                }
            }
            binaryContent.close();

        } catch(Exception ignored){}

    }

    public static byte[] getByteValue(long i, int size){
        if(size == 8){
            return new byte[]{(byte)((i >> 56) & 0xFF), (byte)((i >> 48) & 0xFF), (byte)((i >> 40) & 0xFF), (byte)((i >> 32) & 0xFF),
                    (byte)((i >> 24) & 0xFF),(byte)((i >> 16) & 0xFF),(byte)((i >> 8) & 0xFF),(byte)(i & 0xFF)};
        }
        else if(size == 4){
            i = (int) i;
            return new byte[]{(byte)((i >> 24) & 0xFF),(byte)((i >> 16) & 0xFF),(byte)((i >> 8) & 0xFF),(byte)(i & 0xFF)};
        }
        else{
            return new byte[]{(byte)(i & 0xFF)};
        }
    }

    public static byte[] getByteValue(String s){
        return s.getBytes();
    }



}
