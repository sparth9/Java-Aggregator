package net.sf.jagg;

/**
 * This class is a scanner that helps to parse a property specification string.
 *
 * @author Randy Gettman
 * @since 0.1.0
 */
public class PropertyScanner
{
   /**
    * Enumeration for the different types of Tokens.
    */
   public static enum Token
   {
      TOKEN_ERROR_EOI_IN_SQUOTES(-4),
      TOKEN_ERROR_EOI_IN_DQUOTES(-3),
      TOKEN_ERROR_BUF_NULL(-2),
      TOKEN_UNKNOWN(-1),
      TOKEN_WHITESPACE(0),
      TOKEN_STRING(1),
      TOKEN_COMMA(11),
      TOKEN_DOUBLE_QUOTE(12),
      TOKEN_SINGLE_QUOTE(13),
      TOKEN_LEFT_PAREN(14),
      TOKEN_RIGHT_PAREN(15),
      TOKEN_EOI(99);

      private int myCode;

      // Create a token with a code.
      private Token(int code)
      {
         myCode = code;
      }

      /**
       * Returns the unique code associated with this <code>Token</code>.
       * @return The unique code.
       */
      public int getCode()
      {
         return myCode;
      }
   }
   private static final String PUNCT_CHARS_NOT_AS_STRING = "\"'(),";

   private String myPropertyText;
   private int myOffset;
   private boolean amIInsideDoubleQuotes;
   private boolean amIInsideSingleQuotes;
   private String myCurrLexeme;

   /**
    * Construct a <code>PropertyScanner</code> object, with empty input.
    */
   public PropertyScanner()
   {
      this("");
   }

   /**
    * Construct a <code>PropertyScanner</code> object, with the given input.
    * @param propertyText property tag text to scan.
    */
   public PropertyScanner(String propertyText)
   {
      setPropertyText(propertyText);
   }

   /**
    * Returns the <code>Token</code>.  After this call completes, the current
    * lexeme is available via a call to <code>getCurrLexeme</code>.
    * Starts looking at the current offset, and once the token is found, then
    * the offset is advanced to the start of the next token.
    * @return A <code>Token</code>.
    * @see #getCurrLexeme
    */
   public Token getNextToken()
   {
      int iStartOfToken = myOffset;
      int iTokenLength = 0;
      Token tokenType = Token.TOKEN_UNKNOWN;

      // Inside single-quotes, the whole thing until EOI or another single-quote
      // is one string!
      if (amIInsideDoubleQuotes)
      {
         if (iStartOfToken >= myPropertyText.length())
         {
            // EOI while in double quotes -- error!
            iTokenLength = 0;
            tokenType = Token.TOKEN_ERROR_EOI_IN_DQUOTES;
         }
         else if (myPropertyText.charAt(iStartOfToken) == '"')
         {
            iTokenLength = 1;
            tokenType = Token.TOKEN_DOUBLE_QUOTE;
            amIInsideDoubleQuotes = false;
         }
         else
         {
            while ((iStartOfToken + iTokenLength) < myPropertyText.length() &&
                   myPropertyText.charAt(iStartOfToken + iTokenLength) != '"')
               iTokenLength++;
            tokenType = Token.TOKEN_STRING;
         }
      }
      else if (amIInsideSingleQuotes)
      {
         if (iStartOfToken >= myPropertyText.length())
         {
            // EOI while in singe quotes -- error!
            iTokenLength = 0;
            tokenType = Token.TOKEN_ERROR_EOI_IN_SQUOTES;
         }
         else if (myPropertyText.charAt(iStartOfToken) == '\'')
         {
            iTokenLength = 1;
            tokenType = Token.TOKEN_SINGLE_QUOTE;
            amIInsideSingleQuotes = false;
         }
         else
         {
            while ((iStartOfToken + iTokenLength) < myPropertyText.length() &&
                   myPropertyText.charAt(iStartOfToken + iTokenLength) != '\'')
               iTokenLength++;
            tokenType = Token.TOKEN_STRING;
         }
      }
      else
      {
         // EOI test.
         if (iStartOfToken >= myPropertyText.length())
         {
            // End of input string.
            iTokenLength = 0;
            tokenType = Token.TOKEN_EOI;
         }
         // First char starts a string consisting of letters, numbers, and
         // all but a few punctuation characters.
         else if ((iStartOfToken + iTokenLength) < myPropertyText.length() &&
                  !Character.isWhitespace(myPropertyText.charAt(iStartOfToken + iTokenLength)) &&
                  PUNCT_CHARS_NOT_AS_STRING.indexOf(myPropertyText.charAt(iStartOfToken + iTokenLength)) == -1)
         {
            // String mode.
            while ((iStartOfToken + iTokenLength) < myPropertyText.length() &&
                   !Character.isWhitespace(myPropertyText.charAt(iStartOfToken + iTokenLength)) &&
                   PUNCT_CHARS_NOT_AS_STRING.indexOf(myPropertyText.charAt(iStartOfToken + iTokenLength)) == -1)
            {
               iTokenLength++;
            }
            tokenType = Token.TOKEN_STRING;
         }
         else if (myPropertyText.charAt(iStartOfToken) == ',')
         {
            // Comma.
            iTokenLength = 1;
            tokenType = Token.TOKEN_COMMA;
         }
         else if (myPropertyText.charAt(iStartOfToken) == '"')
         {
            // Double Quote.
            iTokenLength = 1;
            tokenType = Token.TOKEN_DOUBLE_QUOTE;
            amIInsideDoubleQuotes = true;
         }
         else if (myPropertyText.charAt(iStartOfToken) == '\'')
         {
            // Single Quote.
            iTokenLength = 1;
            tokenType = Token.TOKEN_SINGLE_QUOTE;
            amIInsideSingleQuotes = true;
         }
         else if (myPropertyText.charAt(iStartOfToken) == '(')
         {
            // Left parenthesis.
            iTokenLength = 1;
            tokenType = Token.TOKEN_LEFT_PAREN;
         }
         else if (myPropertyText.charAt(iStartOfToken) == ')')
         {
            // Right parenthesis.
            tokenType = Token.TOKEN_RIGHT_PAREN;
            iTokenLength = 1;
         }
         else if (Character.isWhitespace(myPropertyText.charAt(iStartOfToken)))
         {
            // Whitespace.
            while ((iStartOfToken + iTokenLength) < myPropertyText.length() &&
                   Character.isWhitespace(myPropertyText.charAt(iStartOfToken + iTokenLength)))
               iTokenLength++;
            tokenType = Token.TOKEN_WHITESPACE;
         }
      }  // End else from if (amIInsideDoubleQuotes)

      // Note down lexeme for access later.
      myCurrLexeme = myPropertyText.substring(iStartOfToken, iStartOfToken + iTokenLength);

      // Update the offset.
      myOffset += iTokenLength;

      return tokenType;
   }

   /**
    * Returns the current lexeme after a call to <code>getNextToken</code>.
    * @return The current lexeme, or <code>null</code> if
    *    <code>getNextToken</code> hasn't been called yet after a reset.
    * @see #getNextToken
    * @see #reset
    */
   public String getCurrLexeme()
   {
      return myCurrLexeme;
   }

   /**
    * Resets the scanner to the beginning of the property text string.
    */
   public void reset()
   {
      myOffset = 0;
      amIInsideDoubleQuotes = false;
      amIInsideSingleQuotes = false;
      myCurrLexeme = null;
   }

   /**
    * Give the <code>PropertyScanner</code> another property text string to
    * scan.  Resets to the beginning of the string.
    * @param propertyText The property text to scan.
    */
   public void setPropertyText(String propertyText)
   {
      myPropertyText = propertyText;
      reset();
   }
}
