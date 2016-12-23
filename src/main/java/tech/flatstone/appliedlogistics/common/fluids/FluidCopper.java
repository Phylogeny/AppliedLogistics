package tech.flatstone.appliedlogistics.common.fluids;

import tech.flatstone.appliedlogistics.common.blocks.fluids.BlockFluidCopper;

public class FluidCopper extends FluidBase {
    public static FluidCopper INSTANCE;

    public FluidCopper() {
        super("copper", BlockFluidCopper.class, true);
        INSTANCE = this;
        this.getFluid().setLuminosity(15);
        this.getFluid().setViscosity(5000);
    }
}