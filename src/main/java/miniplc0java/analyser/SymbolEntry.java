package miniplc0java.analyser;

public class SymbolEntry {
    boolean isConstant;
    boolean isInitialized;
    String type;
    int stackOffset;

    /**
     * @param isConstant
     * @param isDeclared
     * @param stackOffset
     */
    public SymbolEntry(boolean isConstant, boolean isDeclared,String type, int stackOffset) {
        this.isConstant = isConstant;
        this.isInitialized = isDeclared;
        this.type=type;
        this.stackOffset = stackOffset;
    }

    /**
     * @return the stackOffset
     */
    public int getStackOffset() {
        return stackOffset;
    }

    /**
     * @return the isConstant
     */
    public boolean isConstant() {
        return isConstant;
    }

    /**
     * @return the isInitialized
     */
    public boolean isInitialized() {
        return isInitialized;
    }

    public String Type(){return type;}

    /**
     * @param isConstant the isConstant to set
     */
    public void setConstant(boolean isConstant) {
        this.isConstant = isConstant;
    }

    /**
     * @param isInitialized the isInitialized to set
     */
    public void setInitialized(boolean isInitialized) {
        this.isInitialized = isInitialized;
    }

    public void setType(String type){ this.type=type; }
    /**
     * @param stackOffset the stackOffset to set
     */
    public void setStackOffset(int stackOffset) {
        this.stackOffset = stackOffset;
    }
}
