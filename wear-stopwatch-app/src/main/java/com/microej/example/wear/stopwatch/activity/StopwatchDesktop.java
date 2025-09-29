/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */

package com.microej.example.wear.stopwatch.activity;

import com.microej.example.wear.stopwatch.activity.style.ClassIdentifiers;
import com.microej.example.wear.stopwatch.activity.style.Theme;
import com.microej.example.wear.stopwatch.activity.widget.LapScrollWidget;
import com.microej.example.wear.stopwatch.activity.widget.TimerControlWidget;
import com.microej.example.wear.stopwatch.activity.widget.TimerWidget;
import com.microej.example.wear.stopwatch.model.Stopwatch;
import com.microej.wear.KernelServiceProvider;
import com.microej.wear.services.ResourceService;
import com.microej.wear.util.renderable.RenderableDesktop;

import ej.microui.display.Colors;
import ej.microui.display.Display;
import ej.microvg.VectorFont;
import ej.mwt.Desktop;
import ej.mwt.Widget;
import ej.mwt.style.EditableStyle;
import ej.mwt.style.background.NoBackground;
import ej.mwt.style.background.RectangularBackground;
import ej.mwt.style.background.RoundedBackground;
import ej.mwt.style.dimension.FixedDimension;
import ej.mwt.style.outline.FlexibleOutline;
import ej.mwt.style.outline.border.RoundedBorder;
import ej.mwt.stylesheet.Stylesheet;
import ej.mwt.stylesheet.cascading.CascadingStylesheet;
import ej.mwt.stylesheet.selector.ClassSelector;
import ej.mwt.stylesheet.selector.StateSelector;
import ej.mwt.stylesheet.selector.combinator.AndCombinator;
import ej.mwt.stylesheet.selector.combinator.Combinator;
import ej.mwt.util.Alignment;
import ej.widget.basic.ImageWidget;
import ej.widget.container.Canvas;

/**
 * {@link Desktop} that renders a stopwatch.
 */
public class StopwatchDesktop extends RenderableDesktop {

	private static final String LEFT_GRAPHIC = "/images/left_graphic.png";
	private static final String RIGHT_GRAPHIC = "/images/right_graphic.png";

	private final Stopwatch stopwatch;

	/**
	 * Creates a stopwatch desktop.
	 *
	 * @param stopwatch
	 *            The data manager to use to fetch data from the model.
	 */
	public StopwatchDesktop(Stopwatch stopwatch) {
		super();
		this.stopwatch = stopwatch;

		setStylesheet(createStylesheet());
		setWidget(createRootWidget());
	}

	private Widget createRootWidget() {
		int dpWidth = Display.getDisplay().getWidth();
		int dpHeight = Display.getDisplay().getHeight();

		Canvas root = new Canvas();
		root.addClassSelector(ClassIdentifiers.DESKTOP_ROOT);

		// side graphics
		ResourceService resourceService = KernelServiceProvider.getResourceService();
		ImageWidget leftGraphic = new ImageWidget(resourceService.getImagePath(LEFT_GRAPHIC));
		ImageWidget rightGraphic = new ImageWidget(resourceService.getImagePath(RIGHT_GRAPHIC));

		int sideGraphicWidth = (int) (Theme.SIDE_GRAPHIC_WIDTH * dpWidth);
		int sideGraphicHeight = (int) (Theme.SIDE_GRAPHIC_HEIGHT * dpHeight);
		int leftGraphicX = 0;
		int leftGraphicY = 0;
		int rightGraphicX = dpWidth - sideGraphicWidth;
		int rightGraphicY = 0;
		root.addChild(leftGraphic, leftGraphicX, leftGraphicY, sideGraphicWidth, sideGraphicHeight);
		root.addChild(rightGraphic, rightGraphicX, rightGraphicY, sideGraphicWidth, sideGraphicHeight);

		// add a timer to the center of the screen
		TimerWidget timer = new TimerWidget(getAnimator(), this.stopwatch);

		int timerWidth = Theme.TIMER_CENTRAL_WIDTH + 2 * Theme.TIMER_SIDE_WIDTH;
		int timerHeight = (int) (Theme.TIMER_CENTRAL_FONT_SIZE * 1.1); // central font size + margin
		int timerX = (int) (Theme.TIMER_POS_X * (dpWidth - timerWidth));
		int timerY = (int) (Theme.TIMER_POS_Y * (dpHeight - timerHeight));
		root.addChild(timer, timerX, timerY, timerWidth, timerHeight);

		// add controls below the timer
		TimerControlWidget control = new TimerControlWidget(this.stopwatch);
		control.addClassSelector(ClassIdentifiers.DESKTOP_CONTROLS);

		// control width is: width of 2 buttons and the inner margin
		int controlWidth = (int) (Theme.BUTTON_INNER_MARGIN * dpWidth + 2 * Theme.BUTTON_WIDTH);
		int controlHeight = Theme.BUTTON_HEIGHT;
		int controlX = (int) (Theme.CONTROL_POS_X * (dpWidth - controlWidth));
		int controlY = (int) (Theme.CONTROL_POS_Y * (dpHeight - controlHeight));
		root.addChild(control, controlX, controlY, controlWidth, controlHeight);

		// add laps above the timer
		LapScrollWidget lapList = new LapScrollWidget(this.stopwatch);

		int lapListWidth = (int) (Theme.LAP_LIST_WIDTH * dpWidth);
		int lapListHeight = (int) (Theme.LAP_LIST_HEIGHT * dpHeight);
		int lapListX = (int) (Theme.LAP_LIST_POS_X * (dpWidth - lapListWidth));
		int lapListY = (int) (Theme.LAP_LIST_POS_Y * (dpHeight - lapListHeight));
		root.addChild(lapList, lapListX, lapListY, lapListWidth, lapListHeight);

		return root;
	}

	private Stylesheet createStylesheet() {
		int dpWidth = Display.getDisplay().getWidth();
		int dpHeight = Display.getDisplay().getHeight();

		VectorFont font = KernelServiceProvider.getFontService().getMonospaceFont();
		CascadingStylesheet stylesheet = new CascadingStylesheet();

		EditableStyle style = stylesheet.getDefaultStyle();
		style.setBackground(NoBackground.NO_BACKGROUND);
		style.setColor(Colors.WHITE);
		style.setHorizontalAlignment(Alignment.HCENTER);
		style.setVerticalAlignment(Alignment.VCENTER);

		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.DESKTOP_ROOT));
		style.setBackground(new RectangularBackground(Colors.BLACK));

		// timer: central
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.TIMER_CENTRAL));
		style.setFont(font.getFont(Theme.TIMER_CENTRAL_FONT_SIZE));
		style.setDimension(new FixedDimension(Theme.TIMER_CENTRAL_WIDTH, Widget.NO_CONSTRAINT));
		// timer: sides
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.TIMER_SIDE));
		style.setFont(font.getFont(Theme.TIMER_SIDE_FONT_SIZE));
		style.setDimension(new FixedDimension(Theme.TIMER_SIDE_WIDTH, Widget.NO_CONSTRAINT));

		// controls
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.DESKTOP_CONTROLS));
		style.setVerticalAlignment(Alignment.TOP);

		// buttons
		Combinator orCombinator = new Combinator(new ClassSelector(ClassIdentifiers.CONTROL_BUTTON),
				new ClassSelector(ClassIdentifiers.DISABLED_CONTROL_BUTTON)) {
			@Override
			public boolean appliesToWidget(Widget widget) {
				return getFirstSelector().appliesToWidget(widget) || getSecondSelector().appliesToWidget(widget);
			}
		};
		style = stylesheet.getSelectorStyle(orCombinator);
		style.setDimension(new FixedDimension(Theme.BUTTON_WIDTH, Widget.NO_CONSTRAINT));
		// enabled buttons' border
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.CONTROL_BUTTON));
		style.setBorder(new RoundedBorder(Theme.BUTTON_BORDER_COLOR, 90, Theme.BUTTON_BORDER_WIDTH));
		// disabled buttons' border
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.DISABLED_CONTROL_BUTTON));
		style.setBorder(new RoundedBorder(Theme.DISABLED_BUTTON_BORDER_COLOR, 90, Theme.BUTTON_BORDER_WIDTH));
		// buttons when pressed
		style = stylesheet.getSelectorStyle(new AndCombinator(new ClassSelector(ClassIdentifiers.CONTROL_BUTTON),
				new StateSelector(StateSelector.ACTIVE)));
		style.setBackground(new RoundedBackground(Theme.BUTTON_PRESSED_COLOR, 90, 0));

		// margin between buttons
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.CONTROL_INNER_MARGIN));
		style.setDimension(new FixedDimension((int) (Theme.BUTTON_INNER_MARGIN * dpWidth), Widget.NO_CONSTRAINT));

		// laps
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.LAP));
		style.setDimension(new FixedDimension(Widget.NO_CONSTRAINT, (int) (Theme.LAP_HEIGHT * dpHeight)));
		style.setMargin(new FlexibleOutline(Theme.LAP_MARGIN, 0, 0, 0));
		style.setVerticalAlignment(Alignment.BOTTOM);
		style.setColor(Theme.LAP_FONT_COLOR);
		// laps: id
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.LAP_ID));
		style.setDimension(
				new FixedDimension((int) (Theme.LAP_ID_WIDTH * dpWidth), (int) (Theme.LAP_ID_HEIGHT * dpHeight)));
		style.setMargin(new FlexibleOutline(0, 0, (int) (Theme.LAP_ID_OFFSET * dpHeight), 0));
		style.setFont(font.getFont(Theme.LAP_ID_FONT_SIZE));
		// laps: diff
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.LAP_DIFF));
		style.setDimension(
				new FixedDimension((int) (Theme.LAP_ICON_SIZE * dpWidth), (int) (Theme.LAP_ICON_SIZE * dpHeight)));
		style.setVerticalAlignment(Alignment.TOP);
		// laps: time
		style = stylesheet.getSelectorStyle(new ClassSelector(ClassIdentifiers.LAP_TIME));
		style.setDimension(new FixedDimension((int) (Theme.LAP_TIME_WIDTH * dpWidth), Widget.NO_CONSTRAINT));
		style.setFont(font.getFont(Theme.LAP_TIME_FONT_SIZE));

		return stylesheet;
	}
}
