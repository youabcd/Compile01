package miniplc0java.instruction;

public class GlobalSymbol {
    private boolean isConst;
    private String varName;

    public GlobalSymbol(String name, boolean isConst){
        this.varName = name;
        this.isConst = isConst;
    }

    public boolean getIsConst() {
        return isConst;
    }
    public String getVarName() {
        return varName;
    }

    public void setConst(boolean aConst) {
        isConst = aConst;
    }
    public void setVarName(String varName) {
        this.varName = varName;
    }
}
