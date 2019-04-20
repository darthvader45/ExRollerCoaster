package erc.coaster;

import mochisystems.math.Mat4;
import erc.rail.IRail;
import mochisystems.math.Quaternion;
import mochisystems.math.Vec3d;

import java.util.ArrayList;

public class Coaster implements IGroupUpdater{

	public final CoasterPos pos;
    public final Vec3d position = new Vec3d();
    public final Vec3d prevPosition = new Vec3d();
	public final Mat4 AttitudeMatrix = new Mat4();
	public final Mat4 prevAttitudeMatrix = new Mat4();
	public final Quaternion attitude = new Quaternion();
	public final Quaternion prevAttitude = new Quaternion();

	protected double speed = 0;
	protected CoasterSettings settings = CoasterSettings.Default();

	public IRail GetCurrentRail(){return pos.rail;}
	public void setSpeed(double speed){this.speed = speed;}
	public double getSpeed(){ return this.speed; }

    private Seat[] seatList = new Seat[0];
//	private SeatHolder seatHolder = new SeatHolder(this);

//	/**
//	 * 連結されたコースターを表すリスト
//	 * 1つの連結コースターのグループで１インスタンスとすること
//	 */
	protected Coaster rootCoaster;
	protected Coaster parentCoaster;
	protected Coaster childCoaster;


	public Coaster()
	{
	    pos = new CoasterPos();
		rootCoaster = this;
		parentCoaster = null;
		childCoaster = null;
	}

	public void ConnectChild(Coaster child)
	{
		Coaster lastCoaster = getLastCoaster();
		lastCoaster.childCoaster = child;
		child.parentCoaster = lastCoaster;
		child.rootCoaster = this.rootCoaster;
	}

	protected Coaster getLastCoaster()
	{
		return (childCoaster != null) ? childCoaster.getLastCoaster() : this;
	}

    public void SetNewRail(IRail rail)
    {
        pos.rail = rail;
    }

    public void SetSettingData(CoasterSettings settings)
    {
        this.settings = settings;
    }

    public CoasterSettings GetSettings()
    {
        return settings;
    }

    public void SetSeats(Seat[] seats)
    {
        this.seatList = seats;
    }

    protected long lastUpdatedTick = -1;
    /**
	 * コースター全体更新関数
	 * 連結された全てのコースターの中で最初に呼ばれたUpdateで
	 * 連結しているコースター全ての更新のmoveを行う。
	 * 最初かどうか判定するために利用する値はTickを想定している
	 */
	public void onUpdate(long tick)
	{
		if(lastUpdatedTick == tick) return;
		rootCoaster.Update(tick);
	}

	public void Update(long tick)
	{
		Coaster coaster = rootCoaster;
		while(coaster != null)
		{
			coaster.lastUpdatedTick = tick;
			coaster.move();
			for(int i=0; i < seatList.length; ++i)seatList[i].Update(tick);
			coaster = coaster.childCoaster;
		}
	}

    public void setPosition(double t)
	{
		pos.t = t;
	}

	public void Attacked(double damage)
	{

	}

	protected void move()
	{
		if(pos.hasNotRail()) return;

        prevPosition.CopyFrom(position);
		prevAttitude.CopyFrom(attitude);
		prevAttitudeMatrix.CopyFrom(AttitudeMatrix);

		speed = resist(speed);
		speed = accelerate(speed);
		speed = pos.move(speed);
		pos.rail.CalcAttitudeAt(pos.t, attitude, position);
//		pos.rail.CalcAttitudeAt(pos.t, AttitudeMatrix, position);
	}

	private double resist(double speed)
	{
		return pos.rail.RegisterAt(pos.t, speed);
	}
	
	private double accelerate(double speed)
	{
		return speed + TotalAccel();
	}
	
	private double TotalAccel()
	{
        if(pos.hasNotRail()) return 0;
		double sum = 0;
		Coaster coaster = rootCoaster;
		while(coaster != null)
		{
			sum += coaster.pos.rail.AccelAt(coaster.pos.t) /  coaster.settings.Weight;
			coaster = coaster.childCoaster;
		}
		return sum;
	}
	
	public void Delete()
	{
		if(pos.hasNotRail()) return;
		pos.rail.OnDeleteCoaster();
		parentCoaster.childCoaster = childCoaster;
		childCoaster.parentCoaster = parentCoaster;
	}

	public void ConnectTo(Coaster parent)
    {
        parent.childCoaster = this;
    }

	public class CoasterPos{
		public IRail rail;
		public double t = 0;

		public double t()
        {
            return t;
        }

        public boolean hasNotRail()
		{
			return rail == null;
		}
		
		public double move(double speed)
		{
			t += speed / rail.Length();

			// back and brake if next rail is nothing
			boolean isExistRail = PassOverRail();
			if(isExistRail)
			{
				return speed;
			}
			else
			{
				return speed * -0.1;
			}
		}
		
		public CoasterPos Clone(CoasterPos source)
		{
			this.rail = source.rail;
			this.t = source.t;
			return this;
		}
		
		public void Shift(double shiftLength)
		{
			t += shiftLength;
			PassOverRail();
		}

		// @return : isExistRail
		private boolean PassOverRail()
		{
			IRail overRail = null;
			if(t >= 1.0) overRail = rail.NextRail();
			else if(t < 0) overRail = rail.PrevRail();
			else return true;

			if(overRail == null) {
                rail.GetController().FixConnection();
                if(t >= 1.0) overRail = rail.NextRail();
                else if(t < 0) overRail = rail.PrevRail();
                if (overRail == null) return false;
			}

            rail.OnLeaveCoaster();
            overRail.OnEnterCoaster();

            t += (t < 0) ? 1.0 : -1.0;
            t *= rail.Length();
            t /= overRail.Length();
            rail = overRail;
            return true;
		}
	}
}
