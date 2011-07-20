package foo;

public class Ticker {

    public static void main(String[] args) {
        TickDAO tickDAO = new TickDAO();
        try {
            while (true) {
                tickDAO.insertTick();
                Thread.sleep(1000);
            }
        }
        catch (Exception e) {
        }
    }
}
