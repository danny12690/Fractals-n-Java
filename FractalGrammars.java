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
2. However, this recursion does not allow us to draw a rounded corner because when such a corner is encountered we don't get the required context.
3. The context required is that of the turtle dividing the turn into further levels so as to emulate a curve instead of a sharp turn.
4. Hence, we modify the recursive calls into a set of iterative calls and so we can tackle the issue with rounded corners.
*/


import java.awt.*;
import java.awt.event.*;
public class FractalGrammars extends Frame
{  public static void main(String[] args)
   {  
         new FractalGrammars("Sample.txt");
   }
   FractalGrammars(String fileName)
   {  super("Click left or right mouse button to change the level");
      addWindowListener(new WindowAdapter()
         {public void windowClosing(WindowEvent e){System.exit(0);}});
      setSize(800, 600);
      add("Center", new CvFractalGrammars(fileName));
      show();
   }
}

class CvFractalGrammars extends Canvas
{  String fileName, axiom, strF, strf, strX, strY;
   int maxX, maxY, level = 1; 
   double xLast, yLast, dir,dirLast,rotation, dirStart, fxStart, fyStart,lengthFract, reductFact;

   void error(String str)
   {  System.out.println(str);
      System.exit(1);
   }

   CvFractalGrammars(String fileName)
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
            {  level--;      // Right mouse button decreases level
               if (level < 1)
                  level = 1;
            }
            else            
               level++;      // Left mouse button increases level
            repaint();
         }
      });

   }

   Graphics g;
   int iX(double x){return (int)Math.round(x);}
   int iY(double y){return (int)Math.round(maxY-y);}
   
   //This is where the curve will be drawn (ROUNDED CORNER)

   void drawTo(Graphics g, double x, double y)
   {  g.drawLine(iX(xLast), iY(yLast), iX(xLast + x) ,iY(yLast + y));
      xLast = xLast + x;
      yLast = yLast + y;
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
      dirLast=dirStart; //To maintain the turning context
      String instructions=axiom;
      double instLength=lengthFract*maxY;
      
      /*Because we need context of the previous and the last characters
      which we don't get when we use recursion, we simply use an iterative method
      and expand each turn into levels that are independent of the actual curve.
      */
      for(int k=0;k<level;k++)
      {
      String newInstructions = "";
      for(int j=0;j<instructions.length();j++)
      {
         char c = instructions.charAt(j);
         switch(c)
         {
            case 'F':
               newInstructions += strF;
               break;
            case 'f':
               newInstructions += strf;
               break;
            case 'X':
               newInstructions += strX;
               break;
            case 'Y':
               newInstructions += strY;
               break;
            default:
               newInstructions += c;
               break;
         }
      }
      instructions = newInstructions;
      instLength *= lengthFract;
      }
      // Remove anything that's not F, f, +, -, [, or ]
      instructions = instructions.replaceAll("[^Ff\\+\\-\\]\\[]", "");
      // Remove angles that cancel each other out, makes coding corners easier
      instructions = instructions.replaceAll("\\+\\-|\\-\\+", "");
      
      turtleGraphics(g, instructions, level,instLength);
   }
   
   private static final double CORNER_TURN_FACTOR= .3;

   //Newly implemented to be iterative. 
   public void turtleGraphics(Graphics g, String instruction, 
      int depth, double len) 
   {  
       double xMark=0, yMark=0, dirMark=0; // TODO: stack these
   double rad, dx, dy;
   for (int i=0;i<instruction.length();i++)
   {  char ch = instruction.charAt(i),
         nextCh = (instruction.length() > i+1) ? instruction.charAt(i+1) : '_',
         prevCh = (i > 0) ? instruction.charAt(i-1) : '_';
      switch(ch)
      {
      case 'F': // Step forward and draw
         // Start: (xLast, yLast), direction: dir, steplength: len
         rad = Math.PI/180 * dir; // Degrees -> radians
         dx = len * Math.cos(rad);
         dy = len * Math.sin(rad);
         if((nextCh != '_' && (nextCh == '+' || nextCh == '-')) ||
            (prevCh != '_' && (prevCh == '+' || prevCh == '-')))
            {
               drawTo(g, dx*(CORNER_TURN_FACTOR), dy*(CORNER_TURN_FACTOR));
            }
            else
            {
               drawTo(g, dx, dy);   
            }
               // drawTo(g, dx, dy);   
         break;
      case 'f': // Step forward without drawing
         // Start: (xLast, yLast), direction: dir, steplength: len
         rad = Math.PI/180 * dir; // Degrees -> radians
         dx = len * Math.cos(rad);
         dy = len * Math.sin(rad);
         moveTo(g, xLast + dx, yLast + dy);
         break;
      case '+': // Turn right
         
         if((rotation == 90 || rotation == -90) && prevCh != '_' && prevCh == 'F' && nextCh != '_' && nextCh == 'F')
         {
            double cornerLen = len * Math.sqrt(CORNER_TURN_FACTOR*CORNER_TURN_FACTOR + CORNER_TURN_FACTOR*CORNER_TURN_FACTOR);
            rad = Math.PI/180 * ((dir-rotation/2) % 360); // Degrees -> radians
            dx = cornerLen * Math.cos(rad);
            dy = (cornerLen * Math.sin(rad));
            drawTo(g, dx, dy);
         }
         dir -= rotation; break;
      case '-': // Turn left
         if((rotation == 90 || rotation == -90) && prevCh != '_' && prevCh == 'F' && nextCh != '_' && nextCh == 'F')
         {
            double cornerLen = len * Math.sqrt(CORNER_TURN_FACTOR*CORNER_TURN_FACTOR + CORNER_TURN_FACTOR*CORNER_TURN_FACTOR);
            rad = Math.PI/180 * ((dir+rotation/2) % 360); // Degrees -> radians
            dx = (cornerLen * Math.cos(rad));
            dy = (cornerLen * Math.sin(rad));
            drawTo(g, dx, dy);
         }
         dir += rotation; break;
      case '[': // Save position and direction
         xMark = xLast; yMark = yLast; dirMark = dir; break;
      case ']': // Back to saved position and direction
         xLast = xMark; yLast = yMark; dir = dirMark; break;
      }
    }
  }
   }
