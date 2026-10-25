package fr.stylobow.floatingheaditems.client;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Function;

public class FloatingItemSlider extends AbstractSliderButton {
    private final Function<Double, Component> messageProvider;
    private final Consumer<Double> valueConsumer;
    private final Runnable onReleaseAction;

    public FloatingItemSlider(int x, int y, int width, int height, double initialValue,
                              Function<Double, Component> messageProvider,
                              Consumer<Double> valueConsumer,
                              Runnable onReleaseAction) {
        super(x, y, width, height, Component.empty(), initialValue);
        this.messageProvider = messageProvider;
        this.valueConsumer = valueConsumer;
        this.onReleaseAction = onReleaseAction;
        this.updateMessage();
    }

    @Override
    protected void updateMessage() {
        this.setMessage(this.messageProvider.apply(this.value));
    }

    @Override
    protected void applyValue() {
        this.valueConsumer.accept(this.value);
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.onRelease(mouseX, mouseY);
        if (this.onReleaseAction != null) {
            this.onReleaseAction.run();
        }
    }
}
