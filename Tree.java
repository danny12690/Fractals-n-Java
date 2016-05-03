/*
// FractalGrammars.java

// Copied from Section 8.3 of
//    Ammeraal, L. and K. Zhang (2007). Computer Graphics for Java Programmers, 2nd Edition,
//       Chichester: John Wiley.

 */

/*
CHANGELOG:
Author : Dhananjay Singh
Net Id: dxs145530
UTD Id: 2021250625
1. Clearly, the program works using resursive calls to the turtleGraphics() method.
2. The program takes in an input through a file named "Tree.txt" that contains the grammar for the tree.
3. The class is optimized for tree construction only.
//--------------------------BONUS IMPLEMENTATION------------------------------------//
4. The random offsets for the angle, the width of the branch at every level and the span of the leaf have also been implemented
5. The leaf's direction is oriented as per the branch direction.
*/


import java.awt.*;
import java.awt.event.*;
import java.util.Random;
public class Tree extends Frame
{  public static void main(String[] args)
   { 
         new Tree("Tree.txt");
   }
   Tree(String fileName)
   {  super("Click left or right mouse button to change the level");
      addWindowListener(new WindowAdapter()
         {public void windowClosing(WindowEvent e){System.exit(0);}});
      setSize(800, 600);
      add("Center", new CvTree(fileName));
      show();
   }
}

class CvTree extends Canvas
{  String fileName, axiom, strF, strf, strX, strY;
   int maxX, maxY, level = 1,k=10; 
   int t=0;
   double xLast, yLast, dir, rotation, dirStart, fxStart, fyStart,
      lengthFract, reductFact;
   float thickFactor=10/k;
   
   void error(String str)
   {  System.out.println(str);
      System.exit(1);
   }

   CvTree(String fileName)
   {  Input inp = new Input(fileName);
      if (inp.fails())
         error("Cannot open input file.");
      axiom = inp.readString(); inp.skipRest();
      strF = inp.readString(); inp.skipRest();
      strf = inp.readString(); inp.skipRest();
      strX = inp.readString(); inp.skipRest();
      strY = inp.readString(); inp.skipRest();
      rotation = inp.readFloat(); inp.skipRest();
      dirStart = inp.readFloat(); inp.skipRest();
      fxStart = inp.readFloat(); inp.skipRest();
      fyStart = inp.readFloat(); inp.skipRest();
      lengthFract = inp.readFloat(); inp.skipRest();
      reductFact = inp.readFloat();
      if (inp.fails())
         error("Input file incorrect.");
               
      addMouseListener(new MouseAdapter()
      {  public void mousePressed(MouseEvent evt)
         {  if ((evt.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
            {  
               level--;      // Right mouse button decreases level
               if (level < 1)
                  level = 1;
            }
            else            
               level++;// Left mouse button increases level
         k=1; //k is used to map the thickness function//   
         repaint();
         }
      });

   }

   Graphics g;
   int iX(double x){return (int)Math.round(x);}
   int iY(double y){return (int)Math.round(maxY-y);}

   void drawTo(Graphics g, double x, double y)
   {
      //Introduce Thickness of the stem/branch as a function of depth//
      Graphics2D g2=(Graphics2D)g;
      g2.setStroke(new BasicStroke(thickFactor)); 
      g2.drawLine(iX(xLast), iY(yLast), iX(x) ,iY(y));
      t++;
      xLast = x;
      yLast = y;
   }
   
   //To draw leaves
   void drawLeaf(Graphics g,double x,double y)
   {
      double tempX=xLast;
      double tempY=yLast;
      double rad=(Math.PI/180)*(dir+45);
      Graphics2D g2=(Graphics2D)g;
      //Draw rest of the leaf//
      g2.setStroke(new BasicStroke());
      g2.setColor(Color.green);
      g2.drawLine(iX(tempX),iY(tempY),iX(tempX+thickFactor*2*Math.cos(rad)),iY(tempY+thickFactor*2*Math.sin(rad)));
      tempX=tempX+thickFactor*2*Math.cos(rad);
      tempY=tempY+thickFactor*2*Math.sin(rad);
      rad=(Math.PI/180)*(dir-45);
      g2.drawLine(iX(tempX),iY(tempY),iX(tempX+thickFactor*2*Math.cos(rad)),iY(tempY+thickFactor*2*Math.sin(rad)));
      tempX=tempX+thickFactor*2*Math.cos(rad);
      tempY=tempY+thickFactor*2*Math.sin(rad);
      rad=(Math.PI/180)*(dir-135);
      g2.drawLine(iX(tempX),iY(tempY),iX(tempX+thickFactor*2*Math.cos(rad)),iY(tempY+thickFactor*2*Math.sin(rad)));
      tempX=tempX+thickFactor*2*Math.cos(rad);
      tempY=tempY+thickFactor*2*Math.sin(rad);
      rad=(Math.PI/180)*(dir-225);
      g2.drawLine(iX(tempX),iY(tempY),iX(tempX+thickFactor*2*Math.cos(rad)),iY(tempY+thickFactor*2*Math.sin(rad)));
      g2.setColor(Color.black);
      g2.setStroke(new BasicStroke(thickFactor));
   }

   void moveTo(Graphics g, double x, double y)
   {  xLast = x;
      yLast = y;
   }

   public void paint(Graphics g) 
   {  Dimension d = getSize();
      maxX = d.width - 1;
      maxY = d.height - 1; 
      xLast = fxStart * maxX;
      yLast = fyStart * maxY;
      dir = dirStart;   // Initial direction in degrees
      turtleGraphics(g, axiom, level, lengthFract * maxY);  
   }

   public void turtleGraphics(Graphics g, String instruction, 
      int depth, double len) 
   {  double xMark=0, yMark=0, dirMark=0; 
      double dx=0,dy=0;
      //Introduce randomness in length
      Random randLen=new Random();
      double lenRandom=(randLen.nextInt(((5-1)+1)+1))/10.0;
      //Introduce randomness in angles between branches
      Random randAngle=new Random();
      double angRandom=randAngle.nextInt(((20-10)+1)+10);
      for (int i=0;i<instruction.length();i++) 
      {  char ch = instruction.charAt(i);
         switch(ch)
         {
         case 'F': // Step forward and draw
            // Start: (xLast, yLast), direction: dir, steplength: len
            if (depth == 0)
            {  
                k++;
                System.out.println(k);
                //As the height increases the thickness of the branch decreases//
                thickFactor=5/(float)Math.log10((double)(k));
                double rad = Math.PI/180 * dir; // Degrees -> radians
                dx = (len+len*lenRandom) * Math.cos(rad); dy = (len+len*lenRandom) * Math.sin(rad);
                drawTo(g, xLast + dx, yLast + dy);
                drawLeaf(g,xLast+dx,yLast+dy);
            }
            else
            {
               turtleGraphics(g, strF, depth - 1, reductFact * len);
            }
            break;
         case 'f': // Step forward without drawing
            // Start: (xLast, yLast), direction: dir, steplength: len
            if (depth == 0)
            {  double rad = Math.PI/180 * dir; // Degrees -> radians
                  dx = (len+len*lenRandom) * Math.cos(rad); dy = (len+len*lenRandom) * Math.sin(rad);
               moveTo(g, xLast + dx, yLast + dy);
            }
            else
            {
               turtleGraphics(g, strf, depth - 1, reductFact * len);
            }
            break;
         case 'X':
            if (depth > 0)
               turtleGraphics(g, strX, depth - 1, reductFact * len);
            break;
         case 'Y':
            if (depth > 0)
               turtleGraphics(g, strY, depth - 1, reductFact * len);
            break;
         case '+': // Turn right
            dir -= (rotation+angRandom); break;
         case '-': // Turn left
            dir += (rotation+angRandom); break;
         case '[': // Save position and direction
            xMark = xLast; yMark = yLast; dirMark = dir;
            break;
         case ']': // Back to saved position and direction
            xLast = xMark; yLast = yMark; dir = dirMark; 
            break;
         }
      }
   }
}