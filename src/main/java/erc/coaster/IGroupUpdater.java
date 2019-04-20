package erc.coaster;

/**
 * Created by MOTTY on 2017/12/12.
 * 1Tick に1回まとめてUpdateする動作を要求する
 */
public interface IGroupUpdater {

    /**
     * 外部から呼ばれ、グループのUpdateを開始することを要求する。
     */
    void onUpdate(long tick);

    /**
     * 自身の更新を行う。また、同Tickで2回以降の更新はおこなわないこと。
     */
    void Update(long tick);
}
