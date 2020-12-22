package miniplc0java.instruction;

import java.io.*;

public class WriteFile {
    public static void writeO0File(MidCode midCode, String outFileName){
        try{
            FileOutputStream binaryContent = new FileOutputStream(outFileName);
            binaryContent.write(getBinary(midCode.magic, 4));
            binaryContent.write(getBinary(midCode.version, 4));
            binaryContent.write(getBinary(midCode.getGlobalCounts(), 4));

            for(int i=0; i<midCode.gdList.size(); i++){
                if(midCode.gdList.get(i).isConst()){
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
                    binaryContent.write(getBinary(f.getFnNumber(), 4));
                    binaryContent.write(getBinary(f.getReturnSlots(), 4));
                    binaryContent.write(getBinary(f.getParamSlots(), 4));
                    binaryContent.write(getBinary(f.getLocSlots(), 4));

                    binaryContent.write(getBinary(f.getFuncBodyCount(), 4));

                    for (Instruction ins : f.getFuncBody()) {
                        binaryContent.write(getBinary(ins.getOptValue(), 1));
                        if (ins.hasX())
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
                    binaryContent.write(getBinary(f.getFnNumber(), 4));
                    binaryContent.write(getBinary(f.getReturnSlots(), 4));
                    binaryContent.write(getBinary(f.getParamSlots(), 4));
                    binaryContent.write(getBinary(f.getLocSlots(), 4));

                    binaryContent.write(getBinary(f.getFuncBodyCount(), 4));

                    for (Instruction ins : f.getFuncBody()) {
                        binaryContent.write(getBinary(ins.getOptValue(), 1));
                        if (ins.hasX())
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



}
