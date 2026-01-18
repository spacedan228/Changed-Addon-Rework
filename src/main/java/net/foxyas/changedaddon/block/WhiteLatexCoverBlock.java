package net.foxyas.changedaddon.block;

import net.ltxprogrammer.changed.block.WhiteLatexTransportInterface;
import net.ltxprogrammer.changed.entity.LatexType;

public class WhiteLatexCoverBlock extends LatexCoverBlock implements WhiteLatexTransportInterface {

    public WhiteLatexCoverBlock(Properties pProperties) {
        super(pProperties, LatexType.WHITE_LATEX);
    }
}
