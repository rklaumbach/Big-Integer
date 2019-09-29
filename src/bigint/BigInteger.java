package bigint;

/**
 * This class encapsulates a BigInteger, i.e. a positive or negative integer with 
 * any number of digits, which overcomes the computer storage length limitation of 
 * an integer.
 * 
 */
public class BigInteger {

	/**
	 * True if this is a negative integer
	 */
	boolean negative;
	
	/**
	 * Number of digits in this integer
	 */
	int numDigits;
	
	/**
	 * Reference to the first node of this integer's linked list representation
	 * NOTE: The linked list stores the Least Significant Digit in the FIRST node.
	 * For instance, the integer 235 would be stored as:
	 *    5 --> 3  --> 2
	 *    
	 * Insignificant digits are not stored. So the integer 00235 will be stored as:
	 *    5 --> 3 --> 2  (No zeros after the last 2)        
	 */
	DigitNode front;
	
	/**
	 * Initializes this integer to a positive number with zero digits, in other
	 * words this is the 0 (zero) valued integer.
	 */
	public BigInteger() {
		negative = false;
		numDigits = 0;
		front = null;
	}
	
	/**
	 * Parses an input integer string into a corresponding BigInteger instance.
	 * A correctly formatted integer would have an optional sign as the first 
	 * character (no sign means positive), and at least one digit character
	 * (including zero). 
	 * Examples of correct format, with corresponding values
	 *      Format     Value
	 *       +0            0
	 *       -0            0
	 *       +123        123
	 *       1023       1023
	 *       0012         12  
	 *       0             0
	 *       -123       -123
	 *       -001         -1
	 *       +000          0
	 *       
	 * Leading and trailing spaces are ignored. So "  +123  " will still parse 
	 * correctly, as +123, after ignoring leading and trailing spaces in the input
	 * string.
	 * 
	 * Spaces between digits are not ignored. So "12  345" will not parse as
	 * an integer - the input is incorrectly formatted.
	 * 
	 * An integer with value 0 will correspond to a null (empty) list - see the BigInteger
	 * constructor
	 * 
	 * @param integer Integer string that is to be parsed
	 * @return BigInteger instance that stores the input integer.
	 * @throws IllegalArgumentException If input is incorrectly formatted
	 */
	
	// must be able to properly handle 0000 -> 0
	public static BigInteger parse(String integer) 
	throws IllegalArgumentException {
		BigInteger bigInt = new BigInteger();
		
		for (int i = 0; i < integer.length(); i++) {
			if ((!Character.isDigit(integer.charAt(i)) && (i != 0)) || ( i == 0 && !Character.isDigit(integer.charAt(i)) && integer.charAt(i) != '-' && integer.charAt(i) != '+')) {
				throw new IllegalArgumentException ("invalid format");
			} else if (Character.isDigit(integer.charAt(i))) {
				if(integer.charAt(i) != '0' || bigInt.front != null) {
					bigInt.front = new DigitNode(Character.getNumericValue(integer.charAt(i)), bigInt.front);
					bigInt.numDigits++;
				}
			} else if (integer.charAt(i) == '-') {
				bigInt.negative = true;
			}
		}
		
		if (bigInt.front == null) {
			bigInt.front = new DigitNode(0, bigInt.front);
			bigInt.negative = false;
		}
		
		return bigInt;
	}
	
	/**
	 * Adds the first and second big integers, and returns the result in a NEW BigInteger object. 
	 * DOES NOT MODIFY the input big integers.
	 * 
	 * NOTE that either or both of the input big integers could be negative.
	 * (Which means this method can effectively subtract as well.)
	 * 
	 * @param first First big integer
	 * @param second Second big integer
	 * @return Result big integer
	 */
	public static BigInteger add(BigInteger first, BigInteger second) {
		BigInteger large = new BigInteger();
		BigInteger small = new BigInteger();
		
		
		if (first.numDigits > second.numDigits) {
			large = first;
			small = second;
		} else {
			large = second;
			small = first;
		}
		 
		
		DigitNode cL = large.front;
		DigitNode cS = small.front;
		
		BigInteger answer = new BigInteger();
		answer.front = new DigitNode(cL.digit, null);
		answer.numDigits++;
		DigitNode cA = answer.front;
		
		while(cL.next != null) {
			cA.next = new DigitNode(cL.next.digit, null);
			answer.numDigits++;
			cA = cA.next;
			cL = cL.next;
		}
		
		cA = answer.front;
		
		if ((large.negative == true && small.negative == true)||(large.negative == false && small.negative == false)){
		while(cS.next != null) {
			cA.digit = cA.digit + cS.digit;
			
			cS = cS.next;
			cA = cA.next;
		}
		
		cA.digit = cA.digit + cS.digit;
		
		cA = answer.front;
		while (cA.next != null) {
			if(cA.digit >= 10) {
				cA.digit = cA.digit % 10;
				cA = cA.next;
				cA.digit++;
			} else {
				cA = cA.next;
			}
		}
		if (cA.digit >= 10) {
			cA.digit = cA.digit % 10;
			DigitNode newNode = new DigitNode(1, null);
			cA.next = newNode;
			large.numDigits++;
		}
		} else {
			//perform the basic subtraction of each integer from the other
			
			while(cS.next != null) {
				cA.digit = cA.digit - cS.digit;
				
				cS = cS.next;
				cA = cA.next;
			}
			cA.digit = cA.digit - cS.digit;
			
			cA = answer.front;
			
			
			// check if number of digits in each is the same, if so do some stuff to make the subtraction work
			
			if(large.numDigits == small.numDigits) {
				while(cA.next != null) {
					cA = cA.next;
				}
				if(cA.digit < 0) {
					large.negative = !large.negative;
					cA = answer.front;
					while(cA.next != null) {
						cA.digit = cA.digit * -1;
						cA = cA.next;
					}
					cA.digit = cA.digit * -1;
				}
				
				cA = answer.front;
			}
			
			
			//fix negative nodes
			
			while(cA.next != null) {
				if(cA.digit < 0) {
					cA.digit = 10 + cA.digit;
					cA = cA.next;
					cA.digit--;
				} else {
					cA = cA.next;
				}	
			}
			cA = answer.front;
			
			
			//deletes trailing zeros
			
			String strInt = "";
			
			while(cA.next != null) {
				strInt = Integer.toString(cA.digit) + strInt;
				cA = cA.next;
			}
			strInt = Integer.toString(cA.digit) + strInt;
			
			large = parse(strInt);
			
		}
		return answer;
	}
	
	/**
	 * Returns the BigInteger obtained by multiplying the first big integer
	 * with the second big integer
	 * 
	 * This method DOES NOT MODIFY either of the input big integers
	 * 
	 * @param first First big integer
	 * @param second Second big integer
	 * @return A new BigInteger which is the product of the first and second big integers
	 */
	public static BigInteger multiply(BigInteger first, BigInteger second) {
		BigInteger large = new BigInteger();
		BigInteger small = new BigInteger();
		
		int count = 0;
		
		if (first.numDigits > second.numDigits) {
			large = first;
			small = second;
		} else {
			large = second;
			small = first;
		}
		
		boolean finalSignNeg = true;
		if((large.negative && !small.negative) || (!large.negative && small.negative)) {
			finalSignNeg = true;
		} else {
			finalSignNeg = false;
		}
		
		large.negative = false;
		small.negative = false;
		
		
		
		DigitNode cS = small.front;
		BigInteger answer = new BigInteger();
		answer.front = new DigitNode(0,null);
		
		int place = 1;
		while(cS.next != null) {
			if(cS.digit > 0) {
				for (int i = cS.digit*place; i > 0; i--) {
					answer = add(large, answer);
				}
			}
			place = place * 10;
			cS = cS.next;
		}
		for (int i = cS.digit*place; i > 0; i--) {
			answer = add(large, answer);
		}
		
		answer.negative = finalSignNeg;
		
		return answer;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (front == null) {
			return "0";
		}
		String retval = front.digit + "";
		for (DigitNode curr = front.next; curr != null; curr = curr.next) {
				retval = curr.digit + retval;
		}
		
		if (negative) {
			retval = '-' + retval;
		}
		return retval;
	}
}
