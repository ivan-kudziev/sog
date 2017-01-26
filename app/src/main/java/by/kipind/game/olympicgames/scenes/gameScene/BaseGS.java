package by.kipind.game.olympicgames.scenes.gameScene;

import android.graphics.Color;
import android.view.MotionEvent;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.util.SAXUtils;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.level.EntityLoader;
import org.andengine.util.level.constants.LevelConstants;
import org.andengine.util.level.simple.SimpleLevelEntityLoaderData;
import org.andengine.util.level.simple.SimpleLevelLoader;
import org.xml.sax.Attributes;

import java.io.IOException;

import by.kipind.game.olympicgames.GameSettings;
import by.kipind.game.olympicgames.R;
import by.kipind.game.olympicgames.ResourcesManager;
import by.kipind.game.olympicgames.SceneManager;
import by.kipind.game.olympicgames.SceneManager.SceneType;
import by.kipind.game.olympicgames.sceneElements.ChatWin;
import by.kipind.game.olympicgames.sceneElements.ShortRecordsTable;
import by.kipind.game.olympicgames.scenes.BaseScene;
import by.kipind.game.olympicgames.sprite.Svetofor;
import by.kipind.game.olympicgames.sprite.buttons.AnimBtn;
import by.kipind.game.olympicgames.sprite.buttons.BtnGoLeft;
import by.kipind.game.olympicgames.sprite.buttons.BtnGoRight;

public class BaseGS extends BaseScene implements IOnSceneTouchListener {
    private static final String GAME_TYPE = "RAFTING";
    private static final String GAME_LVL_FILE_PATH = "level/rafting.lvl";

    private static int STEPS_PER_SECOND = 60;
    // ---------
    private static final String TAG_ENTITY = "entity";
    private static final String TAG_ENTITY_ATTRIBUTE_X = "x";
    private static final String TAG_ENTITY_ATTRIBUTE_Y = "y";
    private static final String TAG_ENTITY_ATTRIBUTE_TYPE = "type";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_SVETOFOR = "svetofor";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER = "player";

   /* private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_UFO_L = "ufo_l";
    private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_UFO_L_SH = "ufo_l_sh";
    private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_UFO_R = "ufo_r";
    private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_UFO_R_SH = "ufo_r_sh";
   private static final Object TAG_GAME_ELEMENT_FROM_FILE_SHOOT_AREA_L = "area_l";
    private static final Object TAG_GAME_ELEMENT_FROM_FILE_SHOOT_AREA_R = "area_r";
    private static final Object TAG_GAME_ELEMENT_FROM_FILE_SHOOT_RED_AREA_L = "areaR_l";
    private static final Object TAG_GAME_ELEMENT_FROM_FILE_SHOOT_RED_AREA_R = "areaR_r";*/

    // ----------
    private final int lvlWidth = 800;
    private final int lvlHeight = 450;

    private HUD gameHUD;
	private Svetofor svetofor;

    private ShortRecordsTable rt;
    private ChatWin cw;

    private BtnGoRight hudShootRight;
    private BtnGoLeft hudShootLeft;

    private Sprite hudAreaBordersBl;
    private Sprite hudAreaBlackAlpha;
    private Sprite hudAreaBlackAlphaUp;

   private Sprite hudBtnReplay;
    private Sprite hudBtnBack;
    private Sprite raundResFon;

    private PhysicsWorld physicsWorld;

   //TODO: new game element boad
	// private AnimatedSprite player;

    private Text scoreText;
    private Text worldRecLabel;
    private Text roundResLabel;


    private Float wPersonalRecord;
    private Integer tCounter = 0;

    @Override
    public void createScene() {
	this.wPersonalRecord = GameSettings.W_RECORD_RAFTING;

	createBackground();
	createHUD();
	createPhysics();

	loadLevel();
	setOnSceneTouchListener(this);

    }

    @Override
    public void onBackKeyPressed() {
	SceneManager.getInstance().loadMenuScene(engine);
    }

    @Override
    public SceneType getSceneType() {
	return SceneType.SCENE_GAME;
    }

    @Override
    public void disposeScene() {

	camera.setHUD(null);
	camera.setCenter(SCENE_WIDTH / 2, SCENE_HEIGHT / 2);
	camera.setChaseEntity(null);
	resourcesManager.gameGraf.clear();
    }

    private void createBackground() {

	attachChild(new Sprite(135, this.lvlHeight / 1.8f, resourcesManager.gameGraf.get("game_background_region"), vbom));
	attachChild(new Sprite(405, this.lvlHeight / 1.8f, resourcesManager.gameGraf.get("game_background_region"), vbom));
	attachChild(new Sprite(675, this.lvlHeight / 1.8f, resourcesManager.gameGraf.get("game_background_region"), vbom));
	attachChild(new Sprite(135, 0, resourcesManager.gameGraf.get("game_panel_region"), vbom));
	attachChild(new Sprite(405, 0, resourcesManager.gameGraf.get("game_panel_region"), vbom));
	attachChild(new Sprite(675, 0, resourcesManager.gameGraf.get("game_panel_region"), vbom));

	attachChild(new Sprite(this.lvlWidth / 4, this.lvlHeight / 2f, resourcesManager.gameGraf.get("shoot_tree"), vbom));
	attachChild(new Sprite(3 * this.lvlWidth / 4, this.lvlHeight / 2, resourcesManager.gameGraf.get("shoot_tree"), vbom));

	attachChild(new Sprite(55, this.lvlHeight / 3.5f, resourcesManager.gameGraf.get("tb_left"), vbom));
	attachChild(new Sprite(this.lvlWidth - 55, this.lvlHeight / 3.5f, resourcesManager.gameGraf.get("tb_right"), vbom));

    }

    private void createHUD() {
	gameHUD = new HUD();

	cw = new ChatWin(String.valueOf(GameSettings.W_RECORD_SHOOTING), 10, SCENE_HEIGHT, 5, vbom);

	raundResFon = new Sprite(SCENE_WIDTH / 2f, SCENE_HEIGHT * 0.5f, resourcesManager.gameGraf.get("game_borders_hud_fon"), vbom);
	raundResFon.setVisible(false);

	hudAreaBordersBl = new Sprite(0, 0, resourcesManager.gameGraf.get("game_borders_bl_region"), vbom);
	hudAreaBlackAlpha = new Sprite(0, 0, resourcesManager.gameGraf.get("game_borders_hud_fon"), vbom) {
	    @Override
	    public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
		if (pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN) {
		    return SceneObjectTouch(this);
		}
		return false;
	    }
	};

	hudAreaBordersBl.setScale(0.75f, 1.3f);
	hudAreaBordersBl.setPosition(SCENE_WIDTH * 0.65f, SCENE_HEIGHT * 0.2f); // SCENE_WIDTH - hudAreaBordersBl.getWidth() * 1.5f, SCENE_HEIGHT / 8 * 1.5f

	hudAreaBlackAlpha.setScale(0.85f, 1f);
	hudAreaBlackAlpha.setPosition(cw.getX() + cw.getWidth() / 2f + (hudAreaBlackAlpha.getWidth() * 0.85f) / 2 + 3, SCENE_HEIGHT - (hudAreaBlackAlpha.getHeight() / 2f));
	hudAreaBlackAlpha.setVisible(false);

	hudAreaBlackAlphaUp = new Sprite(0, 0, resourcesManager.gameGraf.get("game_borders_hud_fon_up"), vbom);
	hudAreaBlackAlphaUp.setScale(0.85f, 1f);
	hudAreaBlackAlphaUp.setPosition(hudAreaBlackAlpha.getX(),
		hudAreaBlackAlpha.getY() + (hudAreaBlackAlpha.getHeight() * hudAreaBlackAlpha.getScaleY() - hudAreaBlackAlphaUp.getHeight()) / 2);


	hudShootRight = new BtnGoRight(SCENE_WIDTH / 2f, SCENE_HEIGHT / 2f, (ITiledTextureRegion) ResourcesManager.getInstance().gameGraf.get("bt_shoot_right"), vbom) {
	    @Override
	    public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
		if (pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN) {
		    return SceneObjectTouch(this);
		} else if (pSceneTouchEvent.getAction() == MotionEvent.ACTION_UP) {
		    // redAreaR.setVisible(false);
		}
		return false;
	    }
	};
	hudShootLeft = new BtnGoLeft(SCENE_WIDTH / 2f, SCENE_HEIGHT / 2f, (ITiledTextureRegion) ResourcesManager.getInstance().gameGraf.get("bt_shoot_left"), vbom) {
	    @Override
	    public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
		if (pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN) {
		    return SceneObjectTouch(this);
		} else if (pSceneTouchEvent.getAction() == MotionEvent.ACTION_UP) {
		    // redAreaL.setVisible(false);
		}
		return false;
	    }
	};

	hudShootLeft.setPosition(cw.getX() + cw.getWidth() / 2f - hudShootLeft.getHeight() / 2, hudShootLeft.getWidth() / 2 + 8);
	hudShootRight.setPosition(SCENE_WIDTH - hudShootLeft.getX(), hudShootRight.getWidth() / 2 + 8);

	hudBtnReplay = new Sprite(SCENE_WIDTH / 2, 0, resourcesManager.gameGraf.get("bt_replay"), vbom) {
	    @Override
	    public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
		if (pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN) {
		    return SceneObjectTouch(this);
		}
		return false;
	    }
	};
	hudBtnReplay.setScale(0.5f);
	hudBtnReplay.setPosition(SCENE_WIDTH / 2 - hudBtnReplay.getWidth() / 3, hudBtnReplay.getWidth() / 4);

	hudBtnBack = new Sprite(SCENE_WIDTH / 2, 0, resourcesManager.gameGraf.get("bt_back"), vbom) {
	    @Override
	    public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
		if (pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN) {
		    return SceneObjectTouch(this);
		}
		return false;
	    }
	};
	hudBtnBack.setScale(0.5f);

	hudBtnBack.setPosition(SCENE_WIDTH - hudBtnBack.getWidth() * 0.3f, SCENE_HEIGHT - hudBtnBack.getHeight() * 0.25f);
	hudBtnReplay.setPosition(SCENE_WIDTH - hudBtnReplay.getWidth() * 0.3f, hudBtnBack.getY() - hudBtnBack.getHeight() * 0.25f - hudBtnReplay.getHeight() * 0.25f - 3);


	// CREATE TEXT info
	scoreText = new Text(0, 0, resourcesManager.font_pix_kir, "-fail: 0.1234567890 Null", new TextOptions(HorizontalAlign.LEFT), vbom);
	scoreText.setScale(0.5f);
	scoreText.setPosition(hudAreaBordersBl.getX() - (hudAreaBordersBl.getWidth() / 4), hudAreaBordersBl.getY() - (hudAreaBordersBl.getHeight() / 2));
	scoreText.setAnchorCenter(0, 0);
	scoreText.setText("0.000");


	roundResLabel = new Text(0, 0, resourcesManager.font_pix_kir, context.getString(R.string.game_hud_rs_lb), new TextOptions(HorizontalAlign.CENTER), vbom);
	roundResLabel.setText(context.getString(R.string.game_hud_rs_lb));
	roundResLabel.setScale(0.5f);
	roundResLabel.setPosition(raundResFon.getX(), raundResFon.getY() + (raundResFon.getHeight() / 2f) - (roundResLabel.getHeight() / 5));
	roundResLabel.setVisible(false);

	worldRecLabel = new Text(0, 0, resourcesManager.font_pix_kir, context.getString(R.string.game_hud_wd_lb), new TextOptions(HorizontalAlign.CENTER), vbom);
	worldRecLabel.setText(context.getString(R.string.game_hud_wd_lb));
	worldRecLabel.setScale(0.5f);
	worldRecLabel.setPosition(hudAreaBlackAlpha.getX(), hudAreaBlackAlpha.getY() + (hudAreaBlackAlpha.getHeight() / 2f) - (worldRecLabel.getHeight() / 5));


	rt = new ShortRecordsTable("" + GameSettings.GAME_CODE + GameSettings.ACTIVITY_ID_SHOOTING, GameSettings.WEEK_OF_YEAR, GameSettings.GAME_PLAYER_NICK_ID,
		hudAreaBlackAlpha.getX(),
		hudAreaBlackAlpha.getY(), hudAreaBlackAlpha.getWidth() * 0.85f - 10, hudAreaBlackAlpha.getHeight() - (worldRecLabel.getHeight() / 2), 5, camera, vbom);
	rt.setVisible(false);

	// --------------

	gameHUD.registerTouchArea(hudShootRight);
	gameHUD.registerTouchArea(hudShootLeft);
	gameHUD.registerTouchArea(hudBtnReplay);
	gameHUD.registerTouchArea(hudBtnBack);
	gameHUD.registerTouchArea(hudAreaBlackAlpha);
	gameHUD.setTouchAreaBindingOnActionDownEnabled(true);

	gameHUD.attachChild(raundResFon);
	gameHUD.attachChild(roundResLabel);
	gameHUD.attachChild(hudAreaBordersBl);
	gameHUD.attachChild(hudAreaBlackAlpha);
	gameHUD.attachChild(hudAreaBlackAlphaUp);
	gameHUD.attachChild(hudShootRight);
	gameHUD.attachChild(hudShootLeft);
	gameHUD.attachChild(hudBtnReplay);
	gameHUD.attachChild(hudBtnBack);
	gameHUD.attachChild(rt);
	gameHUD.attachChild(cw);
	gameHUD.attachChild(scoreText);
	// gameHUD.attachChild(ufoLeftText);
	gameHUD.attachChild(worldRecLabel);

	gameHUD.registerUpdateHandler(new TimerHandler(1 / 60f, true, new ITimerCallback() {
	    @Override
	    public void onTimePassed(final TimerHandler pTimerHandler) {
		if ( svetofor.getStatus() == Color.GREEN) {
		    tCounter++;
		    scoreText.setText(String.valueOf((double) tCounter / 1000));




		}
	    }
	}));

	camera.setHUD(gameHUD);
    }

    private void createPhysics() {
	physicsWorld = new FixedStepPhysicsWorld(STEPS_PER_SECOND, new Vector2(0, 0), false);
	physicsWorld.setContactListener(contactListener());
	registerUpdateHandler(physicsWorld);
    }

    private void loadLevel() {
	final SimpleLevelLoader levelLoader = new SimpleLevelLoader(vbom);
	final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);

	levelLoader.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(LevelConstants.TAG_LEVEL) {
	    public IEntity onLoadEntity(final String pEntityName, final IEntity pParent, final Attributes pAttributes,
		    final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData) throws IOException {
		final int width = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_WIDTH);
		final int height = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_HEIGHT);

		camera.setBounds(0, 0, width, height);
		camera.setBoundsEnabled(true);

		return BaseGS.this;
	    }
	});

	levelLoader.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(TAG_ENTITY) {
	    public IEntity onLoadEntity(final String pEntityName, final IEntity pParent, final Attributes pAttributes,
		    final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData) throws IOException {
		final int x = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_X);
		final int y = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_Y);
		final String type = SAXUtils.getAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_TYPE);

		final Sprite levelObject;
		// final String asa = type.substring(0, 5);

		if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER)) {
		   /* bout = new AnimatedSprite(SCENE_WIDTH / 2f, SCENE_HEIGHT / 5f, (ITiledTextureRegion) ResourcesManager.getInstance().gameGraf.get("player_region"), vbom);
			bout.setCurrentTileIndex(1);
		    camera.setChaseEntity(bout);
		    // player.setMaxSpeed(11);
		    levelObject = player;*/
			levelObject = null;
		}  else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_SVETOFOR)) {
		    svetofor = new Svetofor(SCENE_WIDTH / 2, SCENE_HEIGHT / 2, vbom, camera, physicsWorld);
		    svetofor.setRemoveFlag(true);
		    levelObject = svetofor;
		    svetofor.Start();

		} else {
		    throw new IllegalArgumentException();
		}

		levelObject.setCullingEnabled(true);

		return levelObject;
	    }
	});

	levelLoader.loadLevelFromAsset(activity.getAssets(), GAME_LVL_FILE_PATH);
    }

    private boolean SceneObjectTouch(Object touchedObj) {
	boolean res = false;
	if (touchedObj.equals(hudBtnReplay) && hudBtnReplay.isVisible()) {
	    restartGameLvl();
	    res = true;
	} else if (touchedObj.equals(hudBtnBack) && hudBtnBack.isVisible()) {
	    onBackKeyPressed();
	    res = true;
	} else if (touchedObj.equals(hudAreaBlackAlpha)) {
	    hudAreaBlackAlpha.setVisible(!hudAreaBlackAlpha.isVisible());
	    hudAreaBlackAlphaUp.setVisible(!hudAreaBlackAlpha.isVisible());
	    rt.setVisible(hudAreaBlackAlpha.isVisible());
	    if (rt.isVisible()) {
		rt.reload();
	    }
	    res = true;
	}

	return res;
    }

    private ContactListener contactListener() {
	ContactListener contactListener = new ContactListener() {
	    public void beginContact(Contact contact) {
		final Fixture x1 = contact.getFixtureA();
		final Fixture x2 = contact.getFixtureB();

		if (x1.getBody().getUserData() != null && x2.getBody().getUserData() != null) {

		}
	    }

	    public void endContact(Contact contact) {
		final Fixture x1 = contact.getFixtureA();
		final Fixture x2 = contact.getFixtureB();

	    }

	    public void preSolve(Contact contact, Manifold oldManifold) {

	    }

	    public void postSolve(Contact contact, ContactImpulse impulse) {

	    }
	};
	return contactListener;

    }

    @Override
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {

	return false;
    }


    @Override
    public void setHUD() {
	// TODO Auto-generated method stub

    }


    private void showRes() {
	raundResFon.setVisible(true);
	roundResLabel.setVisible(true);
	hudAreaBordersBl.setPosition(raundResFon.getX(), raundResFon.getY() + raundResFon.getHeight() * 0.15f);
	scoreText.setPosition(hudAreaBordersBl.getX() - (hudAreaBordersBl.getWidth() / 4), hudAreaBordersBl.getY() - (hudAreaBordersBl.getHeight() / 2));

	hudBtnBack.setScale(0.7f);
	hudBtnReplay.setScale(0.7f);

	hudBtnBack.setPosition(raundResFon.getX() - hudBtnBack.getWidth() * 0.5f, raundResFon.getY() - raundResFon.getHeight() * 0.25f);
	hudBtnReplay.setPosition(raundResFon.getX() + hudBtnBack.getWidth() * 0.5f, raundResFon.getY() - raundResFon.getHeight() * 0.25f);

    }

    private void hideRes() {
	raundResFon.setVisible(false);
	roundResLabel.setVisible(false);
	hudAreaBordersBl.setPosition(SCENE_WIDTH * 0.65f, SCENE_HEIGHT * 0.2f); // SCENE_WIDTH - hudAreaBordersBl.getWidth() * 1.5f, SCENE_HEIGHT / 8 * 1.5f
	scoreText.setPosition(hudAreaBordersBl.getX() - (hudAreaBordersBl.getWidth() / 4), hudAreaBordersBl.getY() - (hudAreaBordersBl.getHeight() / 2));

	hudBtnBack.setScale(0.5f);
	hudBtnReplay.setScale(0.5f);

	hudBtnBack.setPosition(SCENE_WIDTH - hudBtnBack.getWidth() * 0.3f, SCENE_HEIGHT - hudBtnBack.getHeight() * 0.25f);
	hudBtnReplay.setPosition(SCENE_WIDTH - hudBtnReplay.getWidth() * 0.3f, hudBtnBack.getY() - hudBtnBack.getHeight() * 0.25f - hudBtnReplay.getHeight() * 0.25f - 3);

    }

    private void restartGameLvl() {
	hideRes();

	this.tCounter = 0;




	hudShootRight.setCurrentState(AnimBtn.BTN_STATE_FREE);
	hudShootLeft.setCurrentState(AnimBtn.BTN_STATE_FREE);



	this.wPersonalRecord = GameSettings.W_RECORD_SHOOTING;

	scoreText.setText("0.000");

	svetofor.reSet();
	svetofor.Start();

	//camera.setChaseEntity(player);

    }

}