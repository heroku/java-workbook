package helloheroku;

public class HelloWorld
{

    public static void main(String[] args)
    {
        try
        {
            while(true)
            {
                System.out.println("hello, world");
                Thread.sleep(1000);
            }
        }
        catch (Exception e)
        {

        }
    }
}
