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
    private static final Pattern REDIS_URL_PATTERN = Pattern.compile("^redis://([^:]*):([^@]*)@([^:]*):([^/]*)(/)?");

    private Connection getConn() throws SQLException {
        String dbUrl = System.getenv("DATABASE_URL");
        dbUrl = dbUrl.replaceAll("postgres://(.*):(.*)@(.*)", "jdbc:postgresql://$3?user=$1&password=$2");
        return DriverManager.getConnection(dbUrl);
    }

    private void createTable() {
        try {
            Statement stmt = getConn().createStatement();
            stmt.executeUpdate("CREATE TABLE ticks (tick timestamp)");
        }
        catch (SQLException e) {
        }
    }

    private Jedis getJedis() {
        if (jedisPool == null) {
            Matcher matcher = REDIS_URL_PATTERN.matcher(System.getenv("REDISTOGO_URL"));
            matcher.matches();
            Config config = new Config();
            config.testOnBorrow = true;
            jedisPool = new JedisPool(config, matcher.group(3), Integer.parseInt(matcher.group(4)), Protocol.DEFAULT_TIMEOUT, matcher.group(2));
        }
        return jedisPool.getResource();
    }

    public int getTickCount() {
        Jedis jedis = getJedis();
        int tickcount = -1;
        String tickcountValue = jedis.get(TICKCOUNT_KEY);
        if (tickcountValue != null) {
            tickcount = Integer.parseInt(tickcountValue);
        }
        else {
            try {
                Statement stmt = getConn().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT count(*) FROM ticks");
                rs.next();
                jedis.setex(TICKCOUNT_KEY, 60, rs.getString(1));
                tickcount = rs.getInt(1);
            }
            catch (SQLException e) {
            }
        }
        jedisPool.returnResource(jedis);
        
        return tickcount;
    }

    public void insertTick() {
        try {
            createTable();
            Statement stmt = getConn().createStatement();
            stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
        }
        catch (SQLException e) {
        }
    }
}
