package me.uquark.mineconomy.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import me.uquark.mineconomy.item.Items;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import java.util.ArrayList;
import java.util.List;

public class PayCommand {
    private static List<ItemStack> amountToStacks(float amount) {
        List<ItemStack> result = new ArrayList<>();
        result.add(new ItemStack(Items.GALLEON_ITEM, (int) amount));
        result.add(new ItemStack(Items.SICKLE_ITEM, (int) (amount * 10) % 10));
        result.add(new ItemStack(Items.KNUT_ITEM, (int) (amount * 100) % 10));
        return result;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("pay")
            .then(CommandManager.argument("player", EntityArgumentType.player())
                .then(CommandManager.argument("amount", FloatArgumentType.floatArg())
                    .executes(context -> {
                        if (!context.getSource().hasPermissionLevel(3))
                            return -1;
                        PlayerEntity playerEntity = EntityArgumentType.getPlayer(context, "player");
                        float amount = FloatArgumentType.getFloat(context, "amount");
                        for (ItemStack stack : amountToStacks(amount))
                            playerEntity.giveItemStack(stack);
                        context.getSource().sendFeedback(new LiteralText(String.format("Gave %s %.2f galleons", playerEntity.getName().asString(), amount)), true);
                        return 1;
                    })
                )
            )
        );
    }
}
