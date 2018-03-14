import java.awt.*;
import java.awt.geom.*;
import java.awt.geom.Point2D;
import java.lang.reflect.Constructor;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ShapeModel {
    Shape shape;
    ShapeType myShapeType;

    // Point when create, never change
    // For transformation
    Point startPoint;
    Point endPoint;

    // absolute position of the shape
     int absX ;
     int absY ;
    Point absStartPoint;
    Point absEndPoint;

    // position before dragged
     int pX ;
     int pY ;
    Point pStartPoint;
    Point pEndPoint;

    // Point2D mousePosition;
    boolean isSelected = true;

    int handleSize = 5;

    double absScaleX = 1.0;
    double absScaleY = 1.0;
    double pScale = 1.0;
    // translation
    // rotation

    Rectangle boundingBox;// = shape.getBounds2D();
    int boundingHeight, boundingWidth, boundingX, boundingY;

    boolean scaleSelected = false;
    boolean translateSelected = false;
    boolean rotateSelected = false;

    public ShapeModel(Point startPoint, Point endPoint) {}

    public void setBoundingBox(){
        boundingBox = shape.getBounds();
        boundingHeight = boundingBox.height;
        boundingWidth = boundingBox.width;
        boundingX = boundingBox.x;
        boundingY = boundingBox.y;
    }

    /** Convert from the component's X coordinate to the model's X coordinate. */
    // protected double fromX(int x) {
    //     return (x - this.getInsets().left) / this.scale;
    // }

    /** Convert from the component's Y coordinate to the model's Y coordinate. */
    // protected double fromY(int y) {
    //     return (this.getHeight() - 1 - this.getInsets().bottom - y)
    //             / this.scale;
    // }

    public boolean onBottomCorner(int x, int y) {
        return Math.abs(x - (boundingX + boundingWidth)) < handleSize
                 && Math.abs(y - (boundingY + boundingHeight)) < handleSize;
        // return Math.abs(fromX(x) - this.model.getBase()) < handleSize
        //         && Math.abs(fromY(y) - this.model.getHeight()) < handleSize;
    }

    public Shape getShape() {
        // For printing, do all transformation here

        // // affinetransformation to translate
        // t.translate(absX, absY);
        // // create translated shape 

        // AffineTransform t = new AffineTransform();
        // t.scale(absScaleX, absScaleY);
        // return t.createTransformedShape(shape);

        return shape;
    }

    // You will need to change the hittest to account for transformations.
    public boolean hitTest(Point2D p) {
        return this.getShape().contains(p);
    }

    public Shape updateShape(Point startPoint, Point endPoint) {
        return shape;
    }


    /**
     * Given a ShapeType and the start and end point of the shape, ShapeFactory constructs a new ShapeModel
     * using the class reference in the ShapeType enum and returns it.
     */
    public static class ShapeFactory {
        public ShapeModel getShape(ShapeType shapeType, Point startPoint, Point endPoint) {
           

            try {
                Class<? extends ShapeModel> clazz = shapeType.shape;
                Constructor<? extends ShapeModel> constructor = clazz.getConstructor(Point.class, Point.class);
                
                return constructor.newInstance(startPoint, endPoint);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        
    }

    public enum ShapeType {
        Ellipse(EllipseModel.class),
        Rectangle(RectangleModel.class),
        Line(LineModel.class);

        public final Class<? extends ShapeModel> shape;
        ShapeType(Class<? extends ShapeModel> shape) {
            this.shape = shape;
        }
    }
}
