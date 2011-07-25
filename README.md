Java on Heroku Workbook
=======================


This workbook will walk you through the steps to build Java applications that can run on Heroku.  Before you get started there is some terminology that will be helpful to know.

PaaS - "Platform as a Service" is a general term for managed environments that run applications.

Heroku - A PaaS provider.

heroku - (Lower case "h") The command line client for managing apps on Heroku.

git - A popular distributed version control system which is used to send apps to Heroku.

Heroku Add-on - The primary way Heroku can be extended.  Add-on's are exposed as services which can be used from any application on Heroku.

Dyno -  The isolated container that runs your web and other processes on Heroku.


Prerequisites
-------------

Before you get started with these tutorials you will need to setup your development environment.  Make sure you do each of the following:

* Create an account on Heroku.com
* Install the Heroku command line client (Appendix A)
* Login from the Terminal / command prompt:

    heroku auth:login

If you are on your own computer then you will also need to do the following:

* Install the git tool (Appendix B)
* Create an SSH key and associate it with your Heroku account (Appendix B)
* Install Maven 3 (Appendix C)
* Install and configure PostgreSQL (Appendix D)
* Install and configure Redis (Appendix E)




Tutorial 1: Build a Web App
---------------------------

In this tutorial you will create a web application and deploy it to Heroku. The application will be created using a Maven archetype and will include a web server. You'll be able to run the application locally, and then deploy it to Heroku using git.

### Create the Web Application

Step 1) Run the following Maven command to generate a new project directory containing the basic web application structure, Maven dependencies & build definitions, and a Java class that will start the Jetty web process:

    mvn archetype:generate -DarchetypeCatalog=http://maven.publicstaticvoidmain.net/archetype-catalog.xml

Running this command will prompt you to answer a few questions.  First is the archetype you want to use.  Select "1" for the "embedded-jetty-archetype".  After the dependencies are downloaded you will be prompted for a groupId.  Specify "foo".  Then when asked for the artifactId specify "helloheroku".  Then accept the defaults (by just hitting Enter) for the version, package, and confirmation.

A new project will be created in the "helloheroku" directory.  This project contains everything needed for a Java web application.  The `src/main/webapp` contains a default html page (index.html) and the typical Java web app's `WEB-INF` directory.  The `src/main/java/foo` directory contains a generated `Main.java` file that will later be used to start the web server process.  The main project directory contains a `pom.xml` file which contains the project configuration and dependencies for Maven.

NOTE: Mac and Linux use forward-slashes "/" for file paths while Windows uses back-slashes "\" for paths.  Commands in these instructions will be specific to the operating system.  However the documentation will only use the forward-slashes.  If you are on Windows and want to use a path from the documentation, make sure you switch the forward-slashes to back-slashes.

Step 2) In the "helloheroku" directory tell Maven to compile and install the app into the local Maven repository by running:

    cd helloheroku
    mvn install

Maven will create a jar file for the app and webapp start scripts.  At the end of the output you should see something like:

    [INFO] Installing /home/jamesw/projects/helloheroku/target/helloheroku-1.0-SNAPSHOT.jar to /home/jamesw/.m2/repository/foo/helloheroku/1.0-SNAPSHOT/helloheroku-1.0-SNAPSHOT.jar
    [INFO] Installing /home/jamesw/projects/helloheroku/pom.xml to /home/jamesw/.m2/repository/foo/helloheroku/1.0-SNAPSHOT/helloheroku-1.0-SNAPSHOT.pom
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
    [INFO] ------------------------------------------------------------------------
    [INFO] Total time: 2.980s
    [INFO] Finished at: Fri Jul 22 07:30:10 MDT 2011
    [INFO] Final Memory: 11M/167M
    [INFO] ------------------------------------------------------------------------

Step 3) Set the `REPO` environment variable to the location of the local Maven repository so the dependencies can be located when the web process is started:

On Mac & Linux:

    export REPO=~/.m2/repository

On Windows:

    set REPO=%UserProfile%\.m2\repository

Step 4) Start the webapp process from the main project directory:

On Mac & Linux:

    sh target/bin/webapp

On Windows:

    target\bin\webapp.bat

Running the `webapp` process should output something like:

    2011-07-22 07:32:37.439:INFO::jetty-7.4.4.v20110707
    2011-07-22 07:32:37.626:INFO::started o.e.j.w.WebAppContext{/,file:/home/jamesw/projects/helloheroku/src/main/webapp/}
    2011-07-22 07:32:37.699:INFO::Started SelectChannelConnector@0.0.0.0:8080 STARTING

The Jetty process should now be running and you can open the following URL in your browser:

    http://localhost:8080/

You should see the default index.html page display:

   "hello, world"

Once you have verified that it works, hit "Ctrl-C" to stop the process.  You are now ready to deploy this simple Java web app on Heroku.

### Deploy on Heroku

Step 1) In the main project directory create a new file named `Procfile` containing:

    web: sh target/bin/webapp

This tells Heroku to run the webapp start script for web dynos.

NOTE: All of the files, directories, and code is CASE SENSITIVE.  Make sure that the `Procfile` begins with an upper-case "P" character.

Step 2) Initialize a local git repository, add the files to it, and commit them:

    git init
    git add .
    git commit -m "initial commit"

This should output something like:

    [master (root-commit) b914eee] initial commit
     7 files changed, 165 insertions(+), 0 deletions(-)
     create mode 100644 .gitignore
     create mode 100644 Procfile
     create mode 100644 README.md
     create mode 100644 pom.xml
     create mode 100644 src/main/java/foo/Main.java
     create mode 100644 src/main/webapp/WEB-INF/web.xml
     create mode 100644 src/main/webapp/index.html

NOTE: On Windows you may get "warning : LF will be replaced by CRLF in .gitignore" when doing the `git add .` command.  This can be ignored.

Step 3) Now create a new application provisioning stack on Heroku.  Using the heroku command line client run:

   heroku create --stack cedar

We need to specify to use the "cedar" stack when creating this new application because it supports Java.  The output from running that command should look similar to the following:

	Creating morning-window-956... done, stack is cedar
	http://morning-window-956.herokuapp.com/ | git@heroku.com:morning-window-956.git
	Git remote heroku added

NOTE: The "morning-window-956" is a randomly generated temporary name for the application.  If you want, you can rename to any unique and valid name using the `heroku apps:rename` command.

When the application was created the heroku client outputted the web URL and git URL for this application.  Since we had already created a git repository for this application the heroku client automatically added the heroku remote repository information to the git configuration.

Step 4) If you are on a Dreamforce Lab computer then allow the demo@heroku.com user to push to your Heroku account:

    heroku sharing:add demo@heroku.com

Step 5) Send the application to Heroku using `git push`:

	git push heroku master

That instructs git to push the app to the heroku remote repo and the master branch on that repo.  This will kick off a Maven build on Heroku.  When it finishes you should see something like the following at the end of the output:

	-----> Discovering process types
      	Procfile declares types -> (none)
	-----> Compiled slug size is 17.0MB
	-----> Launching... done, v6
      	http://morning-window-956.herokuapp.com deployed to Heroku
   
	To git@heroku.com:morning-window-956.git
	+ 3bcf805...a72152c master -> master (forced update)

This indicates that everything was built correctly and that the application is ready to run on the cloud.

Step 6) Open the app in the browser using the generated app URL or by running:

    heroku open

You should now see "hello, world" in your browser, this time delivered from the Cloud!

### Scale Your Application on Heroku

By default the app will run on one dyno.  If you want to add more dynos you can use the heroku command line:

    heroku scale web=2

See a list of your processes:

    heroku ps

Scale back to one web dyno:

    heroku scale web=1

### View Application Logs on Heroku

Everything your application outputs to the console (STDOUT & STDERR) can be seen by running:

    heroku logs

You can also see log messages as they happen by using the "tail" mode:

    heroku logs -t



Tutorial 2: Connect to a Database
---------------------------------

This tutorial builds on Tutorial 1 and connects to a database.  You will first test locally with local PostgreSQL database and then use the a shared PostgreSQL database on Heroku.

You can use Hibernate / JPA or other ORM frameworks to connect your Java application to the database, but in this tutorial we will keep things very simple and just use plain JDBC.

Step 1) Update the Maven dependencies to include the PostgreSQL JDBC driver by adding the following dependency to the `pom.xml` file inside the `<dependencies>` tag:

        <dependency>
            <groupId>postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>9.0-801.jdbc4</version>
        </dependency>

Step 2) Create a Data Access Object that will be used to read from and write to the database.  In the `src/main/java/foo` directory create a new file named `TickDAO.java` with the following contents:

    package foo;
    
    import java.sql.*;
    
    public class TickDAO {
        private static String dbUrl;
    
        static {
            dbUrl = System.getenv("DATABASE_URL");
            dbUrl = dbUrl.replaceAll("postgres://(.*):(.*)@(.*)", "jdbc:postgresql://$3?user=$1&password=$2");
        }
    
        public int getTickCount() throws SQLException {
            return getTickcountFromDb();
        }
    
        public static int getScalarValue(String sql) throws SQLException {
            Connection dbConn = null;
            try {
                dbConn = DriverManager.getConnection(dbUrl);
                Statement stmt = dbConn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                rs.next();
                System.out.println("read from database");
                return rs.getInt(1);
            } finally {
                dbConn.close();
            }
        }
    
        private static void dbUpdate(String sql) throws SQLException {
            Connection dbConn = null;
            try {
                dbConn = DriverManager.getConnection(dbUrl);
                Statement stmt = dbConn.createStatement();
                stmt.executeUpdate(sql);
            } finally {
                dbConn.close();
            }
        }
    
        private int getTickcountFromDb() throws SQLException {
            return getScalarValue("SELECT count(*) FROM ticks");
        }
    
        public static void createTable() throws SQLException {
            System.out.println("Creating ticks table.");
            dbUpdate("CREATE TABLE ticks (tick timestamp)");
        }
    
        public void insertTick() throws SQLException {
            dbUpdate("INSERT INTO ticks VALUES (now())");
        }
    }

The TickDAO class gets a database connection using the `DATABASE_URL` environment variable by transforming it into the correct format for JDBC.  When running locally you can set your local `DATABASE_URL` to point to your local database.  When running on Heroku it is automatically set for each dyno that the application runs on.

Step 3) Create a simple JSP file in the src/main/webapp directory named "ticks.jsp" containing:

    <%@ page import="foo.*"%>
    <html>
    <%
    TickDAO tickDAO = new TickDAO();
    tickDAO.insertTick();
    %>
    <%=tickDAO.getTickCount()%> Ticks
    </html>

The "ticks.jsp" file uses the TickDAO to insert a new "tick" into the database and then displays the number of ticks that have been inserted into the database.

Step 4) Create a Java class that will create the database schema in the `src/main/java/foo` directory named `SchemaCreator.java` that contains:

    package foo;
   
    import java.sql.SQLException;
    
    public class SchemaCreator {
        public static void main(String[] args) throws SQLException {
            TickDAO tickDAO = new TickDAO();
            TickDAO.createTable();
        }
    }

Step 5) Update the Maven build to create a start script for the SchemaCreator class.  In the appassembler-maven-plugin's `programs` tag in pom.xml add the following:

    <program>
        <mainClass>foo.SchemaCreator</mainClass>
        <name>schemaCreator</name>
    </program>

Step 6) Set the `DATABASE_URL` to the local PostgreSQL database so you can test locally before running on Heroku. Heroku styled `DATABASE_URL` is formatted as follows:

    postgres://[username]:[password]@[server]/[database-name]

If you have followed instructions to setup PostgreSQL (in appendix D) to the letter, then database url to connect for your local PostgreSQL database should look like this:

* On Mac & Linux:  

    export DATABASE_URL=postgres://foo:foo@localhost/helloheroku

* On Windows:  

    set DATABASE_URL=postgres://foo:foo@localhost/helloheroku


Step 7) Compile and install the application into the local Maven repository so that the webapp script knows where to find it:

    mvn install

Step 8) Create the database schema locally:

* On Mac & Linux:

    sh target/bin/schemaCreator

* On Windows:

    target\bin\schemaCreator.bat

Step 9) Start the webapp:

* On Mac & Linux:

    sh target/bin/webapp

* On Windows:

    target\bin\webapp.bat

You should see something like:

    2011-07-20 18:14:07.342:INFO::jetty-7.4.4.v20110707
    2011-07-20 18:14:07.509:INFO::started o.e.j.w.WebAppContext{/,file:/home/jamesw/projects/java-workbook/tutorial-2/src/main/webapp/}
    2011-07-20 18:14:07.567:INFO::Started SelectChannelConnector@0.0.0.0:8080 STARTING

Step 9) Verify that the ticks.jsp works locally by opening it in your browser:

    http://localhost:8080/ticks.jsp

You should see "1 Ticks" in your browser.  Each time the page is reloaded the number of ticks should increment by 1.

### Deploy on Heroku
	
Step 1) Add the changes to git and commit it:

    git add .
    git commit -m "added new pom, dao and jsp"

Step 2) Push the new version of the app to Heroku:

    git push heroku master

Step 3) Create the database schema on Heroku:

    heroku run "sh target/bin/schemaCreator"

Step 4) Test ticks.jsp in your browser using the app's Heroku URL which will be something like:

    http://empty-winter-343.herokuapp.com/ticks.jsp

You should now see a web page that displays the number of ticks in the database which should increment each time the page is refreshed.

<INSERT SCREENSHOT>



Tutorial 3: Run a Worker Process
--------------------------------

A running application is a collection of processes.  Up until this point, your application has had a single process type - a web process - and you learned how to scale the number of processes of this type. 

In this tutorial you will build out your application to include a new process type - a worker.  You can create many instances of this process type too.  

The Procfile you created in Tutorial 1 lets you define the process types in your application and how to start them.

You will now create a worker process, register it in your Procfile, and scale the number of these processes on Heroku.

Worker processes do "work" in the background and cannot interact with a web process except through other systems like databases and message systems.  You can create multiple worker processes and each worker processes can be run on multiple dynos.

Step 1) Create a worker Java app that adds a new "tick" to the shared database once a second.  In the `src/main/java/foo` directory create a new file named `Ticker.java` that contains:

    package foo;
    
    public class Ticker {
    
        public static void main(String[] args) {
            TickDAO tickDAO = new TickDAO();
            try {
                while (true) {
                    tickDAO.insertTick();
                    System.out.println("tick");
                    Thread.sleep(1000);
                }
            }
            catch (Exception e) {
            }
        }
    }

Step 2) Update the Maven build to create a start script for the Ticker class.  In the appassembler-maven-plugin's `programs` section in pom.xml add the following:

    <program>
        <mainClass>foo.Ticker</mainClass>
        <name>ticker</name>
    </program>

This tells Maven to generate the `ticker` script that will be used to start the worker process.

Step 3) Test the `Ticker` locally by first compiling and installing the application into the local Maven repository:

    mvn install

Step 4) Run the `Ticker`:

* On Mac & Linux:

    sh target/bin/ticker

* On Windows:

    target\bin\ticker.bat

Step 5) Verify that there are no errors.  The process should output "tick" to the console once a second.  Press `Ctrl-C` to stop the process.


### Deploy on Heroku

Step 1) Update the `Procfile` in the main project directory to include the new worker process by adding the following line to the file:

   tick: sh target/bin/ticker

Step 2) Add the changes to git, commit, and push to Heroku:

    git add .
    git commit -m "added worker"
    git push heroku master

Step 3) Start two dynos running the "tick" process:

    heroku scale tick=2

Step 4) Run ticks.jsp in your browser using your own application URL that will be something like:

    http://empty-winter-343.herokuapp.com/ticks.jsp

You will see the number of ticks incrementing much faster now that the `Ticker` processes are also adding new rows to the database.  You can cause the tick rate to increase further by adding more "tick" workers with the `heroku scale` command.

Step 5) Turn off the "tick" workers so they do not continue to use dyno hours:

    heroku scale tick=0

Step 6) Verify that the processes are no longer running:

    heroku ps



Tutorial 4: Use a Heroku Add-on
-------------------------------

Heroku Add-ons provide a simple way to consume other cloud services and extend your application.  In this tutorial you will use the Redis To Go Add-on for distributed caching.  You can find a complete list of Heroku Add-ons at:

    http://addons.heroku.com

This tutorial will begin with the code from Tutorial 3.

Step 1) Add the Jedis client library for Redis to the Maven configuration by adding the following to the `<dependencies>` section of the `pom.xml` file:

    	<dependency>
        	<groupId>redis.clients</groupId>
        	<artifactId>jedis</artifactId>
        	<version>2.0.0</version>
    	</dependency>

Now we will use Redis to cache the number of ticks to reduce the number of database requests.  The cache will be set to expire after 30 seconds.  Begin making the following modifications to the `src/main/java/foo/TickDAO.java` file:

Step 2) Add the following imports directly below the "import java.sql.*;" line:

    import java.util.regex.Matcher;
    import java.util.regex.Pattern;

    import org.apache.commons.pool.impl.GenericObjectPool.Config;

    import redis.clients.jedis.Jedis;
    import redis.clients.jedis.JedisPool;
    import redis.clients.jedis.Protocol;

Step 3) Add the following properties directly below the "public class TickDAO {" line:

        static JedisPool jedisPool;
        private static final String TICKCOUNT_KEY = "tickcount";

Step 4) Below the "static {" line add the following to setup a Redis connection pool:

            Pattern REDIS_URL_PATTERN = Pattern.compile("^redis://([^:]*):([^@]*)@([^:]*):([^/]*)(/)?");
            Matcher matcher = REDIS_URL_PATTERN.matcher(System.getenv("REDISTOGO_URL"));
            matcher.matches();
            Config config = new Config();
            config.testOnBorrow = true;
            jedisPool = new JedisPool(config, matcher.group(3), Integer.parseInt(matcher.group(4)), Protocol.DEFAULT_TIMEOUT, matcher.group(2));

The Redis To Go URL will be set in an environment variable.  When the connection pool is created the necessary parts are parsed out of that variable using Regex.

Step 5) Replace the `getTickCount()` method with the following:

    public int getTickCount() throws SQLException {
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


The `getTickCount()` method now first checks for the tickcount in Redis.  If it doesn't find it there, it proceeds with a database query and then stores the value into Redis.  The value is stored for 30 seconds.

### Run Locally

Step 1) Compile the application with Maven:

    mvn install

Step 2) Set the `REDISTOGO_URL` environment variable:

* On Linux & Mac:

    export REDISTOGO_URL=redis://:@localhost:6379/

* On Windows:

    set REDISTOGO_URL=redis://:@localhost:6379/

Step 3) Run the webapp:

* On Linux & Mac:

    sh target/bin/webapp

* On Windows:

    target/bin/webapp.bat

Step 4) Open `http://localhost:8080/ticks.jsp` in your browser. Refresh it to see that the number of ticks is only updated every 30 seconds; when the Redis cache expires.

### Deploy on Heroku

Step 1) Add the free-tier of the Redis To Go Add-on to the `helloheroku` application:

    heroku addons:add redistogo:nano

You should see something like:

    -----> Adding redistogo:nano to floating-beach-922... done, v20 (free)

Step 2) Add the changes to git, commit, and push to heroku:

    git add .
    git commit -m "added redis support"
    git push heroku master

Step 3) Open `ticks.jsp` on Heroku (using the Heroku application URL) in your browser to verify that the number of ticks is only updated every 30 seconds by reloading the page a few times or by restarting the `tick` worker process.



Tutorial 5: Spring Roo on Heroku
--------------------------------

Spring Roo is a tool that makes building Java web apps incredibly easy.  It utilizes a command line and AspectJ to reduce the time need to do common web app tasks like create CRUD objects and UI elements.  In this tutorial you will create a simple Spring Roo web app, configure it, and then deploy it on Heroku.  You will need Spring Roo version 1.1.4.  Rather than starting from scratch this tutorial will use the Pet Clinic sample that comes with Spring

Step 1) Using your command line / terminal, create a new directory for the project and then enter that directory:

	mkdir petclinic
	cd petclinic

Step 2) Run the sample petclinic script with Spring Roo (substitute the Spring Roo install location into the commands):

On Mac & Linux:

	<SPRING ROO HOME>/bin/roo.sh script --file <SPRING ROOM HOME>/samples/clinic.roo

On Windows:

	<SPRING ROO HOME>\bin\roo.bat script --file <SPRING ROOM HOME>\samples\clinic.roo

You now have a fully functional CRUD application!  Start the application in Tomcat by running:

	mvn tomcat:run

Now open your browser to the following URL to try the application:

   http://localhost:8080/petclinic

Press `Ctrl-C` to shut down Tomcat and return to the command line.

This application could be run as-is on Heroku but it's best if an application and it's environment can be easily reproduced on Heroku.  To do this the app server dependencies, the application dependencies, the application packaging, and the application startup process will all be defined in a reproducible way.

Step 3) Add the Jetty server dependencies and PostgreSQL JDBC Driver to the `pom.xml` file in the `<dependencies>` tag:

     <dependency>
         <groupId>org.eclipse.jetty</groupId>
         <artifactId>jetty-webapp</artifactId>
         <version>7.4.4.v20110707</version>
    	</dependency>
    	<dependency>
         <groupId>org.mortbay.jetty</groupId>
         <artifactId>jsp-2.1-glassfish</artifactId>
         <version>2.1.v20100127</version>
    	</dependency>
     <dependency>
         <groupId>postgresql</groupId>
         <artifactId>postgresql</artifactId>
         <version>9.0-801.jdbc4</version>
     </dependency>

NOTE: For simplicity the Jetty web application server is used since it is easily embeddable.  Tomcat could also be used.

Step 4) By modifying the `pom.xml` file, configure the following dependencies to be added to the runtime environment by changing the `scope` value from `provided` to `compile` for the following artifactId's:

* servlet-api
* org.springframework.roo.annotations
* el-api
* jsp-api

NOTE: For instance, change:

    	<dependency>
        	<groupId>javax.servlet</groupId>
        	<artifactId>servlet-api</artifactId>
        	<version>2.5</version>
        	<scope>provided</scope>
    	</dependency>

To:

    	<dependency>
        	<groupId>javax.servlet</groupId>
        	<artifactId>servlet-api</artifactId>
        	<version>2.5</version>
        	<scope>compile</scope>
    	</dependency>

Step 5) Change the application packaging from a WAR file to a JAR file by removing the following line from the pom.xml file:

	<packaging>war</packaging>

Step 6) Add the `appassembler-maven-plugin` to the `plugins` tag of the `pom.xml` file:

	<plugin>
    	<groupId>org.codehaus.mojo</groupId>
    	<artifactId>appassembler-maven-plugin</artifactId>
    	<version>1.1.1</version>
    	<executions>
        	<execution>
            	<phase>package</phase>
            	<goals><goal>assemble</goal></goals>
            	<configuration>
                	<assembleDirectory>target</assembleDirectory>
                	<generateRepository>false</generateRepository>
                	<extraJvmArguments>-Xmx512m</extraJvmArguments>
                	<programs>
                    	<program>
                        	<mainClass>Main</mainClass>
                        	<name>webapp</name>
                    	</program>
                	</programs>
            	</configuration>
        	</execution>
    	</executions>
	</plugin>

Step 7) Create a new Java class that will start an embedded Jetty instance.  In the `src/main/java` directory create a new file named `Main.java` containing the following code:

	import org.eclipse.jetty.server.Server;
	import org.eclipse.jetty.webapp.WebAppContext;

	public class Main
	{
    	public static void main(String[] args) throws Exception
    	{
        	String webappDirLocation = "src/main/webapp/";

        	String webPort = System.getenv("PORT");
        	if(webPort == null || webPort.isEmpty()) {
            	webPort = "8080";
        	}

        	Server server = new Server(Integer.valueOf(webPort));
        	WebAppContext root = new WebAppContext();

        	root.setContextPath("/");
        	root.setDescriptor(webappDirLocation+"/WEB-INF/web.xml");
        	root.setResourceBase(webappDirLocation);
        	root.setParentLoaderPriority(true);

        	server.setHandler(root);
        	server.start();
        	server.join();   
    	}
	}



Step 8) Update the database connection settings by modifying the `src/main/resources/META-INF/spring/applicationContext.xml` file and changing the following:

    <property name="url" value="${database.url}"/>
    <property name="username" value="${database.username}"/>
    <property name="password" value="${database.password}"/>

To instead read the database URL from an environment variable:

    <property name="url" value="#{systemEnvironment['DATABASE_URL'].replaceAll(
               'postgres://(.*):(.*)@(.*)',
               'jdbc:postgresql://$3?user=$1&amp;password=$2') }"/>


Step 9) In the `src/main/resources/META-INF/spring/database.properties` file change the `org.hsqldb.jdbcDriver` value to `org.postgresql.Driver`.

Step 10) In the `src/main/resources/META-INF/persistence.xml` file changed the value of the `hibernate.dialect` to:

    org.hibernate.dialect.PostgreSQLDialect

Step 10) Use Maven to compile and install the app into the local repository:

	mvn install

Step 11) Set the local database URL and start the app locally:

* On Mac & Linux:

    export DATABASE_URL=postgres://foo:foo@localhost/helloheroku
    export REPO=$HOME/.m2/repository
    sh target/bin/webapp

* On Windows:

    set DATABASE_URL=postgres://foo:foo@localhost/helloheroku
    set REPO=%UserProfile%\.m2\repository
    target\bin\webapp.bat

Step 12) Load the app in your browser to test it:

    http://localhost:8080/


### Deploy on Heroku

Step 1) Create a `Procfile` in the main project directory containing:

    web:   sh target/bin/webapp

Step 2) Create a git repository, add the files to git, and commit them:

    git init
    git add pom.xml src Procfile
    git commit -m "initial commit"

Step 3) Create a new application on Heroku:

    heroku create --stack cedar

Step 4) Push the app to Heroku:

    git push heroku master

Step 5) Open the application on Heroku:

    heroku open




Appendix A: Install the Heroku Command Line
-------------------------------------------

The Heroku command line client wraps the Heroku RESTful APIs and provides access to all of the Heroku management tasks.  

The client is written in Ruby, so in order to install it you first need to install Ruby, the Ruby package manager (RubyGems), and then the client itself.

To install Ruby:

* On Mac: Mac Snow Leopard and Lion ship with Ruby.

* On Ubuntu Linux run:

    sudo apt-get install ruby

* On Windows: Use the RubyInstaller from http://rubyinstaller.org/

To install RubyGems:
Step 1) Download the latest ZIP from http://rubygems.org/
Step 2) Uncompress the downloaded file and change directory to the expanded folder.
Step 3) Run the setup script in a Terminal / command prompt:

* On Windows & Mac:

    ruby setup.rb

* On Linux:

    sudo ruby setup.rb
    sudo ln -s /usr/bin/gem1.8 /usr/bin/gem

To install the Heroku client, run the following in a Terminal / command prompt:

* On Windows & Mac:

    gem install heroku

* On Linux:

    sudo gem install heroku


Verify your installation by running the following command:

    heroku version

You should see something like the following:

    heroku-gem/2.3.6



Appendix B: Install git & Setup a SSH Key
-----------------------------------------

The `git` tool is used to upload your application to Heroku.  It is a native application and installation varies depending on platform.  In order to upload your application to Heroku using git you will need to create a SSH key (if you don't already have one) and associate it with your Heroku account.

Here are links to various git installation and SSH key creation guides for each platform:

On Mac: http://help.github.com/mac-set-up-git/
On Windows: http://help.github.com/win-set-up-git/
On Linux: http://help.github.com/linux-set-up-git/

Once you have your SSH key setup you can associate it with your Heroku account by running:

    heroku keys:add



Appendix C: Install Maven
-------------------------

Download the latest Maven binary release:
http://maven.apache.org/download.html

Extract the archive and follow the installation instructions:
http://maven.apache.org/download.html#Installation



Appendix D: Install and Configure PostgreSQL
--------------------------------------------

Step 1) Download and Install the PostgreSQL database by following the system-specific instructions: http://www.postgresql.org/download/

Step 2) Start the PostgreSQL server (if the installer didn't do so already).

Step 3) Create a new user with `superuser` privileges named 'foo' with a password of 'foo':

* On Linux:

    sudo -u postgres createuser -P foo

* On Mac:

    createuser -P foo

* On Windows:

    createuser -U postgres -P foo


Step 4) Create a new database named "helloheroku":

    createdb -U foo -W -h localhost helloheroku

Step 5) Test the connection to the database:

    psql -U foo -W -h localhost helloheroku

You should see something like:

    Password for user foo:
    psql (9.0.4)

    helloheroku=#

You can type \q to exit the psql command line.


Appendix E: Install and Configure Redis
---------------------------------------

Step 1) Install the Redis Server

* On Ubuntu Linux:

    sudo apt-get install redis-server
The server starts by default.

* On Mac, follow the Redis install instructions:
http://redis.io/download

* On Windows, download and install the Redis Server for Windows from:
https://github.com/dmajkic/redis/downloads
After uncompressing the files, start the server by executing redis-server.exe.

Step 2) Verify it works using the `redis-cli` application.  You should see a command line like (can be different on different platforms / configurations):

    redis 127.0.0.1:6379>

