package lych.soulcraft.util;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegexRedirectable implements StringRedirectable {
    private final Pattern pattern;
    private final Function<? super String, ? extends String> valueFunc;

    public RegexRedirectable(Pattern pattern, Function<? super String, ? extends String> valueFunc) {
        this.pattern = pattern;
        this.valueFunc = valueFunc;
    }

    @Override
    public String redirect(String s, Function<? super String, ? extends String> ifNotFound) {
        Matcher matcher = pattern.matcher(s);
        return matcher.matches() ? valueFunc.apply(s) : ifNotFound.apply(s);
    }
}
