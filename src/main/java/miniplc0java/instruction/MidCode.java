package miniplc0java.instruction;
import miniplc0java.error.AnalyzeError;
import miniplc0java.error.ErrorCode;
import miniplc0java.util.Pos;
import java.util.ArrayList;

public class MidCode {
    public int magic=0x72303b3e;
    public int version=0x00000001;
    public String startFunc="_start";
    private int globalVarNum=0;
    public ArrayList<GlobalSymbol> globaList=new ArrayList<>();
    public ArrayList<FunctionList> funcList=new ArrayList<>();
    public ArrayList<String> globalSymbol=new ArrayList<>();

    private static MidCode midCode=new MidCode();

    public static MidCode getMidCode(){
        return midCode;
    }

    public FunctionList getFunc(String funcName,Pos curPos) throws AnalyzeError{
        for(FunctionList f:funcList){
            if(f.getFuncName().equals(funcName)){
                return f;
            }
        }
        throw new AnalyzeError(ErrorCode.ExpectedToken,curPos);
    }
    public int getGlobalCounts(){
        return this.globalSymbol.size();
    }
    public int getGlobalVarNum(){
        return this.globaList.size();
    }
    public int getNextGlobalVarOffset(){
        return globaList.size();
    }
    public int getFnAddress(String funcName) {
        int i=1;
        for(FunctionList f:funcList){
            if(f.getFuncName().equals(funcName)){
                return i;
            }
            i++;
        }
        return -1;
    }//获取函数的偏移量，从1开始
    public int getFuncNumber(String fnName) {
        int i=0;
        for(String s:globalSymbol){
            if(s.equals(fnName)){
                return i;
            }
            i++;
        }
        return -1;
    }

    /*获取全局符号表中某个符号的位置，不存在则返回-1*/
    public int getSymbolAddress(String name){
        return globalSymbol.indexOf(name);
    }

    public void notInGlobalSymbol(String name, Pos curPos) throws AnalyzeError {
        if(globalSymbol.indexOf(name)>=0){
            throw new AnalyzeError(ErrorCode.DuplicateDeclaration,curPos);
        }
    }

    public void addGlobalVar(GlobalSymbol g){
        globaList.add(g);
    }

    public void addGlobalSymbol(String name, Pos curPos) throws AnalyzeError{
        notInGlobalSymbol(name, curPos);
        globalSymbol.add(name);
    }

    public void addGlobalSymbolToLastPos(String name, Pos curPos) throws AnalyzeError{
        notInGlobalSymbol(name, curPos);
        globalSymbol.add(globalSymbol.size()-2, name);
    }
    /*添加一个全局变量进去*/
    public void addGlobalVar(String name, Pos curPos) throws AnalyzeError{
        notInGlobalSymbol(name, curPos);
        globalSymbol.add(globalVarNum++, name);
    }

    /*在全局符号表中插入一条库函数的记录，不会插入到fn列表中*/
    public int insertLibFunctionBefore(String fnName, String libFn){
        int i=0;
        for(String f: globalSymbol){
            if(f.equals(fnName)){
                break;
            }
            i++;
        }
        globalSymbol.add(i, libFn);
        return i;
    }

    /* 添加一个函数块*/
    public void addFunction(FunctionList f){
        funcList.add(f);
    }



    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        for(GlobalSymbol g:globaList){
            sb.append(g.getVarName()).append('\n');
        }
        for(int i=this.getGlobalVarNum();i<this.globalSymbol.size();i++){
            sb.append(this.globalSymbol.get(i)).append('\n');
        }
        sb.append("\n");
        for(FunctionList f:funcList){
            if(f.getFuncName().equals(startFunc)){
                sb.append(f).append('\n');
            }
        }
        for(FunctionList f:funcList){
            if(!f.getFuncName().equals(startFunc)){
                sb.append(f).append('\n');
            }
        }
        return sb.toString();
    }
}
