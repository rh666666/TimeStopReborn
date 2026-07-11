package com.adoleiiiiii.timestop.data;

import com.adoleiiiiii.timestop.TimeStopReborn;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * 生成物品模型 JSON；time_clock 注册名与纹理 timeclock 不一致。
 */
public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, TimeStopReborn.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent("time_clock", mcLoc("item/generated"))
                .texture("layer0", modLoc("item/timeclock"));
        handheldItem(ResourceLocation.fromNamespaceAndPath(TimeStopReborn.MODID, "knife"));
    }
}
