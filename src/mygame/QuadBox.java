
package mygame;


import com.jme3.math.Quaternion;
import com.jme3.scene.Spatial;
import com.jme3.util.TangentBinormalGenerator;
import com.jme3.scene.Mesh;
import com.jme3.app.Application;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.asset.AssetManager;
import com.jme3.app.SimpleApplication;

public class QuadBox
{
    private static int index;
    private GamePlayAppState gpas;
    private SimpleApplication app;
    private final AssetManager assetManager;
    private Node node;
    private Vector3f position;
    private ColorRGBA color;
    private static Material pbrMat;
    private Material boxMaterial;
    private Node box;
    private Quad front;
    private Quad back;
    private Quad top;
    private Quad botom;
    private Quad left;
    private Quad right;
    private Geometry frontGeometry;
    private Geometry backGeometry;
    private Geometry topGeometry;
    private Geometry botomGeometry;
    private Geometry leftGeometry;
    private Geometry rightGeometry;
    private boolean[] faceArray;
    private static float roughness;
    private float width;
    private float height;
    private float breath;
    private boolean wire;
    private int colorCount;
    
    static Object getPbrMat() {
        return QuadBox.pbrMat;
    }
    
    QuadBox(final Application app, final Node node, final Vector3f position, final float size, final ColorRGBA color, final boolean[] faceArray, final int colorCount) {
        this.wire = false;
        this.app = (SimpleApplication)app;
        this.assetManager = this.app.getAssetManager();
        this.color = color;
        this.position = position;
        this.node = node;
        this.width = size * 2.0f;
        this.height = size * 2.0f;
        this.breath = size * 2.0f;
        this.faceArray = faceArray;
        this.colorCount = colorCount;
        final Material mat1 = new Material(this.assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat1.setTexture("DiffuseMap", this.assetManager.loadTexture("Textures/Terrain/Pond/Pond.jpg"));
        mat1.setTexture("NormalMap", this.assetManager.loadTexture("Textures/Terrain/Pond/Pond_normal.png"));
        mat1.setBoolean("UseMaterialColors", true);
        mat1.setColor("Ambient", ColorRGBA.Blue);
        mat1.setColor("Specular", ColorRGBA.White);
        mat1.setColor("Diffuse", ColorRGBA.Blue);
        mat1.setFloat("Shininess", 5.0f);
        mat1.getAdditionalRenderState().setWireframe(this.wire);
        (this.boxMaterial = new Material(this.assetManager, "Common/MatDefs/Light/Lighting.j3md")).setBoolean("UseMaterialColors", true);
        this.boxMaterial.setColor("Ambient", ColorRGBA.Gray);
        this.boxMaterial.setColor("Specular", ColorRGBA.White);
        this.boxMaterial.setColor("Diffuse", color);
        this.boxMaterial.setFloat("Shininess", 5.0f);
        this.boxMaterial.getAdditionalRenderState().setWireframe(this.wire);
        switch (colorCount) {
            case 1: {
                QuadBox.pbrMat = this.assetManager.loadMaterial("Materials/Metal1.j3m");
                break;
            }
            case 2: {
                QuadBox.pbrMat = this.assetManager.loadMaterial("Materials/Metal2.j3m");
                break;
            }
            case 3: {
                QuadBox.pbrMat = this.assetManager.loadMaterial("Materials/Metal3.j3m");
                break;
            }
            case 4: {
                QuadBox.pbrMat = this.assetManager.loadMaterial("Materials/Metal4.j3m");
                break;
            }
            default: {
                System.out.println("QuadBox colorCount switch is broken!");
                break;
            }
        }
        QuadBox.pbrMat.getAdditionalRenderState().setWireframe(this.wire);
        QuadBox.pbrMat.setFloat("Roughness", QuadBox.roughness);
        (this.box = new Node()).setLocalTranslation(position);
        this.box.setUserData("index", (Object)QuadBox.index);
        this.box.setUserData("Ocolor", (Object)color);
        this.box.setUserData("name", (Object)("box" + QuadBox.index));
        this.left = new Quad(this.width, this.height);
        TangentBinormalGenerator.generate((Spatial)(this.leftGeometry = new Geometry("hoverQuad", (Mesh)this.left)));
        this.leftGeometry.setCullHint(Spatial.CullHint.Never);
        this.leftGeometry.setMaterial(QuadBox.pbrMat);
        this.leftGeometry.setUserData("name", (Object)("box" + QuadBox.index));
        this.leftGeometry.setUserData("colorCount", (Object)colorCount);
        final Quaternion r4 = new Quaternion();
        r4.fromAngleAxis(-1.5707964f, Vector3f.UNIT_Y);
        this.leftGeometry.setLocalRotation(r4);
        this.leftGeometry.setLocalTranslation(0.0f, 0.0f, -this.breath / 2.0f);
        this.right = new Quad(this.width, this.height);
        TangentBinormalGenerator.generate((Spatial)(this.rightGeometry = new Geometry("hoverQuad", (Mesh)this.right)));
        this.rightGeometry.setCullHint(Spatial.CullHint.Never);
        this.rightGeometry.setMaterial(QuadBox.pbrMat);
        this.rightGeometry.setUserData("name", (Object)("box" + QuadBox.index));
        this.rightGeometry.setUserData("colorCount", (Object)colorCount);
        final Quaternion r5 = new Quaternion();
        r5.fromAngleAxis(1.5707964f, Vector3f.UNIT_Y);
        this.rightGeometry.setLocalRotation(r5);
        this.rightGeometry.setLocalTranslation(this.breath, 0.0f, this.breath / 2.0f);
        this.botom = new Quad(this.width, this.height);
        TangentBinormalGenerator.generate((Spatial)(this.botomGeometry = new Geometry("hoverQuad", (Mesh)this.botom)));
        this.botomGeometry.setCullHint(Spatial.CullHint.Never);
        this.botomGeometry.setMaterial(QuadBox.pbrMat);
        this.botomGeometry.setUserData("name", (Object)("box" + QuadBox.index));
        this.botomGeometry.setUserData("colorCount", (Object)colorCount);
        final Quaternion r6 = new Quaternion();
        r6.fromAngleAxis(1.5707964f, Vector3f.UNIT_X);
        this.botomGeometry.setLocalRotation(r6);
        this.botomGeometry.setLocalTranslation(0.0f, 0.0f, -this.breath / 2.0f);
        this.top = new Quad(this.width, this.height);
        TangentBinormalGenerator.generate((Spatial)(this.topGeometry = new Geometry("hoverQuad", (Mesh)this.top)));
        this.topGeometry.setCullHint(Spatial.CullHint.Never);
        this.topGeometry.setMaterial(QuadBox.pbrMat);
        this.topGeometry.setUserData("name", (Object)("box" + QuadBox.index));
        this.topGeometry.setUserData("colorCount", (Object)colorCount);
        final Quaternion r7 = new Quaternion();
        r7.fromAngleAxis(-1.5707964f, Vector3f.UNIT_X);
        this.topGeometry.setLocalRotation(r7);
        this.topGeometry.setLocalTranslation(0.0f, this.breath, this.breath / 2.0f);
        this.back = new Quad(this.width, this.height);
        TangentBinormalGenerator.generate((Spatial)(this.backGeometry = new Geometry("hoverQuad", (Mesh)this.back)));
        this.backGeometry.setCullHint(Spatial.CullHint.Never);
        this.backGeometry.setMaterial(QuadBox.pbrMat);
        this.backGeometry.setUserData("name", (Object)("box" + QuadBox.index));
        this.backGeometry.setUserData("colorCount", (Object)colorCount);
        final Quaternion r8 = new Quaternion();
        r8.fromAngleAxis(3.1415927f, Vector3f.UNIT_X);
        this.backGeometry.setLocalRotation(r8);
        this.backGeometry.setLocalTranslation(0.0f, this.breath, -this.breath / 2.0f);
        this.front = new Quad(this.width, this.height);
        TangentBinormalGenerator.generate((Spatial)(this.frontGeometry = new Geometry("hoverQuad", (Mesh)this.front)));
        this.frontGeometry.setCullHint(Spatial.CullHint.Never);
        this.frontGeometry.setMaterial(QuadBox.pbrMat);
        this.frontGeometry.setUserData("name", (Object)("box" + QuadBox.index));
        this.frontGeometry.setUserData("colorCount", (Object)colorCount);
        final Quaternion r9 = new Quaternion();
        r9.fromAngleAxis(0.0f, Vector3f.UNIT_X);
        this.frontGeometry.setLocalRotation(r9);
        this.frontGeometry.setLocalTranslation(0.0f, 0.0f, this.breath / 2.0f);
        this.box.attachChild((Spatial)this.leftGeometry);
        this.box.attachChild((Spatial)this.rightGeometry);
        this.box.attachChild((Spatial)this.botomGeometry);
        this.box.attachChild((Spatial)this.topGeometry);
        this.box.attachChild((Spatial)this.backGeometry);
        this.box.attachChild((Spatial)this.frontGeometry);
        node.attachChild((Spatial)this.box);
        ++QuadBox.index;
    }
    
    public int getColorCount() {
        return this.colorCount;
    }
    
    public boolean[] getFaceArray() {
        return this.faceArray;
    }
    
    public void setFaceArray(final boolean[] faceArray) {
        this.faceArray = faceArray;
    }
    
    public float getWidth() {
        return this.width;
    }
    
    public void setWidth(final float width) {
        this.width = width;
    }
    
    public float getHeight() {
        return this.height;
    }
    
    public void setHeight(final float height) {
        this.height = height;
    }
    
    public float getBreath() {
        return this.breath;
    }
    
    public void setBreath(final float breath) {
        this.breath = breath;
    }
    
    public ColorRGBA getColor() {
        return this.color;
    }
    
    public void setColor(final ColorRGBA color) {
        this.color = color;
    }
    
    public Material getBoxMaterial() {
        return this.boxMaterial;
    }
    
    public static float getRoughness() {
        return QuadBox.roughness;
    }
    
    public static void setRoughness(final float roughness) {
        QuadBox.roughness = roughness;
        QuadBox.pbrMat.setFloat("Roughness", roughness);
    }
    
    public boolean isWire() {
        return this.wire;
    }
    
    public void setWire(final boolean wire) {
        this.wire = wire;
    }
    
    public Vector3f getPosition() {
        return this.position;
    }
    
    public void setPosition(final Vector3f position) {
        this.position = position;
    }
    
    public Node getBox() {
        return this.box;
    }
    
    public void setBox(final Node box) {
        this.box = box;
    }
    
    public Quad getBack() {
        return this.back;
    }
    
    public void setBack(final Quad back) {
        this.back = back;
    }
    
    public Quad getTop() {
        return this.top;
    }
    
    public void setTop(final Quad top) {
        this.top = top;
    }
    
    public Quad getBotom() {
        return this.botom;
    }
    
    public void setBotom(final Quad botom) {
        this.botom = botom;
    }
    
    public Quad getLeft() {
        return this.left;
    }
    
    public void setLeft(final Quad left) {
        this.left = left;
    }
    
    public Quad getRight() {
        return this.right;
    }
    
    public void setRight(final Quad right) {
        this.right = right;
    }
    
    public Geometry getGeometry(final int i) {
        Geometry ret = null;
        switch (i) {
            case 1: {
                ret = this.frontGeometry;
                break;
            }
            case 2: {
                ret = this.backGeometry;
                break;
            }
            case 3: {
                ret = this.topGeometry;
                break;
            }
            case 4: {
                ret = this.botomGeometry;
                break;
            }
            case 5: {
                ret = this.leftGeometry;
                break;
            }
            case 6: {
                ret = this.rightGeometry;
                break;
            }
        }
        return ret;
    }
    
    public Geometry getFrontGeometry() {
        return this.frontGeometry;
    }
    
    public void setFrontGeometry(final Geometry frontGeometry) {
        this.frontGeometry = frontGeometry;
    }
    
    public Geometry getBackGeometry() {
        return this.backGeometry;
    }
    
    public void setBackGeometry(final Geometry backGeometry) {
        this.backGeometry = backGeometry;
    }
    
    public Geometry getTopGeometry() {
        return this.topGeometry;
    }
    
    public void setTopGeometry(final Geometry topGeometry) {
        this.topGeometry = topGeometry;
    }
    
    public Geometry getBotomGeometry() {
        return this.botomGeometry;
    }
    
    public void setBotomGeometry(final Geometry botomGeometry) {
        this.botomGeometry = botomGeometry;
    }
    
    public Geometry getLeftGeometry() {
        return this.leftGeometry;
    }
    
    public void setLeftGeometry(final Geometry leftGeometry) {
        this.leftGeometry = leftGeometry;
    }
    
    public Geometry getRightGeometry() {
        return this.rightGeometry;
    }
    
    public void setRightGeometry(final Geometry rightGeometry) {
        this.rightGeometry = rightGeometry;
    }
    
    static {
        QuadBox.roughness = 0.0f;
    }
}
