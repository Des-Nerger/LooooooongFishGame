import static java.lang.System.err;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.FloatArray;
import com.esotericsoftware.spine.Animation;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;

public class LooooooongFishGame implements ApplicationListener, InputProcessor {
	public static void main(String[] args) {
		new LwjglApplication(new LooooooongFishGame(), "LooooooongFishGame", 1152, 720);
	}

	private PolygonSpriteBatch batch;
	private Texture bgTexture;
	private Texture rudyTexture;
	private Animation standAnim;
	private Animation walkAnim;
	private Animation sitDownAnim;
	private Animation sitUpAnim;
	private Animation sitWaitAnim;
	private SkeletonRenderer renderer;
	private Skeleton skelet;
	private AnimationState animState;
	private Bone axisBone;
	private Bone toe0;
	private Bone toe1;
	private boolean paused;
	private FloatArray tempFloats;

	enum State {
		STAND,
		SIT
	}

	private State state;

	private AnimationState.TrackEntry entry() {
		return animState.getCurrent(0);
	}

	private boolean is_toe0_supporting() {
		var _entry = this.entry();
		if (_entry.getAnimation() != walkAnim) return true;
		var _animTime = _entry.getAnimationTime();
		return (0.025f <= _animTime) & (_animTime <= 0.7f);
	}

	@Override
	public void create() {
		batch = new PolygonSpriteBatch();
		final var _dirPrefix = "assets/";
		bgTexture = new Texture(Gdx.files.internal(_dirPrefix + "bg.png"));
		renderer = new SkeletonRenderer();
		renderer.setPremultipliedAlpha(true);
		final var _filePrefix = _dirPrefix + "rudy"; // 18 Aug 2023
		var _atlas = new TextureAtlas(Gdx.files.internal(_filePrefix + ".atlas"));
		rudyTexture = _atlas.getTextures().first();
		var _json = new SkeletonJson(_atlas);
		_json.setScale(0.755f);
		var _skeletData = _json.readSkeletonData(Gdx.files.internal(_filePrefix + ".json"));
		standAnim = _skeletData.findAnimation("wait_stand");
		walkAnim = _skeletData.findAnimation("walk-cycle");
		sitDownAnim = _skeletData.findAnimation("sit-down");
		sitUpAnim = _skeletData.findAnimation("sit-up");
		sitWaitAnim = _skeletData.findAnimation("sit-wait");
		var _stateData = new AnimationStateData(_skeletData);
		_stateData.setDefaultMix(0.33f);
		_stateData.setMix(walkAnim, walkAnim, 0.00f);
		// _stateData.setMix(sitDownAnim, standAnim, 0.33f);
		// _stateData.setMix(sitWaitAnim, standAnim, 0.33f);
		animState = new AnimationState(_stateData);
		// animState.setTimeScale(0.1f);
		/*
		for (var timeline: walkAnim.getTimelines()) {
			if (timeline instanceof Animation.EventTimeline) {
				for (var event: ((Animation.EventTimeline) timeline).getEvents()) {
					err.printf("time = %f\n", event.getTime());
				}
			}
		}
		*/
		animState.setAnimation(0, standAnim, true);
		skelet = new Skeleton(_skeletData);
		axisBone = skelet.findBone(/*-* "bone" /*/ "bone2" /*-*/);
		toe0 = skelet.findBone("bone17");
		toe1 = skelet.findBone("bone21");
		skelet.setPosition(1050f, 126f);
		skelet.setScaleX(-1f);
		skelet.updateWorldTransform();
		tempFloats = new FloatArray();
		state = State.STAND;
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void dispose() {}

	@Override
	public void pause() {
		paused = true;
	}

	private void rudy_turnAround() {
		var _axisBone_worldX = axisBone.getWorldX();
		skelet.setScaleX(-skelet.getScaleX());
		skelet.updateWorldTransform();
		skelet.setX(skelet.getX() + (_axisBone_worldX - axisBone.getWorldX()));
		skelet.updateWorldTransform();
		if (paused) {
			paused = false;
			this.render();
			paused = true;
		}
	}

	@Override
	public boolean keyDown(int _keycode) {
		final var WALK_ANIM_START = 0.40f;
		var _entry = this.entry();
		switch (_keycode) {
		case Keys.A:
			if (skelet.getScaleX() > 0f) {
				this.rudy_turnAround();
			}
			if (_entry.getAnimation() == standAnim && _entry.getMixingFrom() == null) {
				_entry = animState.setAnimation(0, walkAnim, false);
				_entry.setAnimationStart(WALK_ANIM_START);
				animState.addAnimation(0, walkAnim, true, 0f);
			}
			break;
		case Keys.D:
			if (skelet.getScaleX() < 0f) {
				this.rudy_turnAround();
			}
			if (_entry.getAnimation() == standAnim && _entry.getMixingFrom() == null) {
				_entry = animState.setAnimation(0, walkAnim, false);
				_entry.setAnimationStart(WALK_ANIM_START);
				animState.addAnimation(0, walkAnim, true, 0f);
			}
			break;
		case Keys.S:
			if (state != State.SIT) {
				state = State.SIT;
				animState.setAnimation(0, sitDownAnim, false);
				animState.addAnimation(0, sitWaitAnim, true, 0f);
			}
			break;
		case Keys.W:
			if (state != State.STAND) {
				state = State.STAND;
				animState.setAnimation(0, sitUpAnim, false);
				animState.addAnimation(0, standAnim, true, 0f);
			}
			break;
		case Keys.ESCAPE:
			Gdx.app.exit();
			break;
		case Keys.P:
			paused ^= true;
			break;
		case Keys.T:
			err.printf("animTime = %f\n", this.entry().getAnimationTime());
			break;
		default:
			return false;
		}
		return true;
	}

	@Override
	public boolean keyUp(int _keycode) {
		switch (_keycode) {
		case Keys.A:
		case Keys.D:
			var _entry = this.entry();
			if (_entry.getAnimation() == walkAnim) {
				animState.setAnimation(0, standAnim, true);
			}
			break;
		default:
			return false;
		}
		return true;
	}

	@Override
	public void render() {
		if (paused) return;
		var _toe0_x = toe0.getWorldX();
		var _toe1_x = toe1.getWorldX();
		var _delta = Gdx.graphics.getDeltaTime();
		animState.update(_delta);
		if (animState.apply(skelet)) {
			skelet.updateWorldTransform();
		}
		var _entry = this.entry();
		if (_entry.getAnimation() == walkAnim) {
			var _skelet_x = skelet.getX();
			var _skelet_scaleX = skelet.getScaleX();
			// if (_entry.getMixingFrom() == null) {
			_skelet_x += this.is_toe0_supporting() ? (_toe0_x - toe0.getWorldX()) : (_toe1_x - toe1.getWorldX());
			// }
			if (_skelet_scaleX < 0) {
				if (_skelet_x < 0) {
					_skelet_x += 1350;
				}
			} else {
				if (_skelet_x > 1250) {
					_skelet_x = -200;
				}
			}
			skelet.setX(_skelet_x);
			skelet.updateWorldTransform();
		}
		batch.begin();
		batch.draw(bgTexture, 0, 0);
		/*
		var _offset = new Vector2();
		var _size = new Vector2();
		skelet.getBounds(_offset, _size, tempFloats);
		batch.draw(
			rudyTexture,
			_offset.x, _offset.y,
			_size.x, _size.y,
			80, 12, 1, 1, // red-colored pixel
			false, false);
		*/
		renderer.draw(batch, skelet);
		/*
		if (entry.getAnimation() == walkAnim) {
			var animTime = entry.getAnimationTime();
			err.printf("animTime = %f\n", animTime);
			if ((0.0667 <= animTime) & (animTime <= 0.7)) {
				batch.draw(
					rudyTexture,
					toe0.getWorldX() - 8, toe0.getWorldY() - 8,
					16, 16,
					259, 127, 1, 1, // green-colored pixel
					false, false);
			}
			if ((1.3 <= animTime) | (animTime <= 0.5333)) {
				batch.draw(
					rudyTexture,
					toe1.getWorldX() - 8, toe1.getWorldY() - 8,
					16, 16,
					80, 12, 1, 1, // red-colored pixel
					false, false);
			}
		}
		*/
		batch.end();
	}

	@Override
	public void resize(int _width, int _height) {}

	@Override
	public void resume() {
		paused = false;
	}

	@Override
	public boolean keyTyped(char _character) {
		return false;
	}

	@Override
	public boolean mouseMoved(int _screenX, int _screenY) {
		return false;
	}

	@Override
	public boolean scrolled(float _amountX, float _amountY) {
		return false;
	}

	@Override
	public boolean touchDown(int _screenX, int _screenY, int _pointer, int _button) {
		return false;
	}

	@Override
	public boolean touchDragged(int _screenX, int _screenY, int _pointer) {
		return false;
	}

	@Override
	public boolean touchUp(int _screenX, int _screenY, int _pointer, int _button) {
		return false;
	}
}
