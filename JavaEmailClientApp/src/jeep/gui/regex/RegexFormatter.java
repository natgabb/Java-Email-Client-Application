package jeep.gui.regex;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.text.DefaultFormatter;

/**
 * This creates a regular expression based on a AbstractFormatter pattern.
 * 
 * @author Ken Fogel
 * 
 */
public class RegexFormatter extends DefaultFormatter {

	private static final long serialVersionUID = 352040531052863432L;

	private Pattern pattern;
	private Matcher matcher;

	public RegexFormatter() {
		super();
	}

	/**
	 * Creates the regular expression from the pattern.
	 */
	public RegexFormatter(String pattern) throws PatternSyntaxException {
		this();
		setPattern(Pattern.compile(pattern));
	}

	/**
	 * Creates a regular expression based <code>AbstractFormatter</code>.
	 * <code>pattern</code> specifies the regular expression that will be used
	 * to determine if a value is legal.
	 */
	public RegexFormatter(Pattern pattern) {
		this();
		setPattern(pattern);
	}

	/**
	 * Sets the pattern that will be used to determine if a value is legal.
	 */
	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	/**
	 * Returns the Pattern used to determine if a value is legal.
	 */
	public Pattern getPattern() {
		return pattern;
	}

	/**
	 * Sets the Matcher used in the most recent test if a value is legal.
	 */
	protected void setMatcher(Matcher matcher) {
		this.matcher = matcher;
	}

	/**
	 * Returns the Matcher from the most test.
	 */
	protected Matcher getMatcher() {
		return matcher;
	}

	/**
	 * Parses text returning an arbitrary Object. Some formatters may return
	 * null.
	 * <p>
	 * If a Pattern has been specified and the text completely matches the
	 * regular expression this will invoke <code>setMatcher</code>.
	 * 
	 * @throws ParseException
	 *             if there is an error in the conversion
	 * @param text
	 *            String to convert
	 * @return Object representation of text
	 */
	public Object stringToValue(String text) throws ParseException {
		Pattern pattern = getPattern();

		if (pattern != null) {
			Matcher matcher = pattern.matcher(text);

			if (matcher.matches()) {
				setMatcher(matcher);
				return super.stringToValue(text);
			}
			throw new ParseException("Pattern did not match", 0);
		}
		return text;
	}
}
