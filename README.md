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

Before you get started with these tutorials you will need to setup your environment.  Make sure you do each of the following:

* Create an account on Heroku.com
* Install the Heroku command line client (Appendix A)
* Install the git tool (Appendix B)
* Create an SSH key and associate it with your Heroku account (Appendix B)
* Install Maven 3 (Appendix C)


Tutorial 01: Hello, World
-------------------------

In this tutorial you will create and deploy the simplest application that could possibly work - a standard JAR-packaged application that writes "hello, world" to the console.  This, and all subsequent tutorials, assume that you have installed all the prerequisites.
### Create and Package a Java App

Once you have all of the prerequisites setup follow the steps below.

Step 1) Create a new project directory named `helloheroku` somewhere on your system.

Step 2) In your project directory create a tree of new directories:

    src/main/java/helloheroku

Step 3) In that directory create a new file named `HelloWorld.java` containing the following code:

   package helloheroku;
   
   public class HelloWorld
   {
          public static void main(String[] args)
       {
           System.out.println("hello, world");
       }
   }

That is a very simple class that just returns a simple string.

Step 4) In the main project directory create a text file named `pom.xml` that will contain the Maven build information.  We need a `pom.xml` because Heroku will actually build the application for cloud deployment.  The `pom.xml` will contain the instructions for how to do the build.  We can't just send Heroku a pre-packaged application because the application needs to instrumented in such a way that Heroku can manage the environment variables and other configuration needed for managing deployment of the application.  In the `pom.xml` file add the following contents:

    <?xml version="1.0" encoding="UTF-8" standalone="no"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">

       <modelVersion>4.0.0</modelVersion>
       <groupId>helloheroku</groupId>
       <artifactId>helloworld</artifactId>
       <packaging>jar</packaging>
       <version>1.0-SNAPSHOT</version>

    </project>

This Maven build file contains the minimum configuration needed to have Maven compile a JAR for us.


Step 5) Compile the class into a JAR run the `package` Maven goal:

   mvn package

This creates the `target/helloworld-1.0-SNAPSHOT.jar` file containing the HelloWorld class.

Step 6) Run that class:

   java -cp target/helloworld-1.0-SNAPSHOT.jar helloheroku.HelloWorld

The application should output `hello, world` and then exit.

### Deploy on Heroku

To deploy the application on Heroku the code and pom.xml file must be checked into a git repository.

Step 1) Create a local git repository by running the following in the main project directory:

   git init

This should return something like the following indicating that the `.git` directory containing the git repository was successfully created:

    Initialized empty Git repository in /home/jamesward/projects/java-workbook/01-helloheroku/.git/

Step 2) Add the pom.xml file and src directory to the local git repository:

    git add pom.xml src

Step 3) Commit the files with a commit message of "initial commit":

    git commit -m "initial commit"

You should see something like the following indicating that the files were successfully committed to the local git repository:

    [master (root-commit) a72152c] initial commit
    2 files changed, 22 insertions(+), 0 deletions(-)
    create mode 100644 pom.xml
    create mode 100644 src/main/java/helloheroku/HelloWorld.java

Step 4) Now create a new application provisioning stack on Heroku.  Using the heroku command line client run:

    heroku create --stack cedar

We need to specify to use the "cedar" stack when creating this new application because it supports Java.  The output from running that command should look similar to the following:

    Creating morning-window-956... done, stack is cedar
    http://morning-window-956.herokuapp.com/ | git@heroku.com:morning-window-956.git
    Git remote heroku added

The "morning-window-956" is a randomly generated temporary name for the application.  If you want, you can rename to any unique and valid name by calling something like:

    heroku apps:rename newuniquename

When the application was created the heroku client outputted the web URL and git URL for this application.  Since we had already created a git repository for this application the heroku client automatically added the heroku remote repository information to the git configuration.

Step 5) Send the application to Heroku using a `git push`:

    git push heroku master

That instructs git to push the app to the heroku remote repo and the master branch on that repo.  This will kick off a Maven build on heroku.  When it finishes you should see something like the following at the end of the output:

    -----> Discovering process types
          Procfile declares types -> (none)
    -----> Compiled slug size is 17.0MB
    -----> Launching... done, v6
          http://morning-window-956.herokuapp.com deployed to Heroku
   
    To git@heroku.com:morning-window-956.git
    + 3bcf805...a72152c master -> master (forced update)

This indicates that everything was built correctly and that the application is ready to run on the Cloud.

Step 6) In order to run the application on Heroku we need to send a command to Heroku that will start the process.  To do that use the `heroku run` command with an argument telling Heroku what to do:

    heroku run "java -Xmx64M -cp target/helloworld-1.0-SNAPSHOT.jar helloheroku.HelloWorld"

Heroku will now start-up a Dyno for you with your application on it and then run the specified command.  You should again see `hello, world` but this time the it's coming from the Cloud!


Tutorial 02: Starting a Worker Process
--------------------------------------

Many applications (like web servers) don't just run and exit like the simple example in Tutorial 01, instead they continue running until they are stopped.  In this tutorial the "hello, world" application from Tutorial 01 will be modified to continue running and output `hello, world` once a second.

Step 1) Using the Tutorial 01 project modify the HelloWorld.java file and change it's contents to the following:

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

Step 2) Package and run the application:

    mvn package
    java -cp target/helloworld-1.0-SNAPSHOT.jar helloheroku.HelloWorld

You should see 'hello, world' outputted every second until you hit Ctrl-C.

Step 3) A `Procfile` instructs Heroku how to run a process automatically on one or more Dynos.  Simply create a file named `Procfile` in the main project directory with the following contents:

    helloworld: java -Xmx64M -cp target/helloworld-1.0-SNAPSHOT.jar helloheroku.HelloWorld

Once deployed, this will tell Heroku that every time a new `helloworld` Dyno is started the HelloWorld Java process is run.

Step 4) Tell git to add the modified `HelloWorld.java` file and the `Procfile` to the local git repo:

    git add src/main/java/helloheroku/HelloWorld.java Procfile

Step 5) Commit and push the changes the Heroku:

    git commit -m "HelloWorld outputs something every second"
    git push heroku master

Step 6) Tell Heroku to start some Dynos running the `helloworld` process.  Start two Dynos by running:

    heroku scale helloworld=2

Verify that the new Dynos are working by running:

    heroku ps

This should output something like:

    Process       State               Command
    ------------  ------------------  ------------------------------
    helloworld.1  up for 5s           java -Xmx64M -cp target/helloworld..
    helloworld.2  up for 0s           java -Xmx64M -cp target/helloworld..

Great!  There are now two Dynos running on Heroku.  Each one has a processes that outputs `hello, world` every second.  You can see that output using the `heroku logs -t` command which should output something like:

    2011-06-24T20:59:43+00:00 app[helloworld.1]: hello, world
    2011-06-24T20:59:43+00:00 app[helloworld.2]: hello, world

Step 7) You can shut down those Dynos by scaling them to use zero Dynos:

    heroku scale helloworld=0

If at any time you want to start up new ones again, just run the `heroku scale` command.


Tutorial 03: Starting a Web Process
-----------------------------------

Similar to the application in Tutorial 02 a web process is an application that continually runs until it is shutdown.  Additionally a web process listens for HTTP connections and returns content (HTML, JSON, images and etc.)

In this tutorial you will create a simple HTTP listener process.  For the sake of simplicity we will utilize the Jetty web server and a very simple Servlet to show how basic HTTP handling works with Heroku.

### Create the App

Step 1) Run the following Maven command to generate a new project directory containing the basic web application structure, Maven dependencies & build definitions, and a Java class that will start the Jetty web process:

   mvn archetype:generate -DarchetypeCatalog=http://maven.publicstaticvoidmain.net/archetype-catalog.xml

Running this command will prompt you to answer a few questions.  First is the archetype you want to use.  Select "1" for the "embedded-jetty-archetype".  After the dependencies are downloaded you will be prompted for a groupId.  Specify "herokuweb".  Then when asked for the artifactId specify "HelloWorld".  Then accept the defaults (by just hitting Enter) for the version, package, and confirmation.

A new project has been created in the "HelloWorld" directory.

Step 2) In the "HelloWorld" directory tell Maven to compile and install the app into the local Maven repository by running:

   mvn install

Maven created a jar file for the app and Jetty start scripts.

Step 3) Set the `REPO` environment variable to the location of the local Maven repository:

On Mac & Linux:

   export REPO=~/.m2/repository

On Windows (replace <USERNAME> with your Windows Username):

   set REPO=C:\Documents and Settings\<USERNAME>\.m2\repository

Step 4) Start the Jetty process:

On Mac & Linux:

   sh target/bin/webapp

On Windows:

   target\bin\webapp.bat

The Jetty process should now be running and you can open the following URL in your browser:

   http://localhost:8080/

You should see the default index.html page display:

  "hello, world"

Once you have verified that it works, hit "Ctrl-C" to stop the process.  You are now ready to deploy this simple Java web app on Heroku.

### Deploy on Heroku

Step 1) In the main project directory create a new file named `Procfile` containing:

   web: sh target/bin/webapp

This tells Heroku to run the webapp Jetty start script for web Dynos.

Step 2) Initialize the local git repository, add the files to it, and commit them:

  git init
  git add .
  git commit -m "initial commit"

Step 3) Create a new app on Heroku:

  heroku create --stack cedar

Step 4) Push the application to Heroku:

   git push heroku master

Step 5) Tell Heroku to run the application on one Dyno:

   heroku scale web=1

Step 6) Open the app in the browser using the generated app URL or by running:

   heroku open

You should now see "hello, world" in your browser, this time delivered from the Cloud!

### Add an Interactive JSP

Now that you have a simple Java web application running on Heroku we will add an interactive JSP to the app.

Step 1) Create a simple JSP file in the src/main/webapp directory named "hello.jsp" containing:

   <html>
   <%
   if (request.getParameter("name") != null) {
   %>
   hello, <%=request.getParameter("name")%>
   <%
   }
   else {
   %>
   <form>
     <input name="name">
     <input type="submit">
   </form>
   <%
   }
   %>
   </html>

This simple JSP will display "hello, " and if it receives a parameter `name` otherwise it will display a form where a name can be entered.

You can test this locally by starting the local Jetty process and opening the hello.jsp in your browser.

Step 2) Add the new file to git and commit it:

   git add src/main/webapp/hello.jsp
   git commit -m "added new jsp"

Step 3) Push the new version of the app to Heroku:

   git push heroku master

Step 4) Open hello.jsp in your browser using the app's Heroku URL which will be something like:

   http://empty-winter-343.herokuapp.com/hello.jsp


Tutorial 04: Connect to a Database
----------------------------------


Tutorial 05: Use a Heroku Add-on
--------------------------------


??? Tutorial 06: Spring Roo on Heroku
-------------------------------------

??? Tutorial 07: Play Framework on Heroku
-----------------------------------------

??? Tutorial 08: Grails on Heroku
---------------------------------

??? Tutorial 09: Lift on Heroku
-------------------------------



Appendix A: Install the Heroku Command Line
-------------------------------------------

The heroku command line client wraps the Heroku RESTful APIs and provides access to all of the Heroku management tasks.  The heroku command line itself is written in Ruby, so in order to install it you first need to install Ruby.

On Mac: Mac Snow Leopard ships with Ruby.

On Ubuntu Linux:
   sudo apt-get install ruby

On Windows: Use the RubyInstaller from http://rubyinstaller.org/

Once Ruby is installed you will need to install gems, a Ruby package manager.

Download the latest ZIP from:
http://rubygems.org/pages/download

Uncompress the downloaded file and then in the created directory run the setup.rb script with ruby (on Linux use sudo to install as root):

   ruby setup.rb

On Linux create a symlink for the 'gem' executable:

   sudo ln -s /usr/bin/gem1.8 /usr/bin/gem

Install the heroku gem (on Linux use sudo to install as root):

   gem install heroku



Now you can run the heroku command line in the Terminal:

   heroku version

You should be the following output:

   heroku-gem/2.3.6


After creating an account at Heroku.com you can use the heroku command line to login:

   heroku auth:login

Appendix B: Install git & Setup a SSH Key
-----------------------------------------

The `git` tool is used to upload your application to Heroku.  It is a native application and installation varies depending on the platform.  In order to upload your application to Heroku using git you will need to create a SSH key (if you don't already have one) and associate it with your Heroku account.

Here are links to various git installation and SSH Key creation guides for each platform:

On Mac: http://help.github.com/mac-set-up-git/
On Windows: http://help.github.com/win-set-up-git/
On Linux: http://help.github.com/linux-set-up-git/

Once you have your ssh key setup you can associate it with your Heroku account by running:

   heroku keys:add


Appendix C: Install Maven 3
---------------------------

Download the latest Maven binary release:
http://maven.apache.org/download.html

Extract the archive and following the installation instructions:
http://maven.apache.org/download.html#Installation
