package micdoodle8.mods.galacticraft.core.entities;

import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable;
import micdoodle8.mods.galacticraft.core.GCItems;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class EntityEvolvedSpider extends EntitySpider implements IEntityBreathable
{
    public EntityEvolvedSpider(World par1World)
    {
        super(par1World);
        this.setSize(1.5F, 1.0F);
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(22.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(ConfigManagerCore.hardMode ? 0.40000001192092896D : 0.30000001192092896D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(ConfigManagerCore.hardMode ? 4.0D : 2.0D);
    }

    @Override
    public boolean canBreath()
    {
        return true;
    }

    /*@Override
    public boolean isAIEnabled()
    {
        return false;
    }*/

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)
    {
        Object p_180482_2_1 = super.onInitialSpawn(difficulty, livingdata);

        if (this.worldObj.rand.nextInt(100) == 0)
        {
            EntitySkeleton entityskeleton = new EntitySkeleton(this.worldObj);
            entityskeleton.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
            entityskeleton.onInitialSpawn(difficulty, (IEntityLivingData) null);
            this.worldObj.spawnEntityInWorld(entityskeleton);
            entityskeleton.mountEntity(this);
        }

        if (p_180482_2_1 == null)
        {
            p_180482_2_1 = new EntitySpider.GroupData();

            if (this.worldObj.getDifficulty() == EnumDifficulty.HARD && this.worldObj.rand.nextFloat() < 0.1F * difficulty.getClampedAdditionalDifficulty())
            {
                ((EntitySpider.GroupData) p_180482_2_1).func_111104_a(this.worldObj.rand);
            }
        }

        if (p_180482_2_1 instanceof EntitySpider.GroupData)
        {
            int i = ((EntitySpider.GroupData) p_180482_2_1).potionEffectId;

            if (i > 0 && Potion.potionTypes[i] != null)
            {
                this.addPotionEffect(new PotionEffect(i, Integer.MAX_VALUE));
            }
        }

        return (IEntityLivingData) p_180482_2_1;
    }

    @Override
    protected void jump()
    {
        this.motionY = 0.52D / WorldUtil.getGravityFactor(this);
        if (this.motionY < 0.26D)
        {
            this.motionY = 0.26D;
        }

        if (this.isPotionActive(Potion.jump))
        {
            this.motionY += (this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F;
        }

        if (this.isSprinting())
        {
            float f = this.rotationYaw * 0.017453292F;
            this.motionX -= MathHelper.sin(f) * 0.2F;
            this.motionZ += MathHelper.cos(f) * 0.2F;
        }

        this.isAirBorne = true;
        ForgeHooks.onLivingJump(this);
    }

    @Override
    protected void addRandomDrop()
    {
        switch (this.rand.nextInt(14))
        {
        case 0:
        case 1:
        case 2:
            this.dropItem(GCItems.cheeseCurd, 1);
            break;
        case 3:
        case 4:
        case 5:
            this.dropItem(Items.fermented_spider_eye, 1);
            break;
        case 6:
        case 7:
            //Oxygen tank half empty or less
            this.entityDropItem(new ItemStack(GCItems.oxTankMedium, 1, 901 + this.rand.nextInt(900)), 0.0F);
            break;
        case 8:
            this.dropItem(GCItems.oxygenGear, 1);
            break;
        case 9:
            this.dropItem(GCItems.oxygenConcentrator, 1);
            break;
        default:
            if (ConfigManagerCore.challengeMode || ConfigManagerCore.challengeMobDropsAndSpawning) this.dropItem(Items.nether_wart, 1);
            break;
        }
    }
}
