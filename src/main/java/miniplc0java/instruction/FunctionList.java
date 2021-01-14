package miniplc0java.instruction;
import miniplc0java.error.AnalyzeError;
import miniplc0java.error.ErrorCode;
import miniplc0java.util.Pos;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class FunctionList {
    public static HashMap<String, String> libFunc=new HashMap<>();
    static {
        libFunc.put("getint","int"); libFunc.put("getdouble","double"); libFunc.put("putdouble","void");
        libFunc.put("getchar","int"); libFunc.put("putint","void"); libFunc.put("putchar","void");
        libFunc.put("putstr","void"); libFunc.put("putln","void");
    }

    public String funcName;
    public int returnSlots=0;
    public String returnType;
    public boolean returned;
    public int paramSlots=0;
    public ArrayList<FunctionParam> paramTable = new ArrayList<>();
    public int locSlots=0;
    public ArrayList<Instruction> funcBody = new ArrayList<>();
    public FunctionList(String funcName){
        this.funcName=funcName;
    }

    public String getFuncName() {
        return funcName;
    }
    public int getReturnSlots() {
        return returnSlots;
    }
    public int getParamSlots() {
        return paramSlots;
    }
    public int getLocSlots() {
        return locSlots;
    }
    public ArrayList<Instruction> getFuncBody() {
        return funcBody;
    }
    public String getReturnType() {
        return returnType;
    }
    public ArrayList<FunctionParam> getParamTable() {
        return paramTable;
    }
    public boolean getIsReturned() {
        return returned;
    }
    public int getFuncBodyCount(){
        return this.funcBody.size();
    }
    public int getFuncNumber(){
        return MidCode.getMidCode().getFuncNumber(this.funcName);
    }
    public void setFuncName(String funcName) {
        this.funcName=funcName;
    }
    public void setReturnSlots(int returnSlots) {
        this.returnSlots=returnSlots;
    }
    public void setParamSlots(int paramSlots) {
        this.paramSlots=paramSlots;
    }
    public void setLocSlots(int locSlots) {
        this.locSlots=locSlots;
    }
    public void setFuncBody(ArrayList<Instruction> funcBody) {
        this.funcBody=funcBody;
    }
    public void setReturnType(String returnType) {
        this.returnType=returnType;
    }
    public void setParamTable(ArrayList<FunctionParam> paramTable) {
        this.paramTable=paramTable;
    }
    public void setReturned(boolean returned) {
        this.returned=returned;
    }

    public void addInstruction(Instruction i){
        funcBody.add(i);
    }

    public void removeInstruction(int i){funcBody.remove(i);}

    public int getInstructionsLength() {
        return funcBody.size();
    }

    public void setBrInstructionValue(int index, Instruction i){
        funcBody.set(index,i);
    }

    public void addParam(String paramName, boolean isConst, String paramType, Pos curPos) throws AnalyzeError {
        for(FunctionParam f: paramTable){
            if(f.getParamName().equals(paramName))
                throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
        }
        paramTable.add(new FunctionParam(paramName, isConst, paramType));
        paramSlots ++;
    }

    public void addLoc(){
        this.locSlots ++;
    }

    public int getNextLocOffset(){
        return this.locSlots-1;
    }

    public void setReturn(String ty){
        if(!ty.equals("void"))
            this.returnSlots = 1;
        this.returnType = ty;
    }

    public int getParamOffset(String name){
        int i=0;
        for(FunctionParam f: paramTable){
            if(f.getParamName().equals(name)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public FunctionParam getOffsetParam(int offset){
        return paramTable.get(offset);
    }

    public boolean haveRet(){
        return this.returnSlots>0;
    }

    public void notInFnParams(String name, Pos curPos) throws AnalyzeError{
        for(FunctionParam f: paramTable){
            if(f.getParamName().equals(name))
                throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
        }
    }

    public void checkParams(Pos curPos, ArrayList<String> params) throws AnalyzeError {
        if(params.size() != this.paramSlots)
            throw new AnalyzeError(ErrorCode.ExpectedToken, curPos);
        for(int i=0; i<params.size(); i++){
            if(!params.get(i).equals(this.paramTable.get(i).getType())){
                throw new AnalyzeError(ErrorCode.StreamError, curPos);
            }
        }
    }

    public void returnFn(String ty, Pos curPos) throws AnalyzeError{
        if(ty.equals(this.returnType)==false){
            throw new AnalyzeError(ErrorCode.ExpectedToken, curPos);
        }
        this.returned = true;
    }

    public boolean checkReturnRoutes(){
        if(this.getReturnType().equals("void")){
            if(!this.funcBody.get(this.funcBody.size()-1).getOpt().equals(Operation.Ret)){
                addInstruction(new Instruction(Operation.Ret));
            }
            return true;
        }
        else {
            return DFS(0,new HashSet<Integer>());
        }
    }

    private boolean DFS(int i,HashSet<Integer> routes){
        if( i>funcBody.size()-1){
            return false;
        }
        else if(routes.contains(i)){
            return true;
        }
        else if(funcBody.get(i).getOpt().equals(Operation.BrTrue)){
            routes.add(i);
            boolean ret=DFS(i+1,routes);
            return ret&&DFS(i+2,routes);
        }
        else if(funcBody.get(i).getOpt().equals(Operation.Br)){
            routes.add(i);
            return DFS(i+(int)funcBody.get(i).getX()+1,routes);
        }
        else if(funcBody.get(i).getOpt().equals(Operation.Ret)){
            return true;
        }
        else{
            routes.add(i);
            return DFS(i+1,routes);
        }
    }

    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append("fn [").append(MidCode.getMidCode().getFuncNumber(this.funcName)).
                append("] ").append(locSlots).append(" ").append(paramSlots).append(" -> ").
                append(returnSlots).append(" {\n");

        int xh=0;
        for(Instruction i : funcBody){
            sb.append(xh+": ");
            sb.append(i).append("\n");
            xh++;
        }
        sb.append("}\n");

        return sb.toString();
    }
}