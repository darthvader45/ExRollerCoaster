package erc._mc.block.rail;

import erc._mc.tileentity.TileEntityRail;

public class BlockNonGravityRail extends BlockRail {

    @Override
    protected TileEntityRail GetTileEntityInstance() {
        return new TileEntityRail.AntiGravity();
    }
}
