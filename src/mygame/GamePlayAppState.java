
package mygame;


import com.jme3.scene.debug.Arrow;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;
import com.jme3.scene.VertexBuffer;
import com.jme3.post.SceneProcessor;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.light.DirectionalLight;
import java.util.Iterator;
import com.jme3.environment.generation.JobProgressListener;
import com.jme3.environment.LightProbeFactory;
import com.jme3.environment.util.EnvMapUtils;
import com.jme3.environment.generation.JobProgressAdapter;
import com.jme3.light.Light;
import com.jme3.app.state.AppState;
import java.util.ArrayList;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.util.TangentBinormalGenerator;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.util.SkyFactory;
import com.jme3.app.Application;
import com.jme3.light.LightProbe;
import com.jme3.environment.EnvironmentCamera;
import java.util.List;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.input.InputManager;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.input.ChaseCamera;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;

public class GamePlayAppState extends AbstractAppState
{
    private SimpleApplication app;
    private ViewPort viewPort;
    private Camera cam;
    private ChaseCamera chaser;
    private Node rootNode;
    private AssetManager assetManager;
    private AppStateManager stateManager;
    private InputManager inputManager;
    private Spatial sky;
    private Mesh mesh;
    private Vector3f axisX;
    private Vector3f axisY;
    private Vector3f axisZ;
    private Node pivotNode;
    private int row;
    private int col;
    private int dep;
    private int Height;
    private int Width;
    private int CellSize;
    private List<QuadBox> boxObject;
    private List<Vector3f> vertexList;
    private List<boolean[]> booleanList;
    private float boxSize;
    private float boxSpcing;
    private int frame;
    private Node tex;
    private Node modelNode;
    private EnvironmentCamera envCam1;
    private LightProbe probe;
    
    public GamePlayAppState() {
        this.axisX = new Vector3f(1.0f, 0.0f, 0.0f);
        this.axisY = new Vector3f(0.0f, 1.0f, 0.0f);
        this.axisZ = new Vector3f(0.0f, 0.0f, 1.0f);
        this.row = 5;
        this.col = 3;
        this.dep = 5;
        this.Height = 5;
        this.Width = 5;
        this.CellSize = 5;
        this.boxSize = 0.5f;
        this.boxSpcing = this.boxSize * 2.0f;
        this.frame = 0;
        this.probe = null;
    }
    
    public void initialize(final AppStateManager stateManager, final Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication)app;
        this.cam = this.app.getCamera();
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        this.viewPort = app.getViewPort();
        this.stateManager = app.getStateManager();
        this.inputManager = app.getInputManager();
        (this.sky = SkyFactory.createSky(this.assetManager, "Textures/Sky/Path.hdr", SkyFactory.EnvMapType.EquirectMap)).setLocalTranslation(1000.0f, 1000.0f, 1000.0f);
        this.viewPort.setBackgroundColor(ColorRGBA.DarkGray);
        this.pivotNode = new Node();
        this.modelNode = new Node("modelNode");
        final Material centerMaterial = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        centerMaterial.setColor("Color", ColorRGBA.Yellow);
        centerMaterial.getAdditionalRenderState().setWireframe(false);
        final Float size = 0.25f;
        final Box center = new Box((float)size, (float)size, (float)size);
        final Geometry centerGeometry = new Geometry("ccenter", (Mesh)center);
        TangentBinormalGenerator.generate((Spatial)centerGeometry);
        centerGeometry.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        centerGeometry.setCullHint(Spatial.CullHint.Never);
        centerGeometry.setMaterial(centerMaterial);
        centerGeometry.setLocalTranslation(new Vector3f(-size / 2.0f, -size / 2.0f, -60.0f));
        this.vertexList = new ArrayList<Vector3f>();
        this.boxObject = new ArrayList<QuadBox>();
        final int index = 0;
        for (int i = 0; i < this.dep; ++i) {
            for (int j = 0; j < this.row; ++j) {
                for (int l = 0; l < this.col; ++l) {
                    this.vertexList.add(new Vector3f(this.boxSpcing * j, this.boxSpcing * l, this.boxSpcing * i));
                }
            }
        }
        this.initMesh();
        final ColorRGBA[] cl = { ColorRGBA.Red, ColorRGBA.Magenta, ColorRGBA.Blue, ColorRGBA.Cyan, ColorRGBA.Green, ColorRGBA.Yellow, ColorRGBA.Red, ColorRGBA.Magenta, ColorRGBA.Blue, ColorRGBA.Cyan, ColorRGBA.Green, ColorRGBA.Yellow };
        int count = 0;
        int colorCount = 1;
        for (int k = 0; k < this.dep; ++k) {
            for (int m = 0; m < this.row; ++m) {
                for (int l2 = 0; l2 < this.col; ++l2) {
                    this.boxObject.add(new QuadBox(app, this.pivotNode, new Vector3f(this.boxSpcing * m, this.boxSpcing * l2, this.boxSpcing * k), this.boxSize, cl[k], this.booleanList.get(count++), colorCount++));
                    if (colorCount > 4) {
                        colorCount = 1;
                    }
                }
            }
        }
        stateManager.attach((AppState)(this.envCam1 = new EnvironmentCamera(256, new Vector3f(0.0f, 3.0f, 0.0f))));
        this.rootNode.attachChild(this.sky);
        this.rootNode.attachChild((Spatial)this.modelNode);
        this.initLights();
    }
    
    public void update(final float tpf) {
        ++this.frame;
        if (this.frame == 2) {
            this.initLightProbe(this.rootNode, 100, new Vector3f(0.0f, 0.0f, 0.0f), false, false);
        }
        if (this.frame > 10 && this.frame < 12) {
            this.modelNode.attachChild((Spatial)this.pivotNode);
            this.modelNode.removeLight((Light)this.probe);
            this.rootNode.detachChild(this.sky);
        }
    }
    
    private void initLightProbe(final Node rn, final int diamater, final Vector3f v, final boolean s, final boolean a) {
        this.probe = LightProbeFactory.makeProbe((EnvironmentCamera)this.stateManager.getState((Class)EnvironmentCamera.class), (Spatial)this.rootNode, (JobProgressListener)new JobProgressAdapter<LightProbe>() {
            public void done(final LightProbe result) {
                GamePlayAppState.this.tex = EnvMapUtils.getCubeMapCrossDebugViewWithMipMaps(result.getPrefilteredEnvMap(), GamePlayAppState.this.assetManager);
            }
        });
        this.probe.getArea().setRadius((float)diamater);
        this.probe.setPosition(v);
        rn.addLight((Light)this.probe);
    }
    
    public void updateRoughness(final float roughness) {
        for (final QuadBox qb : this.boxObject) {
            QuadBox.setRoughness(roughness);
        }
    }
    
    public void updateWire() {
        for (final QuadBox qb : this.boxObject) {
            if (qb.isWire()) {
                qb.setWire(false);
            }
            else {
                qb.setWire(true);
            }
        }
    }
    
    public void draw() {
        for (final QuadBox qb : this.boxObject) {
            if (qb.getFaceArray()[0]) {
                qb.getBox().attachChild((Spatial)qb.getLeftGeometry());
            }
            else {
                qb.getBox().detachChild((Spatial)qb.getLeftGeometry());
            }
            if (qb.getFaceArray()[1]) {
                qb.getBox().attachChild((Spatial)qb.getRightGeometry());
            }
            else {
                qb.getBox().detachChild((Spatial)qb.getRightGeometry());
            }
            if (qb.getFaceArray()[2]) {
                qb.getBox().attachChild((Spatial)qb.getBotomGeometry());
            }
            else {
                qb.getBox().detachChild((Spatial)qb.getBotomGeometry());
            }
            if (qb.getFaceArray()[3]) {
                qb.getBox().attachChild((Spatial)qb.getTopGeometry());
            }
            else {
                qb.getBox().detachChild((Spatial)qb.getTopGeometry());
            }
            if (qb.getFaceArray()[4]) {
                qb.getBox().attachChild((Spatial)qb.getBackGeometry());
            }
            else {
                qb.getBox().detachChild((Spatial)qb.getBackGeometry());
            }
            if (qb.getFaceArray()[5]) {
                qb.getBox().attachChild((Spatial)qb.getFrontGeometry());
            }
            else {
                qb.getBox().detachChild((Spatial)qb.getFrontGeometry());
            }
        }
    }
    
    public void initMesh() {
        this.booleanList = new ArrayList<boolean[]>();
        for (int i = 0; i < this.vertexList.size(); ++i) {
            final Vector3f lti = this.vertexList.get(i);
            final Vector3f z2 = new Vector3f(lti.x, lti.y, lti.z + this.boxSpcing);
            final boolean[] faceArrayTemp = { true, true, true, true, true, true };
            for (int j = 0; j < this.vertexList.size(); ++j) {
                final Vector3f ltj = this.vertexList.get(j);
                if (ltj.equals((Object)new Vector3f(lti.x - this.boxSpcing, lti.y, lti.z))) {
                    faceArrayTemp[0] = false;
                }
                if (ltj.equals((Object)new Vector3f(lti.x + this.boxSpcing, lti.y, lti.z))) {
                    faceArrayTemp[1] = false;
                }
                if (ltj.equals((Object)new Vector3f(lti.x, lti.y - this.boxSpcing, lti.z))) {
                    faceArrayTemp[2] = false;
                }
                if (ltj.equals((Object)new Vector3f(lti.x, lti.y + this.boxSpcing, lti.z))) {
                    faceArrayTemp[3] = false;
                }
                if (ltj.equals((Object)new Vector3f(lti.x, lti.y, lti.z - this.boxSpcing))) {
                    faceArrayTemp[4] = false;
                }
                if (ltj.equals((Object)new Vector3f(lti.x, lti.y, lti.z + this.boxSpcing))) {
                    faceArrayTemp[5] = false;
                }
            }
            this.booleanList.add(faceArrayTemp);
        }
    }
    
    public void meshUpdate() {
        for (int i = 0; i < this.boxObject.size(); ++i) {
            final Vector3f lti = this.boxObject.get(i).getBox().getLocalTranslation();
            final boolean[] faceArrayTemp = { true, true, true, true, true, true };
            for (int j = 0; j < this.boxObject.size(); ++j) {
                final Vector3f ltj = this.boxObject.get(j).getBox().getLocalTranslation();
                if (ltj.equals((Object)new Vector3f(lti.x - this.boxSpcing, lti.y, lti.z))) {
                    faceArrayTemp[0] = false;
                }
                if (ltj.equals((Object)new Vector3f(lti.x + this.boxSpcing, lti.y, lti.z))) {
                    faceArrayTemp[1] = false;
                }
                if (ltj.equals((Object)new Vector3f(lti.x, lti.y - this.boxSpcing, lti.z))) {
                    faceArrayTemp[2] = false;
                }
                if (ltj.equals((Object)new Vector3f(lti.x, lti.y + this.boxSpcing, lti.z))) {
                    faceArrayTemp[3] = false;
                }
                if (ltj.equals((Object)new Vector3f(lti.x, lti.y, lti.z - this.boxSpcing))) {
                    faceArrayTemp[4] = false;
                }
                if (ltj.equals((Object)new Vector3f(lti.x, lti.y, lti.z + this.boxSpcing))) {
                    faceArrayTemp[5] = false;
                }
            }
            this.boxObject.get(i).setFaceArray(faceArrayTemp);
        }
        this.draw();
    }
    
    public void initLights() {
        final DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5f, -0.5f, -0.5f).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        this.rootNode.addLight((Light)sun);
        final int SHADOWMAP_SIZE = 1024;
        final DirectionalLightShadowRenderer sunShad = new DirectionalLightShadowRenderer(this.assetManager, 1024, 3);
        sunShad.setLight(sun);
        this.viewPort.addProcessor((SceneProcessor)sunShad);
    }
    
    public void addBox(final Vector3f norm, final int targetColorCount) {
        this.boxObject.add(new QuadBox((Application)this.app, this.pivotNode, norm, this.boxSize, ColorRGBA.Gray, this.booleanList.get(this.booleanList.size() - 1), targetColorCount));
    }
    
    public void showNormals(final Geometry geometry, final ColorRGBA color) {
        final VertexBuffer position = geometry.getMesh().getBuffer(VertexBuffer.Type.Position);
        final Vector3f[] positionVertexes = BufferUtils.getVector3Array((FloatBuffer)position.getData());
        final VertexBuffer normal = geometry.getMesh().getBuffer(VertexBuffer.Type.Normal);
        final Vector3f[] normalsVectors = BufferUtils.getVector3Array((FloatBuffer)normal.getData());
        for (int arrow = 0; arrow < normalsVectors.length; ++arrow) {
            this.createArrow(positionVertexes[arrow], normalsVectors[arrow], color, arrow);
        }
    }
    
    public void createArrow(final Vector3f location, final Vector3f direction, final ColorRGBA color, final int Count) {
        final Arrow arrow = new Arrow(direction);
        arrow.setLineWidth(4.0f);
        final Geometry g = new Geometry("arrow", (Mesh)arrow);
        final Material mat = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        g.setMaterial(mat);
        g.setLocalTranslation(location);
        this.rootNode.attachChild((Spatial)g);
    }
    
    public Node getPivotNode() {
        return this.pivotNode;
    }
    
    public void setPivotNode(final Node pivotNode) {
        this.pivotNode = pivotNode;
    }
    
    public Node getTex() {
        return this.tex;
    }
    
    public void setTex(final Node tex) {
        this.tex = tex;
    }
    
    public void listRemove(final int i) {
        this.boxObject.remove(i);
    }
    
    public List<QuadBox> getBoxObject() {
        return this.boxObject;
    }
    
    public float getBoxSize() {
        return this.boxSize;
    }
    
    public float getBoxSpcing() {
        return this.boxSpcing;
    }
}
