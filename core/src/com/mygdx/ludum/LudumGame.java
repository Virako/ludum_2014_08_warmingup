package com.mygdx.ludum;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class LudumGame extends ApplicationAdapter {

	private SpriteBatch batch;
	private Texture img;
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera camera;
	private AssetManager assetManager;
	private float GRAVITY = -2.8f;
	private Animation stand;
	private Animation walk;
	private Animation jump;
	private Texture rayaTexture;
	private TextureAtlas atlas;
	private String TEXTURE_ATLAS_OBJECTS = "rayaman.pack";



	private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
		@Override
		protected Rectangle newObject () {
		return new Rectangle();
		}
		};


	private RayaMan raya;

	static class RayaMan {

		static float WIDTH;
		static float HEIGHT;
		static float MAX_VELOCITY = 100f;
		static float JUMP_VELOCITY = 100f;
		static float DAMPING = 0.87f;
		enum State {
			Standing, Walking, Jumping
		}
		final Vector2 position = new Vector2();
		final Vector2 velocity = new Vector2();
		State state = State.Walking;
		float stateTime = 0;
		boolean facesRight = true;
		boolean grounded = false;

		}

	@Override
	public void create () {

		assetManager = new AssetManager();
		createAnimations();

		map = new TmxMapLoader().load("prueba.tmx");

		renderer = new OrthogonalTiledMapRenderer(map, 1);

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 400, 240);
		camera.update();


		raya = new RayaMan();
		raya.position.set(20,20);
	}

	private void createAnimations() {
		assetManager.load(TEXTURE_ATLAS_OBJECTS, TextureAtlas.class);
		assetManager.finishLoading();

		atlas = assetManager.get(TEXTURE_ATLAS_OBJECTS);
		Array<AtlasRegion> regions;

		regions = atlas.findRegions("rayaman_walking");
		walk = new Animation(0.15f, regions);
		walk.setPlayMode(Animation.PlayMode.LOOP);

		regions = atlas.findRegions("rayaman_standing");
		stand = new Animation(0, regions);
		jump = new Animation(0, regions);

		RayaMan.WIDTH = 16f;
		RayaMan.HEIGHT = 21f;

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		float deltaTime = Gdx.graphics.getDeltaTime();

		updateRaya(deltaTime);

		camera.position.x = raya.position.x;
		camera.update();

		renderer.setView(camera);
		renderer.render();

		renderRayaMan(deltaTime);
	}

	private void renderRayaMan (float deltaTime) {
		// based on the koala state, get the animation frame
		TextureRegion frame = null;
		switch (raya.state) {
		case Standing:
			frame = stand.getKeyFrame(raya.stateTime);
			break;
		case Walking:
			frame = walk.getKeyFrame(raya.stateTime);
			break;
		case Jumping:
			frame = jump.getKeyFrame(raya.stateTime);
			break;
		}
		// draw the koala, depending on the current velocity
		// on the x-axis, draw the koala facing either right
		// or left
		Batch batch = renderer.getSpriteBatch();
		batch.begin();
		if (raya.facesRight) {
			if (frame.isFlipX())
				frame.flip(true, false);
			batch.draw(frame, raya.position.x, raya.position.y);
		} else {
			if (!frame.isFlipX())
				frame.flip(true, false);
			batch.draw(frame, raya.position.x, raya.position.y);
		}
		batch.end();
		}

	private void updateRaya(float deltaTime) {
		if (deltaTime == 0)
			return;

		raya.stateTime += deltaTime;

		if (Gdx.input.isKeyPressed(Keys.S) && raya.grounded){
			raya.velocity.y += RayaMan.JUMP_VELOCITY;
			raya.grounded = false;
			raya.state = RayaMan.State.Jumping;
		}

		if (Gdx.input.isKeyPressed(Keys.LEFT)){
			raya.velocity.x = -RayaMan.MAX_VELOCITY;
			if (raya.grounded) raya.state = RayaMan.State.Walking;
			raya.facesRight = false;
		}

		if (Gdx.input.isKeyPressed(Keys.RIGHT)){
			raya.velocity.x = RayaMan.MAX_VELOCITY;
			if (raya.grounded) raya.state = RayaMan.State.Walking;
			raya.facesRight = true;
		}

		raya.velocity.add(0, GRAVITY);

		// clamp the velocity to the maximum, x-axis only
//		if (Math.abs(koala.velocity.x) > Koala.MAX_VELOCITY) {
//		koala.velocity.x = Math.signum(koala.velocity.x) * Koala.MAX_VELOCITY;
//		}

		if (raya.velocity.y < 0.5){
			raya.velocity.y = 0;
			raya.grounded = true;
		}
		// clamp the velocity to 0 if it's < 1, and set the state to standign
		if (Math.abs(raya.velocity.x) < 1) {
		raya.velocity.x = 0;
		if (raya.grounded) raya.state = RayaMan.State.Standing;
		}

		raya.velocity.scl(deltaTime);

		//collision detection


		// unscale the velocity by the inverse delta time and set
		// the latest position
		raya.position.add(raya.velocity);
		raya.velocity.scl(1 / deltaTime);
		// Apply damping to the velocity on the x-axis so we don't
		// walk infinitely once a key was pressed
		raya.velocity.x *= RayaMan.DAMPING;
	}

	private void getTiles (int startX, int startY, int endX, int endY, Array<Rectangle> tiles) {

		TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(1);
		rectPool.freeAll(tiles);
		tiles.clear();
		for (int y = startY; y <= endY; y++) {
			for (int x = startX; x <= endX; x++) {
				Cell cell = layer.getCell(x, y);
				if (cell != null) {
					Rectangle rect = rectPool.obtain();
					rect.set(x, y, 1, 1);
					tiles.add(rect);
					}
				}
			}
		}
}
