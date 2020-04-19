package com.payne.games.piemenu.testMenu.exampleTests;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.payne.games.piemenu.PieMenu;
import com.payne.games.piemenu.PieMenu.PieMenuStyle;
import com.payne.games.piemenu.actions.RadialGroupActionColorBasic;
import com.payne.games.piemenu.actions.RadialGroupActionVisualAngleClose;
import com.payne.games.piemenu.actions.RadialGroupActionVisualAngleOpen;
import com.payne.games.piemenu.testMenu.core.BaseScreen;
import com.payne.games.piemenu.testMenu.core.TestsMenu;


public class ActionMenu extends BaseScreen {
    private PieMenu menu;

    public ActionMenu(TestsMenu game) {
        super(game);
    }


    @Override
    public void show() {
        setScreenColor(.2f, .2f, .2f, 1);

        /* Adding a Table. */
        Table root = new Table();
        root.setFillParent(true);
        root.defaults().padBottom(150);
        game.stage.addActor(root);

        /* ====================================================================\
        |                  HERE BEGINS THE MORE SPECIFIC CODE                  |
        \==================================================================== */

        /* Setting up and creating the widget. */
        PieMenuStyle style = new PieMenuStyle();
        style.backgroundColor = new Color(1, 1, 1, .3f);
        style.selectedColor = new Color(.7f, .3f, .5f, 1);
        style.sliceColor = new Color(0, .7f, 0, 1);
        style.alternateSliceColor = new Color(.7f, 0, 0, 1);
        menu = new PieMenu(game.skin.getRegion("white"), style, 130, 50f/130, 180, 320);
        menu.setVisualAngleAutoUpdate(false);

        /* Customizing the behavior. */
        menu.setInfiniteSelectionRange(true);

        /* Populating the widget. */
        for (int i = 0; i < 5; i++) {
            Label label = new Label(Integer.toString(i), game.skin);
            menu.addActor(label);
        }

        /* Preparing the Actions used for our Animations. */
        RadialGroupActionVisualAngleOpen actionVisualAngleOpen = new RadialGroupActionVisualAngleOpen(menu, .1f, Interpolation.linear);
        Action actionOpen = new ParallelAction(actionVisualAngleOpen, new RadialGroupActionColorBasic(menu));
        RadialGroupActionVisualAngleClose actionVisualAngleClose = new RadialGroupActionVisualAngleClose(menu, .1f, Interpolation.linear);
        Action actionClose = new ParallelAction(actionVisualAngleClose, new RadialGroupActionColorBasic(menu));

        /* Setting up the demo-button. */
        final TextButton textButton = new TextButton("Drag Pie", game.skin);
        textButton.addListener(new ClickListener() {
            /*
            In our particular case, we want to NOT use a ChangeListener because
            else the user would have to release his mouse-click before seeing
            the menu, which goes against our current goal of obtaining a
            "drag-selection" menu.
            */
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                menu.resetSelection();
                menu.centerOnActor(textButton);

                if (menu.getActions().contains(actionClose, true)) {
                    actionClose.restart();
                    menu.removeAction(actionClose);
                }
                if (!menu.getActions().contains(actionOpen, true)) {
                    actionOpen.restart();
                    menu.addAction(actionOpen);
                }

                transferInteraction(game.stage, menu);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
            }
        });
        root.add(textButton).expand().bottom();

        /* Adding a selection-listener. */
        menu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                if (menu.getActions().contains(actionOpen, true)) {
                    actionOpen.restart();
                    menu.removeAction(actionOpen);
                }
                if (!menu.getActions().contains(actionClose, true)) {
                    actionClose.restart();
                    menu.addAction(actionClose);
                }

                int index = menu.getSelectedIndex();
                if (!menu.isValidIndex(index)) {
                    textButton.setText("Drag Pie");
                    return;
                }
                Actor child = menu.getChild(index);
                textButton.setText(((Label) child).getText().toString());
            }
        });

        /* Including the Widget in the Stage. */
        game.stage.addActor(menu);
        menu.setVisible(false);
    }

    /**
     * To be used to get the user to transition directly into
     * {@link InputListener#touchDragged(InputEvent, float, float, int)}
     * as if he had triggered
     * {@link InputListener#touchDown(InputEvent, float, float, int, int)}.<br>
     * I am not certain this is the recommended way of doing this, but for the
     * purposes of this demonstration, it works!
     *
     * @param stage  the stage.
     * @param widget the PieMenu on which to transfer the interaction.
     */
    private void transferInteraction(Stage stage, PieMenu widget) {
        if (widget == null) throw new IllegalArgumentException("widget cannot be null.");
        if (widget.getPieMenuListener() == null) throw new IllegalArgumentException("inputListener cannot be null.");
        stage.addTouchFocus(widget.getPieMenuListener(), widget, widget, 0, widget.getSelectionButton());
    }
}
