package miniplc0java.analyser;

public class SymbolEntry {
    boolean isConstant;
    boolean isInitialized;
    String type;
    String name;
    int stackOffset;
    int depth;

    /**
     * @param isConstant
     * @param isDeclared
     * @param stackOffset
     */
    public SymbolEntry(boolean isConstant, boolean isDeclared,String type, int stackOffset,String name,int depth) {
        this.isConstant = isConstant;
        this.isInitialized = isDeclared;
        this.type=type;
        this.stackOffset = stackOffset;
        this.name=name;
        this.depth=depth;
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

    public String Type(){return this.type;}

    public String getName(){
        return this.name;
    }

    public int getDepth(){
        return this.depth;
    }

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

    public void setName(String name){
        this.name=name;
    }

    public void setDepth(int depth){
        this.depth=depth;
    }

    public void setType(String type){ this.type=type; }
    /**
     * @param stackOffset the stackOffset to set
     */
    public void setStackOffset(int stackOffset) {
        this.stackOffset = stackOffset;
    }
}
