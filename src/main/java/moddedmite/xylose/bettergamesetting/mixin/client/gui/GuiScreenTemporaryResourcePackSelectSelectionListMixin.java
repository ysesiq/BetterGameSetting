package moddedmite.xylose.bettergamesetting.mixin.client.gui;

import moddedmite.xylose.bettergamesetting.api.IGuiScreenTemporaryResourcePackSelect;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(GuiScreenTemporaryResourcePackSelectSelectionList.class)
public abstract class GuiScreenTemporaryResourcePackSelectSelectionListMixin extends GuiSlot {
    @Shadow
    private final ResourcePackRepository field_110511_b;
    @Shadow
    final GuiScreenTemporaryResourcePackSelect field_110512_a;

    public GuiScreenTemporaryResourcePackSelectSelectionListMixin(GuiScreenTemporaryResourcePackSelect par1GuiScreenTemporaryResourcePackSelect, ResourcePackRepository par2ResourcePackRepository) {
        super(IGuiScreenTemporaryResourcePackSelect.func_110344_a(par1GuiScreenTemporaryResourcePackSelect), par1GuiScreenTemporaryResourcePackSelect.width, par1GuiScreenTemporaryResourcePackSelect.height, 32, par1GuiScreenTemporaryResourcePackSelect.height - 55 + 4, 36);
        this.field_110512_a = par1GuiScreenTemporaryResourcePackSelect;
        this.field_110511_b = par2ResourcePackRepository;
        par2ResourcePackRepository.updateRepositoryEntriesAll();
    }

    @Overwrite
    protected void elementClicked(int par1, boolean par2) {
        List var3 = this.field_110511_b.getRepositoryEntriesAll();

        try {
            if (par1 == 0) {
                throw new RuntimeException("This is so horrible ;D");
            }

            this.field_110511_b.setRepositoryEntries(new ResourcePackRepositoryEntry[]{(ResourcePackRepositoryEntry) var3.get(par1 - 1)});
            IGuiScreenTemporaryResourcePackSelect.func_110341_b(this.field_110512_a).refreshResources();
        } catch (Exception var5) {
            this.field_110511_b.setRepositoryEntries(new ResourcePackRepositoryEntry[0]);
            IGuiScreenTemporaryResourcePackSelect.func_110339_c(this.field_110512_a).refreshResources();
        }

        IGuiScreenTemporaryResourcePackSelect.func_110345_d(this.field_110512_a).gameSettings.skin = this.field_110511_b.getResourcePackName();
        IGuiScreenTemporaryResourcePackSelect.func_110334_e(this.field_110512_a).gameSettings.saveOptions();
    }

    @Overwrite
    protected boolean isSelected(int par1) {
        List var2 = this.field_110511_b.getRepositoryEntries();
        return par1 == 0 ? var2.isEmpty() : var2.contains(this.field_110511_b.getRepositoryEntriesAll().get(par1 - 1));
    }
}