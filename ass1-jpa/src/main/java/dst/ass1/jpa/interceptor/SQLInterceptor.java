package dst.ass1.jpa.interceptor;

import org.hibernate.EmptyInterceptor;

public class SQLInterceptor extends EmptyInterceptor {

    private static final long serialVersionUID = -3082243834965597947L;

    public static void resetCounter() {
        // TODO
    }

    public static int getSelectCount() {
        // TODO
        return -1;
    }

    /**
     * If the verbose argument is set, the interceptor prints the intercepted SQL statements to System.out.
     *
     * @param verbose whether or not to be verbose
     */
    public static void setVerbose(boolean verbose) {
        // TODO
    }

    @Override
    public String onPrepareStatement(String sql) {
        // TODO
        return sql;
    }

}
