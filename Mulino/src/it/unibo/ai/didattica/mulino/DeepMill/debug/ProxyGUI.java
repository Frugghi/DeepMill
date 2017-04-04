package it.unibo.ai.didattica.mulino.DeepMill.debug;

import it.unibo.ai.didattica.mulino.domain.State;
import it.unibo.ai.didattica.mulino.gui.Background;
import it.unibo.ai.didattica.mulino.gui.GUI;

import java.lang.reflect.Field;

public class ProxyGUI extends GUI {

    private StateUI ui;

    public ProxyGUI(StateUI ui) {
        this.ui = ui;

        try {
            Field mainFrameField = it.unibo.ai.didattica.mulino.gui.GUI.class.getDeclaredField("mainFrame");
            mainFrameField.setAccessible(true);
            Background mainFrame = (Background)mainFrameField.get(this);
            mainFrame.setVisible(false);
            mainFrame.dispose();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(State aState) {
        ui.update(aState);
    }
}
