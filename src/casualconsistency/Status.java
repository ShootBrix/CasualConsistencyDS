package casualconsistency;

public class Status {

    private Boolean bChanged;

    public Status() { 
        this.bChanged = false;
    }

    public Boolean getChanged(){
        return bChanged;
    }

    public void setChanged(Boolean bChanged){
        this.bChanged = bChanged;
    }

    public String toString(){
        return "" + bChanged;
    }
}
