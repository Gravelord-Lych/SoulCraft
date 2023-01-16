package lych.soulcraft.util;

public abstract class StringRedirector extends Redirector<String, String> {
    protected StringRedirector(String value, String... aliases) {
        super(value, aliases);
    }

    public static StringRedirector caseSensitive(String value, String... aliases) {
        return new Cased(value, aliases);
    }

    public static StringRedirector caseInsensitive(String value, String... aliases) {
        return new Caseless(value, aliases);
    }

    @SuppressWarnings("all")
    @Override
    protected boolean isEqual(String s, String alias) {
        if (s == alias) {
            return true;
        }
        if (s == null) {
            return false;
        }
        return isEqualIn(s, alias);
    }

    protected abstract boolean isEqualIn(String s, String alias);

    private static class Cased extends StringRedirector {
        private Cased(String value, String... aliases) {
            super(value, aliases);
        }

        @Override
        protected boolean isEqualIn(String s, String alias) {
            return s.equals(alias);
        }
    }

    private static class Caseless extends StringRedirector {
        private Caseless(String value, String... aliases) {
            super(value, aliases);
        }

        @Override
        protected boolean isEqualIn(String s, String alias) {
            return s.equalsIgnoreCase(alias);
        }
    }
}
