package com.adoleiiiiii.timestop;

import com.adoleiiiiii.timestop.common.ModSoundsRegister;
import com.adoleiiiiii.timestop.render.entity.KnifeEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;

/**
 * 飞刀物品，右键投掷 {@link KnifeEntity}。
 */
public class KnifeItem extends Item {
    public KnifeItem(Properties properties) {
        super(properties);
    }

    /** @return 飞刀攻击属性组件 */
    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 5.0, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, -1.0, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack handStack = player.getItemInHand(hand);
        int knivesToThrow = player.isShiftKeyDown() ? handStack.getCount() : 1;
        if (!level.isClientSide()) {
            for (int i = 0; i < knivesToThrow; i++) {
                KnifeEntity knifeEntity = KnifeEntity.create(level, player);
                knifeEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.5F,
                        i == 0 ? 1.0F : 28.0F);
                level.addFreshEntity(knifeEntity);
            }
            if (!player.isCreative()) {
                handStack.shrink(knivesToThrow);
            }
            player.playSound(ModSoundsRegister.THROW.get());
        }
        player.swing(hand);
        return InteractionResultHolder.success(handStack);
    }
}
