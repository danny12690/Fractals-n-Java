/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


// Input.java: A class to read numbers and characters from textfiles.

// Copied from Section 5.5 of
//    Ammeraal, L. and K. Zhang (2007). Computer Graphics for Java Programmers, 2nd Edition,
//       Chichester: John Wiley.

// Methods of this class, available for other program files:
//   Input(fileName) (constructor; open input file)
//   Input()         (constructor; prepare for input from keyboard)
//   readInt()       (read an integer)
//   readFloat()     (read a float number)
//   readChar()      (read a character)
//   readString()    (read a string between double quotes)
//   skipRest()      (skip all remaining characters of current line)
//   fails()         (input operation failed)
//   eof()           (failure because of end of file)
//   clear()         (reset error flag)
//   close()         (close input file)
//   pushBack(ch)    (push character ch back into the input stream)
import java.io.*;

class Input
{  private PushbackInputStream pbis;
   private boolean ok = true;
   private boolean eoFile = false;
   
   Input(){pbis = new PushbackInputStream(System.in);}
   
   Input(String fileName) 
   {  try
      {  InputStream is = new FileInputStream(fileName);
         pbis = new PushbackInputStream(is);
      }
      catch(IOException ioe){ok = false;}
   }

   int readInt()
   {  boolean neg = false;
      char ch;
      do {ch = readChar();}while (Character.isWhitespace(ch));
      if (ch == '-'){neg = true; ch = readChar();}
      if (!Character.isDigit(ch))
      {  pushBack(ch);
         ok = false; 
         return 0;
      }
      int x = ch - '0';
      for (;;)
      {  ch = readChar();
         if (!Character.isDigit(ch)){pushBack(ch); break;}
         x = 10 * x + (ch - '0');
      }
      return (neg ? -x : x);
   }

   float readFloat()
   {  char ch;
      int nDec = -1;
      boolean neg = false;
      do 
      {  ch = readChar();
      }  while (Character.isWhitespace(ch));
      if (ch == '-'){neg = true; ch = readChar();}
      if (ch == '.'){nDec = 1; ch = readChar();}
      if (!Character.isDigit(ch)){ok = false; pushBack(ch); return 0;}
      float x = ch - '0';
      for (;;)
      {  ch = readChar();
         if (Character.isDigit(ch))
         {  x = 10 * x + (ch - '0');
            if (nDec >= 0) nDec++;
         }
         else
         if (ch == '.' && nDec == -1) nDec = 0;
         else break;
      }
      while (nDec > 0){x *= 0.1; nDec--;}
      if (ch == 'e' || ch == 'E')
      {  int exp = readInt();
         if (!fails())
         {  while (exp < 0){x *= 0.1; exp++;}
            while (exp > 0){x *= 10; exp--;}
         }
      }
      else pushBack(ch);
      return (neg ? -x : x);
   }

   char readChar()
   {  int ch=0;
      try
      {  ch = pbis.read();
         if (ch == -1) {eoFile = true; ok = false;}
      }
      catch(IOException ioe){ok = false;}
      return (char)ch;
   }

   String readString()  // Read first string between quotes (").
   {  String str = "";
      char ch;
      do ch = readChar(); while (!(eof() || ch == '"')); 
                                                // Initial quote
      for (;;)
      {  ch = readChar();
         if (eof() || ch == '"') // Final quote (end of string)
            break;
         str += ch;
      }
      return str;
   }

   void skipRest()  // Skip rest of line
   {  char ch;
      do ch = readChar(); while (!(eof() || ch == '\n'));
   }
   
   boolean fails(){return !ok;}
   boolean eof(){return eoFile;}
   void clear(){ok = true;}

   void close()
   {  if (pbis != null)
      try {pbis.close();}catch(IOException ioe){ok = false;}
   }

   void pushBack(char ch)
   {  try {pbis.unread(ch);}catch(IOException ioe){}
   }
}

