Java on Heroku Workbook
=======================

This workbook will walk you through the steps to build Java applications that can run on the Heroku Cloud.  Before you get started there is some terminology that will be helpful to know.

PaaS - "Platform as a Service" is a general term for managed environments that run applications.

Heroku - A PaaS provider.

heroku - (Lower case "h") The command line client for managing apps on Heroku.

git - A popular distributed version control system which is used to send apps to Heroku.

Heroku Add-on - The primary way Heroku can be extended.  Add-on's are exposed as services which can be used from any application on Heroku.

Dyno -  The isolated container that runs your web and other processes on Heroku.


Tutorial 1: Hello, World
------------

As we dive into Java on Heroku lets begin with the simplest thing that could possibly work.  In this example we will simply create a standard WAR-packaged web application containing a simple servlet that will handle HTTP requests.  Then we will deploy that application on Heroku.

Before you get started you will need the following prerequisites:

* An account on Heroku.com
* The Heroku command line client installed (Appendix A)
* The git tool installed (Appendix B)
* An SSH key created and associated with your Heroku account (Appendix C)
* Maven 3 installed (Appendix D)

### Building the App

Once you have everything ready to go, create a new project directory named "helloheroku" somewhere on your system.  In that directory create a pom.xml file that will contain the Maven build information.  We need a pom.xml because Heroku will actually build the application for cloud deployment.  The pom.xml will contain the instructions for how to do the build.  We can't just send Heroku a pre-packaged application because the application needs to instrumented in such a way that Heroku can manage the environment variables and other configuration needed for managing deployment of the application.  In the pom.xml file add the following contents:

    <?xml version="1.0" encoding="UTF-8" standalone="no"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">

        <modelVersion>4.0.0</modelVersion>
        <groupId>helloheroku</groupId>
        <artifactId>helloworld</artifactId>
        <packaging>war</packaging>
        <version>1.0-SNAPSHOT</version>

        <dependencies>
            <dependency>
                <groupId>javax.servlet</groupId>
                <artifactId>servlet-api</artifactId>
                <version>2.5</version>
                <scope>provided</scope>
            </dependency>
        </dependencies>

        <build>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-war-plugin</artifactId>
                    <version>2.1.1</version>
                </plugin>
                <plugin>
                    <groupId>org.mortbay.jetty</groupId>
                    <artifactId>jetty-maven-plugin</artifactId>
                    <version>7.3.1.v20110307</version>
                </plugin>
            </plugins>
        </build>
    </project>

This Maven build file contains the minimum configuration needed to create a WAR file that depends on the Servlet API, uses the maven-war-plugin, and jetty-maven-plugin.  The jetty-maven-plugin uses the open source Jetty web server to run the application.  Jetty is the default Java web server on Heroku but you can run Tomcat or other servers with some additional configuration.

Now lets create a very simple Java Servlet.  In your project directory create a tree of new directories:
src/main/java/helloheroku

In that directory create a new file named "HelloWorld.java" containing the following Servlet code:

    package helloheroku;

    import java.io.*;
    import javax.servlet.*;
    import javax.servlet.http.*;

    public class HelloWorld extends HttpServlet
    {
        public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
        {
            PrintWriter out = response.getWriter();
            out.println("hello, world");
            out.close();
        }
    }

That is a very simple servlet that just handles HTTP GET requests and returns a simple string.

To configure the web application to direct requests to the HelloWorld Servlet we need a web.xml file.  Create the following new directory tree in your main project directory:  
    src/main/webapp/WEB-INF

In that directory create a the web.xml file with the following contents:

    <?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
    <web-app xmlns="http://java.sun.com/xml/ns/j2ee"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd"
             version="2.4">

       <servlet>
           <servlet-name>HelloWorld</servlet-name>
           <servlet-class>helloheroku.HelloWorld</servlet-class>
           <load-on-startup>1</load-on-startup>
       </servlet>

       <servlet-mapping>
           <servlet-name>HelloWorld</servlet-name>
           <url-pattern>/</url-pattern>
       </servlet-mapping>

    </web-app>

This file simply instructs the web server to direct requests for "/" to the HelloWorld Servlet.

Now lets start Jetty to test the application locally.  You can do that by running the "jetty:run" Maven goal:

    mvn jetty:run

Maven will now download all of the required dependencies, compile the HelloWorld Java Servlet, assemble a WAR file, then start Jetty with the WAR file deployed in it.  At the end of the output from Maven you should see:  

    2011-06-23 17:39:57.149:INFO::started o.m.j.p.JettyWebAppContext{/,file:/home/jamesw/projects/helloheroku/src/main/webapp},file:/home/jamesw/projects/helloheroku/src/main/webapp
    2011-06-23 17:39:57.232:INFO::Started SelectChannelConnector@0.0.0.0:8080
    [INFO] Started Jetty Server

That indicates that the server was started correctly on port 8080.  Now load the following URL in your browser:

    http://localhost:8080/

That should display "hello, world" - the string which was returned from the HelloWorld Servlet's doGet method.  Now that you’ve verified that everything works locally you can hit "Ctrl-C" to stop Jetty.


### Deploying on Heroku

To deploy the application on Heroku we first need to check the code and configuration files into a local git repository.  The open source git tool is a commonly used distributed version control system.  Shortly we will use git to send the application to Heroku.

To create a local git repository run the "git init" command in the main project directory.  This should return something like the following indicating that the ".git" directory containing the git repository was successfully created:

    Initialized empty Git repository in /home/jamesward/projects/helloheroku/.git/

Lets add the pom.xml file and src directory to the local git repository:

    git add pom.xml src

Now lets commit the files with a commit message of "initial commit":

    git commit -m "initial commit"

You should see something like the following indicating that the files were successfully committed to the local git repository:

    [master (root-commit) 3bcf805] initial commit
    3 files changed, 68 insertions(+), 0 deletions(-)
    create mode 100644 pom.xml
    create mode 100644 src/main/java/helloheroku/HelloWorld.java
    create mode 100644 src/main/webapp/WEB-INF/web.xml

Next thing we need to do is to create a new application provisioning stack on Heroku.  For this we will use the heroku command line client.  See Appendix A if you don't yet have it installed or you haven't logged into heroku via the command line yet.  To create the new application provisioning stack run:

    heroku create -s cedar

We need to specify to use the "cedar" stack when creating this new application because it supports Java.  The output from running that command should look similar to the following:

    Creating morning-window-956... done, stack is cedar
    http://morning-window-956.herokuapp.com/ | git@heroku.com:morning-window-956.git
    Git remote heroku added

The "morning-window-956" is a randomly generated temporary name for the application.  You can rename to any unique and valid name by calling something like:

    heroku apps:rename newuniquename

When the application was created the heroku client outputted the web URL and git URL for this application.  Since we had already created a git repository for this application the heroku client automatically added the heroku remote repository information to the git configuration.

To send the application to heroku we can now just push it there via git:

    git push heroku master

That instructs git to push the app to the heroku remote repo and the master branch on that repo.  This will kick off a Maven build on heroku.  When it finishes you should see something like the following at the end of the output:

    -----> Discovering process types
          Procfile declares types -> web
    -----> Compiled slug size is 17.0MB
    -----> Launching... done, v5
          http://morning-window-956.herokuapp.com deployed to Heroku
    
    To git@heroku.com:morning-window-956.git
    * [new branch]      master -> master

This indicates that everything was built correctly and that the application was started.  You can now connect to the application using the web URL in the output or just run "heroku open".  You should again see "hello, world" in your browser.  Your application is now up and running on the Heroku Cloud!
