/*
 * Copyright 2024-2025 MicroEJ Corp.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.example.wear.settings.activity;

import com.microej.example.wear.settings.activity.widget.RadioButton;
import com.microej.example.wear.settings.activity.widget.RadioButtonGroup;
import com.microej.example.wear.settings.activity.widget.RenderableVectorLabel;
import com.microej.example.wear.settings.activity.widget.Scroll;
import com.microej.example.wear.settings.activity.widget.ScrollableList;
import com.microej.wear.KernelServiceProvider;
import com.microej.wear.services.DeviceService;
import com.microej.wear.services.ResourceService;
import com.microej.wear.util.renderable.RenderableDesktop;

import ej.microui.display.Colors;
import ej.microvg.VectorFont;
import ej.mwt.Desktop;
import ej.mwt.style.EditableStyle;
import ej.mwt.style.background.NoBackground;
import ej.mwt.style.background.RectangularBackground;
import ej.mwt.style.dimension.OptimalDimension;
import ej.mwt.style.outline.FlexibleOutline;
import ej.mwt.style.outline.border.FlexibleRectangularBorder;
import ej.mwt.stylesheet.Stylesheet;
import ej.mwt.stylesheet.cascading.CascadingStylesheet;
import ej.mwt.stylesheet.selector.ClassSelector;
import ej.mwt.stylesheet.selector.TypeSelector;
import ej.mwt.util.Alignment;
import ej.widget.basic.ImageWidget;
import ej.widget.container.LayoutOrientation;
import ej.widget.container.List;

/**
 * {@link Desktop} which renders device settings controls.
 */
public class SettingsDesktop extends RenderableDesktop {

	private static final int TITLE = 1001;
	private static final int SECTION_TITLE = 1002;
	private static final int SECTION_TITLE_TEXT = 1003;
	private static final int SECTION_TITLE_ICON = 1004;
	private static final int SECTION_BODY = 1005;
	private static final int SECTION_ITEM = 1006;
	private static final int SECTION_ITEM_TEXT = 1007;
	private static final int SECTION_ITEM_RADIO_BUTTON = 1008;
	private static final int SECTION_ITEM_ICON = 1009;
	private static final int SECTION_ITEM_SUBTITLE = 1010;

	/**
	 * Creates a settings desktop.
	 */
	public SettingsDesktop() {
		// retrieve device info from DeviceService
		DeviceService deviceService = KernelServiceProvider.getDeviceService();
		String deviceName = deviceService.getDeviceName();
		String deviceAddress = deviceService.getDeviceBluetoothAddress();

		setStylesheet(createStylesheet());

		Scroll scroll = new Scroll(LayoutOrientation.VERTICAL);
		scroll.showScrollbar(false);
		ScrollableList list = new ScrollableList(LayoutOrientation.VERTICAL, false);
		scroll.setChild(list);

		RenderableVectorLabel title = new RenderableVectorLabel("Settings");
		title.addClassSelector(TITLE);
		list.addChild(title);

		List aboutSectionTitle = createSectionTitle("/images/ic_about.png", "About");
		list.addChild(aboutSectionTitle);

		List deviceInfo = new List(LayoutOrientation.VERTICAL);
		deviceInfo.addClassSelector(SECTION_BODY);
		RenderableVectorLabel deviceNameSubtitle = new RenderableVectorLabel("Device name");
		deviceNameSubtitle.addClassSelector(SECTION_ITEM_SUBTITLE);
		RenderableVectorLabel deviceNameLabel = new RenderableVectorLabel(deviceName);
		deviceNameLabel.addClassSelector(SECTION_ITEM_TEXT);
		RenderableVectorLabel deviceAddressSubtitle = new RenderableVectorLabel("Bluetooth address");
		deviceAddressSubtitle.addClassSelector(SECTION_ITEM_SUBTITLE);
		RenderableVectorLabel deviceAddressLabel = new RenderableVectorLabel(deviceAddress);
		deviceAddressLabel.addClassSelector(SECTION_ITEM_TEXT);
		deviceInfo.addChild(deviceNameSubtitle);
		deviceInfo.addChild(deviceNameLabel);
		deviceInfo.addChild(deviceAddressSubtitle);
		deviceInfo.addChild(deviceAddressLabel);
		list.addChild(deviceInfo);

		List languagesSectionTitle = createSectionTitle("/images/ic_languages.png", "Languages");
		list.addChild(languagesSectionTitle);

		List languagesList = new List(LayoutOrientation.VERTICAL);
		languagesList.addClassSelector(SECTION_BODY);
		RadioButtonGroup radioGroup = new RadioButtonGroup();
		String currentLocale = "English";
		String[] locales = new String[] { "English", "Français", "Español" };
		for (String locale : locales) {
			assert locale != null;
			RadioButton button = new RadioButton(locale, radioGroup);
			button.addClassSelector(SECTION_ITEM);
			button.addClassSelector(SECTION_ITEM_RADIO_BUTTON);
			if (currentLocale.equals(locale)) {
				radioGroup.setChecked(button);
			}
			languagesList.addChild(button);
		}

		List addNewLanguages = createButtonWithIcon("Add new languages", "/images/ic_add.png");
		languagesList.addChild(addNewLanguages);

		list.addChild(languagesList);

		setWidget(scroll);
	}

	private List createSectionTitle(String imagePath, String text) {
		List list = new List(LayoutOrientation.HORIZONTAL);
		list.addClassSelector(SECTION_TITLE);
		ResourceService resourceService = KernelServiceProvider.getResourceService();
		ImageWidget icon = new ImageWidget(resourceService.getImagePath(imagePath));
		icon.addClassSelector(SECTION_TITLE_ICON);
		list.addChild(icon);
		RenderableVectorLabel label = new RenderableVectorLabel(text);
		label.addClassSelector(SECTION_TITLE_TEXT);
		list.addChild(label);
		return list;
	}

	private List createButtonWithIcon(String text, String imagePath) {
		List list = new List(LayoutOrientation.HORIZONTAL);
		list.addClassSelector(SECTION_ITEM);
		RenderableVectorLabel label = new RenderableVectorLabel(text);
		label.addClassSelector(SECTION_ITEM_TEXT);
		list.addChild(label);
		ResourceService resourceService = KernelServiceProvider.getResourceService();
		ImageWidget icon = new ImageWidget(resourceService.getImagePath(imagePath));
		icon.addClassSelector(SECTION_ITEM_ICON);
		list.addChild(icon);
		return list;
	}

	private Stylesheet createStylesheet() {
		VectorFont lightFont = KernelServiceProvider.getFontService().getLightFont();
		CascadingStylesheet stylesheet = new CascadingStylesheet();

		EditableStyle style = stylesheet.getDefaultStyle();
		style.setBackground(NoBackground.NO_BACKGROUND);
		style.setColor(Colors.WHITE);

		style = stylesheet.getSelectorStyle(new TypeSelector(Scroll.class));
		style.setBackground(new RectangularBackground(Colors.BLACK));

		style = stylesheet.getSelectorStyle(new TypeSelector(ScrollableList.class));
		style.setPadding(new FlexibleOutline(0, 8, 84, 8));

		// App title
		style = stylesheet.getSelectorStyle(new ClassSelector(TITLE));
		style.setFont(lightFont.getFont(38));
		style.setHorizontalAlignment(Alignment.HCENTER);
		style.setPadding(new FlexibleOutline(34, 0, 7, 0));

		// Section style
		style = stylesheet.getSelectorStyle(new ClassSelector(SECTION_TITLE));
		style.setPadding(new FlexibleOutline(10, 0, 0, 12));
		style.setDimension(OptimalDimension.OPTIMAL_DIMENSION_XY);

		style = stylesheet.getSelectorStyle(new ClassSelector(SECTION_TITLE_ICON));
		style.setPadding(new FlexibleOutline(0, 8, 0, 8));

		style = stylesheet.getSelectorStyle(new ClassSelector(SECTION_TITLE_TEXT));
		style.setPadding(new FlexibleOutline(0, 0, 0, 4));
		style.setFont(lightFont.getFont(32));
		style.setVerticalAlignment(Alignment.VCENTER);

		style = stylesheet.getSelectorStyle(new ClassSelector(SECTION_BODY));
		style.setPadding(new FlexibleOutline(0, 17, 0, 75));

		style = stylesheet.getSelectorStyle(new ClassSelector(SECTION_ITEM));
		style.setPadding(new FlexibleOutline(17, 0, 17, 0));
		style.setBorder(new FlexibleRectangularBorder(0x262a2c, 1, 0, 0, 0));

		style = stylesheet.getSelectorStyle(new ClassSelector(SECTION_ITEM_TEXT));
		style.setFont(lightFont.getFont(32));
		style.setVerticalAlignment(Alignment.VCENTER);

		style = stylesheet.getSelectorStyle(new ClassSelector(SECTION_ITEM_RADIO_BUTTON));
		style.setExtraInt(RadioButton.TEXT_SIZE_STYLE, 32);
		style.setExtraObject(RadioButton.FONT_STYLE, lightFont);
		style.setVerticalAlignment(Alignment.VCENTER);

		style = stylesheet.getSelectorStyle(new ClassSelector(SECTION_ITEM_SUBTITLE));
		int itemSubtitleSize = 26;
		style.setFont(lightFont.getFont(itemSubtitleSize));
		style.setExtraInt(RadioButton.TEXT_SIZE_STYLE, itemSubtitleSize);
		style.setColor(Colors.GRAY);

		style = stylesheet.getSelectorStyle(new ClassSelector(SECTION_ITEM_ICON));
		style.setPadding(new FlexibleOutline(0, 4, 0, 4));

		return stylesheet;
	}
}
