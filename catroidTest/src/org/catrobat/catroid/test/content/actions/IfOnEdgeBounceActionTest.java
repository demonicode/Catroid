/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.content.actions;

import java.io.File;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.content.actions.IfOnEdgeBounceAction;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.test.R;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.UtilFile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.InstrumentationTestCase;

public class IfOnEdgeBounceActionTest extends InstrumentationTestCase {

	private int BOUNCE_LEFT_POS;
	private int BOUNCE_RIGHT_POS;
	private int BOUNCE_BOTTOM_POS;
	private int BOUNCE_TOP_POS;
	private float SCREEN_HALF_HEIGHT;
	private float SCREEN_HALF_WIDTH;
	private static final int IMAGE_FILE_ID = R.raw.icon;

	private final String projectName = "testProject";
	private File testImage;
	private LookData lookData;
	private float width;
	private float height;

	@Override
	public void setUp() throws Exception {
		File projectFile = new File(Constants.DEFAULT_ROOT + "/" + projectName);

		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}

		ScreenValues.SCREEN_HEIGHT = 800;
		ScreenValues.SCREEN_WIDTH = 480;
		SCREEN_HALF_HEIGHT = ScreenValues.SCREEN_HEIGHT / 2;
		SCREEN_HALF_WIDTH = ScreenValues.SCREEN_WIDTH / 2;

		BOUNCE_LEFT_POS = -(ScreenValues.SCREEN_WIDTH + 50);
		BOUNCE_RIGHT_POS = ScreenValues.SCREEN_WIDTH + 50;
		BOUNCE_BOTTOM_POS = -(ScreenValues.SCREEN_HEIGHT + 50);
		BOUNCE_TOP_POS = ScreenValues.SCREEN_HEIGHT + 50;

		Project project = new Project(getInstrumentation().getTargetContext(), projectName);
		project.getXmlHeader().virtualScreenHeight = ScreenValues.SCREEN_HEIGHT;
		project.getXmlHeader().virtualScreenWidth = ScreenValues.SCREEN_WIDTH;
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		testImage = TestUtils.saveFileToProject(this.projectName, "testImage.png", IMAGE_FILE_ID, getInstrumentation()
				.getContext(), TestUtils.TYPE_IMAGE_FILE);

		lookData = new LookData();
		lookData.setLookFilename(testImage.getName());
		lookData.setLookName("LookName");

		Bitmap bitmap = BitmapFactory.decodeFile(testImage.getAbsolutePath());
		width = bitmap.getWidth();
		height = bitmap.getHeight();
	}

	@Override
	protected void tearDown() throws Exception {
		File projectFile = new File(Constants.DEFAULT_ROOT + "/" + projectName);

		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}
		if (testImage != null && testImage.exists()) {
			testImage.delete();
		}
		super.tearDown();
	}

	public void testNoBounce() {
		Sprite sprite = new Sprite("testSprite");
		sprite.look.setLookData(lookData);
		sprite.look.setWidth(width);
		sprite.look.setHeight(height);
		sprite.look.setXInUserInterfaceDimensionUnit(0);
		sprite.look.setYInUserInterfaceDimensionUnit(0);

		IfOnEdgeBounceAction action = ExtendedActions.ifOnEdgeBounce(sprite);
		action.act(1.0f);
		assertEquals("Wrong X-Position!", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
		assertEquals("Wrong direction", 90f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);

	}

	public void testBounceTop() {
		Sprite sprite = new Sprite("testSprite");
		sprite.look.setLookData(lookData);
		sprite.look.setWidth(width);
		sprite.look.setHeight(height);

		IfOnEdgeBounceAction action = ExtendedActions.ifOnEdgeBounce(sprite);

		sprite.look.setDirectionInUserInterfaceDimensionUnit(180f);
		sprite.look.setPositionInUserInterfaceDimensionUnit(0, BOUNCE_TOP_POS);

		action.act(1.0f);

		assertEquals("Wrong X-Position!", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", SCREEN_HALF_HEIGHT - (height / 2),
				sprite.look.getYInUserInterfaceDimensionUnit());
		assertEquals("Wrong direction", 0f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);
		assertEquals("Width shouldn't change", width, sprite.look.getWidth(), 1e-3);
		assertEquals("Height shouldn't change", height, sprite.look.getHeight(), 1e-3);

		sprite.look.setDirectionInUserInterfaceDimensionUnit(150f);
		sprite.look.setPositionInUserInterfaceDimensionUnit(0, BOUNCE_TOP_POS);
		action.restart();
		action.act(1.0f);

		assertEquals("Wrong X-Position!", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", SCREEN_HALF_HEIGHT - (height / 2),
				sprite.look.getYInUserInterfaceDimensionUnit());
		assertEquals("Wrong direction", 30f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);

		sprite.look.setDirectionInUserInterfaceDimensionUnit(150f);
		sprite.look.setPositionInUserInterfaceDimensionUnit(0, BOUNCE_TOP_POS);
		action.restart();
		action.act(1.0f);

		assertEquals("Wrong X-Position!", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", SCREEN_HALF_HEIGHT - (height / 2),
				sprite.look.getYInUserInterfaceDimensionUnit());
		assertEquals("Wrong direction", 30f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);

		sprite.look.setDirectionInUserInterfaceDimensionUnit(42.42f);
		sprite.look.setPositionInUserInterfaceDimensionUnit(0, BOUNCE_TOP_POS);
		action.restart();
		action.act(1.0f);

		assertEquals("Wrong X-Position!", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", SCREEN_HALF_HEIGHT - (height / 2),
				sprite.look.getYInUserInterfaceDimensionUnit());
		assertEquals("Wrong direction", 137.58, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);
	}

	public void testBounceBottom() {
		Sprite sprite = new Sprite("testSprite");
		sprite.look.setLookData(lookData);
		sprite.look.setWidth(width);
		sprite.look.setHeight(height);

		IfOnEdgeBounceAction action = ExtendedActions.ifOnEdgeBounce(sprite);

		sprite.look.setDirectionInUserInterfaceDimensionUnit(180f);
		sprite.look.setPositionInUserInterfaceDimensionUnit(0, BOUNCE_BOTTOM_POS);

		action.act(1.0f);

		assertEquals("Wrong X-Position!", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", -SCREEN_HALF_HEIGHT + (height / 2),
				sprite.look.getYInUserInterfaceDimensionUnit());
		assertEquals("Wrong direction", 0f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);
		assertEquals("Width shouldn't change", width, sprite.look.getWidth(), 1e-3);
		assertEquals("Height shouldn't change", height, sprite.look.getHeight(), 1e-3);

		sprite.look.setDirectionInUserInterfaceDimensionUnit(120f);
		sprite.look.setPositionInUserInterfaceDimensionUnit(0, BOUNCE_BOTTOM_POS);
		action.restart();
		action.act(1.0f);

		assertEquals("Wrong X-Position!", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", -SCREEN_HALF_HEIGHT + (height / 2),
				sprite.look.getYInUserInterfaceDimensionUnit());
		assertEquals("Wrong direction", 60f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);

		sprite.look.setDirectionInUserInterfaceDimensionUnit(30f);
		sprite.look.setPositionInUserInterfaceDimensionUnit(0, BOUNCE_BOTTOM_POS);
		action.restart();
		action.act(1.0f);

		assertEquals("Wrong X-Position!", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", -SCREEN_HALF_HEIGHT + (height / 2),
				sprite.look.getYInUserInterfaceDimensionUnit());
		assertEquals("Wrong direction", 150f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);

		sprite.look.setDirectionInUserInterfaceDimensionUnit(132.42f);
		sprite.look.setPositionInUserInterfaceDimensionUnit(0, BOUNCE_BOTTOM_POS);
		action.restart();
		action.act(1.0f);

		assertEquals("Wrong X-Position!", 0f, sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", -SCREEN_HALF_HEIGHT + (height / 2),
				sprite.look.getYInUserInterfaceDimensionUnit());
		assertEquals("Wrong direction", 47.58f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);

	}

	public void testBounceRight() {
		Sprite sprite = new Sprite("testSprite");
		sprite.look.setLookData(lookData);
		sprite.look.setWidth(width);
		sprite.look.setHeight(height);

		IfOnEdgeBounceAction action = ExtendedActions.ifOnEdgeBounce(sprite);

		sprite.look.setDirectionInUserInterfaceDimensionUnit(90f);
		sprite.look.setPositionInUserInterfaceDimensionUnit(BOUNCE_RIGHT_POS, 0);

		action.act(1.0f);
		action.restart();
		action.act(1.0f);

		assertEquals("Wrong X-Position!", SCREEN_HALF_WIDTH - (width / 2),
				sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
		assertEquals("Wrong direction", 270f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);
		assertEquals("Width shouldn't change", width, sprite.look.getWidth(), 1e-3);
		assertEquals("Height shouldn't change", height, sprite.look.getHeight(), 1e-3);

		sprite.look.setDirectionInUserInterfaceDimensionUnit(30f);
		sprite.look.setPositionInUserInterfaceDimensionUnit(BOUNCE_RIGHT_POS, 0);
		action.restart();
		action.act(1.0f);

		assertEquals("Wrong X-Position!", SCREEN_HALF_WIDTH - (width / 2),
				sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
		assertEquals("Wrong direction", 330f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);

		sprite.look.setDirectionInUserInterfaceDimensionUnit(30f);
		sprite.look.setPositionInUserInterfaceDimensionUnit(BOUNCE_RIGHT_POS, 0);
		action.restart();
		action.act(1.0f);

		assertEquals("Wrong X-Position!", SCREEN_HALF_WIDTH - (width / 2),
				sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
		assertEquals("Wrong direction", 330f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);

		sprite.look.setDirectionInUserInterfaceDimensionUnit(42.42f);
		sprite.look.setPositionInUserInterfaceDimensionUnit(BOUNCE_RIGHT_POS, 0);
		action.restart();
		action.act(1.0f);

		assertEquals("Wrong X-Position!", SCREEN_HALF_WIDTH - (width / 2),
				sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
		assertEquals("Wrong direction", 317.58f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);
	}

	public void testBounceLeft() {
		Sprite sprite = new Sprite("testSprite");
		sprite.look.setLookData(lookData);
		sprite.look.setWidth(width);
		sprite.look.setHeight(height);

		IfOnEdgeBounceAction action = ExtendedActions.ifOnEdgeBounce(sprite);

		sprite.look.setDirectionInUserInterfaceDimensionUnit(90f);
		sprite.look.setPositionInUserInterfaceDimensionUnit(BOUNCE_LEFT_POS, 0);

		action.act(1.0f);

		assertEquals("Wrong X-Position!", -SCREEN_HALF_WIDTH + (width / 2),
				sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
		assertEquals("Wrong direction", 270f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);
		assertEquals("Width shouldn't change", width, sprite.look.getWidth(), 1e-3);
		assertEquals("Height shouldn't change", height, sprite.look.getHeight(), 1e-3);

		sprite.look.setDirectionInUserInterfaceDimensionUnit(30f);
		sprite.look.setPositionInUserInterfaceDimensionUnit(BOUNCE_LEFT_POS, 0);
		action.restart();
		action.act(1.0f);

		assertEquals("Wrong X-Position!", -SCREEN_HALF_WIDTH + (width / 2),
				sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
		assertEquals("Wrong direction", 330f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);

		sprite.look.setDirectionInUserInterfaceDimensionUnit(30f);
		sprite.look.setPositionInUserInterfaceDimensionUnit(BOUNCE_LEFT_POS, 0);
		action.restart();
		action.act(1.0f);

		assertEquals("Wrong X-Position!", -SCREEN_HALF_WIDTH + (width / 2),
				sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
		assertEquals("Wrong direction", 330f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);

		sprite.look.setDirectionInUserInterfaceDimensionUnit(42.42f);
		sprite.look.setPositionInUserInterfaceDimensionUnit(BOUNCE_LEFT_POS, 0);
		action.restart();
		action.act(1.0f);

		assertEquals("Wrong X-Position!", -SCREEN_HALF_WIDTH + (width / 2),
				sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Wrong Y-Position!", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
		assertEquals("Wrong direction", 317.58f, sprite.look.getDirectionInUserInterfaceDimensionUnit(), 1e-3);
	}
}
