package bq_standard.network.handlers;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.network.IPacketHandler;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.cache.CapabilityProviderQuestCache;
import betterquesting.api2.cache.QuestCache;
import betterquesting.api2.storage.DBEntry;
import bq_standard.network.StandardPacketType;
import bq_standard.tasks.TaskInteractItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;

public class PktHandlerInteract implements IPacketHandler
{
    @Override
    public ResourceLocation getRegistryName()
    {
        return StandardPacketType.INTERACT.GetLocation();
    }
    
    @Override
    public void handleServer(NBTTagCompound tag, EntityPlayerMP sender)
    {
        QuestCache qc = sender.getCapability(CapabilityProviderQuestCache.CAP_QUEST_CACHE, null);
		if(qc == null) return;
    
        EnumHand hand = tag.getBoolean("isMainHand") ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
        boolean isHit = tag.getBoolean("isHit");
		
		for(DBEntry<IQuest> entry : QuestingAPI.getAPI(ApiReference.QUEST_DB).bulkLookup(qc.getActiveQuests()))
		{
		    for(DBEntry<ITask> task : entry.getValue().getTasks().getEntries())
            {
                if(task.getValue() instanceof TaskInteractItem) ((TaskInteractItem)task.getValue()).onInteract(entry, sender, hand, ItemStack.EMPTY, Blocks.AIR.getDefaultState(), sender.getPosition(), isHit);
            }
		}
    }
    
    @Override
    public void handleClient(NBTTagCompound tag){}
}
