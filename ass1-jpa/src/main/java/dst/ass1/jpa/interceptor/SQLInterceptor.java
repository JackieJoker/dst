package dst.ass1.jpa.interceptor;

import org.hibernate.EmptyInterceptor;

public class SQLInterceptor extends EmptyInterceptor {

    private static final long serialVersionUID = -3082243834965597947L;
    private static boolean verbose;
    private static int selectCount;

    public static void resetCounter() {
        selectCount = 0;
    }

    public static int getSelectCount() {
        return selectCount;
    }

    /**
     * If the verbose argument is set, the interceptor prints the intercepted SQL statements to System.out.
     *
     * @param verbose whether or not to be verbose
     */
    public static void setVerbose(boolean verbose) {
        SQLInterceptor.verbose = verbose;
    }

    @Override
    public synchronized String onPrepareStatement(String sql) {
        if(verbose) {
            System.out.println(sql);
        }
        if(sql.startsWith("select") && (sql.contains("from Location") || sql.contains("from Trip"))){
            selectCount++;
        }
        return sql;
    }
}
