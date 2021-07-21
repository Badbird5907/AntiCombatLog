package net.badbird5907.anticombatlog.object;

public class Triplet<X, Y,Z> {
    private X x;
    private Y y;
    private Z z;
    public Triplet(X x, Y y,Z z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public X getValue0() {
        return x;
    }

    public Y getValue1() {
        return y;
    }
    public Z getValue2(){
        return z;
    }
    public void setValue0(X x){
        this.x = x;
    }
    public void setValue1(Y y){
        this.y = y;
    }
    public void setValue2(Z y){
        this.z = z;
    }
}
