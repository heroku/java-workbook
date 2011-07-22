package foo;

import java.sql.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.pool.impl.GenericObjectPool.Config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

public class TickDAO {

    static JedisPool jedisPool;
    private static final String TICKCOUNT_KEY = "tickcount";

    static String dbUrl;

    public TickDAO() {
        if (jedisPool == null) {
            Pattern REDIS_URL_PATTERN = Pattern.compile("^redis://([^:]*):([^@]*)@([^:]*):([^/]*)(/)?");
            Matcher matcher = REDIS_URL_PATTERN.matcher(System.getenv("REDISTOGO_URL"));
            matcher.matches();
            Config config = new Config();
            config.testOnBorrow = true;
            jedisPool = new JedisPool(config, matcher.group(3), Integer.parseInt(matcher.group(4)), Protocol.DEFAULT_TIMEOUT, matcher.group(2));
        }

        if (dbUrl == null) {
            dbUrl = System.getenv("DATABASE_URL").replaceAll("postgres://(.*):(.*)@(.*)", "jdbc:postgresql://$3?user=$1&password=$2");
            dbUpdate("CREATE TABLE ticks (tick timestamp)");
        }
    }

    public void insertTick() {
        dbUpdate("INSERT INTO ticks VALUES (now())");
    }

    private void dbUpdate(String sql) {
        Connection dbConn = null;
        try {
            dbConn = DriverManager.getConnection(dbUrl);
            Statement stmt = dbConn.createStatement();
            stmt.executeUpdate(sql);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                dbConn.close();
            }
            catch (SQLException ignore) {
            }
        }
    }

    private int getTickcountFromDb() {
        Connection dbConn = null;
        try {
            dbConn = DriverManager.getConnection(dbUrl);
            Statement stmt = dbConn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT count(*) FROM ticks");
            rs.next();
            System.out.println("read from database");
            return rs.getInt(1);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                dbConn.close();
            }
            catch (SQLException ignore) {
            }
        }
        return -1;
    }

    public int getTickCount() {
        Jedis jedis = jedisPool.getResource();
        int tickcount = 0;
        String tickcountValue = jedis.get(TICKCOUNT_KEY);
        if (tickcountValue != null) {
            System.out.println("read from redis cache");
            tickcount = Integer.parseInt(tickcountValue);
        }
        else {
            tickcount = getTickcountFromDb();
            jedis.setex(TICKCOUNT_KEY, 30, String.valueOf(tickcount));
        }
        jedisPool.returnResource(jedis);

        return tickcount;
    }

}
