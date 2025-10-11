package net.foxyas.changedaddon.entity.api;

import net.foxyas.changedaddon.menu.CustomMerchantOffer;
import net.foxyas.changedaddon.menu.CustomMerchantOffers;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface CustomMerchant {

    void setTradingPlayer(@Nullable Player tradingPlayer);

    @Nullable
    Player getTradingPlayer();

    CustomMerchantOffers getOffers();

    void overrideOffers(CustomMerchantOffers offers);

    void notifyTrade(CustomMerchantOffer offer);

    /**
     * Notifies the merchant of a possible merchantrecipe being fulfilled or not. Usually, this is just a sound byte
     * being played depending if the suggested itemstack is not null.
     */
    void notifyTradeUpdated(ItemStack stack);

    SoundEvent getNotifyTradeSound();

    boolean isClientSide();

    class Client implements CustomMerchant {

        private final Player source;
        private CustomMerchantOffers offers = new CustomMerchantOffers();

        public Client(Player source){
            this.source = source;
        }

        @Override
        public void setTradingPlayer(@Nullable Player tradingPlayer) {}

        @Override
        public @Nullable Player getTradingPlayer() {
            return source;
        }

        @Override
        public CustomMerchantOffers getOffers() {
            return offers;
        }

        @Override
        public void overrideOffers(CustomMerchantOffers offers) {
            this.offers = offers;
        }

        @Override
        public void notifyTrade(CustomMerchantOffer offer) {
            offer.increaseUses();
        }

        @Override
        public void notifyTradeUpdated(ItemStack stack) {}

        @Override
        public SoundEvent getNotifyTradeSound() {
            return null;
        }

        @Override
        public boolean isClientSide() {
            return true;
        }
    }
}
