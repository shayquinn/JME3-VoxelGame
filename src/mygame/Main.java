package mygame;


import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.InputListener;
import java.util.Iterator;
import com.jme3.math.ColorRGBA;
import com.jme3.collision.Collidable;
import com.jme3.math.Ray;
import com.jme3.app.state.AppState;
import java.awt.Dimension;
import com.jme3.system.AppSettings;
import java.awt.Toolkit;
import java.util.List;
import com.jme3.math.FastMath;
import com.jme3.scene.Spatial;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Geometry;
import com.jme3.collision.CollisionResults;
import com.jme3.input.controls.Trigger;
import com.jme3.app.SimpleApplication;

public class Main extends SimpleApplication
{
    private static final Trigger LEFT_CLICK;
    private static final String MAPPING_LEFT_CLICK = "LeftClick";
    private static final Trigger RIGHT_CLICK;
    private static final String MAPPING_RIGHT_CLICK = "RightClick";
    private static final Trigger MOUSE_MOVE_X;
    private static final String MAPPING_MOUSE_MOVE_X = "RightClick";
    private static final Trigger MOUSE_MOVE_Y;
    private static final String MAPPING_MOUSE_MOVE_Y = "RightClick";
    GamePlayAppState state;
    CollisionResults hoverResults;
    Geometry hoverTarget;
    Node hoverNode;
    Vector3f targetNorm;
    Vector3f targetNodePos;
    Vector3f targetPos;
    int targetColorCount;
    Vector2f mouseXY;
    Vector2f snapMouse;
    private final ActionListener actionListener;
    private final AnalogListener analogListener;
    
    public Main() {
        this.actionListener = (ActionListener)new ActionListener() {
            public void onAction(final String name, final boolean keyPressed, final float tpf) {
                if (name.equals("RightClick") && !keyPressed) {
                    if (Main.this.hoverResults.size() > 0) {
                        final Vector3f newPos = new Vector3f(Main.this.targetNodePos.getX() + Main.this.state.getBoxSpcing() * Main.this.targetNorm.getX(), Main.this.targetNodePos.getY() + Main.this.state.getBoxSpcing() * Main.this.targetNorm.getY(), Main.this.targetNodePos.getZ() + Main.this.state.getBoxSpcing() * Main.this.targetNorm.getZ());
                        Main.this.state.addBox(newPos, Main.this.targetColorCount);
                        System.out.println("MAPPING_RIGHT_CLICK!");
                    }
                    else {
                        System.out.println("Selection: Nothing");
                    }
                }
                if (name.equals("LeftClick") && !keyPressed) {
                    boolean trig = true;
                    if (Main.this.hoverResults.size() > 0) {
                        final List<QuadBox> obl = Main.this.state.getBoxObject();
                        for (int i = 0; i < obl.size(); ++i) {
                            if (obl.get(i).getBox().getUserData("name") == Main.this.hoverTarget.getParent().getUserData("name")) {
                                Main.this.state.listRemove(i);
                                Main.this.state.getPivotNode().detachChild((Spatial)Main.this.hoverTarget.getParent());
                                trig = false;
                            }
                        }
                        System.out.println("MAPPING_LEFT_CLICK!");
                    }
                    else {
                        System.out.println("Selection: Nothing");
                    }
                }
                if (name.equals("debug") && !keyPressed) {
                    if (Main.this.state.getTex() == null) {
                        return;
                    }
                    if (Main.this.state.getTex().getParent() == null) {
                        Main.this.guiNode.attachChild((Spatial)Main.this.state.getTex());
                    }
                    else {
                        Main.this.state.getTex().removeFromParent();
                    }
                }
                if (name.equals("wire") && !keyPressed) {
                    System.out.println("main wire");
                    Main.this.state.updateWire();
                }
                if (name.equals("rup") && keyPressed) {
                    Main.this.state.updateRoughness(FastMath.clamp(QuadBox.getRoughness() + 0.1f, 0.0f, 1.0f));
                    QuadBox.setRoughness(FastMath.clamp(QuadBox.getRoughness() + 0.1f, 0.0f, 1.0f));
                }
                if (name.equals("rdown") && keyPressed) {
                    Main.this.state.updateRoughness(FastMath.clamp(QuadBox.getRoughness() - 0.1f, 0.0f, 1.0f));
                    QuadBox.setRoughness(FastMath.clamp(QuadBox.getRoughness() - 0.1f, 0.0f, 1.0f));
                }
            }
        };
        this.analogListener = (AnalogListener)new AnalogListener() {
            public void onAnalog(final String name, final float value, final float tpf) {
                if (name.equals("forward")) {
                    final Vector3f v = Main.this.rootNode.getLocalTranslation();
                    Main.this.rootNode.setLocalTranslation(v.x, v.y, v.z + 0.05f);
                }
                if (name.equals("backward")) {
                    final Vector3f v = Main.this.rootNode.getLocalTranslation();
                    Main.this.rootNode.setLocalTranslation(v.x, v.y, v.z - 0.05f);
                }
                if (name.equals("up")) {
                    final Vector3f v = Main.this.rootNode.getLocalTranslation();
                    Main.this.rootNode.setLocalTranslation(v.x, v.y + 0.05f, v.z);
                }
                if (name.equals("down")) {
                    final Vector3f v = Main.this.rootNode.getLocalTranslation();
                    Main.this.rootNode.setLocalTranslation(v.x, v.y - 0.05f, v.z);
                }
                if (name.equals("Right")) {
                    final Vector3f v = Main.this.rootNode.getLocalTranslation();
                    Main.this.rootNode.setLocalTranslation(v.x + 0.05f, v.y, v.z);
                }
                if (name.equals("Left")) {
                    final Vector3f v = Main.this.rootNode.getLocalTranslation();
                    Main.this.rootNode.setLocalTranslation(v.x - 0.05f, v.y, v.z);
                }
            }
        };
    }
    
    public static void main(final String[] args) {
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final double width = screenSize.getWidth();
        final double height = screenSize.getHeight();
        final Main app = new Main();
        app.showSettings = false;
        final AppSettings settings = new AppSettings(true);
        settings.setTitle("Voxel Game Demo");
        settings.put("Width", width);
        settings.put("Height", height);
        settings.setVSync(true);
        settings.setResolution(1280, 600);
        settings.setFullscreen(false);
        settings.setSamples(2);
        app.setSettings(settings);
        app.start();
    }
    
    public void simpleInitApp() {
        this.flyCam.setMoveSpeed(10.0f);
        this.flyCam.setRotationSpeed(3.0f);
        this.flyCam.setDragToRotate(true);
        this.setDisplayFps(false);
        this.setDisplayStatView(false);
        this.flyCam.setDragToRotate(true);
        this.inputManager.setCursorVisible(true);
        this.state = new GamePlayAppState();
        this.stateManager.attach((AppState)this.state);
        this.initKeys();
    }
    
    public void simpleUpdate(final float tpf) {
        this.hoverResults = new CollisionResults();
        final Vector2f click2d = this.inputManager.getCursorPosition();
        final Vector3f click3d = this.cam.getWorldCoordinates(new Vector2f(click2d.getX(), click2d.getY()), 0.0f);
        final Vector3f dir = this.cam.getWorldCoordinates(new Vector2f(click2d.getX(), click2d.getY()), 1.0f).subtractLocal(click3d);
        final Ray ray = new Ray(click3d, dir);
        this.rootNode.collideWith((Collidable)ray, this.hoverResults);
        if (this.hoverResults.size() > 0) {
            this.hoverTarget = this.hoverResults.getClosestCollision().getGeometry();
            this.targetNodePos = this.hoverTarget.getParent().getLocalTranslation();
            this.targetPos = this.hoverResults.getClosestCollision().getGeometry().getLocalTranslation();
            this.targetNorm = this.hoverResults.getCollision(0).getContactNormal();
            this.targetColorCount = (int)this.hoverTarget.getUserData("colorCount");
            this.rootNode.detachChildNamed("arrow");
            for (final QuadBox bo : this.state.getBoxObject()) {
                if (bo.getBox().getUserData("name") == this.hoverTarget.getParent().getUserData("name")) {
                    final Vector3f v = bo.getBox().getLocalTranslation().add(bo.getWidth() / 2.0f, bo.getHeight() / 2.0f, 0.0f);
                    this.state.createArrow(v, this.targetNorm, ColorRGBA.Blue, 1);
                }
                else {
                    bo.getBoxMaterial().setColor("Diffuse", (ColorRGBA)bo.getBox().getUserData("Ocolor"));
                }
            }
        }
        else {
            this.rootNode.detachChildNamed("arrow");
            for (final QuadBox bo : this.state.getBoxObject()) {
                bo.getBoxMaterial().setColor("Diffuse", (ColorRGBA)bo.getBox().getUserData("Ocolor"));
            }
        }
        this.state.meshUpdate();
    }
    
    private void initKeys() {
        this.inputManager.addMapping("LeftClick", new Trigger[] { Main.LEFT_CLICK });
        this.inputManager.addListener((InputListener)this.actionListener, new String[] { "LeftClick" });
        this.inputManager.addMapping("RightClick", new Trigger[] { Main.RIGHT_CLICK });
        this.inputManager.addListener((InputListener)this.actionListener, new String[] { "RightClick" });
        this.inputManager.addMapping("rup", new Trigger[] { (Trigger)new KeyTrigger(20) });
        this.inputManager.addMapping("rdown", new Trigger[] { (Trigger)new KeyTrigger(34) });
        this.inputManager.addMapping("debug", new Trigger[] { (Trigger)new KeyTrigger(48) });
        this.inputManager.addMapping("wire", new Trigger[] { (Trigger)new KeyTrigger(33) });
        this.inputManager.addListener((InputListener)this.actionListener, new String[] { "rup", "rdown", "debug", "wire" });
        this.inputManager.addMapping("forward", new Trigger[] { (Trigger)new KeyTrigger(23) });
        this.inputManager.addMapping("backward", new Trigger[] { (Trigger)new KeyTrigger(37) });
        this.inputManager.addMapping("up", new Trigger[] { (Trigger)new KeyTrigger(22) });
        this.inputManager.addMapping("down", new Trigger[] { (Trigger)new KeyTrigger(49) });
        this.inputManager.addMapping("Left", new Trigger[] { (Trigger)new KeyTrigger(36) });
        this.inputManager.addMapping("Right", new Trigger[] { (Trigger)new KeyTrigger(38) });
        this.inputManager.addListener((InputListener)this.analogListener, new String[] { "forward", "backward", "up", "down", "Left", "Right" });
    }
    
    static {
        LEFT_CLICK = (Trigger)new MouseButtonTrigger(0);
        RIGHT_CLICK = (Trigger)new MouseButtonTrigger(1);
        MOUSE_MOVE_X = (Trigger)new MouseButtonTrigger(0);
        MOUSE_MOVE_Y = (Trigger)new MouseButtonTrigger(1);
    }
}
