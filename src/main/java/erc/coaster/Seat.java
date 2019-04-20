package erc.coaster;


import mochisystems.math.Mat4;

public class Seat{

    Coaster parentCoaster;
    public final Mat4 AttitudeMatrix = new Mat4();
    long lastUpdatedTick = -1;

    public Seat(Coaster coaster)
    {
        this.parentCoaster = coaster;
    }

    public Coaster GetParent()
    {
        return parentCoaster;
    }

    public void Attacked(double damage)
    {
        parentCoaster.Attacked(damage);
    }

//    public void onUpdate(long tick)
//    {
////        if(lastUpdatedTick == tick) return;
////        parentCoaster.onUpdate(tick);
//    }

    public void Update(long tick)
    {
        lastUpdatedTick = tick;
        move();
    }

    private void move()
    {
        AttitudeMatrix.Pos().CopyFrom(parentCoaster.AttitudeMatrix.Pos());
    }
}
