import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;
import java.util.*;
// import java.util.Observable;
// import java.util.Observer;

public class CanvasView extends JPanel implements Observer {
    DrawingModel model;

    Point2D startMouse;
    Point2D lastMouse;
    Point2D clickPosition;
    
    int lastX, lastY;

    boolean scaling = false;
    boolean moving = false;
    boolean rotating = false;
    boolean drawing = true;

    boolean needStore = false;

   

    public CanvasView(DrawingModel model) {
        super();
        this.model = model;

        MouseAdapter mouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Click - Select or unselect a shape
                super.mousePressed(e);
                // Get clicke position
                clickPosition = e.getPoint();

                // After one shape is selected, unselect all the other shapes 
                boolean setRestUnselected = false;
                // Go through the shape list and do hit test
                for(ShapeModel shape : model.getShapes()) {
                    if (setRestUnselected){
                        shape.isSelected = false;
                    } else if (shape.hitTest(clickPosition)){
                        shape.isSelected = true;
                        setRestUnselected = true;
                    } else {
                        shape.isSelected = false;
                    }
                }
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // Press+drag: outside any shape - drawing
                //             right bottom corner - scaling
                //             inside a shape - translation
                //             top round handle - rotation
                super.mousePressed(e);
                
                startMouse = e.getPoint();
                lastMouse = e.getPoint();

                lastX = e.getX();
                lastY = e.getY();

                for(ShapeModel shape : model.getShapes()) {
                    if (shape.onBottomCorner(lastX, lastY ) && shape.isSelected){
                        // Scale handle selected
                        shape.scaleSelected = true;
                    } 
                    else if (shape.hitTest(startMouse) && shape.isSelected) {
                        // To translate - shape must already selected and press inside the shape
                        shape.translateSelected = true;
                    } 
                    else if (shape.onRotateHandle(lastX, lastY) && shape.isSelected){
                        shape.rotateSelected = true;
                        System.out.println("On rotate handler!");
                    }
                    else {
                        shape.isSelected = false;
                    }
                    System.out.println("scale: "+ shape.scaleSelected + " translate: "+ shape.translateSelected + " selected: "+ shape.isSelected);
                }
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                // update transformation info
                super.mouseDragged(e);

                lastMouse = e.getPoint();


                for(ShapeModel shape : model.getShapes()) {
                    if (shape.scaleSelected) {
                        scaling = true;
                        drawing = false;
                        //shape.updateShape((Point) lastMouse);
                        model.updateShape(shape, shape.startPoint ,(Point) lastMouse);
                        //model.scale(shape, (Point)lastMouse);
                        break;
                    } 
                    else if (shape.translateSelected){
                        moving = true;
                        drawing = false;

                        // int xChange = e.getX() - lastX;
                        // int yChange = e.getY() - lastY;

                        // int newStartX= shape.startPoint.x + xChange;
                        // int newStartY= shape.startPoint.y + yChange;
                        // Point newStartPoint = new Point(newStartX, newStartY);

                        // int newEndX= shape.endPoint.x + xChange;
                        // int newEndY= shape.endPoint.y + yChange;
                        // Point newEndPoint = new Point(newEndX, newEndY);


                        // model.updateShape(shape, newStartPoint, newEndPoint);
               

                        model.translate(shape, e.getX() - lastX, e.getY() - lastY);

                        lastX = e.getX();
                        lastY = e.getY();

                        break;
                    }
                    else if (shape.rotateSelected){
                        rotating = true;
                        drawing = false;
                        model.rotate(shape, (Point)lastMouse);
                        break;
                    } 
                }

                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // store transformation info for printing, undoing/redoing
                super.mouseReleased(e);
                // if (scaling) {

                // } else if (moving){
                //     //model.endTranslate();
                //     //needStore = false;
                // } else if (rotating) {

                // } else 
                if (drawing){
                    // // When draw a new shape, unselect all the other shapes
                    // for(ShapeModel shape : model.getShapes()) {
                    //     shape.isSelected = false;
                    // }

                    ShapeModel shape = new ShapeModel.ShapeFactory().getShape(model.getShape(), (Point) startMouse, (Point) lastMouse);
                    model.addShape(shape);
                    model.addShapeField(shape, (Point) startMouse, (Point) lastMouse);
                }

                for(ShapeModel shape : model.getShapes()) {
                    if (shape.scaleSelected){

                    } else if (shape.translateSelected){
                        model.endTranslate(shape);
                    } else if (shape.rotateSelected){

                    }
                    shape.scaleSelected = false;
                    shape.translateSelected = false;
                    shape.rotateSelected = false;
                }

                startMouse = null;
                lastMouse = null;

                scaling = false;
                moving = false;
                rotating = false;
                drawing = true;
            }
        };

        this.addMouseListener(mouseListener);
        this.addMouseMotionListener(mouseListener);

        model.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        setBackground(Color.WHITE);

        drawAllShapes(g2);
        drawCurrentShape(g2);
    }

    private void drawAllShapes(Graphics2D g2) {
        g2.setColor(new Color(66,66,66));
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for(ShapeModel shape : model.getShapes()) {
            g2.draw(shape.getShape());
            if (shape.isSelected){
                // Draw scale handles
                AffineTransform save = g2.getTransform();

                g2.translate(shape.absX, shape.absY);
                g2.translate(shape.centerPoint.getX(), shape.centerPoint.getY());
                g2.rotate(shape.radians);
                g2.translate(-shape.centerPoint.getX(), -shape.centerPoint.getY());

                shape.setBoundingBox();
                int x = shape.boundingX + shape.boundingWidth;
                int y = shape.boundingY + shape.boundingHeight;

                g2.fillRect(x - shape.handleSize, y - shape.handleSize, 2*shape.handleSize, 2*shape.handleSize);
                // Draw rotation handles
                x = shape.boundingX + (shape.boundingWidth/2);
                y = shape.boundingY - 10;
                g2.fillOval(x - shape.handleSize , y - shape.handleSize, 2*shape.handleSize, 2*shape.handleSize);
                System.out.println("Draw two dots!");
                g2.setTransform(save);
            }

        }
    }

    private void drawCurrentShape(Graphics2D g2) {
        if (startMouse == null || scaling || moving || rotating) {
            return;
        }

        g2.setColor(new Color(66,66,66));
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        g2.draw(new ShapeModel.ShapeFactory().getShape(model.getShape(), (Point) startMouse, (Point) lastMouse).getShape());
    }
}
