package miniplc0java.instruction;

public class FunctionParam {
    private String paramName;
    private boolean isConst;
    private String type;
    public FunctionParam(String paramName, boolean isConst, String type){
        this.paramName = paramName;
        this.isConst = isConst;
        this.type = type;
    }
    public String getParamName() {
        return paramName;
    }
    public boolean getConst() {
        return isConst;
    }
    public String getType() {
        return type;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }
    public void setConst(boolean aConst) {
        isConst = aConst;
    }
    public void setType(String type) {
        this.type = type;
    }
}
